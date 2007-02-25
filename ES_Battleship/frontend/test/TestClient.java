package frontend.test;

import backend.client.IClient;
import backend.state.*;
import backend.state.Board;
import backend.state.Orientation;
import backend.state.Player;
import backend.state.ships.Ships;

public class TestClient implements IClient {

	private Player mPlayer;
	private Board mBoard;
	
	public TestClient(String aid, Board aBoard) {
		mBoard = aBoard;
		
		mBoard.add(Ships.GetAircraftCarrier(), 0, 0, Orientation.VERTICAL);
		mBoard.add(Ships.GetBattleship(), 1, 1, Orientation.HORIZONTAL);
		mBoard.add(Ships.GetCruiser(), 6, 4, Orientation.VERTICAL);
		mBoard.add(Ships.GetPatrolBoat(), 6, 9, Orientation.HORIZONTAL);
		mBoard.add(Ships.GetSubmarine(), 5, 5, Orientation.VERTICAL);
		mBoard.print();
		
		mPlayer = new Player(aid, mBoard);
	}
	
	public void connect(String server, String port) {
		// We will leave this blank for test purposes.

	}

	public void disconnect() {
		// We will leave this blank for test purposes.

	}

	public Player getPlayer() {
		return mPlayer;
	}

	public boolean move(int x, int y) {
		//TEST CODE
//		System.out.println("Inside TestClient.move, player: " + mPlayer.getId());
		boolean rValue = false;			
		if((x%2 == 0)||(y%2 == 0)){
			mPlayer.getOppBoard().setCoordinate(Constants.BOARD_HIT, x, y);
			rValue = true;
		} else {		
			mPlayer.getOppBoard().setCoordinate(Constants.BOARD_MISS, x, y);
			rValue = false;
		}
		mPlayer.setMyTurn(true);
//		System.out.println("number of observers of mPlayer: " + mPlayer.countObservers());
//		System.out.println("player has changed = " + mPlayer.hasChanged());
		return rValue;
	}

}
