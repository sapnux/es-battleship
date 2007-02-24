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
	private static final int mBoardSide = 400;
	private static final int mNumCellsAcross  = 10;
		
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
		this.setSize(mBoardSide + 1, mBoardSide + 1);
		this.setLayout(new GridBagLayout());
		this.setPreferredSize(new Dimension(mBoardSide + 1, mBoardSide + 1));
	}
	
	public void paint(Graphics g){
		super.paint(g);

		g.setColor(Color.BLACK);
		for(int x=0; x<=mBoardSide; x+=this.getCellSide())
			g.drawLine(x, 0, x, mBoardSide);
		for(int y=0; y<=mBoardSide; y+=this.getCellSide())
			g.drawLine(0, y, mBoardSide, y);
	}
	
	protected int getCellSide() {
		return mBoardSide/mNumCellsAcross;
	}
}
