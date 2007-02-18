package backend.Ships;

public class AircraftCarrier implements IShip {
	
	/*
	 * Returns the friendly name of the ship.
	 * @see backend.IShip#getName()
	 */
	public String getName() {
		return "Aircraft Carrier";
	}
	
	/*
	 * Returns the length of the ship.
	 * @see backend.IShip#getSize()
	 */
	public int getSize() {
		return 5;
	}
	
	/*
	 * Returns the symbol to represent each unit of the ship.
	 * @see backend.IShip#getSymbol()
	 */
	public String getSymbol() {
		return "a";
	}
}
