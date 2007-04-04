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

public class TestEJBClient2 {
    static final int N = 1;

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
    
	public TestEJBClient2() throws NamingException, JMSException {
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
            msgUtil.sendReadyMessage(QueueNames.CLIENT_TWO, QueueNames.GAME_ENGINE, "test_client2", board);
            System.out.println("sendRecvAsync, sent msg");
        }
        System.out.println("End sendRecvAsync");
    }
    
    public static void main(String args[]) throws Exception {
        TestEJBClient2 client = new TestEJBClient2();
        client.sendMsgs();
        System.exit(0);
    }
	
}
