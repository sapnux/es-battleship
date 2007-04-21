package backend.client;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueReceiver;
import javax.naming.NamingException;

import backend.constants.GameResult;
import backend.constants.MsgHeader;
import backend.constants.QueueNames;
import backend.state.Board;
import backend.state.Player;
import backend.util.BackendException;
import backend.util.JMSMsgUtils;
import backend.util.Logger;

public class EJBClient implements IEJBClient, Runnable {
	private Player player;
	private String queueName;
	private boolean connected;
	private JMSMsgUtils msgUtil;

	public EJBClient(String queueName, String playerId, Board board) throws JMSException, NamingException {
		this.player = new Player(playerId, board);
		this.msgUtil = new JMSMsgUtils();
		this.queueName = queueName;
        QueueReceiver queueReceiver = this.msgUtil.getSession().createReceiver(this.msgUtil.getQueueByName(queueName));
        queueReceiver.setMessageListener(new ClientListener());
	}

    public class ClientListener implements MessageListener {
		public void onMessage(Message msg) {
			MapMessage map = (MapMessage) msg;
			try {
				if (!map.itemExists("header")) {
					Logger.LogWarning("The header did not exist in the message.");
					return;
				}
				int header = Integer.parseInt(map.getString("header"));
				int x, y;
				switch(header)
				{
					case MsgHeader.TURN_INFO:
						boolean isMyTurn = map.getBoolean("isMyTurn");
						player.setMyTurn(isMyTurn);
						break;
					case MsgHeader.MOVE_RESULT:
						x = map.getInt("x");
						y = map.getInt("y");
						if (!map.itemExists("isHit")) {
							throw new BackendException("isHit did not exist.");
						}
						boolean isHit = map.getBoolean("isHit");
						if (isHit) {
							player.addMessage("Your strategic attack at [" + x + ", " + y + "] is a STRIKE!!");
							player.addMessage("Attack again!");
							player.getOppBoard().setHit(x, y);
						} else {
							player.addMessage("Your clumsy attack at [" + x +", " + y + "] was unsuccessful..");
							player.addMessage("Your enemy is contemplating their attack. Please wait..");
							player.getOppBoard().setMiss(x, y);
						}
						player.setMyTurn(isHit);
						break;
					case MsgHeader.MOVE_NOTICE:
						x = map.getInt("x");
						y = map.getInt("y");
						if (player.getMyBoard().isHit(x, y)) {
							player.getMyBoard().setHit(x, y);
							player.addMessage("You've been hit at [" + x + ", " + y + "] by your enemy!");
							player.addMessage("Your enemy is contemplating their attack. Please wait..");
							player.setMyTurn(false);
						} else {
							player.getMyBoard().setMiss(x, y);
							player.addMessage("Your enemy's foolish attack at [" + x + ", " + y + "] missed..");
							player.addMessage("Seize this opportunity and attack carefully..");
							player.setMyTurn(true);
						}
						break;
					case MsgHeader.GAME_OVER:
						x = map.getInt("x");
						y = map.getInt("y");
						GameResult result = GameResult.valueOf(map.getString("result"));
						if (player.isMyTurn()) {
							player.getOppBoard().setHit(x, y);
							player.addMessage("Your strategic attack at [" + x + ", " + y + "] is a STRIKE!!");
							player.addMessage("--- GAME OVER. YOU ARE THE WINNER! ---");
						} else {
							player.getMyBoard().setHit(x, y);
							player.addMessage("You've been hit at [" + x + ", " + y + "] by your enemy!");
							player.addMessage("--- GAME OVER. YOU HAVE LOST! ---");
						}
						player.setGameResult(result);
						player.setChanged();
						break;
					case MsgHeader.ERROR:
						String message = map.getString("errorMsg");
						player.addMessage(message);
						player.setChanged();
						player.notifyObservers();
						break;
					case MsgHeader.READY:
					case MsgHeader.MOVE_INFO:
						throw new BackendException("Header (" + header + ") was invalid.");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
    /**
     * 
     */
    public void run() {
		signalReadiness();
		
		while(this.connected) {
			if (!this.player.isMyTurn()) {
				this.waitForTurn();
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Logger.LogError(e.getMessage());
				e.printStackTrace();
			}
		}
	}

    /**
     * Sends the 'ready' message to the server. 
     */
	public void signalReadiness() {
		try {
			msgUtil.sendReadyMessage(this.queueName, QueueNames.GAME_ENGINE, this.player.getId(), this.player.getMyBoard());
			this.connected = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends coordinates of attack. Returns true if HIT, false otherwise.
	 * 
	 * @see backend.IEJBClient#move(int, int)
	 */
	public void move(int x, int y) {
		try {
			this.msgUtil.sendMoveMessage(this.queueName, QueueNames.GAME_ENGINE, this.player.getId(), x, y);
			this.waitForTurn();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @deprecated Do not use this in the EJB component framework.
	 */
	public void connect(String server, String port) {
		// do nothing
	}
	
	/**
	 * @deprecated Do not use this in the EJB component framework.
	 */
	public void disconnect() {
		// do nothing
	}
	
	/**
	 * Returns the current instance of the player.
	 * 
	 * @see backend.IEJBClient#getBoard()
	 */
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * 
	 * @see backend.IEJBClient#waitForTurn()
	 */
	public void waitForTurn() {
		while (!this.getPlayer().isMyTurn()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Logger.LogError(e.getMessage());
				e.printStackTrace();
			}
		}
	}
}