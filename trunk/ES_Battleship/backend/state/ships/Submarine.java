package backend.state.ships;

public class Submarine implements IShip {
	
	/*
	 * Returns the friendly name of the ship.
	 * @see backend.IShip#getName()
	 */
	public String getName() {
		return "Submarine";
	}
	
	/*
	 * Returns the length of the ship.
	 * @see backend.IShip#getSize()
	 */
	public int getSize() {
		return 3;
	}
	
	/*
	 * Returns the symbol to represent each unit of the ship.
	 * @see backend.IShip#getSymbol()
	 */
	public char getSymbol() {
		return 's';
	}
}
