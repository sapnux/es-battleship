package frontend;

import java.awt.event.*;
import java.awt.Color;
import java.awt.Graphics;
import backend.state.*;
import java.util.*;

public class EsbOpponentGridPanel extends EsbGridPanel  {
	
	private boolean mCanClick;
	private EsbFrontendController mFController;
	Iterator<Coordinates> mHitsIterator;
	Iterator<Coordinates> mMissIterator;
	
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
		
		//capture mouse clicks on the panel
		this.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				int gridX, gridY;
				if(mCanClick){
					EsbGridPanel tThePanel = (EsbGridPanel) e.getComponent();
					int cellSide = tThePanel.getCellSide();
					gridX = e.getX() / cellSide;
					gridY = e.getY() / cellSide;

					//TEST CODE
					System.out.println("click detected " 
							+ gridX 
							+ " " 
							+ gridY);
					
					//Check to ensure that click is within legal game board
					if(((gridX >= 0)&&(gridX <= cellSide))&&
							((gridY >= 0)&&(gridY <= cellSide)))
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

		
		g.setColor(Color.RED);
		while(mHitsIterator.hasNext()){
			
		}
		
		g.setColor(Color.WHITE);
		while(mMissIterator.hasNext()){
			
		}
	}
	
	public void screenNotify(){		
		mHitsIterator = mFController.getOpponentHits().iterator();
		mMissIterator = mFController.getOpponentMisses().iterator();
	}
}
