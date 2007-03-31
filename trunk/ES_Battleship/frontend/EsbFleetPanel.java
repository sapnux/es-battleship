package frontend;

import java.awt.Graphics;
import java.awt.Color;
import java.util.*;
import javax.swing.JOptionPane;
import frontend.state.ships.*;
import backend.state.*;

public class EsbFleetPanel extends EsbGridPanel {

	protected List <CanDrawShip> mShipsList = null;
	protected Coordinates mReticle          = null;
	
	public EsbFleetPanel(List <CanDrawShip> aShipsList){
		mShipsList = aShipsList;
	}
	
	public Coordinates getReticle() {
		return mReticle;
	}
	
	public void setReticle(int aGridX, int aGridY){
		mReticle = new Coordinates(aGridX, aGridY);
		repaint();
	}
	
	public void clearReticle(){
		mReticle = null;
		repaint();
	}
	
	@Override
	protected void drawFeatures(Graphics g) {
		this.drawMyShips(g);
	}

	@Override
	public void screenNotify() {
		// NOP We are leaving this blank.
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
		
		//draw location of user's first click on the grid
		if(mReticle != null){
			int tDispX = mReticle.getX() * this.mCellSide;
			int tDispY = mReticle.getY() * this.mCellSide;
			
			g.setColor(Color.ORANGE);
			g.drawRect(tDispX, tDispY, this.mCellSide, this.mCellSide);
		}
	}

}
