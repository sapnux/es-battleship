package backend.test.state;

import backend.test.state.ships.AllShipsTests;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllStateTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for backend.state");
		//$JUnit-BEGIN$
		suite.addTestSuite(BoardTest.class);
		suite.addTest(AllShipsTests.suite());
		suite.addTestSuite(PlayerTest.class);
		suite.addTestSuite(CoordinatesTest.class);
		//$JUnit-END$
		return suite;
	}

}
