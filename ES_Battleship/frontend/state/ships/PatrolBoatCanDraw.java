package frontend.state.ships;

import java.awt.Color;

import backend.state.ships.*;

public class PatrolBoatCanDraw extends CanDrawShip {

	public PatrolBoatCanDraw(){
		super(new PatrolBoat(), 
				"frontend/images/PTBoatH.gif",
				"frontend/images/PTBoatV.gif");
		mShipColor = Color.pink;
	}
}
