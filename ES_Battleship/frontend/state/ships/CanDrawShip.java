package frontend.state.ships;

import backend.constants.Orientation;
import backend.state.ships.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;

public abstract class CanDrawShip {
	protected IShip mShip;
	protected int mX = 0;
	protected int mY = 0;
	protected Orientation mOrientation = null;
	protected Color mShipColor;
	protected boolean mDrawDefault = false;
	protected BufferedImage mHImage = null, mVImage = null;
	
	public CanDrawShip(){
	}
	
	public CanDrawShip(IShip aShip, String aHFileName, String aVFileName){
		mShip = aShip;
		mShipColor = Color.GRAY;
		
		try {
			java.net.URL tHFile = getClass().getClassLoader().getResource(aHFileName);
			java.net.URL tVFile = getClass().getClassLoader().getResource(aVFileName);
			mHImage = ImageIO.read(tHFile);
			mVImage = ImageIO.read(tVFile);
		} catch (IOException e) {
			mDrawDefault = true;
			//TEST CODE
			System.out.println(e.getMessage());
		}		
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
		if(mDrawDefault)
			return drawByDefault(g, aCellSide);
		else
			return drawByImage(g, aCellSide);
	}
	
	protected boolean drawByDefault(Graphics g, int aCellSide){
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
	
	protected boolean drawByImage(Graphics g, int aCellSide){
		if(aCellSide == 0)
			return false;

		int dispX = mX * aCellSide + 1;
		int dispY = mY * aCellSide + 1;
		
		if(this.mOrientation == Orientation.HORIZONTAL)
			g.drawImage(mHImage, dispX, dispY, null);
		else
			g.drawImage(mVImage, dispX, dispY, null);
			
		return true;
	}
	
	public String toString(){
		return mShip.getName();
	}
}
