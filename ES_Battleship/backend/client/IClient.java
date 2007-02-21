package backend.client;

import backend.state.Board;

public interface IClient {
	void connect(String server, String port);
	void disconnect();
	boolean move(int x, int y);
	Board getBoard();
}
