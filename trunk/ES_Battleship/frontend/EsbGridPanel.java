/**
 * 
 */
package frontend;

import java.awt.*;

import javax.swing.JPanel;

import backend.state.Coordinates;

import java.awt.Dimension;
import java.util.Iterator;
import java.util.List;

/**
 * @author cloud
 *
 */
public abstract class EsbGridPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	protected static final int mBoardSide = 400;
	protected static final int mNumCellsAcross  = 10;
	//stores the width of each grid cell by number of pixels
	//used for resolving grid coordinates to screen coordinates & vice versa
	protected int mCellSide;
	/**
	 * Background color can be set by children.
	 */
	protected Color mBackgroundColor;
	/**
	 * GridLine color can be set by children 
	 */
	protected Color mGridLineColor;
		
	protected List<Coordinates> mHitsList   = null;
	protected List<Coordinates> mMissesList = null;
	
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
		mCellSide = mBoardSide/mNumCellsAcross;	
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
	 * Returns the width of each cell in pixels.
	 * @return The number of grid locations along one side of the (square) board.
	 */
	public int getCellSide() {
		return mCellSide;
	}
	
	public int getNumCellsAcross() {
		return mNumCellsAcross;
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
	
	protected void drawHits(Graphics g) {
		int dispX, dispY;
		
		Coordinates moveLoc;
		Iterator<Coordinates> tHitsIterator = mHitsList.iterator();

		g.setColor(Color.RED);
		while(tHitsIterator.hasNext()){
			moveLoc = (Coordinates) tHitsIterator.next();
			dispX = (moveLoc.getX() * mCellSide)+1;
			dispY = (moveLoc.getY() * mCellSide)+1;
			
			g.fillRect(dispX, dispY, mCellSide-1, mCellSide-1);
		}
	}
	
	protected void drawMisses(Graphics g) {
		int dispX, dispY;
		Coordinates moveLoc;		
		
		Iterator<Coordinates> tMissesIterator = mMissesList.iterator();
		g.setColor(Color.WHITE);
		while(tMissesIterator.hasNext()){
			moveLoc = (Coordinates) tMissesIterator.next();
			dispX = (moveLoc.getX() * mCellSide)+1;
			dispY = (moveLoc.getY() * mCellSide)+1;
			
			g.fillRect(dispX, dispY, mCellSide-1, mCellSide-1);			
		}		
	}
}
