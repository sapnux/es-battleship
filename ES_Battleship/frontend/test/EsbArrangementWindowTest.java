package frontend.test;

import junit.framework.TestCase;
import java.util.*;

import org.uispec4j.*;
import org.uispec4j.assertion.*;
import frontend.*;
import frontend.state.ships.*;
import backend.state.*;
import java.awt.Rectangle;



public class EsbArrangementWindowTest extends TestCase {
	
	private Window mTestWin             = null;
	private ListBox mShipListBox        = null;
	private Panel mRightPanel			= null;
	private Panel mLeftPanel            = null;
	
	private Board mBoard                = null;
	private List<CanDrawShip> mShipList = null;
	private String[] mParams            = new String[3];
	
	static {
        UISpec4J.init();
    }
	
	public EsbArrangementWindowTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		mBoard = new Board();
		mShipList = new ArrayList<CanDrawShip>();
		mParams[0] = "127.0.0.1";
		mParams[1] = "7555";
		mParams[2] = "Test Player";
		mTestWin = new Window(new EsbArrangmentWindow(
				mBoard, mShipList, mParams));
		
		mRightPanel = mTestWin.getPanel("Fleet Panel");
		mLeftPanel = mTestWin.getPanel("Control Panel");
		mShipListBox = mLeftPanel.getListBox();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testWindowNotNull(){
		assertNotNull(mTestWin);
	}
	
	public void testTitle(){
		assertEquals(mTestWin.getTitle(), "ES Battleship");
	}
	
	public void testLeftPanel(){
		assertNotNull(mLeftPanel);
		
		assertNotNull(mShipListBox);
		String[] tShipNames = {
				"Aircraft Carrier", "Battleship",
				"Cruiser", "Patrol Boat", "Submarine"};
		UISpecAssert.assertTrue(mShipListBox.contentEquals(tShipNames));
		
		Button tReadyButton = mLeftPanel.getButton();
		assertNotNull(tReadyButton);
//		UISpecAssert.assertFalse(tReadyButton.isEnabled());
		tReadyButton.click();
	}
	
	public void testRightPanel(){
		assertNotNull(mRightPanel);
	}
	
	public void testOneClick(){
		// Check if listbox is selectable
		UISpecAssert.assertTrue(mShipListBox.isEnabled());		
		Mouse.doClickInRectangle(mRightPanel, 
					new Rectangle(1, 1, 10, 10), 
					false, Key.Modifier.NONE);
		UISpecAssert.assertTrue(mShipListBox.isEnabled());	
		// Select first ship
		mShipListBox.selectIndex(0);
		UISpecAssert.assertTrue(mShipListBox.selectionEquals("Aircraft Carrier"));
		// Click first cell (valid click)
		Mouse.doClickInRectangle(mRightPanel, 
				new Rectangle(0, 0, 399, 399), 
				false, Key.Modifier.NONE);		
		UISpecAssert.assertFalse(mShipListBox.isEnabled());		
	}
}
