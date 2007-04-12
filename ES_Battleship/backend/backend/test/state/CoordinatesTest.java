package backend.test.state;

import junit.framework.TestCase;
import backend.state.Coordinates;

public class CoordinatesTest extends TestCase {

	private Coordinates coordinates;
	
	protected void setUp() {
		coordinates = new Coordinates(5,6);
	}
	
	public void testHashCode() {
		assertEquals("Bad hashCode", 23464, coordinates.hashCode());
	}

	public void testCoordinatesIntInt() {
		Coordinates testCoords = new Coordinates(2,7);
		assertEquals("Bad coordinate initialization", 2, testCoords.getX());
		assertEquals("Bad coordinate initialization", 7, testCoords.getY());
	}

	public void testGetX() {
		assertEquals("Bad coordinate initialization", 5, coordinates.getX());
	}

	public void testSetX() {
		coordinates.setX(7);
		assertEquals("Coordinates set incorrectly", 7, coordinates.getX());
	}

	public void testGetY() {
		assertEquals("Bad coordinate initialization", 6, coordinates.getY());
	}

	public void testSetY() {
		coordinates.setY(9);
		assertEquals("Coordinates set incorrectly", 9, coordinates.getY());
	}

	public void testEqualsObject() {
		Coordinates testCoords = new Coordinates(5,6);
		assertEquals("equals() implementation incorrect", coordinates, testCoords); 
	}

	public void testToString() {
		assertEquals("toString() incorrect", "[5,6]", coordinates.toString());
	}

}
