package backend;

public class Client implements IClient {
	private Board board;

	/*
	 * 
	 */
	public Client(Board board) {
		this.board = board;
	}
	
	/*
	 * Open a connection with the server and port specified.
	 */
	public void connect(String server, String port) {
		
	}

	/*
	 * Close any connections with the server.
	 */
	public void disconnect() {
		
	}

	/*
	 * Sends coordinates of attack. Returns true if HIT, false otherwise.
	 */
	public boolean move(int x, int y) {
		return true;
	}

	/*
	 * Returns the current instance of the game board.
	 */
	public Board getBoard() {
		return this.board;
	}
}
