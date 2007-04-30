package backend.test.engine;

import javax.jms.DeliveryMode;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;

import com.mockrunner.jms.*;
import com.mockrunner.mock.jms.*;
import com.mockrunner.ejb.*;

import backend.constants.*;
import backend.engine.*;
import backend.state.*;
import backend.state.ships.*;
import backend.client.*;

public class EJBGameEngineTestMock extends JMSTestCaseAdapter{
	private EJBTestModule mEjbModule;
	private EJBGameEngine mGameEngine;
	private EJBClient mClient1;
	private MockQueue mGameEngQueue;
	private MockQueue mClient1Queue;
	private MockQueue mClient2Queue;
	private MockQueue mServerQueue;
	private QueueReceiver mClient1Receiver;
		
	protected void setUp() throws Exception{
        super.setUp();
        mEjbModule = createEJBTestModule();
        mEjbModule.bindToContext("ConnectionFactory", 
          getJMSMockObjectFactory().getMockQueueConnectionFactory());
        
        mGameEngQueue = getDestinationManager().createQueue("C");
        mEjbModule.bindToContext(QueueNames.GAME_ENGINE, mGameEngQueue);

        mClient1Queue = getDestinationManager().createQueue("A");
        mEjbModule.bindToContext(QueueNames.CLIENT_ONE, mClient1Queue);

        mClient2Queue = getDestinationManager().createQueue("B");
        mEjbModule.bindToContext(QueueNames.CLIENT_TWO, mClient2Queue);        
        
        mServerQueue = getDestinationManager().createQueue("D");
        mEjbModule.bindToContext(QueueNames.SERVER, mServerQueue);
                
        mGameEngine = new EJBGameEngine();
        //mClient1 = new EJBClient(QueueNames.CLIENT_ONE, "1", new Board());

		MockConnection tCurrConn = getJMSMockObjectFactory().getMockQueueConnectionFactory().getLatestConnection();
		//Session tSession = tCurrConn.createSession(false, QueueSession.AUTO_ACKNOWLEDGE);
		Session tSession = (Session)tCurrConn.getSessionList().get(0);
		
		mClient1Receiver = ((QueueSession)tSession).createReceiver(mClient1Queue);
		mClient1Receiver.setMessageListener(new MessageListener() {			
			public void onMessage(Message msg) {
				System.out.println(msg.toString());
			}
		});
	}
	
//	protected void tearDown(){
//		MockContextFactory.revertSetAsInitial();
//	}
	
	public void testInitPrintMessageReceiver() throws Exception
    {
        verifyQueueConnectionStarted();
        verifyNumberQueueSessions(1);
        verifyNumberQueueReceivers(0, mGameEngQueue.getQueueName(), 1);
        verifyNumberQueueReceivers(0, mClient1Queue.getQueueName(), 1);
    }
	
	public void testSendReadyMessage() throws Exception{
		
		MockMapMessage tMsg = new MockMapMessage();
		tMsg.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
		tMsg.setJMSReplyTo(mClient1Queue);
		tMsg.setInt("header", MsgHeader.READY);
		tMsg.setString("destination", QueueNames.GAME_ENGINE);
		tMsg.setString("playerId", "1");
		tMsg.setString("board", new Board().toString());
	    
	    mGameEngQueue.addMessage(tMsg);
		
//	    msgUtil.sendReadyMessage("queue/A", QueueNames.GAME_ENGINE, 
//	    		"1", new Board());
	    verifyNumberOfReceivedQueueMessages(mGameEngQueue.getQueueName(), 1);	   
	    
	    verifyAllReceivedQueueMessagesAcknowledged(mClient1Queue.getQueueName());
	    verifyNumberOfReceivedQueueMessages(mClient1Queue.getQueueName(), 1);
	    
	    MockMapMessage tRecdMsg = new MockMapMessage();
		tRecdMsg.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
		tRecdMsg.setJMSReplyTo(mGameEngQueue);
		tRecdMsg.setInt("header", MsgHeader.ERROR);
		tRecdMsg.setString("destination", QueueNames.CLIENT_ONE);
		tRecdMsg.setString("playerId", "1");
		tRecdMsg.setString("errorMsg", "Waiting for an opponent.. Please wait..");
		
		verifyReceivedQueueMessageEquals(mClient1Queue.getQueueName(), 0, 
                tRecdMsg);
	}
	
