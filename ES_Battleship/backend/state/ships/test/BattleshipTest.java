package backend.state.ships.test;

import backend.state.ships.Battleship;
import junit.framework.TestCase;

public class BattleshipTest extends TestCase {

	private Battleship bs;

	protected void setUp() throws Exception {
		bs = new Battleship();
	}

	public void testGetName() {
		assertEquals("Wrong ship name", "Battleship", bs.getName());
	}

	public void testGetSize() {
		assertEquals("Wrong ship size", 4, bs.getSize());
	}

	public void testGetSymbol() {
		assertEquals("Wrong ship symbol", 'b', bs.getSymbol());
	}

}
