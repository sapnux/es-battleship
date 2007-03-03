package frontend;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

import javax.swing.JOptionPane;

import backend.state.*;
import frontend.state.ships.*;

public class EsbPlayerGridPanel extends EsbGridPanel {
	
	private EsbFrontendController mFController;
	private List<CanDrawShip> mShipsList;
	
	public EsbPlayerGridPanel(EsbFrontendController aFController){
		super();
		mFController = aFController;
		initialize();
	}
	
	private void initialize(){
		mBackgroundColor = Color.CYAN;
		this.setBackground(mBackgroundColor);

		mHitsList = mFController.getPlayerHits();
		mMissesList = mFController.getPlayerMisses();
		mShipsList = mFController.getShips();
	}
	
	protected void drawFeatures(Graphics g){
		this.drawMyShips(g);
		this.drawHits(g);
		this.drawMisses(g);	
	}
	
	protected void drawMyShips(Graphics g){
		Iterator<CanDrawShip> tShipsIterator = mShipsList.iterator();
		
		try {
			while(tShipsIterator.hasNext()){
				if(!tShipsIterator.next().drawMe(g, this.mCellSide))
					throw new Exception("Failure to draw CanDrawShip");
			}
		} catch (Exception e) {			
			System.err.println(e.getMessage());			
			JOptionPane.showMessageDialog(this, e.getMessage(), 
					"Error", JOptionPane.ERROR_MESSAGE);			
		}
	}
	
	public void screenNotify(){
		mHitsList   = mFController.getPlayerHits();
		mMissesList = mFController.getPlayerMisses();		
	}
}
