package backend.server;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueConnectionFactory;

@MessageDriven(mappedName = "jms/Queue", activationConfig = {
	 @ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
	 @ActivationConfigProperty(propertyName="destination", propertyValue="queue/B"),
})
public class ServerBean implements MessageListener {
	
    static final Logger logger = Logger.getLogger("ServerBean");
    
    @Resource(mappedName="ConnectionFactory")
    private QueueConnectionFactory qcf;
    
    public void onMessage(Message inMessage) {

    }


}
