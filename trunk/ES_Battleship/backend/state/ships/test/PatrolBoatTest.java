package backend.state.ships.test;

import junit.framework.TestCase;
import backend.state.ships.PatrolBoat;

public class PatrolBoatTest extends TestCase {

	private PatrolBoat pb;

	protected void setUp() throws Exception {
		pb = new PatrolBoat();
	}

	public void testGetName() {
		assertEquals("Wrong ship name", "Patrol Boat", pb.getName());
	}

	public void testGetSize() {
		assertEquals("Wrong ship size", 2, pb.getSize());
	}

	public void testGetSymbol() {
		assertEquals("Wrong ship symbol", 'p', pb.getSymbol());
	}

}
