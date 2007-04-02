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
	private QueueConnection connect;
	private QueueSession session;
	
	public JMSMsgUtils() throws NamingException, JMSException {
        iniCtx = new InitialContext();
        Object tmp = iniCtx.lookup("ConnectionFactory");
        qcf = (QueueConnectionFactory) tmp;
        connect = qcf.createQueueConnection();
        session = connect.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
		connect.start();
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
	
	//Method to forward message to Destination
	public void forwardMessage(String destStr, MapMessage message) throws NamingException, JMSException {
		QueueSender sender = session.createSender(lookupQueueByName(destStr));
		sender.send(message);
	}
	
	private Queue lookupQueueByName(String destStr) throws NamingException {
		Queue ret = null;
		if (destStr.equals(QueueNames.CLIENT_ONE)) {
			ret = getClientOneQueue();
		} else if (destStr.equals(QueueNames.CLIENT_TWO)) {
			ret = getClientTwoQueue();
		} else if (destStr.equals(QueueNames.GAME_ENGINE)) {
			ret = getGameEngineQueue();
		} else if (destStr.equals(QueueNames.SERVER)) {
			ret = getServerQueue();
		}
		return ret;
	}

	//Messages from Clients to Server
	public void sendTestMessage (String dest) throws Exception {
	    QueueSender sender = session.createSender(getServerQueue());
	    MapMessage msg = session.createMapMessage();
	    msg.setInt("header", MsgHeader.READY);
	    msg.setString("destination", dest);
	    msg.setString("body", "test");
	    sender.send(msg);
	}
	
	public void sendReadyMessage(String dest, String pId, Board myBoard) throws Exception {
	    QueueSender sender = session.createSender(getServerQueue());
	    MapMessage msg = session.createMapMessage();
	    msg.setInt("header", MsgHeader.READY);
	    msg.setString("destination", dest);
	    msg.setString("playerId", pId);
	    msg.setString("board", myBoard.toString());
	    sender.send(msg);
	}

	public void sendMoveMessage (String dest, String pId, int x, int y) throws Exception {
	    QueueSender sender = session.createSender(getServerQueue());
	    MapMessage msg = session.createMapMessage();
	    msg.setInt("header", MsgHeader.MOVE_INFO);
	    msg.setString("destination", dest);
	    msg.setString("playerId", pId);
	    msg.setInt("x", x);
	    msg.setInt("x", y);
	    sender.send(msg);
	}
	
	//Messages from GameEngine to Clients
	public void sendIsHitMessage (String dest, String pId, MoveResult moveResult) throws Exception {
	    QueueSender sender = session.createSender(getServerQueue());
	    MapMessage msg = session.createMapMessage();
	    msg.setInt("header", MsgHeader.MOVE_RESULT);
	    msg.setString("destination", dest);
	    msg.setString("playerId", pId);
	    if (moveResult == MoveResult.HIT) {
	    	msg.setBoolean("isHit", true);
		} else {
			msg.setBoolean("isHit", false);
		}
	    sender.send(msg);
	}
	
	public void sendTurnMessage (String dest, String pId, boolean turn) throws Exception {
	    QueueSender sender = session.createSender(getServerQueue());
	    MapMessage msg = session.createMapMessage();
	    msg.setInt("header", MsgHeader.TURN_INFO);
	    msg.setString("destination", dest);
	    msg.setString("playerId", pId);
	    msg.setBoolean("isMyTurn", turn);
	    sender.send(msg);
	    connect.stop();
	    connect.close();
	}
	
	public void sendMoveNotifyMessage (String dest, String pId, String x, String y) throws Exception {
	    QueueSender sender = session.createSender(getServerQueue());
	    MapMessage msg = session.createMapMessage();
	    msg.setInt("header", MsgHeader.MOVE_NOTICE);
	    msg.setString("destination", dest);
	    msg.setString("playerId", pId);
	    msg.setInt("x", Integer.parseInt(x));
	    msg.setInt("x", Integer.parseInt(y));
	    sender.send(msg);
	    connect.stop();
	    connect.close();
	}

	public void sendGameOverMessage(String dest, String pId, String x, String y, String result) throws Exception {
	    QueueSender sender = session.createSender(getServerQueue());
	    MapMessage msg = session.createMapMessage();
	    msg.setInt("header", MsgHeader.GAME_OVER);
	    msg.setString("destination", dest);
	    msg.setString("playerId", pId);
	    msg.setInt("x", Integer.parseInt(x));
	    msg.setInt("x", Integer.parseInt(y));
	    msg.setString("result", result);
	    sender.send(msg);
	}

	public void sendErrorMessage(String dest, String pId, String errorMsg) throws Exception {
	    QueueSender sender = session.createSender(getServerQueue());
	    MapMessage msg = session.createMapMessage();
	    msg.setInt("header", MsgHeader.GAME_OVER);
	    msg.setString("destination", dest);
	    msg.setString("playerId", pId);
	    msg.setString("errorMsg", errorMsg);
	    sender.send(msg);
	}

	//Other
	public void stop() throws JMSException {
        connect.stop();
        session.close();
        connect.close();
	}
	
	public QueueSession getSession() throws JMSException {
		return session;
	}

}
