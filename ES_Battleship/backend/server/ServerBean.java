package backend.server;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.naming.NamingException;

import backend.util.JMSMsgUtils;
import backend.util.Logger;

@MessageDriven(mappedName = "jms/Queue", activationConfig = {
	 @ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
	 @ActivationConfigProperty(propertyName="destination", propertyValue="queue/D"),
})
public class ServerBean implements MessageListener {
	
    private static JMSMsgUtils msgUtil;
    
    /**
     *  
     * @throws NamingException
     * @throws JMSException
     */
    public ServerBean() throws NamingException, JMSException {
		if (msgUtil == null) {
			msgUtil = new JMSMsgUtils();
		}
	}

    /**
     * 
     */
    public void onMessage(Message message) {
        try {
            if (message instanceof MapMessage) {
                MapMessage mapMessage = (MapMessage) message;
                String destination = mapMessage.getString("destination");
                Logger.LogInfo("[ServerBean] Forwarding message type=" + mapMessage.getString("header") + " from " + mapMessage.getJMSReplyTo() + " to " + destination);
                msgUtil.forwardMessage(destination, mapMessage);
            } else {
            	Logger.LogWarning("[ServerBean] Message Type Incorrect: " + message.getClass().getName());
            }
        } catch (JMSException ex) {
        	Logger.LogError("[ServerBean] JMSException: " + ex.toString());
        } catch (Throwable ex) {
        	Logger.LogError("[ServerBean] Exception: " + ex.toString());
        	Logger.LogError("[ServerBean] Stacktrace: " + ex.getStackTrace().toString());
        }
    }
}
