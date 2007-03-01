package backend.state;

import java.util.ArrayList;
import java.util.List;

import backend.state.ships.IShip;
import backend.state.ships.Ships;
import backend.util.Logger;

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
		for(int i = 0; i < this.board.length; i++)
		{
			for(int j = 0; j < this.board.length; j++)
			{
				this.board[i][j] = Constants.BOARD_EMPTY;
			}
		}
	}

	/**
	 * Sets specified coordinates as a hit 
	 * @param x
	 * @param y
	 */
	public void setHit(int x, int y) {
		setCoordinate(Constants.BOARD_HIT, x, y);
	}
	
	/**
	 * Sets specified coordinates as a miss 
	 * @param x
	 * @param y
	 */
	public void setMiss(int x, int y) {
		setCoordinate(Constants.BOARD_MISS, x, y);
	}
	
	/**
	 * Returns true if the coordinates are a HIT.
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isHit(int x, int y) {
		if(this.board[x][y] == Constants.BOARD_HIT)
		{
			return true;
		}
		
		List<IShip> ships = Ships.getAllShips();
		for(int i = 0; i < ships.size(); i++)
		{
			IShip ship = ships.get(i);
			if(this.board[x][y] == ship.getSymbol())
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Counts the number of hits. If the number of hits is wqual to the sum of
	 * all ship sizes, then we can infer that the player has lost. This method
	 * returns true if the current board has lost, true otherwise.
	 */
	public boolean hasLost() {
		int total = 0;		
		List<IShip> ships = Ships.getAllShips();
		for(int i = 0; i < ships.size(); i++)
		{
			IShip ship = ships.get(i);
			total += ship.getSize();
		}
		return (getAllHitCoordinates().size() == total);
	}
	
	/**
	 * Set the x,y coordinates with the given character.
	 * @param c
	 * @param x
	 * @param y
	 */
	public void setCoordinate(char c, int x, int y) {
		this.board[x][y] = c;
	}
	
	/**
	 * Returns the board symbol at the given coordinates. 
	 * @param x
	 * @param y
	 * @return character at coordinates x, y
	 */
	public char getCoordinate(int x, int y) {
		return this.board[x][y];
	}
	
	/**
	 * Returns a list of coordinates occupied by the given ship.
	 * @param ship
	 * @return 
	 */
	public List<Coordinates> getShipCoordinates(IShip ship) {
		return getCoordinatesByChar(ship.getSymbol());
	}
	
	/**
	 * Returns a list of MISSED coordinates on the board.
	 * @return
	 */
	public List<Coordinates> getAllMissedCoordinates() {
		return getCoordinatesByChar(Constants.BOARD_MISS);
	}
	
	/**
	 * Returns a list of HIT coordinates on the board.
	 * @return
	 */
	public List<Coordinates> getAllHitCoordinates() {
		return getCoordinatesByChar(Constants.BOARD_HIT);
	}
	
	/**
	 * Returns a list of EMPTY coordinates on the board.
	 * @return
	 */
	public List<Coordinates> getAllEmptyCoordinates() {
		return getCoordinatesByChar(Constants.BOARD_EMPTY);
	}
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	private List<Coordinates> getCoordinatesByChar(char c) {
		List<Coordinates> list = new ArrayList<Coordinates>();
		for(int i = 0; i < this.board.length; i++)
		{
			for(int j = 0; j < this.board.length; j++)
			{
				if(this.board[i][j] == c)
				{
					Coordinates coordinates = new Coordinates(i,j);
					list.add(coordinates);
				}
			}
		}
		return list;
	}

	/**
	 * Returns the length (size) of the game board. Assumes that the board
	 * is square.
	 * @return
	 */
	public int length() {
		return this.board.length;
	}
	
	/**
	 * Adds the given ship to the board at the given coordinates and orientation.
	 * @param ship
	 * @param coordinates
	 * @param orientation
	 */
	public void add(IShip ship, Coordinates coordinates, Orientation orientation) {
		add(ship, coordinates.getX(), coordinates.getY(), orientation);
	}
	
	/**
	 * Adds the given ship to the board at the given coordinates and orientation.
	 * @param ship
	 * @param x
	 * @param y
	 * @param vertical
	 */
	public void add(IShip ship, int x, int y, Orientation orientation) {
		Logger.LogInfo("adding " + ship.getName() + " (" + ship.getSize() + ")..");
		int size = ship.getSize();
		int a, b;
		
		// add each ship to the board
		for(int i = x; i < x + size; i++)
		{
			if(orientation.equals(Orientation.HORIZONTAL))
			{
				// place ship horizonally
				a = x;
				b = i;
			}
			else
			{
				// place ship vertically
				a = i;
				b = y;
			}
			
			// validate for out of bounds conditions
			if(Math.abs(a) > this.board.length || Math.abs(b) > this.board.length)
			{
				Logger.LogError("This ship placement results in an OutOfBoundsException.");
				return;
			}
			
			// ensure that the coordinate is empty
			if(this.board[a][b] == Constants.BOARD_EMPTY)
			{
				this.board[a][b] = ship.getSymbol();
			}
			else
			{
				Logger.LogError("The coordinate (" + a + "," + b + ") is already occupied.");
				return;
			}
		}
	}
	
	/*
	 * Prints out an ASCII version of the current board. Helpful for debugging.
	 */
	public void print() {
		System.out.println("  0123456789");
		for(int i = 0; i < this.board.length; i++)
		{
			for(int j = 0; j < this.board.length; j++)
			{
				if(j == this.board.length - 1)
				{
					System.out.print(this.board[i][j] + "]" + i);
				}
				else
				{
					if(j == 0)
					{
						System.out.print(i + "[");
					}
					System.out.print(this.board[i][j]);
				}
			}
			System.out.println();
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
		for(int i = 0; i < myBoard.length() && i < oppBoard.length(); i++)
		{
			System.out.print(i + " ");
			for(int j = 0; j < myBoard.length(); j++)
			{
				System.out.print(myBoard.getCoordinate(i, j));
			}
			System.out.print(" " + i + " | " + i + " ");
			for(int j = 0; j < oppBoard.length(); j++)
			{
				System.out.print(oppBoard.getCoordinate(i, j));
			}
			System.out.println(" " + i);
		}
		System.out.println("  0123456789   |   0123456789\n");
	}
	
	/**
	 * 
	 * @return
	 */
	public String serialize() {
		String string = "";
		for(int i = 0; i < this.board.length; i++)
		{
			for(int j = 0; j < this.board.length; j++)
			{
				string += this.board[i][j];
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
		for (int x = 0; x < board.board.length; x++) {
			for (int y = 0; y < board.board.length; y++) {
				board.setCoordinate(string.charAt(strCursor), x, y);
				strCursor++;
			}
		}
		return board;
	}
}
