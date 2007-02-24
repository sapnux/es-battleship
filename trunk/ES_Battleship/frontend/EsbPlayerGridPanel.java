package frontend;

import java.awt.Color;

public class EsbPlayerGridPanel extends EsbGridPanel {
	
	public EsbPlayerGridPanel(){
		super();
		initialize();
	}
	
	private void initialize(){
		mBackgroundColor = Color.CYAN;
		this.setBackground(mBackgroundColor);		
	}
}
