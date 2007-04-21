package backend.engine;

import backend.constants.MoveResult;
import backend.state.Board;
import backend.state.Player;
import backend.util.BackendException;
import backend.util.Logger;

public class GameEngine {
	
	private Player player1 = null;
	private Player player2 = null;
	
	public GameEngine() {
		resetGame();
	}

	/**
	 * Adds player to the game, if possible
	 * @param pId New player's ID
	 * @param board New player's board
	 */
	public void addPlayer(String pId, Board board) {
		if (this.player1 == null) {
			this.player1 = new Player(pId, board);
		} else if (this.player2 == null) {
			this.player2 = new Player(pId, board);
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
		try {
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
		} catch (BackendException e) {
			Logger.LogError(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the actual board of the opponent for a given player ID
	 * @param pId Player's id whose opponent's board to return
	 * @return revealed board of the opponent
	 */
	private Board getOpponentBoard(String pId) {
		if (pId.equals(this.player1.getId())) {
			return this.player2.getMyBoard();
		} else if (pId.equals(this.player2.getId())){
			return this.player1.getMyBoard();
		} else {
			throw new RuntimeException("No such player");
		}
	}

	/**
	 * Determines if both players are connected and initialized
	 * 
	 * @return true if both players are connected and initialized
	 */
	public boolean isGameReady() {
		return this.player1 != null && this.player2 != null;
	}

	/**
	 * Obtain Player reference for a given ID
	 * @param pId Player's ID
	 * @return Player reference
	 */
	private Player getPlayer(String pId) {
		if (pId.equals(this.player1.getId())) {
			return this.player1;
		} else if (pId.equals(this.player2.getId())){
			return this.player2;
		} else {
			throw new RuntimeException("No such player");
		}
	}
	
	/**
	 * Get whether it is the given playerId's turn.
	 * @param playerId
	 * @return
	 */
	public boolean isMyTurn(String playerId) {
		return playerId.equals(this.player1.getId());
	}

	/**
	 * Resets the game.
	 *
	 */
	public void resetGame() {
		this.player1 = null;
		this.player2 = null;
	}
}
