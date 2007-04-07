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
			System.err.println("Usage: BattleShip <queue name> <player-id>");
			System.exit(-1);
		}

		//Common data between the two windows
		Board theBoard = new Board();
		List <CanDrawShip> shipList = new ArrayList<CanDrawShip>();		
		
		//Fleet arrangement section
		//--------------------------
		EsbArrangmentWindow theAWindow = 
			new EsbArrangmentWindow(theBoard, shipList, args);
		theAWindow.setVisible(true);
		
	}

}
