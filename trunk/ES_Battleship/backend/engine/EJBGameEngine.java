package backend.engine;

import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueReceiver;
import javax.naming.NamingException;

import backend.constants.MoveResult;
import backend.state.Board;
import backend.state.Player;
import backend.util.JMSMsgUtils;

public class EJBGameEngine {
	
	private Player player1 = null;
	private Player player2 = null;
	String whoMoves = null;
	private Vector playerQueues;
    
    private JMSMsgUtils msgUtil;
	private static boolean isGameActive = false;
	
    private class PlayerQMap {
		private String playerId;
		private String queue;

		public PlayerQMap(String pId, String q) {
			playerId = pId;
			queue = q;
		}

		public String getPlayerId() {
			return playerId;
		}
		public void setPlayerId(String playerId) {
			this.playerId = playerId;
		}
		public String getQueue() {
			return queue;
		}
		public void setQueue(String queue) {
			this.queue = queue;
		}
	}
    
    public class GameEngineListener implements MessageListener {

		public void onMessage(Message msg) {
			String playerId = "";
			try {
				playerId = ((MapMessage) msg).getString("playerId");
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Received message from " + playerId);
		}

	}

	public EJBGameEngine() throws NamingException, JMSException {
		this.msgUtil = new JMSMsgUtils();
		QueueReceiver recv = msgUtil.getSession().createReceiver(
				msgUtil.getGameEngineQueue());
		recv.setMessageListener(new GameEngineListener());
		resetGame();
	}

	/**
	 * Adds player to the game, if possible
	 * @param pId New player's ID
	 * @param board New player's board
	 */
	public void addPlayer(String pId, Board board) {
		if (this.player1 == null) {
			this.player1 = new Player(pId, board);
		} else if (this.player2 == null) {
			this.player2 = new Player(pId, board);
		} else {
			throw new RuntimeException("Game full");
		}
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
	 */
	public MoveResult move(String pId, String x, String y) {
		Board oppBoard = getOpponentBoard(pId);
		Player thisMovePlayer = getPlayer(pId);
		int xCoord = Integer.parseInt(x);
		int yCoord = Integer.parseInt(y);

		if (oppBoard.isHit(xCoord, yCoord)) {
			thisMovePlayer.getOppBoard().setHit(xCoord, yCoord);
			if (thisMovePlayer.getOppBoard().hasLost()) {
				isGameActive = false;
				return MoveResult.WIN;
			}
			return MoveResult.HIT;
		} else {
			thisMovePlayer.getOppBoard().setMiss(xCoord, yCoord);
			return MoveResult.MISS;
		}
	}

	/**
	 * Returns the actual board of the opponent for a given player ID
	 * @param pId Player's id whose opponent's board to return
	 * @return revealed board of the opponent
	 */
	private Board getOpponentBoard(String pId) {
		if (pId.equals(this.player1.getId())) {
			return this.player2.getMyBoard();
		} else if (pId.equals(this.player2.getId())){
			return this.player1.getMyBoard();
		} else {
			throw new RuntimeException("No such player");
		}
	}

	/**
	 * Determines if both players are connected and initialized
	 * 
	 * @return true if both players are connected and initialized
	 */
	public boolean isGameReady() {
		return this.player1 != null && this.player2 != null;
	}

	/**
	 * Obtain Player reference for a given ID
	 * 
	 * @param pId
	 *            Player's ID
	 * @return Player reference
	 */
	private Player getPlayer(String pId) {
		if (pId.equals(this.player1.getId())) {
			return this.player1;
		} else if (pId.equals(this.player2.getId())) {
			return this.player2;
		} else {
			throw new RuntimeException("No such player");
		}
	}
	
	public boolean isMyTurn(String playerId) {
		return playerId.equals(this.player1.getId());
	}

	public void resetGame() {
		isGameActive = true;
		this.player1 = null;
		this.player2 = null;
	}
	
	public void closeConnections() throws JMSException {
		this.msgUtil.stop();
	}
	
    public static void main(String args[]) throws Exception {
        System.out.println("Begin SendRecvClient,now=" + 
                           System.currentTimeMillis());
        EJBGameEngine engine = new EJBGameEngine();
        while (isGameActive) {
        	Thread.sleep(10000);
        }
        engine.closeConnections();
        System.exit(0);
    }
}
