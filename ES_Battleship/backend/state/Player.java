package backend.state;

import java.util.*;

public class Player extends Observable {
	
	private String id;
	private Board myBoard;
	private Board oppBoard;
	private boolean myTurn;
	
	public Player(String id, Board board) {
		this.id = id;
		this.myBoard = board;
		this.oppBoard = new Board();
	}

	public Board getMyBoard() {
		return myBoard;
	}

	public String getId() {
		return id;
	}

	public Board getOppBoard() {
		return oppBoard;
	}

	public boolean isMyTurn() {
		return myTurn;
	}
}
