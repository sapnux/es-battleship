package backend.state;

import backend.state.ships.IShip;
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
	public Board()
	{
		for(int i = 0; i < this.board.length; i++)
		{
			for(int j = 0; j < this.board.length; j++)
			{
				this.board[i][j] = Constants.BOARD_EMPTY;
			}
		}
	}

	/**
	 * Returns the board symbol at the given coordinates. 
	 * @param x
	 * @param y
	 * @return character at coordinates x, y
	 */
	public char getCoordinate(int x, int y)
	{
		return this.board[x][y];
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
		
		for(int i = x; i < x + size; i++)
		{
			if(orientation.equals(Orientation.Horizontal))
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
			
			// output coordinates
			//System.out.println("[" + a + "," + b + "]");
			
			if(this.board[a][b] == Constants.BOARD_EMPTY)
			{
				this.board[a][b] = ship.getSymbol();
			}
		}
	}
	
	/*
	 * Prints out an ASCII version of the current board. Helpful for debugging.
	 */
	public void print()
	{
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
}
