package backend.client;

import backend.state.Board;
import backend.state.ships.*;

public class TestDriver {
	
	public static void main (String args[]) throws InterruptedException {
		Client cl = new Client();
		String server = args[0];
		String port = args[1];
		cl.connect(server, port);
		cl.sendTestPacket();
		Thread.sleep(5000);
		cl.move(2, 3);
		Thread.sleep(5000);
		cl.disconnect();
		
		Board board = new Board();
		board.add(Ships.GetAircraftCarrier(), 0, 0, true);
		board.add(Ships.GetBattleship(), 1, 1, false);
		board.add(Ships.GetCruiser(), 6, 4, true);
		board.add(Ships.GetPatrolBoat(), 6, 9, false);
		board.add(Ships.GetSubmarine(), 5, 5, true);
		board.print();
		
//		Client cl = new Client(board);
//		String server = args[0];
//		String port = args[1];
//		cl.connect(server, port);
//		cl.sendTestPacket();
//		Thread.sleep(5000);
//		cl.move(2, 3);
//		Thread.sleep(5000);
//		cl.disconnect();
	}
	
}
