package frontend.state.ships;

import java.awt.Color;
import java.awt.Graphics;
import backend.state.ships.*;

public class CruiserCanDraw extends CanDrawShip {

	public CruiserCanDraw(){
		super(new Cruiser(), 
				"frontend/images/DestroyerH.gif",
				"frontend/images/DestroyerV.gif");
		mShipColor = Color.green;
	}
}
