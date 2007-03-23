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


import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class SimpleMessageClient {
    static Object waitUntilDone = new Object();
    static SortedSet outstandingRequests = Collections.synchronizedSortedSet(new TreeSet());

    public static void main(String[] args) {
        InitialContext ic = null;
        ConnectionFactory cf = null; // App Server
        Connection con = null;
        Session pubSession = null;
        MessageProducer producer = null;
        Topic pTopic = null;
        TemporaryTopic replyTopic = null;
        Session subSession = null;
        MessageConsumer consumer = null;
        TextMessage message = null;

        /*
         * Create a JNDI API InitialContext object.
         */
        try {
            ic = new InitialContext();
        } catch (NamingException e) {
            System.err.println("Could not create JNDI API " + "context: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }

        /*
         * Look up connection factories and topic.  If any do not
         * exist, exit.
         */
        try {
            cf = (ConnectionFactory) ic.lookup("ConnectionFactory");
            pTopic = (Topic) ic.lookup("topic/myTopic");
        } catch (NamingException e) {
            System.err.println("JNDI API lookup failed: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            // Create a connection.
            con = cf.createConnection();

            // Create session for producer.
            pubSession = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create temporary topic for replies.
            replyTopic = pubSession.createTemporaryTopic();

            // Create sessions for consumer.
            subSession = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

            /*
             * Create a consumer, set message listener, and
             * start connection.
             */
            consumer = subSession.createConsumer(replyTopic);
            consumer.setMessageListener(new ReplyListener(outstandingRequests));
            con.start();

            // Create a producer.
            producer = pubSession.createProducer(pTopic);

            /*
             * Create and send a set of messages at 1.5-second intervals.  For
             * each message, set the JMSReplyTo message header to
             * a reply topic, and set an id property.  Add the
             * message ID to the list of outstanding requests for
             * the message listener.
             */
            message = pubSession.createTextMessage();

            int id = 1;

            for (int i = 0; i < 5; i++) {
                Thread.sleep(1500);
                message.setJMSReplyTo(replyTopic);
                message.setIntProperty("id", id);
                message.setText("text: id=" + id + " to remote app server");

                try {
                    producer.send(message);
                    System.out.println("Sent message: " + message.getText());
                    outstandingRequests.add(message.getJMSMessageID());
                } catch (Exception e) {
                    System.err.println("Exception: Caught " + "failed send to connection factory");
                    e.printStackTrace();
                }

                id++;
                Thread.sleep(1500);
            }

            /*
             * Wait for replies.
             */
            System.out.println("Waiting for " + outstandingRequests.size() + " message(s) " + "from remote app server");

            while (outstandingRequests.size() > 0) {
                synchronized (waitUntilDone) {
                    waitUntilDone.wait();
                }
            }
            System.out.println("Finished");
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.toString());
            e.printStackTrace();
        } finally {
            System.out.println("Closing connection");

            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    System.err.println("Error closing " + "connection: " +
                        e.toString());
                }
            }
            System.exit(0);
        }
    }
    
    static class ReplyListener implements MessageListener {
        SortedSet outstandingRequests = null;

        /**
         * Constructor for ReplyListener class.
         *
         * @param outstandingRequests   set of outstanding
         *                              requests
         */
        ReplyListener(SortedSet outstandingRequests) {
            this.outstandingRequests = outstandingRequests;
        }

        /**
         * onMessage method, which displays the contents of the
         * id property and text and uses the JMSCorrelationID to
         * remove from the list of outstanding requests the
         * message to which this message is a reply.  If this is
         * the last message, it notifies the client.
         *
         * @param message     the incoming message
         */
        public void onMessage(Message message) {
            TextMessage tmsg = (TextMessage) message;
            String txt = null;
            int id = 0;
            String correlationID = null;

            try {
                id = tmsg.getIntProperty("id");
                txt = tmsg.getText();
                correlationID = tmsg.getJMSCorrelationID();
            } catch (JMSException e) {
                System.err.println("ReplyListener.onMessage: " +
                    "JMSException: " + e.toString());
            }

            System.out.println("ReplyListener: Received " + "message: id=" +
                id + ", text=" + txt);
            outstandingRequests.remove(correlationID);

            if (outstandingRequests.size() == 0) {
                synchronized (waitUntilDone) {
                    waitUntilDone.notify();
                }
            } else {
                System.out.println("ReplyListener: Waiting " + "for " +
                    outstandingRequests.size() + " message(s)");
            }
        }
    }
} // class
