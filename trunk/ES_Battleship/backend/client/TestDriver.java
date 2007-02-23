package backend.client;

import backend.state.Board;

public class TestDriver {
	
	public static void main (String args[]) {
		Client cl = new Client(new Board());
		String server = args[0];
		String port = args[1];
		cl.connect(server, port);
		cl.sendTestPacket();
		cl.move(2, 3);
		cl.disconnect();
	}
	
}
