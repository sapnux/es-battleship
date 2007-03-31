package frontend.test;

import junit.framework.TestCase;
import java.util.*;

import org.uispec4j.*;
import org.uispec4j.assertion.*;
import org.uispec4j.interception.*;
import frontend.*;
import frontend.state.ships.*;
import backend.state.*;

import java.awt.AWTException;
import java.awt.Rectangle;



public class EsbArrangementWindowTest extends TestCase {
	
	private Window mTestWin             = null;
	private ListBox mShipListBox        = null;
	private Panel mContentPane			= null;
	private Panel mRightPanel			= null;
	private Panel mLeftPanel            = null;
	private Button mReadyButton           = null;
	private Button mCancelSelectionButton = null;
	
	private Board mBoard                = null;
	private List<CanDrawShip> mShipList = null;
	private String[] mParams            = new String[3];
	private String[] mShipNames 		= {"Aircraft Carrier", "Battleship",
										   "Cruiser", "Patrol Boat", "Submarine"};
	
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
		
		final EsbArrangmentWindow tWindow = new EsbArrangmentWindow(
				mBoard, mShipList, mParams);
		//mTestWin = new Window(tWindow);
				
		mTestWin = WindowInterceptor.run(new Trigger() {
			public void run() {
				tWindow.setVisible(true);
			}
		});
		
		mContentPane = mTestWin.getPanel("Content Pane");
		mRightPanel = mTestWin.getPanel("Fleet Panel");
		mLeftPanel = mTestWin.getPanel("Control Panel");
		mShipListBox = mLeftPanel.getListBox();
		mReadyButton = mLeftPanel.getButton("Ready");
		mCancelSelectionButton = mLeftPanel.getButton("Cancel Selection");
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
		UISpecAssert.assertTrue(mShipListBox.contentEquals(mShipNames));
		
		assertNotNull(mReadyButton);
		UISpecAssert.assertFalse(mReadyButton.isEnabled());
		
		assertNotNull(mCancelSelectionButton);
		UISpecAssert.assertTrue(mCancelSelectionButton.isEnabled());
	}
	
	public void testRightPanel(){
		assertNotNull(mRightPanel);
	}
	
	public void testOneClick(){
		// Check if listbox is selectable
		UISpecAssert.assertTrue(mShipListBox.isEnabled());		
		//need to use doubleclick bc single does not register- double registers only once...
		Mouse.doDoubleClickInRectangle(mRightPanel, 
				new Rectangle(0, 0, 39, 39));		
		UISpecAssert.assertTrue(mShipListBox.isEnabled());	
		
		for (int tShipIndex = 0; tShipIndex < mShipNames.length; tShipIndex++)
		{		
			for (int tGridX=0; tGridX< 400; tGridX+=40)
			{
				for (int tGridY=0; tGridY< 400; tGridY+=40)
				{
					// Select ship
					mShipListBox.selectIndex(tShipIndex);
					UISpecAssert.assertTrue(mShipListBox.selectionEquals(mShipNames[tShipIndex]));
					
					// Click first cell (valid click)
					// TODO - for loop for all cells
					Mouse.doDoubleClickInRectangle(mRightPanel, 
							new Rectangle(tGridX, tGridY, 39, 39));		
					// Check to see that Ship List becomes disabled after 
					// first placement click
					UISpecAssert.assertFalse(mShipListBox.isEnabled());
					assertTrue(((EsbFleetPanel)mRightPanel.getAwtComponent()).getReticle().equals(
							new Coordinates(tGridX/40, tGridY/40)));
					
					mCancelSelectionButton.click();
					// Check to see that pressing the cancel button reenables 
					// the Ship List
					UISpecAssert.assertTrue(mShipListBox.isEnabled());
				} // end of tGridY for loop			
			} // end of tGridX for loop			
		} // end of tShipIndex for loop
		
		
	}
}
