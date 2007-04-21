package frontend.test;

import junit.framework.TestCase;
import java.util.*;

import org.uispec4j.*;
import org.uispec4j.assertion.*;
import org.uispec4j.interception.*;
import frontend.*;
import frontend.state.ships.*;
import backend.state.*;
import backend.constants.*;
import backend.state.ships.*;
import backend.util.BackendException;

import java.awt.AWTException;
import java.awt.Rectangle;
import javax.swing.*;



public class EsbArrangementWindowTest extends TestCase {
	
	private Window mTestWin             = null;
	private ListBox mShipListBox        = null;
	private Panel mContentPane			= null;
	private Panel mRightPanel			= null;
	private Panel mLeftPanel            = null;
	private Button mReadyButton           = null;
	private Button mCancelSelectionButton = null;
	
	private Board mBoard                = null;
	private Board mTestBoard			= null;
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
		mTestBoard = new Board();
		mShipList = new ArrayList<CanDrawShip>();
		mParams[0] = "queue/A";
		mParams[1] = "Test Player";
		
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
					
					// Valid click
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
	
	public void testOneClickOffBoard() {
		// Check if listbox is selectable
		UISpecAssert.assertTrue(mShipListBox.isEnabled());		

		// Select a ship
		mShipListBox.selectIndex(0);
		UISpecAssert.assertTrue(mShipListBox.selectionEquals(mShipNames[0]));

		for (int tGridX=0; tGridX<= 400; tGridX+=40)
		{
			// Invalid click - offboard
			Mouse.doDoubleClickInRectangle(mRightPanel, 
					new Rectangle(tGridX, 400, 39, 39));		

			assertNull(((EsbFleetPanel)mRightPanel.getAwtComponent()).getReticle());
			UISpecAssert.assertTrue(mShipListBox.isEnabled());
			
			// Invalid click - offboard
			Mouse.doDoubleClickInRectangle(mRightPanel, 
					new Rectangle(400, tGridX, 39, 39));		

			assertNull(((EsbFleetPanel)mRightPanel.getAwtComponent()).getReticle());
			UISpecAssert.assertTrue(mShipListBox.isEnabled());	
		} // end of tGridX for loop			
	}
	
	public void testDiscardSecondClick() {
		// Check if listbox is selectable
		UISpecAssert.assertTrue(mShipListBox.isEnabled());		

		// Select a ship
		mShipListBox.selectIndex(0);
		UISpecAssert.assertTrue(mShipListBox.selectionEquals(mShipNames[0]));
		
		Mouse.doDoubleClickInRectangle(mRightPanel, 
				new Rectangle(200, 200, 39, 39));	
		
		// Check to see that Ship List becomes disabled after 
		// first placement click
		UISpecAssert.assertFalse(mShipListBox.isEnabled());
		assertTrue(((EsbFleetPanel)mRightPanel.getAwtComponent()).getReticle().equals(
				new Coordinates(200/40, 200/40)));	
		
		// Second Clicks
		//   a.) Same location
		//   b.) Diagonal
		//   c.) On outer Gridline
	
		//   a.) Same location
		Mouse.doDoubleClickInRectangle(mRightPanel, 
				new Rectangle(200, 200, 39, 39));	
		
		UISpecAssert.assertFalse(mShipListBox.isEnabled());
		assertTrue(((EsbFleetPanel)mRightPanel.getAwtComponent()).getReticle().equals(
				new Coordinates(200/40, 200/40)));	
		
		//   b.) Diagonal
		for (int tGridX=0; tGridX< 400; tGridX+=40)
		{
			if(tGridX == 200)
				continue;
			for (int tGridY=0; tGridY< 400; tGridY+=40){
				if(tGridY == 200)
					continue;
				
				Mouse.doDoubleClickInRectangle(mRightPanel, 
						new Rectangle(tGridX, tGridY, 39, 39));	
				
				UISpecAssert.assertFalse(mShipListBox.isEnabled());
				assertTrue(((EsbFleetPanel)mRightPanel.getAwtComponent()).getReticle().equals(
						new Coordinates(200/40, 200/40)));	
			}
		}//diagonal outer loop
		
		//  c.) On outer Gridline
		for (int tGridX=0; tGridX<= 400; tGridX+=40)
		{
			// Invalid click - offboard
			Mouse.doDoubleClickInRectangle(mRightPanel, 
					new Rectangle(tGridX, 400, 39, 39));		

			UISpecAssert.assertFalse(mShipListBox.isEnabled());
			assertTrue(((EsbFleetPanel)mRightPanel.getAwtComponent()).getReticle().equals(
					new Coordinates(200/40, 200/40)));	
			
			// Invalid click - offboard
			Mouse.doDoubleClickInRectangle(mRightPanel, 
					new Rectangle(400, tGridX, 39, 39));		

			UISpecAssert.assertFalse(mShipListBox.isEnabled());
			assertTrue(((EsbFleetPanel)mRightPanel.getAwtComponent()).getReticle().equals(
					new Coordinates(200/40, 200/40)));	
		} // end of On Outer Gridline Test
	}
	
