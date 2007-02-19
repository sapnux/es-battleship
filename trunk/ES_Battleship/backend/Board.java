package backend;

import backend.Ships.IShip;

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
		for(int i = 0; i < this.board.length - 1; i++)
		{
			for(int j = 0; j < this.board.length - 1; j++)
			{
				this.board[i][j] = Constants.BOARD_EMPTY;
			}
		}
		this.print();
	}
	
	/*
	 * Adds the given ship to the board at the given coordinates and orientation.
	 */
	public void add(IShip ship, int x, int y)
	{
		Logger.LogInfo("adding " + ship.getName() + "..");
		
		if(this.board[x][y] == Constants.BOARD_EMPTY)
		{
			
		}
	}
	
	/*
	 * Prints out an ASCII version of the current board. Helpful for debugging.
	 */
	public void print()
	{
		for(int i = 0; i < this.board.length - 1; i++)
		{
			for(int j = 0; j < this.board.length - 1; j++)
			{
				if(j == 0)
				{
					System.out.print("[");
				}
				else if(j == this.board.length - 2)
				{
					System.out.print(this.board[i][j] + "]");
				}
				else
				{
					System.out.print(this.board[i][j] + ",");
				}
			}
			System.out.println();
		}
	}
}
