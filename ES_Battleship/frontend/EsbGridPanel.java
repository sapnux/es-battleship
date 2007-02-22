/**
 * 
 */
package frontend;

import java.awt.*;
import javax.swing.JPanel;
import java.awt.Dimension;

/**
 * @author cloud
 *
 */
public class EsbGridPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * This is the default constructor
	 */
	public EsbGridPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(400, 400);
		this.setLayout(new GridBagLayout());
		this.setPreferredSize(new Dimension(400, 400));
	}
	
	public void paint(Graphics g){
		super.paint(g);

		g.setColor(Color.BLACK);
		for(int x=40; x<400; x+=40)
			g.drawLine(x, 0, x, 400);
		for(int y=40; y<400; y+=40)
			g.drawLine(0, y, 400, y);
	}
}