	public void testPlaceShips(){
		ArrayList<String> tShipNames = new ArrayList<String>(mShipNames.length);
		for (int tIdx=0; tIdx<mShipNames.length; tIdx++)
			tShipNames.add(mShipNames[tIdx]);

		// Place Aircraft Carrier
		String tCurrShip = tShipNames.remove(0);
		placeShip(tCurrShip, tShipNames.toArray(new String[1]), 200, 0, 240, 0);

		// Place Battleship
		tCurrShip = tShipNames.remove(0);
		placeShip(tCurrShip, tShipNames.toArray(new String[1]), 360, 240, 360, 280);

		// Place Cruiser
		tCurrShip = tShipNames.remove(0);
		placeShip(tCurrShip, tShipNames.toArray(new String[1]), 120, 120, 120, 160);

		// Place Patrol Boat
		tCurrShip = tShipNames.remove(0);
		placeShip(tCurrShip, tShipNames.toArray(new String[1]), 160, 160, 200, 160);

		// Place Submarine
		tCurrShip = tShipNames.remove(0);
		placeShip(tCurrShip, null, 240, 360, 280, 360);
		
		//Add ships to test board
		try {
			mTestBoard.add(new AircraftCarrier(), 5, 0, Orientation.HORIZONTAL);
			mTestBoard.add(new Battleship(), 9, 6, Orientation.VERTICAL);
			mTestBoard.add(new Cruiser(), 3, 3, Orientation.VERTICAL);
			mTestBoard.add(new PatrolBoat(), 4, 4, Orientation.HORIZONTAL);
			mTestBoard.add(new Submarine(), 6, 9, Orientation.HORIZONTAL);
		} catch (BackendException e) {
			fail("problem adding ship to board");
		}
		
		//Compare test board to the board produced by simulating clicking.
		for(int i=0; i<10; i++){
			for(int j=0; j<10; j++)
				try {
					assertEquals(mBoard.getCoordinate(i, j), mTestBoard.getCoordinate(i, j));
				} catch (BackendException e) {
					e.printStackTrace();
				}
		}
		
		UISpecAssert.assertTrue(mReadyButton.isEnabled());
		
		// Bring up next window
		Window tBattleWindow = WindowInterceptor.run(new Trigger() {
			public void run() {
				mReadyButton.click();
			}
		});
		
		UISpecAssert.assertFalse(mTestWin.isVisible());
		UISpecAssert.assertTrue(tBattleWindow.isVisible());
		assertEquals(tBattleWindow.getTitle(),
					 "ES Battleship");
		
	}
	
	protected void placeShip(String aShipType, String[] aShipList,
							int aFirstX,  int aFirstY,
							int aSecondX, int aSecondY){
		mShipListBox.selectIndex(0);
		UISpecAssert.assertTrue(mShipListBox.selectionEquals(aShipType));
		
		//Place the ship on the board.
		Mouse.doDoubleClickInRectangle(mRightPanel, 
				new Rectangle(aFirstX, aFirstY, 39, 39));		
		UISpecAssert.assertFalse(mShipListBox.isEnabled());
		assertTrue(((EsbFleetPanel)mRightPanel.getAwtComponent()).getReticle().equals(
				new Coordinates(aFirstX/40, aFirstY/40)));
		Mouse.doDoubleClickInRectangle(mRightPanel, 
				new Rectangle(aSecondX, aSecondY, 39, 39));
		
		//Check that the ship was successfully placed
		UISpecAssert.assertTrue(mShipListBox.isEnabled());
		assertNull(((EsbFleetPanel)mRightPanel.getAwtComponent()).getReticle());
		//The placed ship should no longer be in the ship list box
		if(aShipList == null){
			assertEquals(mShipListBox.getSize(), 0);
			UISpecAssert.assertTrue(mShipListBox.isEmpty());
		}
		else{
			assertEquals(mShipListBox.getSize(), aShipList.length);
			UISpecAssert.assertTrue(mShipListBox.contentEquals(aShipList));
		}
	}
	
