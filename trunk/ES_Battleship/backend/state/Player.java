package backend.state;

import java.util.*;

public class Player extends Observable {
	
	private String id;
	private Board myBoard;
	private Board oppBoard;
	private boolean myTurn = false;
	
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
