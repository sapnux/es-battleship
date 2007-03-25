package frontend.state.ships;

import java.awt.Color;
import java.awt.Graphics;

import backend.constants.Orientation;
import backend.state.ships.*;

public class SubmarineCanDraw extends CanDrawShip {

	public SubmarineCanDraw(){
		super(new Submarine());
	}
	
	public boolean drawMe(Graphics g, int aCellSide){
		if(aCellSide == 0)
			return false;
		
		int height, width;
		int dispX = mX * aCellSide;
		int dispY = mY * aCellSide;
		
		g.setColor(Color.YELLOW);

		if(this.mOrientation == Orientation.HORIZONTAL){
			height = aCellSide;
			width = aCellSide * this.mShip.getSize();
		} else{
			height = aCellSide * this.mShip.getSize();
			width = aCellSide;			
		}
		
		g.fillRoundRect(dispX, dispY, width, height, (aCellSide), (aCellSide));
		return true;
	}
}
