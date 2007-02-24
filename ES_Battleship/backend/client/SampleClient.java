package backend.client;

import java.util.Observable;
import java.util.Observer;

import backend.state.Board;
import backend.state.Orientation;
import backend.state.ships.Ships;

public class SampleClient implements Observer
{
	public SampleClient(String server, String port) throws InterruptedException
	{
		Board board = new Board();
		board.add(Ships.GetAircraftCarrier(), 0, 0, Orientation.VERTICAL);
		board.add(Ships.GetBattleship(), 1, 1, Orientation.HORIZONTAL);
		board.add(Ships.GetCruiser(), 6, 4, Orientation.VERTICAL);
		board.add(Ships.GetPatrolBoat(), 6, 9, Orientation.HORIZONTAL);
		board.add(Ships.GetSubmarine(), 5, 5, Orientation.VERTICAL);
		board.print();

		Client cl = new Client(board);
		cl.connect(server, port);
		cl.sendTestPacket();
		Thread.sleep(5000);
		cl.move(2, 3);
		cl.getPlayer().addObserver(this);
		Thread.sleep(5000);
		cl.disconnect();		
	}
	
	public void update(Observable o, Object obj)
	{
		backend.util.Logger.LogWarning("IT WORKS");
	}
}