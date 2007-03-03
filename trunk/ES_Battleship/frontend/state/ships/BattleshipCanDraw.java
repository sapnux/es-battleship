package frontend.state.ships;

import java.awt.Color;
import java.awt.Graphics;
import backend.state.ships.*;

public class BattleshipCanDraw extends CanDrawShip {

	public BattleshipCanDraw(){
		super(new Battleship());
		mShipColor = Color.magenta;
	}
}
