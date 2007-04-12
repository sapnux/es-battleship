package backend.test.util;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllUtilTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for backend.util");
		//$JUnit-BEGIN$
		suite.addTestSuite(BackendExceptionTest.class);
		suite.addTestSuite(MsgUtilsTest.class);
		//$JUnit-END$
		return suite;
	}

}
