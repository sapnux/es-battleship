package backend.test.state.ships;

import junit.framework.TestCase;
import backend.state.ships.*;

public class ShipsTest extends TestCase {

	public void testGetAircraftCarrier() {
		IShip ship =  Ships.getAircraftCarrier();
		assertEquals("Wrong ship class instance", AircraftCarrier.class, ship.getClass());
	}

	public void testGetBattleship() {
		IShip ship =  Ships.getBattleship();
		assertEquals("Wrong ship class instance", Battleship.class, ship.getClass());
	}

	public void testGetCruiser() {
		IShip ship =  Ships.getCruiser();
		assertEquals("Wrong ship class instance", Cruiser.class, ship.getClass());
	}

	public void testGetPatrolBoat() {
		IShip ship =  Ships.getPatrolBoat();
		assertEquals("Wrong ship class instance", PatrolBoat.class, ship.getClass());
	}

	public void testGetSubmarine() {
		IShip ship =  Ships.getSubmarine();
		assertEquals("Wrong ship class instance", Submarine.class, ship.getClass());
	}

}
