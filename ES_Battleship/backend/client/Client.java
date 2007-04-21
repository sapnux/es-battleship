package backend.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.net.SocketFactory;

import backend.constants.GameResult;
import backend.state.Board;
import backend.state.Player;
import backend.util.Logger;
import backend.util.MsgUtils;

public class Client implements IClient, Runnable {
	private Player player;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private boolean connected;
	private boolean listening = true;
	private String mServer, mPort;

	public Client(String id, Board board) {
		this.player = new Player(id, board);
	}

	/**
	 * Open a connection with the server and port specified.
	 * 
	 * @see backend.IClient#connect(java.lang.String, java.lang.String)
	 */
	public void connect(String server, String port) {
		mServer = server;
		mPort = port;
	}

	public void run(){
		signalReadiness();
		
		while(this.connected) {
			if (!this.player.isMyTurn()) {
				this.waitForTurn();
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Logger.LogError(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void signalReadiness() {
		SocketFactory sf = SocketFactory.getDefault();
		
		try {
			socket = sf.createSocket(mServer, Integer.parseInt(mPort));
			in = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			MsgUtils.sendReadyMessage(out, this.player.getId(), this.player
					.getMyBoard());

			String reply = in.readLine();
			StringTokenizer st = new StringTokenizer(reply, "|");
			if (Integer.parseInt(st.nextToken()) == 3) {
				this.player.setMyTurn(Boolean.parseBoolean(st.nextToken()));
			}
			this.connected = true;
		} catch (Exception e) {
			Logger.LogError(e.getMessage());
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
			this.connected = false;
		} catch (IOException e) {
			Logger.LogError(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Sends coordinates of attack. Returns true if HIT, false otherwise.
	 * 
	 * @see backend.IClient#move(int, int)
	 */
	public void move(int x, int y) {
		boolean isHit = false;
		try {
			MsgUtils.sendMoveMessage(out, this.player.getId(), x, y);
			String reply = in.readLine();
			StringTokenizer st = new StringTokenizer(reply, "|");
			isHit = false;
			switch (Integer.parseInt(st.nextToken())) {
			case 2:
				isHit = Boolean.parseBoolean(st.nextToken());
				if (isHit) {
					this.player.addMessage("You hit at: "+x+", " +y);
					this.player.addMessage("Your turn");
					this.player.getOppBoard().setHit(x, y);
				} else {
					this.player.addMessage("You missed at: "+x+", " +y);
					this.player.addMessage("Opponent's turn");
					this.player.getOppBoard().setMiss(x, y);
				}
				this.player.setMyTurn(isHit);
				break;
			case 5:
				this.player.getOppBoard().setHit(x, y);
				this.player.addMessage("You hit at: "+x+", " +y);
				this.player.setGameResult(GameResult.WIN);
				this.player.addMessage("[DONE] You are the WINNER!");
				this.player.setChanged();
				break;
			}

		} catch (IOException e) {
			Logger.LogError(e.getMessage());
			e.printStackTrace();
		}
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
			listening = true;
			while (listening && !this.player.isMyTurn()
					&& (inputLine = in.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(inputLine, "|");
				int x, y;
				switch (Integer.parseInt(st.nextToken())) {
				case 4:
					// format 4|<x coordinate>|<y coordinate>
					x = Integer.parseInt(st.nextToken());
					y = Integer.parseInt(st.nextToken());
					if (this.player.getMyBoard().isHit(x, y)) {
						this.player.getMyBoard().setHit(x, y);
						this.player.addMessage("Opponent hit at: "+x+", " +y);
						this.player.addMessage("Opponent's turn");
						this.player.setMyTurn(false);
					} else {
						this.player.getMyBoard().setMiss(x, y);
						this.player.addMessage("Opponent missed at: "+x+", " +y);
						this.player.addMessage("Your turn");
						this.player.setMyTurn(true);
						listening = false;
					}
					break;
				case 5:
					x = Integer.parseInt(st.nextToken());
					y = Integer.parseInt(st.nextToken());
					this.player.getMyBoard().setHit(x, y);
					this.player.addMessage("Opponent hit at: "+x+", " +y);
					this.player.setGameResult(GameResult.LOSS);
					this.player.addMessage("[DONE] You have LOST!");
					this.player.setChanged();
					this.player.notifyObservers();
					break;
				}
			}
		} catch (IOException e) {
			Logger.LogError(e.getMessage());
			e.printStackTrace();
		}
	}

}
