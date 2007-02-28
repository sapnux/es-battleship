package backend.test.state.ships;

import junit.framework.TestCase;
import backend.state.ships.*;

public class ShipsTest extends TestCase {

	public void testGetAircraftCarrier() {
		IShip ship =  Ships.GetAircraftCarrier();
		assertEquals("Wrong ship class instance", AircraftCarrier.class, ship.getClass());
	}

	public void testGetBattleship() {
		IShip ship =  Ships.GetBattleship();
		assertEquals("Wrong ship class instance", Battleship.class, ship.getClass());
	}

	public void testGetCruiser() {
		IShip ship =  Ships.GetCruiser();
		assertEquals("Wrong ship class instance", Cruiser.class, ship.getClass());
	}

	public void testGetPatrolBoat() {
		IShip ship =  Ships.GetPatrolBoat();
		assertEquals("Wrong ship class instance", PatrolBoat.class, ship.getClass());
	}

	public void testGetSubmarine() {
		IShip ship =  Ships.GetSubmarine();
		assertEquals("Wrong ship class instance", Submarine.class, ship.getClass());
	}

}
