package frontend;

import java.awt.event.*;
import java.awt.Color;
import java.awt.Graphics;
import backend.state.*;
import java.util.*;

public class EsbOpponentGridPanel extends EsbGridPanel  {
	
	private boolean mCanClick;
	private EsbFrontendController mFController;
	//stores the width of each grid cell by number of pixels
	//used for resolving grid coordinates to screen coordinates & vice versa
	private int mCellSide;
	private List<Coordinates> mHitsList;
	private List<Coordinates> mMissesList;
	
	public EsbOpponentGridPanel(EsbFrontendController aFController){
		super();		
		mFController = aFController;
		initialize();
	}
	
	private void initialize(){
		mBackgroundColor = Color.BLUE;
		
		this.setBackground(mBackgroundColor);
		//the default state of the game is to disable clicking so that the player
		//cannot make moves.
		this.setCanClick(false);
		mCellSide = this.getCellSide();		
		mHitsList = mFController.getOpponentHits();
		mMissesList = mFController.getOpponentMisses();
		
		//capture mouse clicks on the panel
		this.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				int gridX, gridY;
				if(mCanClick){
					gridX = e.getX() / mCellSide;
					gridY = e.getY() / mCellSide;

					//TEST CODE
					System.out.println("click detected " 
							+ gridX 
							+ " " 
							+ gridY);
					
					//Check to ensure that click is within legal game board
					if(((gridX >= 0)&&(gridX < mNumCellsAcross))&&
							((gridY >= 0)&&(gridY < mNumCellsAcross)))
						mFController.makeMove(gridX, gridY);					
				}
			}
		});
	}
	
	/**
	 * Locks the panel from clicking and makes a visual cue so that the player knows that
	 * they are unable to currently make a move in the game.
	 * @param aCanClick True if it has become the player's turn to make a move, false otherwise.
	 */
	public void setCanClick(boolean aCanClick){
		if(aCanClick)
			mGridLineColor = Color.BLACK;
		else
			mGridLineColor = Color.GRAY;
		
		mCanClick = aCanClick;
	}
	
	protected void drawFeatures(Graphics g){
		int dispX, dispY;
		Coordinates moveLoc;
		
		Iterator<Coordinates> tHitsIterator = mHitsList.iterator();
		Iterator<Coordinates> tMissesIterator = mMissesList.iterator();
		
		g.setColor(Color.RED);
		while(tHitsIterator.hasNext()){
			moveLoc = (Coordinates) tHitsIterator.next();
			dispX = (moveLoc.getX() * mCellSide)+1;
			dispY = (moveLoc.getY() * mCellSide)+1;
			
			g.fillRect(dispX, dispY, mCellSide-1, mCellSide-1);
		}
		
		g.setColor(Color.WHITE);
		while(tMissesIterator.hasNext()){
			moveLoc = (Coordinates) tMissesIterator.next();
			dispX = (moveLoc.getX() * mCellSide)+1;
			dispY = (moveLoc.getY() * mCellSide)+1;
			
			g.fillRect(dispX, dispY, mCellSide-1, mCellSide-1);			
		}
		
	}
	
	public void screenNotify(){		
		mHitsList = mFController.getOpponentHits();
		mMissesList = mFController.getOpponentMisses();
	}
}
