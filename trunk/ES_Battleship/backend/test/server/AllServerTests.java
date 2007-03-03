package backend.test.server;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllServerTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for backend.server");
		//$JUnit-BEGIN$
		suite.addTestSuite(ServerThreadTest.class);
		suite.addTestSuite(ServerTest.class);
		//$JUnit-END$
		return suite;
	}

}
