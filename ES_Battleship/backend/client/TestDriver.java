package backend.client;

import backend.state.*;
import backend.state.ships.*;
import java.util.*;

public class TestDriver {
	
	public static void main (String args[]) throws InterruptedException {
	
		Board board = new Board();
		board.add(Ships.GetAircraftCarrier(), 0, 0, Orientation.Vertical);
		board.add(Ships.GetBattleship(), 1, 1, Orientation.Horizontal);
		board.add(Ships.GetCruiser(), 6, 4, Orientation.Vertical);
		board.add(Ships.GetPatrolBoat(), 6, 9, Orientation.Horizontal);
		board.add(Ships.GetSubmarine(), 5, 5, Orientation.Vertical);
		board.print();

		Client cl = new Client(board);
		String server = args[0];
		String port = args[1];
		cl.connect(server, port);
		cl.sendTestPacket();
		Thread.sleep(5000);
		cl.move(2, 3);
//		cl.getPlayer().addObserver(this);
		Thread.sleep(5000);
		cl.disconnect();
	}
	
	public void update(Observable o)
	{}
	
}
