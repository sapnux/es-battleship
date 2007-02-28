package backend.state;

public class Coordinates {
	
	private int x;
	private int y;
	
	/**
	 * Creates a new Coordinates class with unintialized x and y coordinates.
	 *
	 */
	public Coordinates() {}
	
	/**
	 * Creates a new Coodinates class with x and y coordinates set.
	 * @param x
	 * @param y
	 */
	public Coordinates(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Get the X coordinate.
	 * @return
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Set the X coordinate.
	 * @param x
	 */
	public void setX(int x) {
		this.x = x;
	}
	
	/**
	 * Get the Y coordinate.
	 * @return
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Set the Y coordinate.
	 * @param y
	 */
	public void setY(int y) {
		this.y = y;
	}
	
	/**
	 * Override the equals(Object) method for the Coordinates class. 
	 * @param coordinates
	 * @return 
	 */
	public boolean equals(Object obj) {
		if(obj == null) { return false;}
		Coordinates coordinates = (Coordinates)obj;
		return this.x == coordinates.getX() && this.y == coordinates.getY();
	}
	
	/**
	 * Overrides the hashCode() for the Coordinates class.
	 */
	public int hashCode() {
		int result = 17;
		result = 37*result + this.x;
		result = 37*result + this.y;
		return result;
	}
	
	/**
	 * Overrides the toString() implementation for the Coordinates class.
	 */
	public String toString() {
		return "[" + this.x + "," + this.y + "]";
	}
}
