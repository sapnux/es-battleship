package backend.util;

public class BackendException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 *
	 */
	public BackendException() {
	}

	/**
	 * 
	 * @param message
	 */
	public BackendException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param cause
	 */
	public BackendException(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public BackendException(String message, Throwable cause) {
		super(message, cause);
	}

}
