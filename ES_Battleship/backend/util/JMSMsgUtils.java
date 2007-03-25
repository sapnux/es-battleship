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
import backend.constants.MsgHeaders;
import backend.state.Board;

public class JMSMsgUtils {

	private Context iniCtx;
	private QueueConnectionFactory qcf;
	private QueueConnection connect;
	private QueueSession session;
//	private QueueSender sender;
	
	public JMSMsgUtils() throws NamingException, JMSException {
        iniCtx = new InitialContext();
        Object tmp = iniCtx.lookup("ConnectionFactory");
        qcf = (QueueConnectionFactory) tmp;
        connect = qcf.createQueueConnection();
        session = connect.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
	}
	
	//Messages from Clients to Server
	public void sendTestMessage (Queue dest) throws JMSException {
		connect.start();
	    QueueSender sender = session.createSender(dest);
	    MapMessage msg = session.createMapMessage();
	    msg.setInt("header", MsgHeaders.READY);
	    msg.setString("body", "test");
	    sender.send(msg);
	    connect.close();
	}
	
	public void sendReadyMessage(Queue dest, String pId, Board myBoard) throws JMSException {
		connect.start();
	    QueueSender sender = session.createSender(dest);
	    MapMessage msg = session.createMapMessage();
	    msg.setInt("header", MsgHeaders.READY);
	    msg.setString("player_id", pId);
	    msg.setString("board", myBoard.toString());
	    sender.send(msg);
	    connect.close();
	}

	public void sendMoveMessage (Queue dest, String pId, int x, int y) throws JMSException {
		connect.start();
	    QueueSender sender = session.createSender(dest);
	    MapMessage msg = session.createMapMessage();
	    msg.setInt("header", MsgHeaders.MOVE_INFO);
	    msg.setString("player_id", pId);
	    msg.setInt("x", x);
	    msg.setInt("x", y);
	    sender.send(msg);
	    connect.close();
	}
	
	//Messages from Server to Clients
	public void sendIsHitMessage (Queue dest, MoveResult moveResult) throws JMSException {
		connect.start();
	    QueueSender sender = session.createSender(dest);
	    MapMessage msg = session.createMapMessage();
	    msg.setInt("header", MsgHeaders.MOVE_RESULT);
	    if (moveResult == MoveResult.HIT) {
	    	msg.setBoolean("is_hit", true);
		} else {
			msg.setBoolean("is_hit", false);
		}
	    sender.send(msg);
	    connect.close();
	}
	
	public void sendTurnMessage (Queue dest, boolean turn) throws JMSException {
		connect.start();
	    QueueSender sender = session.createSender(dest);
	    MapMessage msg = session.createMapMessage();
	    msg.setInt("header", MsgHeaders.TURN_INFO);
	    msg.setBoolean("is_my_turn", turn);
	    sender.send(msg);
	    connect.close();
	}
	
	public void sendMoveNotifyMessage (Queue dest, String x, String y) throws JMSException {
		connect.start();
	    QueueSender sender = session.createSender(dest);
	    MapMessage msg = session.createMapMessage();
	    msg.setInt("header", MsgHeaders.MOVE_NOTICE);
	    msg.setInt("x", Integer.parseInt(x));
	    msg.setInt("x", Integer.parseInt(y));
	    sender.send(msg);
	    connect.close();
	}

	public void sendGameOverMessage(Queue dest, String x, String y, String result) throws JMSException {
		connect.start();
	    QueueSender sender = session.createSender(dest);
	    MapMessage msg = session.createMapMessage();
	    msg.setInt("header", MsgHeaders.GAME_OVER);
	    msg.setInt("x", Integer.parseInt(x));
	    msg.setInt("x", Integer.parseInt(y));
	    msg.setString("result", result);
	    sender.send(msg);
	    connect.close();
	}

}
