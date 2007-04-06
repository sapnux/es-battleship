package backend.util;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import backend.constants.MoveResult;
import backend.constants.MsgHeader;
import backend.constants.QueueNames;
import backend.state.Board;

public class JMSMsgUtils {

	private Context iniCtx;
	private QueueConnectionFactory qcf;
	private QueueConnection queueConnection;
	private QueueSession queueSession;
	
	public JMSMsgUtils() throws NamingException, JMSException {
        iniCtx = new InitialContext();
        Object tmp = iniCtx.lookup("ConnectionFactory");
        qcf = (QueueConnectionFactory) tmp;
        queueConnection = qcf.createQueueConnection();
        queueSession = queueConnection.createQueueSession(false, 0);
		queueConnection.start();
	}
	
	//Getters for various Queues
	public Queue getClientOneQueue() throws NamingException {
		return (Queue) iniCtx.lookup(QueueNames.CLIENT_ONE);
	}
	
	public Queue getClientTwoQueue() throws NamingException {
		return (Queue) iniCtx.lookup(QueueNames.CLIENT_TWO);
	}
	
	public Queue getServerQueue() throws NamingException {
		return (Queue) iniCtx.lookup(QueueNames.SERVER);
	}
	
	public Queue getGameEngineQueue() throws NamingException {
		return (Queue) iniCtx.lookup(QueueNames.GAME_ENGINE);
	}
	
	/**
	 * Forwards messages to the destination queue.
	 * 
	 * @param destStr
	 * @param message
	 * @throws NamingException
	 * @throws JMSException
	 */
	public void forwardMessage(String destStr, MapMessage message)
			throws NamingException, JMSException {
		QueueSender sender = queueSession.createSender(lookupQueueByName(destStr));
		sender.send(message);
		sender.close();
	}
	
	/**
	 * 
	 * @param destStr
	 * @return
	 * @throws NamingException
	 */
	private Queue lookupQueueByName(String destStr) throws NamingException {
		if (destStr.equals(QueueNames.CLIENT_ONE)) {
			return this.getClientOneQueue();
		} else if (destStr.equals(QueueNames.CLIENT_TWO)) {
			return this.getClientTwoQueue();
		} else if (destStr.equals(QueueNames.GAME_ENGINE)) {
			return this.getGameEngineQueue();
		} else if (destStr.equals(QueueNames.SERVER)) {
			return this.getServerQueue();
		}
		return null;
	}

	//Messages from Clients to Server
	public void sendTestMessage(String dest) throws Exception {
	    QueueSender sender = queueSession.createSender(getServerQueue());
	    MapMessage msg = queueSession.createMapMessage();
	    msg.setInt("header", MsgHeader.READY);
	    msg.setString("destination", dest);
	    msg.setString("body", "test");
	    sender.send(msg);
	    sender.close();
	}
	
	/***
	 * 
	 * @param source
	 * @param dest
	 * @param pId
	 * @param myBoard
	 * @throws Exception
	 */
	public void sendReadyMessage(String source, String dest, String pId, Board myBoard) throws Exception {
	    QueueSender sender = queueSession.createSender(getServerQueue());
	    MapMessage msg = queueSession.createMapMessage();
	    msg.setJMSReplyTo(lookupQueueByName(source));
	    msg.setInt("header", MsgHeader.READY);
	    msg.setString("destination", dest);
	    msg.setString("playerId", pId);
	    msg.setString("board", myBoard.toString());
	    sender.send(msg);
	    sender.close();
	}

	/**
	 * 
	 * @param source
	 * @param dest
	 * @param pId
	 * @param x
	 * @param y
	 * @throws Exception
	 */
	public void sendMoveMessage (String source, String dest, String pId, int x, int y) throws Exception {
	    QueueSender sender = queueSession.createSender(getServerQueue());
	    MapMessage msg = queueSession.createMapMessage();
	    msg.setJMSReplyTo(lookupQueueByName(source));
	    msg.setInt("header", MsgHeader.MOVE_INFO);
	    msg.setString("destination", dest);
	    msg.setString("playerId", pId);
	    msg.setInt("x", x);
	    msg.setInt("y", y);
	    sender.send(msg);
	    sender.close();
	}
	
	//Messages from GameEngine to Clients
	public void sendIsHitMessage (String source, String dest, String pId, MoveResult moveResult) throws Exception {
	    QueueSender sender = queueSession.createSender(getServerQueue());
	    MapMessage msg = queueSession.createMapMessage();
	    msg.setJMSReplyTo(lookupQueueByName(source));
	    msg.setInt("header", MsgHeader.MOVE_RESULT);
	    msg.setString("destination", dest);
	    msg.setString("playerId", pId);
	    if (moveResult == MoveResult.HIT) {
	    	msg.setBoolean("isHit", true);
		} else {
			msg.setBoolean("isHit", false);
		}
	    sender.send(msg);
	    sender.close();
	}
	
	public void sendTurnMessage (String source, String dest, String pId, boolean turn) throws Exception {
	    QueueSender sender = queueSession.createSender(getServerQueue());
	    MapMessage msg = queueSession.createMapMessage();
	    msg.setJMSReplyTo(lookupQueueByName(source));
	    msg.setInt("header", MsgHeader.TURN_INFO);
	    msg.setString("destination", dest);
	    msg.setString("playerId", pId);
	    msg.setBoolean("isMyTurn", turn);
	    sender.send(msg);
	    sender.close();
	    queueConnection.stop();
	    queueConnection.close();
	}
	
	public void sendMoveNotifyMessage(String source, String dest, String pId, int x, int y) throws Exception {
	    QueueSender sender = queueSession.createSender(getServerQueue());
	    MapMessage msg = queueSession.createMapMessage();
	    msg.setJMSReplyTo(lookupQueueByName(source));
	    msg.setInt("header", MsgHeader.MOVE_NOTICE);
	    msg.setString("destination", dest);
	    msg.setString("playerId", pId);
	    msg.setInt("x", x);
	    msg.setInt("y", y);
	    sender.send(msg);
	    sender.close();
	    queueConnection.stop();
	    queueConnection.close();
	}

	public void sendGameOverMessage(String source, String dest, String pId, int x, int y, String result) throws Exception {
	    QueueSender sender = queueSession.createSender(getServerQueue());
	    MapMessage msg = queueSession.createMapMessage();
	    msg.setJMSReplyTo(lookupQueueByName(source));
	    msg.setInt("header", MsgHeader.GAME_OVER);
	    msg.setString("destination", dest);
	    msg.setString("playerId", pId);
	    msg.setInt("x", x);
	    msg.setInt("y", y);
	    msg.setString("result", result);
	    sender.send(msg);
	    sender.close();
	}

	public void sendErrorMessage(String source, String dest, String pId, String errorMsg) throws Exception {
	    QueueSender sender = queueSession.createSender(getServerQueue());
	    MapMessage msg = queueSession.createMapMessage();
	    msg.setJMSReplyTo(lookupQueueByName(source));
	    msg.setInt("header", MsgHeader.GAME_OVER);
	    msg.setString("destination", dest);
	    msg.setString("playerId", pId);
	    msg.setString("errorMsg", errorMsg);
	    sender.send(msg);
	    sender.close();
	}

	//Other
	public void stop() throws JMSException {
		System.out.println("stopping everything");
        queueConnection.stop();
        queueSession.close();
        queueConnection.close();
	}
	
	public QueueSession getSession() throws JMSException {
		return this.queueSession;
	}

}
