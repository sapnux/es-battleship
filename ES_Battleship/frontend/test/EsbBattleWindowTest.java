package frontend.test;

import java.awt.Rectangle;

import org.uispec4j.Trigger;
import org.uispec4j.assertion.*;
import org.uispec4j.*;
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
		mClient.enableOpponentGrid();
		Mouse.doDoubleClickInRectangle(mOpponentGrid, 
				new Rectangle(0, 0, 39, 39));
		assertTrue((mClient.getLastClick().getX() == 0)
				&&(mClient.getLastClick().getY() == 0));
		
		//test clicking each cell.
	}
	
//TODO write test classes to help capture info and allow us to test at all!
	/*
	 * Test that clicking on opponent window generates the right locations
	 * Test that we can't click on opponent window when it is disabled.
	 * Test enabling/disabling clicks.
	 * */
}
