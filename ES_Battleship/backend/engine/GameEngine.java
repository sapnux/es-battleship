package backend.engine;

import java.util.Map;

import backend.constants.MoveResult;
import backend.state.Board;
import backend.state.Player;

public class GameEngine {
	
	private Player player1 = null;
	private Player player2 = null;
	String whoMoves = null;
	private Map queueMap;
	
	public GameEngine() {
		resetGame();
	}

	/**
	 * Adds player to the game, if possible
	 * @param pId New player's ID
	 * @param board New player's board
	 */
	public void addPlayer(String pId, Board board) {
		if (player1 == null) {
			player1 = new Player(pId, board);
		} else if (player2 == null) {
			player2 = new Player(pId, board);
		} else {
			throw new RuntimeException("Game full");
		}
	}
	/**
	 * Updates player's representation of opponent's board with a hit or a miss,
	 * and returns whether the move was a hit or a miss
	 * @param pId Player id
	 * @param x x-coordinate of the move
	 * @param y y-coordinate of the move
	 * @return true if move is a hit
	 */
	public MoveResult move(String pId, String x, String y) {
		Board oppBoard = getOpponentBoard(pId);
		Player thisMovePlayer = getPlayer(pId);
		int xCoord = Integer.parseInt(x);
		int yCoord = Integer.parseInt(y);
		
		if (oppBoard.isHit(xCoord, yCoord)) {
			thisMovePlayer.getOppBoard().setHit(xCoord, yCoord);
			if (thisMovePlayer.getOppBoard().hasLost()) 
				return MoveResult.WIN;
			return MoveResult.HIT;
		} else {
			thisMovePlayer.getOppBoard().setMiss(xCoord, yCoord);
			return MoveResult.MISS;
		}
	}

	/**
	 * Returns the actual board of the opponent for a given player ID
	 * @param pId Player's id whose opponent's board to return
	 * @return revealed board of the opponent
	 */
	private Board getOpponentBoard(String pId) {
		if (pId.equals(player1.getId())) {
			return player2.getMyBoard();
		} else if (pId.equals(player2.getId())){
			return player1.getMyBoard();
		} else {
			throw new RuntimeException("No such player");
		}
	}

	/**
	 * Determines if both players are connected and initialized
	 * @return true if both players are connected and initialized
	 */
	public boolean isGameReady() {
//		 TODO Auto-generated method stub
//		return true;
		return player1 != null &&player2 != null;
	}

	/**
	 * Obtain Player reference for a given ID
	 * @param pId Player's ID
	 * @return Player reference
	 */
	private Player getPlayer(String pId) {
		if (pId.equals(player1.getId())) {
			return player1;
		} else if (pId.equals(player2.getId())){
			return player2;
		} else {
			throw new RuntimeException("No such player");
		}
	}
	
	public boolean isMyTurn(String playerId) {
		return playerId.equals(player1.getId());
	}

	public void resetGame() {
		player1=null;
		player2=null;
	}
}