	public void testGetPlayerByQueue() throws Exception{
		//Add two players to the game
		//Player1
		MockMapMessage tJoin1 = new MockMapMessage();
		tJoin1.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
		tJoin1.setJMSReplyTo(mClient1Queue);
		tJoin1.setInt("header", MsgHeader.READY);
		tJoin1.setString("destination", QueueNames.GAME_ENGINE);
		tJoin1.setString("playerId", "Player1");
		tJoin1.setString("board", new Board().toString());
	    mGameEngQueue.addMessage(tJoin1);
	    //Player2
		MockMapMessage tJoin2 = new MockMapMessage();
		tJoin2.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
		tJoin2.setJMSReplyTo(mClient2Queue);
		tJoin2.setInt("header", MsgHeader.READY);
		tJoin2.setString("destination", QueueNames.GAME_ENGINE);
		tJoin2.setString("playerId", "Player2");
		tJoin2.setString("board", new Board().toString());	    
	    mGameEngQueue.addMessage(tJoin2);
	    
	    //Test results
	    Player tPlayer;
	    tPlayer = mGameEngine.getPlayerByQueue(QueueNames.CLIENT_ONE);
	    assertNotNull(tPlayer);
	    assertEquals(tPlayer.getId(), "Player1");

	    tPlayer = mGameEngine.getPlayerByQueue(QueueNames.CLIENT_TWO);
	    assertNotNull(tPlayer);
	    assertEquals(tPlayer.getId(), "Player2");
	    
	    tPlayer = mGameEngine.getPlayerByQueue("not a queue");
	    assertNull(tPlayer);
	}
	
	public void testGetQueueByPlayerId() throws Exception{
		//Add two players to the game
		//Player1
		MockMapMessage tJoin1 = new MockMapMessage();
		tJoin1.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
		tJoin1.setJMSReplyTo(mClient1Queue);
		tJoin1.setInt("header", MsgHeader.READY);
		tJoin1.setString("destination", QueueNames.GAME_ENGINE);
		tJoin1.setString("playerId", "Player1");
		tJoin1.setString("board", new Board().toString());
	    mGameEngQueue.addMessage(tJoin1);
	    //Player2
		MockMapMessage tJoin2 = new MockMapMessage();
		tJoin2.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
		tJoin2.setJMSReplyTo(mClient2Queue);
		tJoin2.setInt("header", MsgHeader.READY);
		tJoin2.setString("destination", QueueNames.GAME_ENGINE);
		tJoin2.setString("playerId", "Player2");
		tJoin2.setString("board", new Board().toString());	    
	    mGameEngQueue.addMessage(tJoin2);		
	    
	    //Test Results
	    String tQueueName;
	    tQueueName = mGameEngine.getQueueByPlayerId("Player1");
	    assertNotNull(tQueueName);
	    assertEquals(QueueNames.CLIENT_ONE, tQueueName);

	    tQueueName = mGameEngine.getQueueByPlayerId("Player2");
	    assertNotNull(tQueueName);
	    assertEquals(QueueNames.CLIENT_TWO, tQueueName);

	    tQueueName = mGameEngine.getQueueByPlayerId("NoPlayer");
	    assertNull(tQueueName);
	}
	
	public void testMove() throws Exception{
		//Player1
		MockMapMessage tJoin1 = new MockMapMessage();
		tJoin1.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
		tJoin1.setJMSReplyTo(mClient1Queue);
		tJoin1.setInt("header", MsgHeader.READY);
		tJoin1.setString("destination", QueueNames.GAME_ENGINE);
		tJoin1.setString("playerId", "Player1");
		tJoin1.setString("board", new Board().toString());
	    mGameEngQueue.addMessage(tJoin1);
	    
	    //Player2
		MockMapMessage tJoin2 = new MockMapMessage();
		
		Board tBoard = new Board();
		tBoard.add(new AircraftCarrier(), 0, 0, Orientation.HORIZONTAL);
		tBoard.add(new Cruiser(), 5, 0, Orientation.HORIZONTAL);
		tBoard.add(new PatrolBoat(), 8, 0, Orientation.HORIZONTAL);
		tBoard.add(new Battleship(), 0, 1, Orientation.HORIZONTAL);
		tBoard.add(new Submarine(), 4, 1, Orientation.HORIZONTAL);
		
		tJoin2.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
		tJoin2.setJMSReplyTo(mClient2Queue);
		tJoin2.setInt("header", MsgHeader.READY);
		tJoin2.setString("destination", QueueNames.GAME_ENGINE);
		tJoin2.setString("playerId", "Player2");
		tJoin2.setString("board", tBoard.toString());	    
	    mGameEngQueue.addMessage(tJoin2);
	    
	    //Test Results
	    MoveResult tResult;
	    tResult = mGameEngine.move("Player1", 0, 9);
	    assertEquals(tResult, MoveResult.MISS);
	    
	    for(int i=0; i<16; i++){
	    	tResult = mGameEngine.move("Player1", i%10, i/10);
	    	assertEquals(tResult, MoveResult.HIT);
	    }
	    
	    tResult = mGameEngine.move("Player1", 6, 1);
	    assertEquals(tResult, MoveResult.WIN);
	}
}
