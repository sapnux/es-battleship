package frontend;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.util.List;

import backend.state.*;
import backend.util.BackendException;
import backend.client.*;
import backend.constants.Orientation;
import frontend.state.*;
import frontend.state.ships.*;
import frontend.test.TestClient;

import java.util.*;

public class EsbArrangmentWindow extends JFrame {

	//TODO make a real serialID value, or remove it entirely
	private static final long serialVersionUID = 1L;

	private JPanel  jContentPane            = null;
	private JPanel  mControlPanel           = null;
	private JButton mReadyButton            = null;
	private JButton mCancelShipButton       = null;
	private JList   mShipSelection          = null;
	private Vector <CanDrawShip> mShipTypes = null;
	private EsbFleetPanel mFleetPanel       = null;
	private JFrame mLocalWindow             = this;
	
	private Board mPlayerBoard           = null;
	private List <CanDrawShip> mShipList = null;
	private CanDrawShip mSelectedShip    = null;  //  @jve:decl-index=0:
	private String[] mParams             = null;
	private int mNumClicks               = 0;

	/**
	 * Default constructor for the fleet arrangement window
	 * @param aBoard Empty board object that is populated by this object and passed to Battle Window
	 * @param aShipList Empty list of ships that is populated by this object and passed to Battle Window
	 * @param aParams Contains the IP address & Port of the server, and the player's name respectively.
	 * 			Used to connect to the server.
	 */
	public EsbArrangmentWindow(Board aBoard, List <CanDrawShip> aShipList,
							   String[] aParams) {
		mPlayerBoard = aBoard;
		mShipList    = aShipList;
		mParams = aParams;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.initializeFleetPanel();		
		this.initializeControlPanel();
		
		this.setResizable(false);
		this.setContentPane(getJContentPane());
		this.setTitle("ES Battleship");
//		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	System.exit(0);
            }
        });

//		((JPanel) getContentPane()).registerKeyboardAction(new ActionListener() {
//			  public void actionPerformed(ActionEvent actionEvent) {
////					TEST CODE
//					System.out.println("'Esc' typed");					
//					clearClicks();				     
//				  }
//				}, 
//				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
//				JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		this.pack();		
	}

	private void initializeControlPanel() {
		mControlPanel = new JPanel();
		mControlPanel.setName("Control Panel");
		mControlPanel.setPreferredSize(new Dimension(150, 400));
		mControlPanel.setSize(new Dimension(150, 400));
		mControlPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.gridwidth  = 1;
		//gridBagConstraints.insets = new Insets(10, 10, 10, 10);	
		
		mShipTypes = new Vector<CanDrawShip>(); 
		mShipTypes.add(new AircraftCarrierCanDraw());
		mShipTypes.add(new BattleshipCanDraw());
		mShipTypes.add(new CruiserCanDraw());
		mShipTypes.add(new PatrolBoatCanDraw());
		mShipTypes.add(new SubmarineCanDraw());
		
		mShipSelection = new JList(mShipTypes);
		mControlPanel.add(mShipSelection, gridBagConstraints);
		mShipSelection.setVisible(true);
		mShipSelection.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		mShipSelection.addListSelectionListener(new ListSelectionListener(){
//			public void valueChanged(ListSelectionEvent e){
//				if ((!e.getValueIsAdjusting()) || (e.getFirstIndex() == -1)) {
//					return;
//				}
//				
//				mSelectedShip = (CanDrawShip)(((JList)e.getSource()).getSelectedValue());
////				//TEST CODE
//				System.out.println(mSelectedShip);
//			}
//		});

		mCancelShipButton = new JButton("Cancel Selection");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weightx = 0.0;
		gridBagConstraints.weighty = 0.0;
		//gridBagConstraints.anchor = GridBagConstraints.NORTH;
		//gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.gridwidth  = 1;
		mControlPanel.add(mCancelShipButton, gridBagConstraints);
		mCancelShipButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				// TEST CODE
				//System.out.println("Cancel Selection Pressed");
				clearClicks();
			}
		});
		mCancelShipButton.setVisible(true);
		mCancelShipButton.setEnabled(true);		
		
		mReadyButton = new JButton("Ready");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.weightx = 0.0;
		gridBagConstraints.weighty = 0.0;
		//gridBagConstraints.anchor = GridBagConstraints.NORTH;
		//gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.gridwidth  = 1;
		mControlPanel.add(mReadyButton, gridBagConstraints);
		mReadyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				
				//TODO candidate for refactoring
				//TEST CODE
