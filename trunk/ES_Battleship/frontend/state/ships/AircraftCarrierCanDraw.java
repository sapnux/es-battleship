package frontend.state.ships;

import java.awt.*;
import backend.state.ships.*;
import backend.state.*;

public class AircraftCarrierCanDraw extends CanDrawShip {
	
	public AircraftCarrierCanDraw() {
		super(new AircraftCarrier());
	}
}
