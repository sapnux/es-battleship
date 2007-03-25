package beans;
/*
 * Copyright (c) 2006 Sun Microsystems, Inc.  All rights reserved.  U.S.
 * Government Rights - Commercial software.  Government users are subject
 * to the Sun Microsystems, Inc. standard license agreement and
 * applicable provisions of the FAR and its supplements.  Use is subject
 * to license terms.
 *
 * This distribution may include materials developed by third parties.
 * Sun, Sun Microsystems, the Sun logo, Java and J2EE are trademarks
 * or registered trademarks of Sun Microsystems, Inc. in the U.S. and
 * other countries.
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. Tous droits reserves.
 *
 * Droits du gouvernement americain, utilisateurs gouvernementaux - logiciel
 * commercial. Les utilisateurs gouvernementaux sont soumis au contrat de
 * licence standard de Sun Microsystems, Inc., ainsi qu'aux dispositions
 * en vigueur de la FAR (Federal Acquisition Regulations) et des
 * supplements a celles-ci.  Distribue par des licences qui en
 * restreignent l'utilisation.
 *
 * Cette distribution peut comprendre des composants developpes par des
 * tierces parties. Sun, Sun Microsystems, le logo Sun, Java et J2EE
 * sont des marques de fabrique ou des marques deposees de Sun
 * Microsystems, Inc. aux Etats-Unis et dans d'autres pays.
 */


import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;


@MessageDriven(mappedName = "jms/Queue", activationConfig = {
	 @ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
	 @ActivationConfigProperty(propertyName="destination", propertyValue="queue/D"),
})
public class SimpleMessageBean implements MessageListener {
    static final Logger logger = Logger.getLogger("SimpleMessageBean");
    
    @Resource(mappedName="ConnectionFactory")
    private QueueConnectionFactory qcf;
    
    public void onMessage(Message inMessage) {
        TextMessage msg;

        try {
            if (inMessage instanceof TextMessage) {
                msg = (TextMessage) inMessage;
                logger.info("SimpleMessageBean: Received message: " 
                		+ msg.getText());             
                String replyTopicStr = sendReply(msg);
                logger.info("Sent reply to " + replyTopicStr);
            } else {
                logger.warning("Message of wrong type: " +
                    inMessage.getClass().getName());
            }
        } catch (JMSException e) {
            logger.severe("SimpleMessageBean.onMessage: JMSException: " +
                e.toString());
            e.printStackTrace();
        } catch (Throwable te) {
            logger.severe("SimpleMessageBean.onMessage: Exception: " +
                te.toString());
            te.printStackTrace();
        }
    }
    
    public String sendReply(TextMessage msg) throws JMSException{

    	Queue queue = (Queue)msg.getJMSReplyTo();
    	QueueConnection connect = qcf.createQueueConnection();
	    QueueSession session = connect.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
	    QueueSender sender = session.createSender(queue);
	    TextMessage replyMsg = session.createTextMessage("SimpleMessageBean " +
                "processed message: " + msg.getText());
	    sender.send(replyMsg);
	    String ret = sender.getDestination().toString();
	    connect.close();
	    
	    return ret;
	}

}
