package backend;

/*
 * Encapsulates the logging framework.
 */
public abstract class Logger {
	/*
	 * Wraps logging of informational messages to console.
	 */
	public static void LogInfo(String message) {
		System.out.println("[INFO] " + message);
	}
	
	/*
	 * Wraps logging of warning messages to console.
	 */
	public static void LogWarning(String message) {
		System.out.println("[WARNING] " + message);
	}
	
	/*
	 * Wraps logging of error messages to console.
	 */
	public static void LogError(String message) {
		System.out.println("[ERROR] " + message);
	}
}
