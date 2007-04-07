package frontend.test;

import backend.client.IClient;
import backend.constants.Constants;
import backend.constants.Orientation;
import backend.state.Board;
import backend.state.Player;
import backend.state.ships.Ships;
import backend.util.BackendException;

public class TestClient implements IClient {

	private Player mPlayer;
	private Board mBoard;
	
	public TestClient(String aid, Board aBoard) {
//		try {
//			mBoard = aBoard;
//			mBoard.add(Ships.getAircraftCarrier(), 0, 0, Orientation.VERTICAL);
//			mBoard.add(Ships.getBattleship(), 1, 1, Orientation.HORIZONTAL);
//			mBoard.add(Ships.getCruiser(), 6, 4, Orientation.VERTICAL);
//			mBoard.add(Ships.getPatrolBoat(), 6, 9, Orientation.HORIZONTAL);
//			mBoard.add(Ships.getSubmarine(), 5, 5, Orientation.VERTICAL);
//		} catch(BackendException ex) {
//			// exception occurred use ex.getMessage() to get friendly error
//		}
//		mBoard.print();
		
		mBoard = aBoard;
		mPlayer = new Player(aid, mBoard);
	}
	
	public void connect(String server, String port) {
		// We will leave this blank for test purposes.

		mPlayer.setMyTurn(true);
	}

	public void disconnect() {
		// We will leave this blank for test purposes.

	}

	public Player getPlayer() {
		return mPlayer;
	}

	public void move(int x, int y) {
		//TEST CODE
		boolean rValue = false;			
		if(x<=4){
			mPlayer.getOppBoard().setCoordinate(Constants.BOARD_HIT, x, y);
			rValue = true;
		} else {		
			mPlayer.getOppBoard().setCoordinate(Constants.BOARD_MISS, x, y);
			rValue = false;
		}
		
		// If Hit, still player's turn
		mPlayer.setMyTurn(rValue);
		
		if (!rValue) {
			Thread t = new Thread(new TestOpponent(mPlayer));
			t.start();
		}
		
		//mPlayer.getMyBoard().setCoordinate(c, x, y);
		
		//return rValue;
	}
	
	public void waitForTurn()
	{
		//added to comply with IClient
	}

	public void signalReadiness() {
//		added to comply with IClient
	}

}
