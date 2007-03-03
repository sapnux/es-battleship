package frontend;

import java.awt.Graphics;
import java.util.*;

import javax.swing.JOptionPane;

import frontend.state.ships.*;

public class EsbFleetPanel extends EsbGridPanel {

	List <CanDrawShip> mShipsList = null;
	
	public EsbFleetPanel(List <CanDrawShip> aShipsList){
		mShipsList = aShipsList;
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
	}

}
