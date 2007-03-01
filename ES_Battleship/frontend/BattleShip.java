package frontend;

import backend.client.*;
import backend.state.*;
import backend.state.ships.Ships;
import frontend.test.*;
import frontend.state.ships.*;
import java.util.*;

public class BattleShip {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length != 2) {
			System.err.println("Usage: BattleShip <server-ip-address> <server-port>");
			System.exit(-1);
		}

		//Fleet arrangement section
		//--------------------------
		
		//TEST CODE
		String playerID = "player1";
		Board theBoard = new Board();
		TestClient theClient = new TestClient(playerID, theBoard);
		List <CanDrawShip> shipList = new ArrayList<CanDrawShip>();
		
		shipList.add(new AircraftCarrierCanDraw());
		shipList.get(0).setPosition(0, 0, Orientation.VERTICAL);
		shipList.add(new BattleshipCanDraw());
		shipList.get(1).setPosition(1, 1, Orientation.HORIZONTAL);
		shipList.add(new CruiserCanDraw());
		shipList.get(2).setPosition(6, 4, Orientation.VERTICAL);
		shipList.add(new PatrolBoatCanDraw());
		shipList.get(3).setPosition(6, 9, Orientation.HORIZONTAL);
		shipList.add(new SubmarineCanDraw());
		shipList.get(4).setPosition(5, 5, Orientation.VERTICAL);
		//--------------------------
		
		//Gameplay Section
		EsbFrontendController theFController = 
				new EsbFrontendController(theClient);
		
		theFController.setShips(shipList);
		EsbBattleWindow theBWindow = new EsbBattleWindow(theFController);
		theBWindow.setVisible(true);
		//--------------------------
	}

}
