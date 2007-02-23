package backend.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.net.SocketFactory;

import backend.state.Board;

public class Client implements IClient {
	private Board playerBoard;
	private Board opponentBoard;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;

	/*
	 * 
	 */
	public Client(Board board) {
		this.playerBoard = board;
		this.opponentBoard = new Board();
	}
	
	/*
	 * Open a connection with the server and port specified.
	 * @see backend.IClient#connect(java.lang.String, java.lang.String)
	 */
	public void connect(String server, String port) {
		SocketFactory sf = SocketFactory.getDefault();
		try {
			socket = sf.createSocket(server,Integer.parseInt(port));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Close any open connections with the server.
	 * @see backend.IClient#disconnect()
	 */
	public void disconnect() {
		try {
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Sends coordinates of attack. Returns true if HIT, false otherwise.
	 * @see backend.IClient#move(int, int)
	 */
	public boolean move(int x, int y) {
		boolean isValidMove = false;
		try {
			String pack = "1|"+Integer.toString(x)+"|"+Integer.toString(y);
			out.println(pack);
			String reply = in.readLine();
	        System.out.println(reply);
	        StringTokenizer st = new StringTokenizer(reply,"|");
	        isValidMove = false;
	        if (Integer.parseInt(st.nextToken()) == 2) {
	        	isValidMove = Boolean.parseBoolean(st.nextToken());
	        	System.out.println(isValidMove);
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isValidMove;
	}

	/*
	 * Returns the current instance of the game board.
	 * @see backend.IClient#getBoard()
	 */
	public Board getBoard() {
		return this.playerBoard;
	}
	
	public void sendTestPacket() {
		String pack = "0|test";
		out.println(pack);
	}
	
}




