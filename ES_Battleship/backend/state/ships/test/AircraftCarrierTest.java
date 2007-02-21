package backend.state.ships.test;

import backend.state.ships.AircraftCarrier;
import junit.framework.TestCase;

public class AircraftCarrierTest extends TestCase {
	
	private AircraftCarrier ac;
	
	protected void setUp() throws Exception {
		ac = new AircraftCarrier();
	}

	public void testGetName() {
		assertEquals("Wrong ship name", "Aircraft Carrier", ac.getName());
	}

	public void testGetSize() {
		assertEquals("Wrong ship size", 5, ac.getSize());
	}

	public void testGetSymbol() {
		assertEquals("Wrong ship symbol", 'a', ac.getSymbol());
	}

}
