package backend.state;

import java.util.ArrayList;
import java.util.List;

import backend.state.ships.IShip;
import backend.state.ships.Ships;
import backend.util.BackendException;

/* Board Symbols:
 *	a = Aircraft Carrier (length of 5)
 *	b = Battleship (length of 4)
 *	c = Cruiser (length of 3)
 *	p = Patrol Boat (length of 2)
 *	s = Submarine (length of 3)
 *	x = shot was a HIT [See Constants.java]
 *  m = shot was a MISS [See Constants.java]
 *  * = empty unit [See Constants.java]
 */

public class Board {
	private char[][] board = new char[10][10];

	/*
	 * Populate the board with empty asteriks.
	 */
	public Board() {
		for (int y = 0; y < this.board.length; y++) {
			for (int x = 0; x < this.board.length; x++) {
				this.board[x][y] = Constants.BOARD_EMPTY;
			}
		}
	}

	/**
	 * Sets specified coordinates as a hit
	 * 
	 * @param x
	 * @param y
	 */
	public void setHit(int x, int y) {
		setCoordinate(Constants.BOARD_HIT, x, y);
	}

	/**
	 * Sets specified coordinates as a miss
	 * 
	 * @param x
	 * @param y
	 */
	public void setMiss(int x, int y) {
		setCoordinate(Constants.BOARD_MISS, x, y);
	}

	/**
	 * Returns true if the coordinates are a HIT.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isHit(int x, int y) {
		if (this.board[x][y] == Constants.BOARD_HIT) {
			return true;
		}

		List<IShip> ships = Ships.getAllShips();
		for (int i = 0; i < ships.size(); i++) {
			IShip ship = ships.get(i);
			if (this.board[x][y] == ship.getSymbol()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Counts the number of hits. If the number of hits is wqual to the sum of
	 * all ship sizes, then we can infer that the player has lost. This method
	 * returns true if the current board has lost, true otherwise.
	 * 
	 * @return
	 */
	public boolean hasLost() {
		int total = 0;
		List<IShip> ships = Ships.getAllShips();
		for (int i = 0; i < ships.size(); i++) {
			IShip ship = ships.get(i);
			total += ship.getSize();
		}
		return (getAllHitCoordinates().size() == total);
	}

	/**
	 * Set the x,y coordinates with the given character.
	 * 
	 * @param c
	 * @param x
	 * @param y
	 */
	public void setCoordinate(char c, int x, int y) {
		this.board[x][y] = c;
	}

	/**
	 * Returns the board symbol at the given coordinates.
	 * 
	 * @param x
	 * @param y
	 * @return character at coordinates x, y
	 */
	public char getCoordinate(int x, int y) {
		return this.board[x][y];
	}

	/**
	 * Returns a list of coordinates occupied by the given ship.
	 * 
	 * @param ship
	 * @return
	 */
	public List<Coordinates> getShipCoordinates(IShip ship) {
		return getCoordinatesByChar(ship.getSymbol());
	}

	/**
	 * Returns a list of MISSED coordinates on the board.
	 * 
	 * @return
	 */
	public List<Coordinates> getAllMissedCoordinates() {
		return getCoordinatesByChar(Constants.BOARD_MISS);
	}

	/**
	 * Returns a list of HIT coordinates on the board.
	 * 
	 * @return
	 */
	public List<Coordinates> getAllHitCoordinates() {
		return getCoordinatesByChar(Constants.BOARD_HIT);
	}

	/**
	 * Returns a list of EMPTY coordinates on the board.
	 * 
	 * @return
	 */
	public List<Coordinates> getAllEmptyCoordinates() {
		return getCoordinatesByChar(Constants.BOARD_EMPTY);
	}

	/**
	 * 
	 * @param c character to compare against
	 * @return a list of Coordinates objects
	 */
	private List<Coordinates> getCoordinatesByChar(char c) {
		List<Coordinates> list = new ArrayList<Coordinates>();
		for (int y = 0; y < this.board.length; y++) {
			for (int x = 0; x < this.board.length; x++) {
				if (this.board[x][y] == c) {
					Coordinates coordinates = new Coordinates(x, y);
					list.add(coordinates);
				}
			}
		}
		return list;
	}

