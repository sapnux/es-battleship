package frontend.state.ships;

import java.awt.Color;
import java.awt.Graphics;
import backend.state.ships.*;

public class BattleshipCanDraw extends CanDrawShip {

	public BattleshipCanDraw(){
		super(new Battleship(), 
				"frontend/images/BattleShipH.gif",
				"frontend/images/BattleShipV.gif");
		mShipColor = Color.magenta;
	}
}
