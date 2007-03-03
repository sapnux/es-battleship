package backend.test.engine;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllEngineTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for backend.engine");
		//$JUnit-BEGIN$
		suite.addTestSuite(GameEngineTest.class);
		//$JUnit-END$
		return suite;
	}

}
