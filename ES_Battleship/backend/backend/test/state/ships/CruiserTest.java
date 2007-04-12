package backend.test.state.ships;

import junit.framework.TestCase;
import backend.state.ships.Cruiser;

public class CruiserTest extends TestCase {

	private Cruiser cr;

	protected void setUp() throws Exception {
		cr = new Cruiser();
	}

	public void testGetName() {
		assertEquals("Wrong ship name", "Cruiser", cr.getName());
	}

	public void testGetSize() {
		assertEquals("Wrong ship size", 3, cr.getSize());
	}

	public void testGetSymbol() {
		assertEquals("Wrong ship symbol", 'c', cr.getSymbol());
	}

}