//				TestClient theClient = new TestClient(mParams[2], mPlayerBoard);
				EsbBattleWindow theBWindow = null;
				EsbFrontendController theFController = null;
				EJBClient theClient = null;
				Thread tClientThread = null;
				try {
					theClient = new EJBClient(mParams[0], mParams[1], mPlayerBoard);
					tClientThread = new Thread(theClient);

					theFController = 
						new EsbFrontendController(theClient);

					theFController.setShips(mShipList);
					theBWindow = new EsbBattleWindow(theFController);
					tClientThread.start();
				} catch (Exception e) {							
					JOptionPane.showMessageDialog(mLocalWindow, e.getMessage(), 
							"Error", JOptionPane.ERROR_MESSAGE);
					System.exit(-1);
				}				
				
				// Gets Frame from subcomponents and hides the arrangement window
				mLocalWindow.setVisible(false);
				theBWindow.setVisible(true);
			}
		});
		mReadyButton.setVisible(true);
		mReadyButton.setEnabled(false);
		// TEST CODE - comment back out when done
		//mReadyButton.setEnabled(true);
	}
	
	private void initializeFleetPanel(){
		mFleetPanel = new EsbFleetPanel(this.mShipList);
		mFleetPanel.setName("Fleet Panel");
		mFleetPanel.setSize(new Dimension(400, 400));
		mFleetPanel.setBackground(Color.BLUE);
		
		//TODO consider making a separate, fully fledged class for this
		mFleetPanel.addMouseListener(new MouseAdapter(){
			private int mCellSide = mFleetPanel.getCellSide();
			private int mGrid1X = 0, mGrid1Y = 0;
			private int mGrid2X = 0, mGrid2Y = 0;
			private Orientation mOrientation;
			private int mNumCellsAcross = mFleetPanel.getNumCellsAcross();
			
			public void mouseClicked(MouseEvent e){
				// TEST CODE
				//System.out.println("Got a click! " + e.getX() + " " + e.getY());
				
				//TODO move numClicks if blocks out of isEmpty if block.
				if(!mShipSelection.isSelectionEmpty()){//(mSelectedShip != null){
					//TODO refactor mNumClicks into a boolean
					if(mNumClicks == 0){
						mSelectedShip = (CanDrawShip) mShipSelection.getSelectedValue();
						mGrid1X = e.getX() / mCellSide;
						mGrid1Y = e.getY() / mCellSide;

						//Check to ensure that click is within legal game board
						if( ((mGrid1X < 0) || (mGrid1X >= mNumCellsAcross)) ||
						    ((mGrid1Y < 0) || (mGrid1Y >= mNumCellsAcross))    ) {
							return;
						}						
						
						//TEST CODE
//						System.out.println("First Placement Click: "+ mGrid1X + ", " +
//								mGrid1Y + " " + mCellSide);
						
						mFleetPanel.setReticle(mGrid1X, mGrid1Y);
						mNumClicks = 1;	
						mShipSelection.setEnabled(false);
					} else if(mNumClicks == 1){
						//TODO create JUnit code to test
						mGrid2X = e.getX() / mCellSide;
						mGrid2Y = e.getY() / mCellSide;						
						
//						TEST CODE
//						System.out.println("Second Placement Click: "+ mGrid2X + ", " +
//								mGrid2Y);
						
						//We want to throw out clicks on the same cell.
						if((mGrid1X == mGrid2X)&&(mGrid1Y == mGrid2Y))
							return;
						
						//Check to ensure that click is within legal game board
						if( ((mGrid2X < 0) || (mGrid2X >= mNumCellsAcross)) ||
						    ((mGrid2Y < 0) || (mGrid2Y >= mNumCellsAcross))    ) {
							return;
						}							
						
						int tNWx, tNWy;
						
						/* Resolve the orientation of the two clicks relative to
						 * each other so we can determine the direction of the ship.
						 */ 
						if(mGrid1X == mGrid2X){
							tNWx = mGrid1X;
							tNWy = Math.min(mGrid1Y, mGrid2Y);
							mOrientation = Orientation.VERTICAL;
						} else if(mGrid1Y == mGrid2Y){
							tNWy = mGrid1Y;
							tNWx = Math.min(mGrid1X, mGrid2X);
							mOrientation = Orientation.HORIZONTAL;
						} else //this was a diagonal series of clicks, throw it out
							return;	
						
						/* Add the ship to the board given the coordinates entered
						 * above.*/
						try {
							mPlayerBoard.add(mSelectedShip.getIShip(), tNWx, tNWy, mOrientation);						
							mSelectedShip.setPosition(tNWx, tNWy, mOrientation);
							mShipList.add(mSelectedShip);	
							
							// Refresh JList with remaining ships
							mShipTypes.remove(mShipSelection.getSelectedIndex());
							mShipSelection.setListData(mShipTypes);
							
							if (mShipTypes.isEmpty()) {
								mReadyButton.setEnabled(true);
							}
							
							((JPanel)e.getSource()).repaint();	
						} catch (BackendException e1) {
							
							// TEST CODE
							//System.err.println(e1.getMessage());
							//mPlayerBoard.print();							
							
							JOptionPane.showMessageDialog(mLocalWindow, e1.getMessage(), 
										"Error", JOptionPane.ERROR_MESSAGE);							
						}

						clearClicks();
					}
				}
			}
		}); //add MouseListener
		
		mFleetPanel.addMouseMotionListener(new MouseMotionAdapter(){
			private int mCellSide = mFleetPanel.getCellSide();
			private int mRecentGridX      = -1;
			private int mRecentGridY      = -1;
			private Coordinates mRefPoint = null;			
			
			public void mouseMoved(MouseEvent e){
				
				//TODO move code out of numClicks if block.
				//TODO refactor mNumClicks into a boolean				
				if(mNumClicks == 1){
					int tGridX = e.getX()/mCellSide;
					int tGridY = e.getY()/mCellSide;
					
					//TODO consider refactoring code around cellChanged 
					boolean cellChanged = false;
					
					if((tGridX != mRecentGridX)||(tGridY != mRecentGridY))
						cellChanged = true;
					//if the mouse is in the same cell, don't change anything
					if(!cellChanged)
						return;
					else{ // TODO this else statement is useless logically
						mRefPoint = mFleetPanel.getReticle();
						mRecentGridX = tGridX;
						mRecentGridY = tGridY;
					}
					
					//if mouse is on top of reticle cell, don't draw and exit
					if((tGridX == mRefPoint.getX())&&(tGridY == mRefPoint.getY())){
						mFleetPanel.setDrawGuide(false);
						return;
					}
					
					int tNWx, tNWy;
					Rectangle tGuide;
					//Guide should be vertical
					if(tGridX == mRefPoint.getX()){
						tNWx = tGridX * mCellSide;
						tNWy = Math.min(tGridY, mRefPoint.getY()) * mCellSide;
						tGuide = new Rectangle(tNWx, tNWy, 
								mCellSide,
								mSelectedShip.getIShip().getSize() * mCellSide);
					}
					//Guide should be horizontal
					else if(tGridY == mRefPoint.getY()){
						tNWy = tGridY * mCellSide;
						tNWx = Math.min(tGridX, mRefPoint.getX()) * mCellSide;
						tGuide = new Rectangle(tNWx, tNWy, 
								mSelectedShip.getIShip().getSize() * mCellSide,
								mCellSide);
					} 
//					//diagonal orientation, don't draw and exit
					else{ 
						mFleetPanel.setDrawGuide(false);
						return;	
					}
					
					mFleetPanel.setGuide(tGuide);
					mFleetPanel.setDrawGuide(true);
				}//numclicks == 1
			}//mousemoved
		});//add MouseMotionListener
	}
	
	
	/**
	 * This method is called when we need to reset the state
	 * of the board to just after the last ship was placed.  If
	 * no ships have yet been placed, we default to the initial
	 * state of the board.
	 * */
	protected void clearClicks(){
		mNumClicks = 0;
		mSelectedShip = null;
		mShipSelection.clearSelection();
		mShipSelection.setEnabled(true);
		mFleetPanel.clearReticle();
		mFleetPanel.clearGuide();
		
		//TEST CODE
//		System.out.println("clearClicks called");
	}
		
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setName("Content Pane");
			jContentPane.setLayout(new GridBagLayout());
		}
		GridBagConstraints gridBagConstraints = new GridBagConstraints();

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.gridwidth  = 1;
		gridBagConstraints.insets = new Insets(10, 10, 10, 10);	
		jContentPane.add(mControlPanel, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.gridwidth  = 1;		
		jContentPane.add(mFleetPanel, gridBagConstraints);
				
		return jContentPane;
	}

}
