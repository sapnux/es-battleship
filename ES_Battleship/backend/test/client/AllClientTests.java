package backend.test.client;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllClientTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for backend.client");
		//$JUnit-BEGIN$
		suite.addTestSuite(ClientTest.class);
		//$JUnit-END$
		return suite;
	}

}
