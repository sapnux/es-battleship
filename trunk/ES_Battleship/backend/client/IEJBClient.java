package backend.client;

import backend.state.Player;

public interface IEJBClient {
	void move(int x, int y);
	Player getPlayer();
	void signalReadiness();
	void waitForTurn();
}