package client;
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


import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import EDU.oswego.cs.dl.util.concurrent.CountDown;


public class SimpleMessageClient {
    static final int N = 2;
    static CountDown done = new CountDown(N);

    QueueConnection conn;
    QueueSession session;
    static Queue client1Que;
    Queue serverQue;
    
    public static class ClientListener implements MessageListener {
        public void onMessage(Message msg)
        {
            done.release();
            TextMessage tm = (TextMessage) msg;
            try {
                System.out.println("onMessage, recv text="+tm.getText());
            } catch(Throwable t) {
                t.printStackTrace();
            }
        }
    }
    
    public void sendRecvAsync(String textBase)
        throws JMSException, NamingException, InterruptedException {
        System.out.println("Begin sendRecvAsync");

        InitialContext iniCtx = new InitialContext();
        Object tmp = iniCtx.lookup("ConnectionFactory");
        QueueConnectionFactory qcf = (QueueConnectionFactory) tmp;
        conn = qcf.createQueueConnection();
        client1Que = (Queue) iniCtx.lookup(QueueNames.CLIENT_TWO);
        serverQue = (Queue) iniCtx.lookup(QueueNames.SERVER);
        session = conn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
        conn.start();

        // Set the async listener for queA
        QueueReceiver recv = session.createReceiver(client1Que);
        recv.setMessageListener(new ClientListener());

        // Send a few text msgs to queB
        QueueSender send = session.createSender(serverQue);

        for(int m = 0; m < N; m ++) {
            TextMessage tm = session.createTextMessage(textBase+"#"+m);
            tm.setJMSReplyTo(client1Que);
            send.send(tm);
            System.out.println("sendRecvAsync, sent text=" + tm.getText());
        }
        System.out.println("End sendRecvAsync");
    }
    
    public void stop() throws JMSException {
        conn.stop();
        session.close();
        conn.close();
    }
    
    public static void main(String args[]) throws Exception {
        System.out.println("Begin SendRecvClient,now=" + 
                           System.currentTimeMillis());
        SimpleMessageClient client = new SimpleMessageClient();
        client.sendRecvAsync("A text msg");
        client.done.acquire();
        client.stop();
        System.exit(0);
        System.out.println("End SendRecvClient");
    }
	
}
