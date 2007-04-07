package backend.engine;

import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueReceiver;
import javax.naming.NamingException;

import backend.constants.GameResult;
import backend.constants.MoveResult;
import backend.constants.MsgHeader;
import backend.constants.QueueNames;
import backend.state.Board;
import backend.state.Player;
import backend.util.BackendException;
import backend.util.JMSMsgUtils;
import backend.util.Logger;

public class EJBGameEngine {

    private JMSMsgUtils msgUtil;
    private Vector<PlayerContainer> players;
    private static boolean isGameActive = false;
	private final static int MAX_PLAYERS = 2;
	
    private class PlayerContainer {
		private String queue;
		private Player player;

		/**
		 * Create a PlayerContainer object to map queues to players
		 * and vice-versa.
		 * 
		 * @param queue
		 * @param player
		 */
		public PlayerContainer(String queue, Player player) {
			this.queue = queue;
			this.player = player;
		}
		
		/**
		 * Get the current queue name.
		 * @return
		 */
		public String getQueue() {
			return this.queue;
		}

		/**
		 * Get the current player object.
		 * @return
		 */
		public Player getPlayer() {
			return this.player;
		}
	}
    
    /**
     * Get the player object from the queue name.
     * @param queueName
     * @return
     */
    public Player getPlayerByQueue(String queueName) {
    	for (int i = 0; i < this.players.size(); i++) {
    		String currentQueue = this.players.elementAt(i).getQueue();
    		if (currentQueue.equals(queueName)) {
    			return this.players.elementAt(i).getPlayer();
    		}
		}
    	return null;
    }
    
    /**
     * Get the queue object fomr the player id.
     * @param playerId
     * @return
     */
    public String getQueueByPlayerId(String playerId) {
    	for (int i = 0; i < this.players.size(); i++) {
    		String currentPid = this.players.elementAt(i).getPlayer().getId();
    		if (currentPid.equals(playerId)) {
    			return this.players.elementAt(i).getQueue();
    		}
		}
    	return null;
    }
    
    public class GameEngineListener implements MessageListener {

