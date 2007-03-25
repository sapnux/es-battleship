package frontend.state.ships;

import backend.constants.Orientation;
import backend.state.ships.*;
import java.awt.*;

public abstract class CanDrawShip {
	protected IShip mShip;
	protected int mX = 0;
	protected int mY = 0;
	protected Orientation mOrientation = null;
	protected Color mShipColor;
	
	public CanDrawShip(){
	}
	
	public CanDrawShip(IShip aShip){
		mShip = aShip;
		mShipColor = Color.GRAY;
	}
	
	public IShip getIShip(){
		return mShip;
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
		
		g.setColor(mShipColor);

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
	
	public String toString(){
		return mShip.getName();
	}
}
