package backend.engine;

import backend.state.Board;
import backend.state.Player;

public class GameEngine {
	
	private Player player1 = null;
	private Player player2 = null;
	
	public GameEngine() {
		resetGame();
	}

	public void addPlayer(String pId, Board board) {
		if (player1 == null) {
			player1 = new Player(pId, board);
		} else if (player2 == null) {
			player2 = new Player(pId, board);
		} else {
			throw new RuntimeException("Game full");
		}
	}
	
	public boolean move(String pId, String x, String y) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isGameReady() {
//		 TODO Auto-generated method stub
		return true;
//		return player1 != null &&player2 != null;
	}

	public boolean isMyTurn(String playerId) {
		return playerId.equals(player1.getId());
	}

	public void resetGame() {
		player1=null;
		player2=null;
	}
}
