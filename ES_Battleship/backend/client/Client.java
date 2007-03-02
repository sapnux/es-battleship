package backend.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.net.SocketFactory;

import backend.state.Board;
import backend.state.GameResult;
import backend.state.Player;
import backend.util.MsgUtils;

public class Client implements IClient {
	private Player player;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private boolean listening = true;

	public Client(String id, Board board) {
		this.player = new Player(id, board);
	}

	/**
	 * Open a connection with the server and port specified.
	 * 
	 * @see backend.IClient#connect(java.lang.String, java.lang.String)
	 */
	public void connect(String server, String port) {
		SocketFactory sf = SocketFactory.getDefault();
		try {
			socket = sf.createSocket(server, Integer.parseInt(port));
			in = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			MsgUtils.sendReadyMessage(out, this.player.getId(), this.player
					.getMyBoard());

			String reply = in.readLine();
			System.out.println(reply);
			StringTokenizer st = new StringTokenizer(reply, "|");
			if (Integer.parseInt(st.nextToken()) == 3) {
				this.player.setMyTurn(Boolean.parseBoolean(st.nextToken()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Close any open connections with the server.
	 * 
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

	/**
	 * Sends coordinates of attack. Returns true if HIT, false otherwise.
	 * 
	 * @see backend.IClient#move(int, int)
	 */
	public boolean move(int x, int y) {
		boolean isHit = false;
		try {
			MsgUtils.sendMoveMessage(out, this.player.getId(), x, y);
			String reply = in.readLine();
			System.out.println(reply);
			StringTokenizer st = new StringTokenizer(reply, "|");
			isHit = false;
			switch (Integer.parseInt(st.nextToken())) {
			case 2:
				isHit = Boolean.parseBoolean(st.nextToken());
				if (isHit) {
					this.player.getOppBoard().setHit(x, y);
				} else {
					this.player.getOppBoard().setMiss(x, y);
				}
				this.player.setMyTurn(isHit);
				break;
			case 5:
				this.player.getOppBoard().setHit(x, y);
				this.player.setGameResult(GameResult.WIN);
				this.player.setChanged();
				this.player.notifyObservers();
				break;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isHit;
	}

	/**
	 * Returns the current instance of the player.
	 * 
	 * @see backend.IClient#getBoard()
	 */
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * Listens until a message is received from the server.
	 * 
	 * @see backend.client.IClient#waitForTurn()
	 */
	public void waitForTurn() {
		String inputLine;
		try {
			while (listening && !this.player.isMyTurn()
					&& (inputLine = in.readLine()) != null) {
				listening = true;
				System.out.println(inputLine);
				StringTokenizer st = new StringTokenizer(inputLine, "|");
				int x, y;
				switch (Integer.parseInt(st.nextToken())) {
				case 4:
					// format 4|<x coordinate>|<y coordinate>
					x = Integer.parseInt(st.nextToken());
					y = Integer.parseInt(st.nextToken());
					if (this.player.getMyBoard().isHit(x, y)) {
						this.player.getMyBoard().setHit(x, y);
						this.player.setMyTurn(false);
					} else {
						this.player.getMyBoard().setMiss(x, y);
						this.player.setMyTurn(true);
						listening = false;
					}
					break;
				case 5:
					x = Integer.parseInt(st.nextToken());
					y = Integer.parseInt(st.nextToken());
					this.player.getMyBoard().setHit(x, y);
					this.player.setGameResult(GameResult.LOSS);
					this.player.setChanged();
					this.player.notifyObservers();
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
