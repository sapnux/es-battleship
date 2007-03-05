package backend.client;

import backend.state.Player;

public interface IClient {
	void connect(String server, String port);
	void disconnect();
	boolean move(int x, int y);
	Player getPlayer();
	void signalReadiness();
	void waitForTurn();
}