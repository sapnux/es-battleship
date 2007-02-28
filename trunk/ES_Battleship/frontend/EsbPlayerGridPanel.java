package frontend;

import java.awt.Color;
import java.awt.Graphics;

public class EsbPlayerGridPanel extends EsbGridPanel {
	
	private EsbFrontendController mFController;
	
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
	}
	
	protected void drawFeatures(Graphics g){
		this.drawHits(g);
		this.drawMisses(g);	
	}
	
	public void screenNotify(){
		mHitsList   = mFController.getPlayerHits();
		mMissesList = mFController.getPlayerMisses();		
	}
}
