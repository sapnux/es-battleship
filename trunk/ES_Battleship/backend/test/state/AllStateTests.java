package backend.test.state;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllStateTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for backend.state");
		//$JUnit-BEGIN$
		suite.addTestSuite(BoardTest.class);
		//$JUnit-END$
		return suite;
	}

}
