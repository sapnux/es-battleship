package backend.server;

import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.TextMessage;

@MessageDriven(mappedName = "jms/Queue", activationConfig = {
	 @ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
	 @ActivationConfigProperty(propertyName="destination", propertyValue="queue/D"),
})
public class ServerBean implements MessageListener {
	
    static final Logger logger = Logger.getLogger("ServerBean");
    
    @Resource(mappedName="ConnectionFactory")
    private QueueConnectionFactory qcf;
    private Map queueMap;
    
//    private class PlayerQueueMap {
//    	private String playerId;
//    	private Queue queue;
//    	
//    }
    public void onMessage(Message inMessage) {
    	MapMessage msg = (MapMessage) inMessage;
    	
        try {
            if (inMessage instanceof MapMessage) {
                msg = (MapMessage) inMessage;
                logger.info("ServerBean: Received message: " + msg.getString("header") + " from " + msg.getJMSReplyTo());             
                String replyTopicStr = forwardMsg(msg);
                logger.info("Forwarded to: " + replyTopicStr);
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
    
    public String forwardMsg(MapMessage msg) {
    	return "";
    }


}
