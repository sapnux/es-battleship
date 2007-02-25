package backend.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.net.SocketFactory;

import backend.server.Server;
import backend.state.Board;
import backend.state.Player;
import backend.util.MsgUtils;

public class Client implements IClient {
	private Player player;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private boolean listening = false;

	/*
	 * 
	 */
	public Client(String id, Board board) {
		player = new Player(id, board);
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
			MsgUtils.sendReadyMessage(out, player.getId(), player.getMyBoard());
			
			String reply = in.readLine();
			System.out.println(reply);
	        StringTokenizer st = new StringTokenizer(reply,"|");
	        if (Integer.parseInt(st.nextToken()) == 3) {
	        	player.setMyTurn(Boolean.parseBoolean(st.nextToken()));
	        }
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
		boolean isHit = false;
		try {
			MsgUtils.sendMoveMessage(out, player.getId(), x, y);
			String reply = in.readLine();
	        System.out.println(reply);
	        StringTokenizer st = new StringTokenizer(reply,"|");
	        isHit = false;
	        if (Integer.parseInt(st.nextToken()) == 2) {
	        	isHit = Boolean.parseBoolean(st.nextToken());
	        	if (isHit) {
	        		player.getOppBoard().setHit(x,y);
	        	} else {
	        		player.getOppBoard().setMiss(x,y);
	        	}
	        	player.setMyTurn(isHit);
	        }
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isHit;
	}

	/*
	 * Returns the current instance of the player.
	 * @see backend.IClient#getBoard()
	 */
	public Player getPlayer() {
		return player;
	}
	
	public void listenForMessages() {
		String inputLine;
		try {
			while (!listening && !player.isMyTurn() && (inputLine = in.readLine()) != null) {
				listening = true;
				StringTokenizer st = new StringTokenizer(inputLine, "|");
				switch (Integer.parseInt(st.nextToken())) {
				case 4:
					//format 4|<x coordinate>|<y coordinate>
					int x = Integer.parseInt(st.nextToken());
					int y = Integer.parseInt(st.nextToken());
					if (player.getMyBoard().isHit(x, y)) {
						player.getMyBoard().setHit(x, y);
						player.setMyTurn(false);
					} else {
						player.getMyBoard().setMiss(x, y);
						player.setMyTurn(true);
					}
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendTestPacket() {
		MsgUtils.sendTestMessage(out);
	}


	
}




