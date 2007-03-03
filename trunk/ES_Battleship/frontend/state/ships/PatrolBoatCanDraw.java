package frontend.state.ships;

import java.awt.Color;

import backend.state.ships.*;

public class PatrolBoatCanDraw extends CanDrawShip {

	public PatrolBoatCanDraw(){
		super(new PatrolBoat());
		mShipColor = Color.pink;
	}
}
