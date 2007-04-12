package backend.test.state.ships;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllShipsTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for backend.state.ships");
		//$JUnit-BEGIN$
		suite.addTestSuite(PatrolBoatTest.class);
		suite.addTestSuite(CruiserTest.class);
		suite.addTestSuite(BattleshipTest.class);
		suite.addTestSuite(ShipsTest.class);
		suite.addTestSuite(SubmarineTest.class);
		suite.addTestSuite(AircraftCarrierTest.class);
		//$JUnit-END$
		return suite;
	}

}
