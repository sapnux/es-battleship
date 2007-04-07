package backend.client;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
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
	private boolean connected;
	private JMSMsgUtils msgUtil;
	private Queue queue;
	String queueName;

	public EJBClient(String queueName, String id, Board board) throws JMSException, NamingException {
		this.player = new Player(id, board);
		this.msgUtil = new JMSMsgUtils();
		this.queue = this.msgUtil.lookupQueueByName(queueName);
		this.queueName = queueName;
        QueueReceiver recv = msgUtil.getSession().createReceiver(queue);
        recv.setMessageListener(new ClientListener());
	}

    public class ClientListener implements MessageListener {
		public void onMessage(Message msg) {
			MapMessage map = (MapMessage) msg;
			try {
				if(!map.itemExists("header")) {
					Logger.LogWarning("The header did not exist in the message.");
					return;
				}
				String playerId = map.getString("playerId");
				String destination = map.getString("destination");
				int header = Integer.parseInt(map.getString("header"));
				int x, y;
				System.out.println("<< Received " + header + " message from " + playerId + " to " + destination);
						
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
							player.addMessage("You hit at: "+x+", " +y);
							player.addMessage("Your turn");
							player.getOppBoard().setHit(x, y);
						} else {
							player.addMessage("You missed at: "+x+", " +y);
							player.addMessage("Opponent's turn");
							player.getOppBoard().setMiss(x, y);
						}
						player.setMyTurn(isHit);
						break;
					case MsgHeader.MOVE_NOTICE:
						x = map.getInt("x");
						y = map.getInt("y");
						if (player.getMyBoard().isHit(x, y)) {
							player.getMyBoard().setHit(x, y);
							player.addMessage("Opponent hit at: "+x+", " +y);
							player.addMessage("Opponent's turn");
							player.setMyTurn(false);
						} else {
							player.getMyBoard().setMiss(x, y);
							player.addMessage("Opponent missed at: "+x+", " +y);
							player.addMessage("Your turn");
							player.setMyTurn(true);
						}
						break;
					case MsgHeader.GAME_OVER:
						x = map.getInt("x");
						y = map.getInt("y");
						GameResult result = (GameResult)map.getObject("result");
						if (player.isMyTurn()) {
							player.getOppBoard().setHit(x, y);
							player.addMessage("You hit at: "+x+", " +y);
							player.addMessage("[DONE] You have WON!");
						} else {
							player.getMyBoard().setHit(x, y);
							player.addMessage("Opponent hit at: "+x+", " +y);
							player.addMessage("[DONE] You have LOST!");
						}
						player.setGameResult(result);
						player.setChanged();
						player.notifyObservers();
						break;
					case MsgHeader.ERROR:
						String message = map.getString("errorMsg");
						player.addMessage(message);
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
	
    public void run() {
		signalReadiness();
		
		while(this.connected) {
			if (!this.player.isMyTurn()) {
				//?this.waitForTurn();
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Logger.LogError(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void signalReadiness() {
		try {
			msgUtil.sendReadyMessage(queueName, QueueNames.GAME_ENGINE, this.player.getId(), this.player.getMyBoard());
			this.connected = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends coordinates of attack. Returns true if HIT, false otherwise.
	 * 
	 * @see backend.IClient#move(int, int)
	 */
	public void move(int x, int y) {
		try {
			msgUtil.sendMoveMessage(queueName, QueueNames.GAME_ENGINE, this.player.getId(), x, y);
			waitForTurn();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the current instance of the player.
	 * 
	 * @see backend.IClient#getBoard()
	 */
	public Player getPlayer() {
		return this.player;
	}

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