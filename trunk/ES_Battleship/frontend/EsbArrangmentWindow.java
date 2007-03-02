package frontend;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.util.List;

import backend.state.*;
import frontend.state.*;
import frontend.state.ships.*;

import java.util.*;

public class EsbArrangmentWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel  jContentPane            = null;
	private JPanel  mControlPanel           = null;
	private JButton mReadyButton            = null;
	private JList   mShipSelection          = null;
	private Vector <CanDrawShip> mShipTypes = null;
	private EsbFleetPanel mFleetPanel       = null;
	
	private Board mPlayerBoard           = null;
	private List <CanDrawShip> mShipList = null;
	private CanDrawShip mSelectedShip    = null;

	/**
	 * This is the default constructor
	 */
	public EsbArrangmentWindow(Board aBoard, List <CanDrawShip> aShipList) {
		super();
		mPlayerBoard = aBoard;
		mShipList    = aShipList;
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
		
//		this.setVisible(true);
//		try {
//			Thread.sleep(3000);
//			
//			CanDrawShip[] tShipTypes = { 
//					 new AircraftCarrierCanDraw(),
//					 new BattleshipCanDraw()};
//
//			mShipSelection.setListData(tShipTypes);	
//			
//			
//		} catch (Exception e) {
//			System.err.println(e.getMessage());
//		}
		
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
		mReadyButton.setVisible(true);
		mReadyButton.setEnabled(false);
	}
	
	private void initializeFleetPanel(){
		mFleetPanel = new EsbFleetPanel(this.mShipList);
		mFleetPanel.setSize(new Dimension(400, 400));
		mFleetPanel.setBackground(Color.BLUE);
		
		mFleetPanel.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(mSelectedShip != null){
					mSelectedShip.setPosition(0, 0, Orientation.HORIZONTAL);
					mShipList.add(mSelectedShip);
					((JPanel)e.getSource()).repaint();
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