		public void onMessage(Message msg) {
			MapMessage map = (MapMessage) msg;
			try {
				if (!map.itemExists("header")) {
					Logger.LogWarning("Header was null. Skipping..");
					return;
				}
				String playerId = map.getString("playerId");
				String opponentId = "";
				String destination = map.getString("destination");
				int header = Integer.parseInt(map.getString("header"));
				
				//System.out.println("<< " + header + " message from " + playerId + " to " + destination);
						
				switch(header)
				{
					case MsgHeader.READY:
						Board board = Board.deserialize(map.getString("board"));
						addPlayer(playerId, board);
						if (!isGameReady()) {
							msgUtil.sendErrorMessage(destination, getQueueByPlayerId(playerId), playerId, "Waiting for an opponent.. Please wait..");
							Logger.LogInfo("Waiting for " + (MAX_PLAYERS - players.size()) + " more player..");
							break;
						} else {
							Logger.LogInfo("Game is READY!");
						}
						opponentId = getOpponentId(playerId);
						msgUtil.sendTurnMessage(QueueNames.GAME_ENGINE, getQueueByPlayerId(playerId), playerId, isMyTurn(playerId));
						msgUtil.sendTurnMessage(QueueNames.GAME_ENGINE, getQueueByPlayerId(opponentId), opponentId, isMyTurn(opponentId));
						break;
					case MsgHeader.MOVE_INFO:
						int x = map.getInt("x");
						int y = map.getInt("y");
						MoveResult moveResult = move(playerId, x, y);
						opponentId = getOpponentId(playerId);
						
						Logger.LogInfo("PlayerId: " + playerId + " moved to (" + x + ", " + y + ") against " + opponentId + ". Result: " + moveResult.toString());
						
						// check if the game is over
						if (moveResult == MoveResult.WIN) {
							Logger.LogError("GAME OVER! " + playerId + " WINS THE GAME!");
							msgUtil.sendGameOverMessage(QueueNames.GAME_ENGINE, getQueueByPlayerId(playerId), playerId, x, y, GameResult.WIN);
							msgUtil.sendGameOverMessage(QueueNames.GAME_ENGINE, getQueueByPlayerId(opponentId), opponentId, x, y, GameResult.LOSS);
							break;
						}
						
						msgUtil.sendIsHitMessage(QueueNames.GAME_ENGINE, getQueueByPlayerId(playerId), playerId, moveResult, x, y);
						msgUtil.sendMoveNotifyMessage(QueueNames.GAME_ENGINE, getQueueByPlayerId(opponentId), opponentId, x, y);
						break;
					// client side messages should not be used here
					case MsgHeader.TURN_INFO:
					case MsgHeader.MOVE_RESULT:
					case MsgHeader.MOVE_NOTICE:
					case MsgHeader.GAME_OVER:
					case MsgHeader.ERROR:
						throw new BackendException("Header (" + header + ") was invalid.");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

    /**
     * 
     * @throws NamingException
     * @throws JMSException
     */
	public EJBGameEngine() throws NamingException, JMSException {
		this.msgUtil = new JMSMsgUtils();
		this.players = new Vector<PlayerContainer>(2);
		QueueReceiver queueReceiver = msgUtil.getSession().createReceiver(this.msgUtil.getGameEngineQueue());
		queueReceiver.setMessageListener(new GameEngineListener());
		this.resetGame();
	}

	/**
	 * Adds player to the game, if possible
	 * @param pId New player's ID
	 * @param board New player's board
	 * @throws Exception 
	 */
	public void addPlayer(String pId, Board board) throws Exception {
		if (this.players.size() == MAX_PLAYERS) {
			//msgUtil.sendErrorMessage(QueueNames.GAME_ENGINE, getQueueByPlayerId(pId), pId, "Game full.");
			throw new BackendException("Game full");
		}

		Player player = new Player(pId, board);
		String queue = QueueNames.CLIENT_ONE;
		if (this.players.size() == 1) {
			queue = QueueNames.CLIENT_TWO;
		}
		this.players.add(new PlayerContainer(queue, player));
		Logger.LogInfo("Adding " + pId + " to " + queue);
	}
	
	/**
	 * Updates player's representation of opponent's board with a hit or a miss,
	 * and returns whether the move was a hit or a miss
	 * 
	 * @param pId
	 *            Player id
	 * @param x
	 *            x-coordinate of the move
	 * @param y
	 *            y-coordinate of the move
	 * @return true if move is a hit
	 * @throws Exception 
	 */
	public MoveResult move(String pId, int x, int y) throws Exception {
		Board oppBoard = getOpponentBoard(pId);
		Player thisMovePlayer = getPlayer(pId);

		if (oppBoard.isHit(x, y)) {
			thisMovePlayer.getOppBoard().setHit(x, y);
			if (thisMovePlayer.getOppBoard().hasLost()) {
				isGameActive = false;
				return MoveResult.WIN;
			}
			return MoveResult.HIT;
		} else {
			thisMovePlayer.getOppBoard().setMiss(x, y);
			return MoveResult.MISS;
		}
	}

	/***
	 * Gets the opponent's playerId.
	 * @param playerId
	 * @return
	 * @throws Exception
	 */
	private String getOpponentId(String playerId) throws Exception {
		if (this.players.size() != MAX_PLAYERS) {
			msgUtil.sendErrorMessage(QueueNames.GAME_ENGINE, getQueueByPlayerId(playerId), playerId, "No opponent found.");
			throw new BackendException("There is no opponent!");
		} else {
			for (int i = 0; i < this.players.size(); i++) {
				String opponentId = this.players.elementAt(i).getPlayer().getId();
				if(!opponentId.equals(playerId)){
					return opponentId;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the actual board of the opponent for a given player ID
	 * @param pId Player's id whose opponent's board to return
	 * @return revealed board of the opponent
	 * @throws Exception 
	 */
	private Board getOpponentBoard(String pId) throws Exception {
		if (this.players.size() != MAX_PLAYERS) {
			this.msgUtil.sendErrorMessage(QueueNames.GAME_ENGINE, getQueueByPlayerId(pId), pId, "No opponent found.");
			throw new BackendException("There is no opponent!");
		} else {
			for (int i = 0; i < this.players.size(); i++) {
				String currentPid = this.players.elementAt(i).getPlayer().getId();
				if(!currentPid.equals(pId)){
					return this.players.elementAt(i).getPlayer().getMyBoard();
				}
			}
		}
		return null;
	}

	/**
	 * Determines if both players are connected and initialized
	 * 
	 * @return true if both players are connected and initialized
	 */
	public boolean isGameReady() {
		return (this.players.size() == MAX_PLAYERS);
	}

	/**
	 * Get the Player object by playerId.
	 * 
	 * @param playerId
	 *            Player's ID
	 * @return Player reference
	 */
	private Player getPlayer(String playerId) {
		for (int i = 0; i < this.players.size(); i++) {
			String thisPlayerId = this.players.elementAt(i).getPlayer().getId();
			if (thisPlayerId.equals(playerId)) {
				return this.players.elementAt(i).getPlayer();
			}
		}
		return null;
	}
	
	/**
	 * Checks whether it is the given player's turn.
	 * 
	 * @param playerId
	 * @return true if player's turn, false otherwise.
	 */
	public boolean isMyTurn(String playerId) {
		if (playerId.equals(this.players.elementAt(0).getPlayer().getId())) {
			return true;
		}
		return false;
	}

	/***
	 * Resets the current game.
	 *
	 */
	public void resetGame() {
		isGameActive = true;
		this.players.clear();
	}
	
	/***
	 * Close the current JMS connection.
	 * @throws JMSException
	 */
	public void closeConnections() throws JMSException {
		this.msgUtil.stop();
	}
	
	/***
	 * This is the main entry point for the GameEngine.
	 * @param args
	 * @throws Exception
	 */
    public static void main(String args[]) throws Exception {
    	Logger.LogInfo("Game Engine has been initialized.. Waiting for players..");
        EJBGameEngine engine = new EJBGameEngine();
        while (isGameActive) {
        	Thread.sleep(10000);
        }
        engine.players.clear();
        engine.closeConnections();
        Logger.LogInfo("Game Engine is shutting down. Thank you for playing.");
    }
}
