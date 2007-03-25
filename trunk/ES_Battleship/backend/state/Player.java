package backend.state;

import java.util.*;

import backend.constants.GameResult;

public class Player extends Observable {

	private String id;
	private Board myBoard;
	private Board oppBoard;
	private boolean myTurn = false;
	private GameResult gameResult = GameResult.UNKNOWN;
	private Vector<String> messages;

	/**
	 * Creates a new player object with the given player id and game board.
	 * 
	 * @param id
	 * @param board
	 */
	public Player(String id, Board board) {
		this.id = id;
		this.myBoard = board;
		this.oppBoard = new Board();
		this.messages = new Vector<String>();
	}

	/**
	 * Calls the setChanged() method on Observable.
	 */
	public void setChanged() {
		super.setChanged();
	}

	/**
	 * Get this player's board where opponent shots and misses are recorded.
	 * 
	 * @return
	 */
	public Board getMyBoard() {
		return this.myBoard;
	}

	/**
	 * Get the current Id of this player.
	 * 
	 * @return
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Get opponent board where this player's shots and misses are recorded.
	 * 
	 * @return
	 */
	public Board getOppBoard() {
		return this.oppBoard;
	}

	/**
	 * Get the current game result.
	 * 
	 * @return
	 */
	public GameResult getGameResult() {
		return gameResult;
	}

	/**
	 * Sets the current game result.
	 * 
	 * @param gameResult
	 */
	public void setGameResult(GameResult gameResult) {
		this.gameResult = gameResult;
	}

	/**
	 * Get whether or not it is this player's turn.
	 * 
	 * @return
	 */
	public boolean isMyTurn() {
		return this.myTurn;
	}

	/**
	 * Set turn. True for this player, false for opponent.
	 * 
	 * @param myTurn
	 */
	public void setMyTurn(boolean myTurn) {
		this.myTurn = myTurn;
		setChanged();
		notifyObservers();
	}

	/**
	 * Returns Vector of message strings to be displayed
	 * 
	 * @return Vector of Strings
	 */
	public Vector<String> getMessages() {
		return messages;
	}

	/**
	 * Adds message to the Vector of message Strings to be displayed
	 * 
	 * @param message
	 */
	public void addMessage(String message) {
		messages.add(message);
	}

	/**
	 * Prints contents of messages Vector to sysout
	 * 
	 */
	public void printMessages() {
		for (int i = 0; i < messages.size(); i++) {
			System.out.println(messages.elementAt(i));
		}
	}

	/**
	 * Empties the messages Vector
	 * 
	 */
	public void resetMessages() {
		messages.clear();
	}
}
