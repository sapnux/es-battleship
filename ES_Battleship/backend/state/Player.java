package backend.state;

import java.util.*;

public class Player extends Observable {
	
	private String id;
	private Board myBoard;
	private Board oppBoard;
	private boolean myTurn = false;
	private GameResult gameResult = GameResult.UNKNOWN;
	
	/**
	 * 
	 * @param id
	 * @param board
	 */
	public Player(String id, Board board) {
		this.id = id;
		this.myBoard = board;
		this.oppBoard = new Board();
	}

	/**
	 * 
	 */
	public void setChanged()
	{
		super.setChanged();
	}
	
	/**
	 * 
	 * @return
	 */
	public Board getMyBoard() {
		return this.myBoard;
	}

	/**
	 * 
	 * @return
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * 
	 * @return
	 */
	public Board getOppBoard() {
		return this.oppBoard;
	}
	
	public GameResult getGameResult() {
		return gameResult;
	}

	public void setGameResult(GameResult gameResult) {
		this.gameResult = gameResult;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isMyTurn() {
		return this.myTurn;
	}
	
	public void setMyTurn(boolean myTurn) {
		this.myTurn = myTurn;
		setChanged();
		notifyObservers();
	}
}
