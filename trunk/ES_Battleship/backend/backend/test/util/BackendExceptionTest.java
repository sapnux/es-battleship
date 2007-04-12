package backend.test.util;

import junit.framework.TestCase;
import backend.util.BackendException;

public class BackendExceptionTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testBackendException() {
		try
		{
		throw new BackendException();
		}
		catch(BackendException ex) {
			// do nothing, this is a pass
		}
		catch(Exception ex){
			fail("The exception was not of type BackendException.");
		}
	}

	public void testBackendExceptionString() {
		String message = "This is a nice exception string";
		try
		{
		throw new BackendException(message);
		}
		catch(BackendException ex) {
			if(!ex.getMessage().equals(message)) {
				fail("The BackendException message was not expected: " + message);
			}
		}
		catch(Exception ex){
			fail("The exception was not of type BackendException.");
		}
	}

	public void testBackendExceptionThrowable() {
		String message = "This is a nice exception string";
		try
		{
		throw new BackendException(message);
		}
		catch(BackendException ex) {
			if(!ex.getMessage().equals(message)) {
				fail("The BackendException message was not expected: " + message);
			}
		}
		catch(Exception ex){
			fail("The exception was not of type BackendException.");
		}
	}

	public void testBackendExceptionStringThrowable() {
		String message = "This is a nice exception string";
		try
		{
		throw new BackendException(message);
		}
		catch(BackendException ex) {
			if(!ex.getMessage().equals(message)) {
				fail("The BackendException message was not expected: " + message);
			}
		}
		catch(Exception ex){
			fail("The exception was not of type BackendException.");
		}
	}

}
