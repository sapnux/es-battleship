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
	private EsbGridPanel mPlayerBoard = null;
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
		mPlayerBoard = new EsbGridPanel();
		this.setContentPane(getJContentPane());
		this.setTitle("Battle Window");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		/*
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.setBackground(Color.white);
			jContentPane.setVisible(false);
			jContentPane.add(mPlayerBoard, gridBagConstraints);
			mPlayerBoard.setBackground(Color.CYAN);
			mPlayerBoard.setVisible(true);
		}
		return jContentPane;
		*/
		mPlayerBoard.setBackground(Color.CYAN);
		mPlayerBoard.setVisible(true);
		return mPlayerBoard;
	}

}
