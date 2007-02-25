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
public abstract class EsbGridPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int mBoardSide = 400;
	private static final int mNumCellsAcross  = 10;
	/**
	 * Background color can be set by children.
	 */
	protected Color mBackgroundColor;
	/**
	 * GridLine color can be set by children 
	 */
	protected Color mGridLineColor;
		
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
		mBackgroundColor = Color.WHITE;
		mGridLineColor = Color.BLACK;
		//We add 1 to the board size so that the final gridlines are drawn in the x and y planes.
		this.setSize(mBoardSide + 1, mBoardSide + 1);
		this.setLayout(new GridBagLayout());
		this.setPreferredSize(new Dimension(mBoardSide + 1, mBoardSide + 1));
	}
	
	
	public void paint(Graphics g){
		super.paint(g);

		//draw the gridlines
		g.setColor(mGridLineColor);
		for(int x=0; x<=mBoardSide; x+=this.getCellSide())
			g.drawLine(x, 0, x, mBoardSide);
		for(int y=0; y<=mBoardSide; y+=this.getCellSide())
			g.drawLine(0, y, mBoardSide, y);
		
		drawFeatures(g);
	}
	
	/**
	 * Returns the number of cells per side, i.e. the number of separate grid locations
	 * going across and down the game board.
	 * @return The number of grid locations along one side of the (square) board.
	 */
	protected int getCellSide() {
		return mBoardSide/mNumCellsAcross;
	}
	
	/**
	 * Abstract method to be implemented by child classes or left blank.  This
	 * is where a child class would draw out its own unique features.  This
	 * function would be called after the gridlines have been put down on the
	 * panel.
	 * @param g The graphics object for the panel.
	 */
	protected abstract void drawFeatures(Graphics g);
	
	/**
	 * Abstract method to be implemented by child classes or left blank.  This
	 * is called by the container to tell the panel that some screen feature
	 * has been updated and provides the panel with the opportunity to update
	 * its information outside of the Paint() method.
	 */
	public abstract void screenNotify();
}