	/**
	 * Get the length (size) of the game board assuming that the board is
	 * square.
	 * 
	 * @return length of the board
	 */
	public int length() {
		return this.board.length;
	}

	/**
	 * Insert the given ship to the board at the given coordinates and
	 * orientation.
	 * 
	 * @param ship
	 * @param coordinates
	 * @param orientation
	 * @throws BackendException
	 */
	public void add(IShip ship, Coordinates coordinates, Orientation orientation)
			throws BackendException {
		add(ship, coordinates.getX(), coordinates.getY(), orientation);
	}

	/**
	 * Insert the given ship to the board at the given coordinates and
	 * orientation.
	 * 
	 * @param ship
	 * @param x
	 * @param y
	 * @param orientation
	 * @throws BackendException
	 */
	public void add(IShip ship, int x, int y, Orientation orientation)
			throws BackendException {
		if (orientation.equals(Orientation.HORIZONTAL)) {
			// place ship horizontally
			for (int i = x; i < x + ship.getSize(); i++) {
				this.place(ship, i, y);
			}
		} else {
			// place ship vertically
			for (int i = y; i < y + ship.getSize(); i++) {
				this.place(ship, x, i);
			}
		}
	}

	/**
	 * Ensures that the coordinates are empty and within the board boundaries
	 * before adding the ship to the game board.
	 * 
	 * @param ship
	 * @param x
	 * @param y
	 * @throws BackendException
	 */
	private void place(IShip ship, int x, int y) throws BackendException {
		if (x > this.board.length - 1 || y > this.board.length - 1) {
			throw new BackendException("The placement of the " + ship.getName()
					+ " is out of bounds.");
		}

		if (this.board[x][y] == Constants.BOARD_EMPTY) {
			this.board[x][y] = ship.getSymbol();
		} else {
			throw new BackendException("The coordinate (" + x + "," + y
					+ ") is already occupied. Cannot place " + ship.getName()
					+ ".");
		}
	}

	/**
	 * Prints out an ASCII version of the current board. Helpful for debugging.
	 *
	 */
	public void print() {
		System.out.println("  0123456789");
		for (int y = 0; y < this.board.length; y++) {
			System.out.print(y + " ");
			for (int x = 0; x < this.board.length; x++) {
				System.out.print(this.board[x][y]);
			}
			System.out.println(" " + y);
		}
		System.out.println("  0123456789");
	}

	/**
	 * 
	 * @param myBoard
	 * @param oppBoard
	 */
	public static void print(Board myBoard, Board oppBoard) {
		System.out.println("--- My Board --|-- Opp Board ---");
		System.out.println("  0123456789   |   0123456789");
		for (int y = 0; y < myBoard.length() && y < oppBoard.length(); y++) {
			System.out.print(y + " ");
			for (int x = 0; x < myBoard.length(); x++) {
				System.out.print(myBoard.getCoordinate(x, y));
			}
			System.out.print(" " + y + " | " + y + " ");
			for (int x = 0; x < oppBoard.length(); x++) {
				System.out.print(oppBoard.getCoordinate(x, y));
			}
			System.out.println(" " + y);
		}
		System.out.println("  0123456789   |   0123456789\n");
	}

	/**
	 * Override the equals method to compare two Board object accurately.
	 */
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		Board board2 = (Board) obj;
		for (int y = 0; y < this.board.length; y++) {
			for (int x = 0; x < this.board.length; x++) {
				if (this.board[x][y] != board2.getCoordinate(x, y)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @return
	 */
	public String serialize() {
		String string = "";
		for (int y = 0; y < this.board.length; y++) {
			for (int x = 0; x < this.board.length; x++) {
				string += this.board[x][y];
			}
		}
		return string;
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	public static Board deserialize(String string) {
		Board board = new Board();
		int strCursor = 0;
		for (int y = 0; y < board.length(); y++) {
			for (int x = 0; x < board.length(); x++) {
				board.setCoordinate(string.charAt(strCursor), x, y);
				strCursor++;
			}
		}
		return board;
	}
}
