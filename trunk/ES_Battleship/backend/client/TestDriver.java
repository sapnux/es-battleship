package backend.client;

import backend.state.Board;

public class TestDriver {
	
	public static void main (String args[]) throws InterruptedException {
		Client cl = new Client(new Board());
		String server = args[0];
		String port = args[1];
		cl.connect(server, port);
		cl.sendTestPacket();
		Thread.sleep(5000);
		cl.move(2, 3);
		Thread.sleep(5000);
		cl.disconnect();
	}
	
}