	public void testErrorOutofBounds() {
		
		for(int shipIndex=0; shipIndex < mShipNames.length; shipIndex++){
			if (!(mShipNames[shipIndex].equals("Patrol Boat"))) {
				for (int gridLoc = 0; gridLoc < 400; gridLoc+=40) {
					//left to right
					outOfBoundsHelper(mShipNames[shipIndex], shipIndex, 
								      320, gridLoc, 360, gridLoc);
					//right to left
					outOfBoundsHelper(mShipNames[shipIndex], shipIndex, 
						              360, gridLoc, 320, gridLoc);					
					//top to bottom
					outOfBoundsHelper(mShipNames[shipIndex], shipIndex, 
							          gridLoc, 320, gridLoc, 360);
					//bottom to top
					outOfBoundsHelper(mShipNames[shipIndex], shipIndex, 
							          gridLoc, 360, gridLoc, 320);					
				}//grid loc loop			
			}
		}//ship name loop
	}
	
	protected void outOfBoundsHelper(final String aShipName, int aShipIndex,
										int aFirstX,  int aFirstY,
										final int aSecondX, final int aSecondY){
		// Select a ship
		mShipListBox.selectIndex(aShipIndex);
		UISpecAssert.assertTrue(mShipListBox.selectionEquals(aShipName));
		Mouse.doDoubleClickInRectangle(mRightPanel, 
				new Rectangle(aFirstX, aFirstY, 39,	39));
		
		// Check to see that Ship List becomes disabled after 
		// first placement click
		UISpecAssert.assertFalse(mShipListBox.isEnabled());
		assertTrue(((EsbFleetPanel) mRightPanel.getAwtComponent()).getReticle()
				.equals(new Coordinates(aFirstX / 40, aFirstY / 40)));
		
		WindowInterceptor.init(new Trigger() {
			public void run() {
				Mouse.doDoubleClickInRectangle(mRightPanel, 
						new Rectangle(aSecondX, aSecondY, 39, 39));
			}
		}).process(new WindowHandler() {
			public Trigger process(Window window) {
				String tMessage = (String) (window
						.getTextBox("OptionPane.label").getText());
				assertEquals(tMessage,
				"The placement of the " + aShipName + " is out of bounds.");
				return window.getButton("OK").triggerClick(); // return a trigger that will close it
			}
		}).run();
		
		// Assert that state is restored after clicking 'ok'
		UISpecAssert.assertTrue(mShipListBox.isEnabled());
		assertNull(((EsbFleetPanel) mRightPanel.getAwtComponent()).getReticle());
	}
	
