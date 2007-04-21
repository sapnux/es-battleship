package frontend.test;

import java.awt.Rectangle;

import org.uispec4j.Mouse;
import org.uispec4j.Trigger;
import org.uispec4j.Window;
import org.uispec4j.assertion.*;
import org.uispec4j.*;
import org.uispec4j.interception.WindowHandler;
import org.uispec4j.interception.WindowInterceptor;

import frontend.*;
import junit.framework.TestCase;
import backend.state.*;
import backend.client.*;

public class EsbBattleWindowTest extends TestCase {
	private Board mBoard                      = null;
	private TestClientAuto mClient            = null;
	private Window mTestWin                   = null;
	private EsbFrontendController mController = null;
	
	private Panel mPlayerGrid   = null;
	private Panel mOpponentGrid = null;
	private TextBox mMessageBox = null;

	static {
        UISpec4J.init();
    }
	
	public EsbBattleWindowTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {	
		super.setUp();
		
		mBoard = new Board();
		mClient = new TestClientAuto("autotest", mBoard);
		mController = new EsbFrontendController(mClient);

		final EsbBattleWindow tWindow = new EsbBattleWindow(mController);
				
		mTestWin = WindowInterceptor.run(new Trigger() {
			public void run() {
				tWindow.setVisible(true);
			}
		});		
		
		mPlayerGrid = mTestWin.getPanel("Player Grid");
		mOpponentGrid = mTestWin.getPanel("Opponent Grid");
		mMessageBox = mTestWin.getTextBox();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testInitialState(){
		assertNotNull(mTestWin);
		assertEquals("ES Battleship", mTestWin.getTitle());
		
		assertNotNull(mPlayerGrid);
		assertNotNull(mOpponentGrid);
		assertNotNull(mMessageBox);
		UISpecAssert.assertTrue(mMessageBox.textIsEmpty());
		UISpecAssert.assertFalse(mMessageBox.isEditable());
	}
	
	public void testClickingOpponentGrid(){
		//test that grid is initially locked
		Mouse.doDoubleClickInRectangle(mOpponentGrid, 
				new Rectangle(0, 0, 39, 39));
		assertTrue((mClient.getLastClick().getX() == -1)
				&&(mClient.getLastClick().getY() == -1));
		
		//unlock grid
		mClient.setMyTurn(true);
		Mouse.doDoubleClickInRectangle(mOpponentGrid, 
				new Rectangle(0, 0, 39, 39));
		assertTrue((mClient.getLastClick().getX() == 0)
				&&(mClient.getLastClick().getY() == 0));
		mClient.clearLastClick();
		
		//test clicking each cell.
		for(int i=0; i<400; i+=40){
			for(int j=0; j<400; j+=40){				
				Mouse.doDoubleClickInRectangle(mOpponentGrid, 
						new Rectangle(i, j, 39, 39));
				assertFalse((mClient.getLastClick().getX() == (i/40))
						&&(mClient.getLastClick().getY() == (j/40)));

				mClient.setMyTurn(true);				
				Mouse.doDoubleClickInRectangle(mOpponentGrid, 
						new Rectangle(i, j, 39, 39));
				assertTrue((mClient.getLastClick().getX() == (i/40))
						&&(mClient.getLastClick().getY() == (j/40)));	
			}//for j
		}//for i
	}
	
	public void testClicksOffGrid(){
		for (int tGridX=0; tGridX<= 400; tGridX+=40)
		{
			// Invalid click - offboard
			Mouse.doDoubleClickInRectangle(mOpponentGrid, 
					new Rectangle(tGridX, 400, 39, 39));		

			assertTrue((mClient.getLastClick().getX() == -1)
					&&(mClient.getLastClick().getY() == -1));
			
			// Invalid click - offboard
			Mouse.doDoubleClickInRectangle(mOpponentGrid, 
					new Rectangle(400, tGridX, 39, 39));		

			assertTrue((mClient.getLastClick().getX() == -1)
					&&(mClient.getLastClick().getY() == -1));
		} // end of tGridX for loop			
	}
	
	public void testEnableDisable(){
		for(int i=0; i<10; i++){
			mClient.setMyTurn(false);
			mClient.clearLastClick();
			Mouse.doDoubleClickInRectangle(mOpponentGrid, 
					new Rectangle(0, 0, 39, 39));
			assertTrue((mClient.getLastClick().getX() == -1)
					&&(mClient.getLastClick().getY() == -1));
			
			mClient.setMyTurn(true);
			Mouse.doDoubleClickInRectangle(mOpponentGrid, 
					new Rectangle(0, 0, 39, 39));
			assertTrue((mClient.getLastClick().getX() == 0)
					&&(mClient.getLastClick().getY() == 0));
		}
	}
	
	public void testMessageBox(){
		int count = 1;
		String[] tMessageSequence = {
			"Message 1", 
			"Message 2", 
			"Message 3",
			"Message 4", 
			"Message 5",
			"Message 6"};
		String tTestString = "";
		//Buffering one line
		mClient.sendMessage(tMessageSequence[0]);
		UISpecAssert.assertTrue(mMessageBox.textIsEmpty());
		mClient.setMyTurn(true);
		tTestString = tTestString + count + ">  " + tMessageSequence[0] + "\n";
		assertEquals(mMessageBox.getText(), tTestString);
	
		//Buffering 2 Lines
		mClient.sendMessage(tMessageSequence[1]);
		mClient.sendMessage(tMessageSequence[2]);
		//	didn't update test string yet... == to last time
		assertEquals(mMessageBox.getText(), tTestString);
		mClient.setMyTurn(false);
		count++;
		tTestString = tTestString + count + ">  " + tMessageSequence[1] + "\n";
		count++;
		tTestString = tTestString + count + ">  " + tMessageSequence[2] + "\n";
		assertEquals(mMessageBox.getText(), tTestString);

		//Buffering 3 Lines
		mClient.sendMessage(tMessageSequence[3]);
		mClient.sendMessage(tMessageSequence[4]);
		mClient.sendMessage(tMessageSequence[5]);
		//	didn't update test string yet... == to last time
		assertEquals(mMessageBox.getText(), tTestString);
		mClient.setMyTurn(false);
		count++;
		tTestString = tTestString + count + ">  " + tMessageSequence[3] + "\n";
		count++;
		tTestString = tTestString + count + ">  " + tMessageSequence[4] + "\n";
		count++;
		tTestString = tTestString + count + ">  " + tMessageSequence[5] + "\n";
		assertEquals(mMessageBox.getText(), tTestString);
		
		//test in a short loop
		for(int i=0; i<6; i++){
			mClient.sendMessage(tMessageSequence[i]);
			mClient.setMyTurn(true);
			count++;
			tTestString = tTestString + count + ">  " + tMessageSequence[i] + "\n";
			assertEquals(mMessageBox.getText(), tTestString);
		}
	}
	
//	public void testWin(){
//		WindowInterceptor.init(new Trigger() {
//			public void run() {
//				mClient.setWinLose(backend.constants.GameResult.WIN);
//				mClient.setMyTurn(true);
//		}
//		}).process(new WindowHandler() {
//			public Trigger process(Window window) {
//				String tMessage = (String) (window
//						.getTextBox("OptionPane.label").getText());
//				assertEquals("End of Game", window.getTitle());
//				assertEquals(tMessage,
//				"You Win!");
//				return Trigger.DO_NOTHING;//window.getButton("OK").triggerClick(); // return a trigger that will close it
//			}
//		}).run();
//	}
	
//	public void testLose(){
//		WindowInterceptor.init(new Trigger() {
//			public void run() {
//				mClient.setWinLose(backend.constants.GameResult.LOSS);
//				mClient.setMyTurn(true);
//		}
//		}).process(new WindowHandler() {
//			public Trigger process(Window window) {
//				String tMessage = (String) (window
//						.getTextBox("OptionPane.label").getText());
//				assertEquals("End of Game", window.getTitle());
//				assertEquals(tMessage,
//				"You Lose!");
//				return window.getButton("OK").triggerClick(); // return a trigger that will close it
//			}
//		}).run();
//		assertNull(mTestWin);		
//	}
	
//TODO write test classes to help capture info and allow us to test at all!
	/*
	 * DONE Test that clicking on opponent window generates the right locations
	 * DONE Test that we can't click on opponent window when it is disabled.
	 * DONE Test enabling/disabling clicks.
	 * CANCEL Test that opponent hits, misses, and player hits, misses are populated correctly
	 * CANCEL Test Win/Lose
	 * DONE Test Message Box
	 * */
}
