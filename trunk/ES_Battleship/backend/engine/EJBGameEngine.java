package backend.engine;

import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueReceiver;
import javax.naming.NamingException;

import backend.constants.MoveResult;
import backend.constants.MsgHeader;
import backend.constants.QueueNames;
import backend.state.Board;
import backend.state.Player;
import backend.util.BackendException;
import backend.util.JMSMsgUtils;

public class EJBGameEngine {
	
	private Vector<PlayerContainer> players;
    
    private JMSMsgUtils msgUtil;
	private static boolean isGameActive = false;
	private final static int MAX_PLAYERS = 2;
	
    private class PlayerContainer {
		private String queue;
		private Player player;

		public PlayerContainer(String q, Player p) {
			this.queue = q;
			this.player = p;
		}
		
		public String getQueue() {
			return this.queue;
		}
		
		public void setQueue(String queue) {
			this.queue = queue;
		}

		public Player getPlayer() {
			return this.player;
		}

		public void setPlayer(Player player) {
			this.player = player;
		}
	}
    
    public Player getPlayerByQueue(String q) {
    	for (int i = 0; i < players.size(); i++) {
    		String currentQueue = players.elementAt(i).getQueue();
    		if (currentQueue.equals(q)) {
    			return players.elementAt(i).getPlayer();
    		}
		}
    	return null;
    }
    
    public String getQueueByPlayerId(String pId) {
    	for (int i = 0; i < players.size(); i++) {
    		String currentPid = players.elementAt(i).getPlayer().getId();
    		if (currentPid.equals(pId)) {
    			return players.elementAt(i).getQueue();
    		}
		}
    	return null;
    }
    
    public class GameEngineListener implements MessageListener {

		public void onMessage(Message msg) {
			MapMessage map = (MapMessage) msg;
			try {
				String playerId = map.getString("playerId");
				String opponentId = getOpponentId(playerId);
				String destination = map.getString("destination");
				int header = Integer.parseInt(map.getString("header"));
				
				System.out.println("<< " + header + " message from " + playerId + " to " + destination);
						
				switch(header)
				{
					case MsgHeader.READY:
						Board board = Board.deserialize(map.getString("board"));
						board.print();
						addPlayer(playerId, board);
						while (!isGameReady()){
							Thread.sleep(2000);
							continue;
						}
						msgUtil.sendTurnMessage(QueueNames.GAME_ENGINE, getQueueByPlayerId(playerId), playerId, isMyTurn(playerId));
						break;
					case MsgHeader.MOVE_INFO:
						int x = map.getInt("x");
						int y = map.getInt("y");
						MoveResult moveResult = move(playerId, x, y);
						msgUtil.sendIsHitMessage(QueueNames.GAME_ENGINE, "look_up", playerId, moveResult);
						msgUtil.sendMoveNotifyMessage(QueueNames.GAME_ENGINE, "look_up_other_player", opponentId, x, y);
						break;
					case MsgHeader.TURN_INFO:
					case MsgHeader.MOVE_RESULT:
					case MsgHeader.MOVE_NOTICE:
					case MsgHeader.GAME_OVER:
					case MsgHeader.ERROR:
						throw new Exception("Header (" + header + ") was invalid.");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public EJBGameEngine() throws NamingException, JMSException {
		this.msgUtil = new JMSMsgUtils();
		this.players = new Vector<PlayerContainer>(2);
		QueueReceiver recv = msgUtil.getSession().createReceiver(
				msgUtil.getGameEngineQueue());
		recv.setMessageListener(new GameEngineListener());
		resetGame();
	}

	/**
	 * Adds player to the game, if possible
	 * @param pId New player's ID
	 * @param board New player's board
	 * @throws Exception 
	 */
	public void addPlayer(String pId, Board board) throws Exception {
		if (this.players.size() == MAX_PLAYERS) {
			msgUtil.sendErrorMessage(QueueNames.GAME_ENGINE, getQueueByPlayerId(pId), pId, "Game full.");
			throw new BackendException("Game full");
		}

		Player tmpPlayer = new Player(pId, board);
		String tmpQueue = getQueueByPlayerId(pId);
		this.players.add(new PlayerContainer(tmpQueue, tmpPlayer));
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
	 * 
	 * @param pId
	 * @return
	 * @throws Exception
	 */
	private String getOpponentId(String pId) throws Exception {
		if (this.players.size() != MAX_PLAYERS) {
			msgUtil.sendErrorMessage(QueueNames.GAME_ENGINE, getQueueByPlayerId(pId), pId, "No opponent found.");
			throw new BackendException("There is no opponent!");
		} else {
			for (int i = 0; i < this.players.size(); i++) {
				String currentPid = this.players.elementAt(i).getPlayer().getId();
				if(!currentPid.equals(pId)){
					return currentPid;
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
			msgUtil.sendErrorMessage(QueueNames.GAME_ENGINE, getQueueByPlayerId(pId), pId, "No opponent found.");
			throw new BackendException("There is no opponent!");
		} else {
			for (int i = 0; i < this.players.size(); i++) {
				String currentPid = this.players.elementAt(i).getPlayer().getId();
				if(currentPid.equals(pId)){
					return this.players.elementAt(i).getPlayer().getOppBoard();
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
	 * Obtain Player reference for a given ID
	 * 
	 * @param pId
	 *            Player's ID
	 * @return Player reference
	 */
	private Player getPlayer(String pId) {
		for (int i = 0; i < this.players.size(); i++) {
			String currentPid = this.players.elementAt(i).getPlayer().getId();
			if (currentPid.equals(pId)) {
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
		players.clear();
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
        System.out.println("Game Engine started at " + System.currentTimeMillis());
        EJBGameEngine engine = new EJBGameEngine();
        while (isGameActive) {
        	Thread.sleep(10000);
        }
        engine.closeConnections();
        System.exit(0);
    }
}
