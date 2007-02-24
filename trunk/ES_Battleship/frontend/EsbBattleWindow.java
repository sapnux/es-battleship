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
	private EsbPlayerGridPanel mPlayerBoard   = null;
	private EsbOpponentGridPanel mOpponentBoard = null;
	/**
	 * This is the default constructor
	 */
	public EsbBattleWindow() {
		super();
		initialize();
		this.pack();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		//this.setSize(400, 400);
		mPlayerBoard   = new EsbPlayerGridPanel();
		mOpponentBoard = new EsbOpponentGridPanel(); 
		this.setContentPane(getJContentPane());
		this.setTitle("Battle Window");
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
			
			mPlayerBoard.setBackground(Color.CYAN);
			mPlayerBoard.setVisible(true);		
			
			mOpponentBoard.setBackground(Color.BLUE);
			mOpponentBoard.setVisible(true);	
			
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.setBackground(Color.white);
			jContentPane.setVisible(true);
			jContentPane.add(mPlayerBoard,  gridBagConstraints);
			
			gridBagConstraints.gridx = 1;
			jContentPane.add(mOpponentBoard, gridBagConstraints);
		}
		return jContentPane;
		
	}

}
