package backend.state;

public class Coordinates {
	
	private int x;
	private int y;
	
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
	 * 
	 * @param coordinates
	 * @return
	 */
	public boolean equals(Object obj)
	{
		Coordinates coordinates = (Coordinates)obj;
		return this.x == coordinates.getX() && this.y == coordinates.getY();
	}
	
	/**
	 * 
	 */
	public int hashCode()
	{
		//TODO: this is probably wrong
		return super.hashCode();
	}
	
	/**
	 * 
	 */
	public String toString()
	{
		return "[" + this.x + "," + this.y + "]";
	}
}
