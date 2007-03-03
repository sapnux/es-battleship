package backend.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import backend.test.client.AllClientTests;
import backend.test.engine.AllEngineTests;
import backend.test.server.AllServerTests;
import backend.test.state.AllStateTests;
import backend.test.util.AllUtilTests;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for backend");
		//$JUnit-BEGIN$
		suite.addTest(AllStateTests.suite());
		suite.addTest(AllEngineTests.suite());
		suite.addTest(AllServerTests.suite());
		suite.addTest(AllClientTests.suite());
		suite.addTest(AllUtilTests.suite());
		// $JUnit-END$
		return suite;
	}

}
