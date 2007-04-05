package frontend.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	
	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for Frontend");
		//$JUnit-BEGIN$
		suite.addTestSuite(EsbArrangementWindowTest.class);
		suite.addTestSuite(EsbBattleWindowTest.class);
		// $JUnit-END$
		return suite;
	}
}
