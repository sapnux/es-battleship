package frontend;

import java.awt.event.*;
import java.awt.Color;

public class EsbOpponentGridPanel extends EsbGridPanel  {
	
	private boolean mCanClick;
	
	public EsbOpponentGridPanel(){
		super();
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
				if(mCanClick){
					EsbGridPanel tThePanel = (EsbGridPanel) e.getComponent();

					//TEST CODE
					System.out.println("click detected " 
							+ e.getX() / tThePanel.getCellSide() 
							+ " " 
							+ e.getY() / tThePanel.getCellSide());
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
}
