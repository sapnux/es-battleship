package frontend;

import backend.client.*;
import backend.state.*;
import frontend.test.*;

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
		
		//Gameplay Section
		EsbFrontendController theFController = 
				new EsbFrontendController(theClient);
		
		EsbBattleWindow theBWindow = new EsbBattleWindow(theFController);
		theBWindow.setVisible(true);
		//--------------------------
	}

}
