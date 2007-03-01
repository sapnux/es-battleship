package frontend.state.ships;

import backend.state.ships.*;
import backend.state.Orientation;
import java.awt.*;

public abstract class CanDrawShip {
	protected IShip mShip;
	protected int mX = 0;
	protected int mY = 0;
	protected Orientation mOrientation = null;
	
	public CanDrawShip(){
	}
	
	public CanDrawShip(IShip aShip){
		mShip = aShip;
	}
	
	public void setPosition(int x, int y, Orientation aOrientation) {
		mX = x;
		mY = y;
		mOrientation = aOrientation;
	}
	
	public boolean drawMe(Graphics g, int aCellSide){
		if(aCellSide == 0)
			return false;
		
		int height, width;
		int dispX = mX * aCellSide;
		int dispY = mY * aCellSide;
		
		g.setColor(Color.GRAY);

		if(this.mOrientation == Orientation.HORIZONTAL){
			height = aCellSide;
			width = aCellSide * this.mShip.getSize();
		} else{
			height = aCellSide * this.mShip.getSize();
			width = aCellSide;			
		}
		
		g.fillRect(dispX, dispY, width, height);
		return true;
	}
}
