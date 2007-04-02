package backend.server;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.naming.NamingException;

import backend.util.JMSMsgUtils;

@MessageDriven(mappedName = "jms/Queue", activationConfig = {
	 @ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
	 @ActivationConfigProperty(propertyName="destination", propertyValue="queue/D"),
})
public class ServerBean implements MessageListener {
	
    static final Logger logger = Logger.getLogger("ServerBean");
    
    private JMSMsgUtils msgUtil;
    
    public ServerBean() throws NamingException, JMSException{
    	msgUtil = new JMSMsgUtils();
    }


    
    public void onMessage(Message inMessage) {
    	MapMessage msg = (MapMessage) inMessage;
    	
        try {
            if (inMessage instanceof MapMessage) {
                msg = (MapMessage) inMessage;
                logger.info("ServerBean: Received message: " + msg.getString("header") + " from " + msg.getJMSReplyTo());
                String destStr = msg.getString("destination");
                msgUtil.forwardMessage(destStr, msg);
                logger.info("Forwarded to: " + destStr);
            } else {
                logger.warning("Message of wrong type: " + inMessage.getClass().getName());
            }
        } catch (JMSException e) {
            logger.severe("SimpleMessageBean.onMessage: JMSException: " + e.toString());
            e.printStackTrace();
        } catch (Throwable te) {
            logger.severe("SimpleMessageBean.onMessage: Exception: " + te.toString());
            te.printStackTrace();
        }
    	
    }
    
//    public String forwardMsg(MapMessage msg) throws JMSException, NamingException {
//    	Queue sender = (Queue) msg.getJMSReplyTo();
//	    String ret = "";
//    	if (msg.getInt("header") == MsgHeader.READY) {
//    		String pId = msg.getString("playerId");
//    		if (playerQueues.get(0) == null) {
//    			
//    			
//    			playerQueues.put(QueueNames.CLIENT_ONE, msg.getString("playerId"));
//    		} else if (!playerQueues.containsKey(QueueNames.CLIENT_TWO)) {
//    			playerQueues.put(QueueNames.CLIENT_TWO, msg.getString("playerId"));
//    		} else {
//    			msgUtil.sendErrorMessage(sender, pId, "Game is full.");
//    			return msg.getJMSReplyTo().toString();
//    		}
//    		
//    	}
//    	
//    	if (sender.toString().contains(".A") || sender.toString().contains(".B")) {
//    		msgUtil.forwardMessage(msgUtil.getGameEngineQueue(), msg);
//    	    ret = msgUtil.getGameEngineQueue().toString();
//    	} else if (sender.toString().contains(".C")) {
//    		
//    	}
//    	return ret;
//    }


}
