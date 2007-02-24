package frontend;

import java.awt.event.*;

public class EsbOpponentGridPanel extends EsbGridPanel  {
	
	public EsbOpponentGridPanel(){
		super();
		this.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				EsbGridPanel tThePanel = (EsbGridPanel) e.getComponent();
				System.out.println("click detected " 
						+ e.getX() / tThePanel.getCellSide() 
						+ " " 
						+ e.getY() / tThePanel.getCellSide());
				
			}
		});
	}
}
