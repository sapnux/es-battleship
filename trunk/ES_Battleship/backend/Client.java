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
	 * @see backend.IClient#connect(java.lang.String, java.lang.String)
	 */
	public void connect(String server, String port) {
		
	}

	/*
	 * Close any open connections with the server.
	 * @see backend.IClient#disconnect()
	 */
	public void disconnect() {
		
	}

	/*
	 * Sends coordinates of attack. Returns true if HIT, false otherwise.
	 * @see backend.IClient#move(int, int)
	 */
	public boolean move(int x, int y) {
		return true;
	}

	/*
	 * Returns the current instance of the game board.
	 * @see backend.IClient#getBoard()
	 */
	public Board getBoard() {
		return this.board;
	}
}
