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


public class SampleClient implements Observer
{
	private Client cl;
	public SampleClient(String server, String port, String id) throws InterruptedException
	{
		Board board = new Board();
		board.add(Ships.GetAircraftCarrier(), 0, 0, Orientation.VERTICAL);
		board.add(Ships.GetBattleship(), 1, 1, Orientation.HORIZONTAL);
		board.add(Ships.GetCruiser(), 6, 4, Orientation.VERTICAL);
		board.add(Ships.GetPatrolBoat(), 6, 9, Orientation.HORIZONTAL);
		board.add(Ships.GetSubmarine(), 5, 5, Orientation.VERTICAL);
		board.print();
		
		cl = new Client(id, board);
		cl.getPlayer().addObserver(this);
		cl.connect(server, port);	
	}
	
	public void update(Observable o, Object obj)
	{	
		backend.util.Logger.LogInfo("myBoard:");
		cl.getPlayer().getMyBoard().print();

		backend.util.Logger.LogInfo("oppBoard:");
		cl.getPlayer().getOppBoard().print();
		
		if (cl.getPlayer().getGameResult() == GameResult.WIN) {
			System.out.println("You Win");
			cl.disconnect();
			System.exit(0);
		} else if (cl.getPlayer().getGameResult() == GameResult.LOSS) {
			System.out.println("You Lose");
			cl.disconnect();
			System.exit(0);
		}
		
		System.out.println("isMyTurn: " + cl.getPlayer().isMyTurn());
		
		boolean myTurn = cl.getPlayer().isMyTurn();
		if(myTurn)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			Coordinates move = makeMove();
			cl.move(move.getX(), move.getY());
		}
		else
		{
			System.out.println("It's not our turn. Waiting for our turn.");
			cl.listenForMessages();
		}

	}
	
	private Coordinates makeMove()
	{
		List<Coordinates> list = cl.getPlayer().getOppBoard().getAllEmptyCoordinates();
		Random random = new Random();
		int index = random.nextInt(list.size()); // nextInt returns [0, size)
		return list.get(index);
	}
	
}