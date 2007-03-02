package backend.client;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import backend.state.Board;
import backend.state.Coordinates;
import backend.state.GameResult;
import backend.state.Orientation;
import backend.state.ships.Ships;
import backend.util.BackendException;
import backend.util.Logger;

public class SampleClient implements Observer {
	private IClient cl;

	public SampleClient(String server, String port, String id)
			throws InterruptedException {

		Board board = new Board();
		try {
			board.add(Ships.getAircraftCarrier(), 0, 0, Orientation.VERTICAL);
			board.add(Ships.getBattleship(), 1, 1, Orientation.HORIZONTAL);
			board.add(Ships.getCruiser(), 6, 4, Orientation.VERTICAL);
			board.add(Ships.getPatrolBoat(), 6, 9, Orientation.HORIZONTAL);
			board.add(Ships.getSubmarine(), 5, 5, Orientation.VERTICAL);
		} catch (BackendException ex) {
			Logger.LogError(ex.getMessage());
		}
		board.print();

		cl = new Client(id, board);
		cl.getPlayer().addObserver(this);
		cl.connect(server, port);
	}

	/**
	 * This method is called each time the player's turn changes.
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object obj) {
		Board.print(cl.getPlayer().getMyBoard(), cl.getPlayer().getOppBoard());

		if (cl.getPlayer().getGameResult() == GameResult.WIN) {
			System.out.println("[DONE] You are the WINNER!");
			cl.disconnect();
			System.exit(0);
		} else if (cl.getPlayer().getGameResult() == GameResult.LOSS) {
			System.out.println("[DONE] You have LOST!");
			cl.disconnect();
			System.exit(0);
		}

		if (cl.getPlayer().isMyTurn()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
			}
			Coordinates move = makeMove();
			System.out.println("[MOVE] You are attacking [" + move.getX() + ","
					+ move.getY() + "].");
			cl.move(move.getX(), move.getY());
		} else {
			System.out.println("[WAIT] Waiting for our turn.");
			cl.waitForTurn();
		}
	}

	/**
	 * Returns a random move (x,y) from all the empty coordinates remaining on
	 * the game board.
	 * 
	 * @return
	 */
	private Coordinates makeMove() {
		List<Coordinates> list = cl.getPlayer().getOppBoard()
				.getAllEmptyCoordinates();
		Random random = new Random();
		int index = random.nextInt(list.size()); // nextInt returns [0, size)
		return list.get(index);
	}
}