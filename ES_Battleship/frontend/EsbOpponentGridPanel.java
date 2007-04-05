package frontend;

import java.awt.event.*;
import java.awt.Color;
import java.awt.Graphics;
import backend.state.*;
import java.util.*;

public class EsbOpponentGridPanel extends EsbGridPanel  {
	
	private boolean mCanClick;
	private EsbFrontendController mFController;
	
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
//					System.out.println("click detected " 
//							+ gridX 
//							+ " " 
//							+ gridY);
					
					//Check to ensure that click is within legal game board
					if((((gridX >= 0)&&(gridX < mNumCellsAcross))&&
						((gridY >= 0)&&(gridY < mNumCellsAcross))) &&
						(!mHitsList.contains(new Coordinates(gridX, gridY))) &&
						(!mMissesList.contains(new Coordinates(gridX, gridY)))){
						((EsbOpponentGridPanel) e.getSource()).setCanClick(false);
						mFController.makeMove(gridX, gridY);					
					}
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
		if(aCanClick){
			mGridLineColor = Color.BLACK;
			this.setOpaque(true);
		}else{
			mGridLineColor = Color.GRAY;
			this.setOpaque(false);
		}
		
		mCanClick = aCanClick;
	}
	
	protected void drawFeatures(Graphics g){
		this.drawHits(g);
		this.drawMisses(g);	
	}
	
	public void screenNotify(){		
		mHitsList = mFController.getOpponentHits();
		mMissesList = mFController.getOpponentMisses();
	}
}
