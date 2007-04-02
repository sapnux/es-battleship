package backend.client;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueReceiver;
import javax.jms.TextMessage;
import javax.naming.NamingException;

import backend.constants.Orientation;
import backend.constants.QueueNames;
import backend.state.Board;
import backend.state.ships.Ships;
import backend.util.JMSMsgUtils;

public class TestEJBClient1 {
    static final int N = 3;

	private JMSMsgUtils msgUtil;
    
    public static class ClientListener implements MessageListener {
        public void onMessage(Message msg)
        {
            TextMessage tm = (TextMessage) msg;
            try {
                System.out.println("onMessage, recv text="+tm.getText());
            } catch(Throwable t) {
                t.printStackTrace();
            }
        }
    }
    
	public TestEJBClient1() throws NamingException, JMSException {
		msgUtil = new JMSMsgUtils();
        QueueReceiver recv = msgUtil.getSession().createReceiver(msgUtil.getGameEngineQueue());
        recv.setMessageListener(new ClientListener());
	}
    
    public void sendMsgs() throws Exception {
        System.out.println("Begin sendRecvAsync");

		Board board = new Board();

		board.add(Ships.getAircraftCarrier(), 0, 0, Orientation.VERTICAL);
		board.add(Ships.getBattleship(), 1, 1, Orientation.HORIZONTAL);
		board.add(Ships.getCruiser(), 6, 4, Orientation.VERTICAL);
		board.add(Ships.getPatrolBoat(), 6, 9, Orientation.HORIZONTAL);
		board.add(Ships.getSubmarine(), 5, 5, Orientation.VERTICAL);

        for(int m = 0; m < N; m ++) {
            MapMessage msg = msgUtil.getSession().createMapMessage();
            msg.setJMSReplyTo(msgUtil.getClientOneQueue());
            msgUtil.sendReadyMessage(QueueNames.GAME_ENGINE, "test_client1", board);
            System.out.println("sendRecvAsync, sent msg");
        }
        System.out.println("End sendRecvAsync");
    }
    
    public static void main(String args[]) throws Exception {
        TestEJBClient1 client = new TestEJBClient1();
        client.sendMsgs();
        System.exit(0);
    }
	
}
