package frontend;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.util.List;

import backend.state.*;
import backend.util.BackendException;
import frontend.state.*;
import frontend.state.ships.*;
import frontend.test.TestClient;

import java.util.*;

public class EsbArrangmentWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel  jContentPane            = null;
	private JPanel  mControlPanel           = null;
	private JButton mReadyButton            = null;
	private JList   mShipSelection          = null;
	private Vector <CanDrawShip> mShipTypes = null;
	private EsbFleetPanel mFleetPanel       = null;
	private JFrame mLocalWindow             = this;
	
	private Board mPlayerBoard           = null;
	private List <CanDrawShip> mShipList = null;
	private CanDrawShip mSelectedShip    = null;
	private String[] mParams             = null;

	/**
	 * This is the default constructor
	 */
	public EsbArrangmentWindow(Board aBoard, List <CanDrawShip> aShipList,
							   String[] aParams) {
		super();
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
		this.pack();		
	}

	private void initializeControlPanel() {
		mControlPanel = new JPanel();
		mControlPanel.setPreferredSize(new Dimension(150, 400));
		mControlPanel.setSize(new Dimension(150, 400));
		mControlPanel.setLayout(new BorderLayout());
		
		mShipTypes = new Vector<CanDrawShip>(); 
		mShipTypes.add(new AircraftCarrierCanDraw());
		mShipTypes.add(new BattleshipCanDraw());
		mShipTypes.add(new CruiserCanDraw());
		mShipTypes.add(new PatrolBoatCanDraw());
		mShipTypes.add(new SubmarineCanDraw());
		
		mShipSelection = new JList(mShipTypes);
		mControlPanel.add(mShipSelection, BorderLayout.NORTH);
		mShipSelection.setVisible(true);
		mShipSelection.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mShipSelection.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				if ((!e.getValueIsAdjusting()) || (e.getFirstIndex() == -1)) {
					return;
				}
				
				mSelectedShip = (CanDrawShip)(((JList)e.getSource()).getSelectedValue());
				//TEST CODE
				System.out.println(mSelectedShip);
			}
		});

		mReadyButton = new JButton("Ready");
		mControlPanel.add(mReadyButton, BorderLayout.SOUTH);
		mReadyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				
				//TODO Change TestClient to Client for real end-to-end testing
				//TEST CODE
				TestClient theClient = new TestClient(mParams[2], mPlayerBoard);
				//--------------------------
				
				//Initialize Game Play objects
				EsbFrontendController theFController = 
						new EsbFrontendController(theClient);
				
				theFController.setShips(mShipList);
				EsbBattleWindow theBWindow = new EsbBattleWindow(theFController);
				try {
					theClient.connect(mParams[0], mParams[1]);
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
	}
	
	private void initializeFleetPanel(){
		mFleetPanel = new EsbFleetPanel(this.mShipList);
		mFleetPanel.setSize(new Dimension(400, 400));
		mFleetPanel.setBackground(Color.BLUE);
		
		mFleetPanel.addMouseListener(new MouseAdapter(){
			private int mCellSide = mFleetPanel.getCellSide();
			private int mGrid1X = 0, mGrid1Y = 0;
			private int mGrid2X = 0, mGrid2Y = 0;
			private Orientation mOrientation;
			private int mNumClicks           = 0;
			private int mNumCellsAcross = mFleetPanel.getNumCellsAcross();
			
			public void mouseClicked(MouseEvent e){
				if(mSelectedShip != null){										
					if(mNumClicks == 0){
						mGrid1X = e.getX() / mCellSide;
						mGrid1Y = e.getY() / mCellSide;

						//Check to ensure that click is within legal game board
						if( ((mGrid1X < 0) || (mGrid1X >= mNumCellsAcross)) ||
						    ((mGrid1Y < 0) || (mGrid1Y >= mNumCellsAcross))    ) {
							return;
						}						
						
						//TEST CODE
						System.out.println("First Placement Click: "+ mGrid1X + ", " +
								mGrid1Y);
						
						mNumClicks = 1;	
						mShipSelection.setEnabled(false);
					} else if(mNumClicks == 1){
						mGrid2X = e.getX() / mCellSide;
						mGrid2Y = e.getY() / mCellSide;						
						
//						TEST CODE
						System.out.println("Second Placement Click: "+ mGrid2X + ", " +
								mGrid2Y);
						
						//We want to throw out clicks on the same cell.
						if((mGrid1X == mGrid2X)&&(mGrid1Y == mGrid2Y))
							return;
						
						//Check to ensure that click is within legal game board
						if( ((mGrid2X < 0) || (mGrid2X >= mNumCellsAcross)) ||
						    ((mGrid2Y < 0) || (mGrid2Y >= mNumCellsAcross))    ) {
							return;
						}							
						
						int tNWx, tNWy;
						
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
							
							System.err.println(e1.getMessage());
							mPlayerBoard.print();							
							
							JOptionPane.showMessageDialog(mLocalWindow, e1.getMessage(), 
										"Error", JOptionPane.ERROR_MESSAGE);							
						}

						mNumClicks = 0;
						mSelectedShip = null;
						mShipSelection.clearSelection();
						mShipSelection.setEnabled(true);
					}
				}
			}
		});
	}
		
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
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
