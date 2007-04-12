package backend.state.ships;

public class Battleship implements IShip {
	
	/*
	 * Returns the friendly name of the ship.
	 * @see backend.IShip#getName()
	 */
	public String getName() {
		return "Battleship";
	}
	
	/*
	 * Returns the length of the ship.
	 * @see backend.IShip#getSize()
	 */
	public int getSize() {
		return 4;
	}
	
	/*
	 * Returns the symbol to represent each unit of the ship.
	 * @see backend.IShip#getSymbol()
	 */
	public char getSymbol() {
		return 'b';
	}
}
