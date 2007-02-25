/**
 * 
 */
package frontend;

import java.awt.*;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Color;

/**
 * @author cloud
 *
 */
public class EsbBattleWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private EsbPlayerGridPanel mPlayerPanel   = null;
	private EsbOpponentGridPanel mOpponentPanel = null;
	private EsbFrontendController mFController;
	
	/**
	 * This is the default constructor
	 */
	public EsbBattleWindow(EsbFrontendController aFController) {
		super();
		
		mFController = aFController;
		this.mFController.setBattleWindow(this);
		
		initialize();
		
		//TEST CODE
		mOpponentPanel.setCanClick(true);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		mPlayerPanel   = new EsbPlayerGridPanel(mFController);
		mOpponentPanel = new EsbOpponentGridPanel(mFController); 
		this.setContentPane(getJContentPane());
		this.setTitle("Battle Window");
		this.pack();
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridheight = 2;
			gridBagConstraints.gridwidth  = 1;
			gridBagConstraints.insets = new Insets(10, 10, 10, 10);
			
			mPlayerPanel.setVisible(true);		
			mOpponentPanel.setVisible(true);	
			
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.setBackground(Color.white);
			jContentPane.setVisible(true);
			jContentPane.add(mPlayerPanel,  gridBagConstraints);
			
			gridBagConstraints.gridx = 1;
			jContentPane.add(mOpponentPanel, gridBagConstraints);
		}
		return jContentPane;		
	}
	
	/**
	 * This method should be called from a controller object that detects a
	 * change in the data that is to be displayed.  This method allows 
	 * components to handle the change in their own way.
	 */
	public void notifyComponents(){
		mPlayerPanel.screenNotify();
		mOpponentPanel.screenNotify();
	}

}