	public void testShipOverlap() {
		
		final String[] tShipNames = {"Aircraft Carrier", "Battleship", 
					           "Cruiser", "Submarine"};
		
		// Select a Patrol Boat
		mShipListBox.selectIndex(3);
		UISpecAssert.assertTrue(mShipListBox.selectionEquals("Patrol Boat"));
		
		//Place Patrol Boat near the middle of the board.
		Mouse.doDoubleClickInRectangle(mRightPanel, 
				new Rectangle(160, 160, 39, 39));		
		UISpecAssert.assertFalse(mShipListBox.isEnabled());
		assertTrue(((EsbFleetPanel)mRightPanel.getAwtComponent()).getReticle().equals(
				new Coordinates(160/40, 160/40)));
		Mouse.doDoubleClickInRectangle(mRightPanel, 
				new Rectangle(200, 160, 39, 39));
				
		for (int tShipIndex = 0; tShipIndex < tShipNames.length; tShipIndex++) {
			
			// Select a ship that is not the Patrol Boat
			mShipListBox.selectIndex(tShipIndex);
			UISpecAssert.assertTrue(mShipListBox.selectionEquals(tShipNames[tShipIndex]));
			
			// first click - vertical
			Mouse.doDoubleClickInRectangle(mRightPanel, 
					new Rectangle(160, 120, 39, 39));	
				
			final int tCurrIndex = tShipIndex;
			WindowInterceptor.init(new Trigger() {
				public void run() {
					// second click - vertical
					Mouse.doDoubleClickInRectangle(mRightPanel,
							new Rectangle(160, 200, 39, 39));
				}
			}).process(new WindowHandler() {
				public Trigger process(Window window) {
					String tMessage = (String) (window
							.getTextBox("OptionPane.label").getText());
					assertEquals(tMessage, "The coordinate (4,4) is already occupied. Cannot place "
							+ tShipNames[tCurrIndex] + ".");
					return window.getButton("OK").triggerClick(); // return a trigger that will close it
				}
			}).run();
			
			// Select a ship that is not the Patrol Boat
			mShipListBox.selectIndex(tShipIndex);
			UISpecAssert.assertTrue(mShipListBox.selectionEquals(tShipNames[tShipIndex]));
			
			// first click - horizontal
			Mouse.doDoubleClickInRectangle(mRightPanel, 
					new Rectangle(120, 160, 39, 39));	
				
			WindowInterceptor.init(new Trigger() {
				public void run() {
					// second click - horizontal
					Mouse.doDoubleClickInRectangle(mRightPanel,
							new Rectangle(160, 160, 39, 39));
				}
			}).process(new WindowHandler() {
				public Trigger process(Window window) {
					String tMessage = (String) (window
							.getTextBox("OptionPane.label").getText());
					assertEquals(tMessage, "The coordinate (4,4) is already occupied. Cannot place "
							+ tShipNames[tCurrIndex] + ".");
					return window.getButton("OK").triggerClick(); // return a trigger that will close it
				}
			}).run();			
		}//end loop				
	}
	/**
	 * This is necessary because the other ship types were
	 * made to overlap the Patrol Boat, but it was impossible
	 * to make that happen for the Patrol Boat itself in 
	 * the general testShipOverlap code.*/
	public void testPatrolBoatOverlap(){

		//Select Aircraft Carrier
		mShipListBox.selectIndex(0);
		UISpecAssert.assertTrue(mShipListBox.selectionEquals("Aircraft Carrier"));
		
		//Place AC near the middle of the board.
		Mouse.doDoubleClickInRectangle(mRightPanel, 
				new Rectangle(160, 160, 39, 39));		
		UISpecAssert.assertFalse(mShipListBox.isEnabled());
		assertTrue(((EsbFleetPanel)mRightPanel.getAwtComponent()).getReticle().equals(
				new Coordinates(160/40, 160/40)));
		Mouse.doDoubleClickInRectangle(mRightPanel, 
				new Rectangle(200, 160, 39, 39));
		
		// Select the Patrol Boat
		mShipListBox.selectIndex(2);
		UISpecAssert.assertTrue(mShipListBox.selectionEquals("Patrol Boat"));
		
		// first click - vertical
		Mouse.doDoubleClickInRectangle(mRightPanel, 
				new Rectangle(160, 120, 39, 39));	

		WindowInterceptor.init(new Trigger() {
			public void run() {
				// second click - vertical
				Mouse.doDoubleClickInRectangle(mRightPanel,
						new Rectangle(160, 200, 39, 39));
			}
		}).process(new WindowHandler() {
			public Trigger process(Window window) {
				String tMessage = (String) (window
						.getTextBox("OptionPane.label").getText());
				assertEquals(tMessage, "The coordinate (4,4) is already occupied. Cannot place "
						+ "Patrol Boat.");
				return window.getButton("OK").triggerClick(); // return a trigger that will close it
			}
		}).run();
		
		// Select the Patrol Boat
		mShipListBox.selectIndex(2);
		UISpecAssert.assertTrue(mShipListBox.selectionEquals("Patrol Boat"));
		
		// first click - horizontal
		Mouse.doDoubleClickInRectangle(mRightPanel, 
				new Rectangle(120, 160, 39, 39));	
			
		WindowInterceptor.init(new Trigger() {
			public void run() {
				// second click - horizontal
				Mouse.doDoubleClickInRectangle(mRightPanel,
						new Rectangle(160, 160, 39, 39));
			}
		}).process(new WindowHandler() {
			public Trigger process(Window window) {
				String tMessage = (String) (window
						.getTextBox("OptionPane.label").getText());
				assertEquals(tMessage, "The coordinate (4,4) is already occupied. Cannot place "
						+ "Patrol Boat.");
				return window.getButton("OK").triggerClick(); // return a trigger that will close it
			}
		}).run();
	}
	
}
