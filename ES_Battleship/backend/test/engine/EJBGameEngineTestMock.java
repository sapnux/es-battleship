package backend.test.engine;

import javax.jms.DeliveryMode;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
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
//		Session tSession = tCurrConn.createSession(false, QueueSession.AUTO_ACKNOWLEDGE);
		Session tSession = (Session)tCurrConn.getSessionList().get(0);
		
		mClient1Receiver = ((QueueSession)tSession).createReceiver(mServerQueue);
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
        verifyNumberQueueReceivers(0, mServerQueue.getQueueName(), 1);
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
	    
	    verifyNumberOfReceivedQueueMessages(mGameEngQueue.getQueueName(), 1);	   
	    
	    verifyAllReceivedQueueMessagesAcknowledged(mServerQueue.getQueueName());
	    verifyNumberOfReceivedQueueMessages(mServerQueue.getQueueName(), 1);
	    
	    MockMapMessage tRecdMsg = new MockMapMessage();
		tRecdMsg.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
		tRecdMsg.setJMSReplyTo(mGameEngQueue);
		tRecdMsg.setInt("header", MsgHeader.ERROR);
		tRecdMsg.setString("destination", QueueNames.CLIENT_ONE);
		tRecdMsg.setString("playerId", "1");
		tRecdMsg.setString("errorMsg", "Waiting for an opponent.. Please wait..");
		
		verifyReceivedQueueMessageEquals(mServerQueue.getQueueName(), 0, 
                tRecdMsg);
	}
	
	protected void playerJoin(MockQueue aRepQueue, String aPlayerID, String aBoard) 
	throws Exception{
		MockMapMessage tJoin1 = new MockMapMessage();
		tJoin1.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
		tJoin1.setJMSReplyTo(aRepQueue);
		tJoin1.setInt("header", MsgHeader.READY);
		tJoin1.setString("destination", QueueNames.GAME_ENGINE);
		tJoin1.setString("playerId", aPlayerID);
		tJoin1.setString("board", aBoard);
	    mGameEngQueue.addMessage(tJoin1);
	}
	
	public void testGameReady() throws Exception{
		//Add two players to the game
		//Player1
		playerJoin(mClient1Queue, "Player1", new Board().toString());
	    //Player2
		playerJoin(mClient2Queue, "Player2", new Board().toString());
		
		//Test Results
		verifyNumberOfReceivedQueueMessages(mServerQueue.getQueueName(), 3);
		
		//test message1
		MockMapMessage tRecdMsg1 = new MockMapMessage();
		tRecdMsg1.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
		tRecdMsg1.setJMSReplyTo(mGameEngQueue);
		tRecdMsg1.setInt("header", MsgHeader.TURN_INFO);
		tRecdMsg1.setString("destination", QueueNames.CLIENT_ONE);
		tRecdMsg1.setString("playerId", "Player1");
		tRecdMsg1.setBoolean("isMyTurn", true);		
		
		//test message2
		MockMapMessage tRecdMsg2 = new MockMapMessage();
		tRecdMsg2.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
		tRecdMsg2.setJMSReplyTo(mGameEngQueue);
		tRecdMsg2.setInt("header", MsgHeader.TURN_INFO);
		tRecdMsg2.setString("destination", QueueNames.CLIENT_TWO);
		tRecdMsg2.setString("playerId", "Player2");
		tRecdMsg2.setBoolean("isMyTurn", false);
				
		verifyReceivedQueueMessageEquals(mServerQueue.getQueueName(), 1, 
                tRecdMsg2);		
		verifyReceivedQueueMessageEquals(mServerQueue.getQueueName(), 2, 
                tRecdMsg1);

	    //Player3
		playerJoin(mClient2Queue, "Player3", new Board().toString());

		verifyNumberOfReceivedQueueMessages(mServerQueue.getQueueName(), 4);

		MockMapMessage tRecdMsg3 = new MockMapMessage();
		tRecdMsg3.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
		tRecdMsg3.setJMSReplyTo(mGameEngQueue);
		tRecdMsg3.setInt("header", MsgHeader.ERROR);
		tRecdMsg3.setString("destination", null);
		tRecdMsg3.setString("playerId", "Player3");
		tRecdMsg3.setString("errorMsg", "Sorry, but the game is full. There are already two people enjoying themselves.");
				
		verifyReceivedQueueMessageEquals(mServerQueue.getQueueName(), 3, 
                tRecdMsg3);	
	}
	
	public void testMakeMove() throws Exception {
		
		//Player1
		playerJoin(mClient1Queue, "Player1", new Board().toString());	
		
		MockMapMessage tMsg = new MockMapMessage();
	    tMsg.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
	    tMsg.setJMSReplyTo(mClient1Queue);
	    tMsg.setInt("header", MsgHeader.MOVE_INFO);
	    tMsg.setString("destination", QueueNames.GAME_ENGINE);
	    tMsg.setString("playerId", "Player1");
	    tMsg.setInt("x", 0);
	    tMsg.setInt("y", 0);
	    mGameEngQueue.addMessage(tMsg);
	    
		MockMapMessage tRecdMsg1 = new MockMapMessage();
		tRecdMsg1.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
		tRecdMsg1.setJMSReplyTo(mGameEngQueue);
		tRecdMsg1.setInt("header", MsgHeader.ERROR);
		tRecdMsg1.setString("destination", QueueNames.CLIENT_ONE);
		tRecdMsg1.setString("playerId", "Player1");
		tRecdMsg1.setString("errorMsg", "No opponent found.");
		
		verifyNumberOfReceivedQueueMessages(mServerQueue.getQueueName(), 2);
		verifyReceivedQueueMessageEquals(mServerQueue.getQueueName(), 1, 
                tRecdMsg1);	
		
	    //Player2
		Board tBoard = new Board();
		tBoard.add(new AircraftCarrier(), 0, 0, Orientation.HORIZONTAL);
		tBoard.add(new Cruiser(), 5, 0, Orientation.HORIZONTAL);
		tBoard.add(new PatrolBoat(), 8, 0, Orientation.HORIZONTAL);
		tBoard.add(new Battleship(), 0, 1, Orientation.HORIZONTAL);
		tBoard.add(new Submarine(), 4, 1, Orientation.HORIZONTAL);
						
		playerJoin(mClient2Queue, "Player2", tBoard.toString());
		// +2 msgs ServerQueue, total of 4 at this point
		mGameEngQueue.addMessage(tMsg);
		
		MockMapMessage tRecdMsg2 = new MockMapMessage();
		tRecdMsg2.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
		tRecdMsg2.setJMSReplyTo(mGameEngQueue);
		tRecdMsg2.setInt("header", MsgHeader.MOVE_RESULT);
		tRecdMsg2.setString("destination", QueueNames.CLIENT_ONE);
		tRecdMsg2.setString("playerId", "Player1");
		tRecdMsg2.setBoolean("isHit", true);
		tRecdMsg2.setInt("x", 0);
		tRecdMsg2.setInt("y", 0);
		
		MockMapMessage tRecdMsg3 = new MockMapMessage();
		tRecdMsg3.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
		tRecdMsg3.setJMSReplyTo(mGameEngQueue);
		tRecdMsg3.setInt("header", MsgHeader.MOVE_NOTICE);
		tRecdMsg3.setString("destination", QueueNames.CLIENT_TWO);
		tRecdMsg3.setString("playerId", "Player2");
		tRecdMsg3.setInt("x", 0);
	    tRecdMsg3.setInt("y", 0);
	    
		verifyNumberOfReceivedQueueMessages(mServerQueue.getQueueName(), 6);
		verifyReceivedQueueMessageEquals(mServerQueue.getQueueName(), 4, 
                tRecdMsg2);		
		verifyReceivedQueueMessageEquals(mServerQueue.getQueueName(), 5, 
                tRecdMsg3);
		//up to 6 msgs on mServerQueue
	    
		//do the next 15 hits
		for(int i=1; i<16; i++){
		    tMsg.setInt("x", i%10);
		    tMsg.setInt("y", i/10);

		    mGameEngQueue.addMessage(tMsg);
	    }
		//+30 msgs ServerQueue, total of 36 at this point
	    tMsg.setInt("x", 6);
	    tMsg.setInt("y", 1);
	    mGameEngQueue.addMessage(tMsg);
	    //+2 msgs ServerQueue, total of 38 at this point
		
	    //Check win messages
	    MockMapMessage tRecdMsg4 = new MockMapMessage();
	    tRecdMsg4.setJMSRedelivered(false);
	    tRecdMsg4.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
	    tRecdMsg4.setJMSReplyTo(mGameEngQueue);
	    tRecdMsg4.setInt("header", MsgHeader.GAME_OVER);
	    tRecdMsg4.setString("destination", QueueNames.CLIENT_ONE);
	    tRecdMsg4.setString("playerId", "Player1");
	    tRecdMsg4.setInt("x", 6);
	    tRecdMsg4.setInt("y", 1);
	    tRecdMsg4.setString("result", "WIN");

	    MockMapMessage tRecdMsg5 = new MockMapMessage();
	    tRecdMsg5.setJMSRedelivered(false);
	    tRecdMsg5.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
	    tRecdMsg5.setJMSReplyTo(mGameEngQueue);
	    tRecdMsg5.setInt("header", MsgHeader.GAME_OVER);
	    tRecdMsg5.setString("destination", QueueNames.CLIENT_TWO);
	    tRecdMsg5.setString("playerId", "Player2");
	    tRecdMsg5.setInt("x", 6);
	    tRecdMsg5.setInt("y", 1);
	    tRecdMsg5.setString("result", "LOSS");
	    
	    verifyNumberOfReceivedQueueMessages(mServerQueue.getQueueName(), 38);

		verifyReceivedQueueMessageEquals(mServerQueue.getQueueName(), 36, 
                tRecdMsg4);		
		verifyReceivedQueueMessageEquals(mServerQueue.getQueueName(), 37, 
                tRecdMsg5);
	}
	
	public void testOtherHeaders() throws Exception{
		//Add two players to the game
		//Player1
		playerJoin(mClient1Queue, "Player1", new Board().toString());
	    //Player2
		playerJoin(mClient2Queue, "Player2", new Board().toString());
		
	    MockMapMessage tMsg = new MockMapMessage();
	    tMsg.setJMSRedelivered(false);
	    tMsg.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
	    tMsg.setJMSReplyTo(mGameEngQueue);
	    tMsg.setInt("header", MsgHeader.TURN_INFO);
	    mGameEngQueue.addMessage(tMsg);

	    tMsg.setInt("header", MsgHeader.MOVE_RESULT);
	    mGameEngQueue.addMessage(tMsg);

	    tMsg.setInt("header", MsgHeader.MOVE_NOTICE);
	    mGameEngQueue.addMessage(tMsg);

	    tMsg.setInt("header", MsgHeader.GAME_OVER);
	    mGameEngQueue.addMessage(tMsg);

	    tMsg.setInt("header", MsgHeader.ERROR);
	    mGameEngQueue.addMessage(tMsg);
	    
	    verifyNumberOfReceivedQueueMessages(mServerQueue.getQueueName(), 3);
	}
	
	public void testGetPlayerByQueue() throws Exception{
		//Add two players to the game
		//Player1
		playerJoin(mClient1Queue, "Player1", new Board().toString());
	    //Player2
		playerJoin(mClient2Queue, "Player2", new Board().toString());
	    
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
		playerJoin(mClient1Queue, "Player1", new Board().toString());
	    //Player2
		playerJoin(mClient2Queue, "Player2", new Board().toString());	
	    
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
		playerJoin(mClient1Queue, "Player1", new Board().toString());
	    
	    //Player2
		Board tBoard = new Board();
		tBoard.add(new AircraftCarrier(), 0, 0, Orientation.HORIZONTAL);
		tBoard.add(new Cruiser(), 5, 0, Orientation.HORIZONTAL);
		tBoard.add(new PatrolBoat(), 8, 0, Orientation.HORIZONTAL);
		tBoard.add(new Battleship(), 0, 1, Orientation.HORIZONTAL);
		tBoard.add(new Submarine(), 4, 1, Orientation.HORIZONTAL);
		
		playerJoin(mClient2Queue, "Player2", tBoard.toString());
	    
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
	
	public void testMockStuff() throws Exception{

		MockQueue tTestQueue = getDestinationManager().createQueue("testQueue");
        mEjbModule.bindToContext("queue/testQueue", tTestQueue);
		
		MockConnection tCurrConn = getJMSMockObjectFactory().getMockQueueConnectionFactory().getLatestConnection();
//		Session tSession = tCurrConn.createSession(false, QueueSession.AUTO_ACKNOWLEDGE);
		Session tSession = (Session)tCurrConn.getSessionList().get(0);
		
		MockMapMessage tJoin1 = new MockMapMessage();
		tJoin1.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
		tJoin1.setJMSReplyTo(mClient1Queue);
		tJoin1.setInt("header", MsgHeader.READY);
		tJoin1.setString("destination", QueueNames.GAME_ENGINE);
		tJoin1.setString("playerId", "Player1");
		tJoin1.setString("board", new Board().toString());
		
		QueueReceiver tTestReceiver = ((QueueSession)tSession).createReceiver(tTestQueue);
		tTestReceiver.setMessageListener(new MessageListener() {			
			public void onMessage(Message msg) {
				System.out.println(msg.toString());
			}
		});	
		
//		QueueSender tTestSender = ((QueueSession)tSession).createSender(tTestQueue);
//		tTestSender.send(tJoin1);
		tTestQueue.addMessage(tJoin1);
		
	    verifyAllReceivedQueueMessagesAcknowledged("testQueue");
	    verifyNumberOfReceivedQueueMessages("testQueue", 1);
		
	}
}
