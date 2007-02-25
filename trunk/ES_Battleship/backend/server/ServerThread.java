package backend.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import backend.state.Board;
import backend.util.MsgUtils;

public class ServerThread extends Thread {
	private Socket sock = null;
	private int threadNum;

	public ServerThread(Socket s, int threadNum) {
		sock = s;
		this.threadNum = threadNum;
	}

	public void run() {
		String inputLine = "";
		Server.serverConsole.write("ServerThread: Query received");
		try {
			PrintWriter myOut = (PrintWriter) Server.outputters.get(String.valueOf(threadNum));
			PrintWriter oppOut = null;  //need to initialize after game is ready
			BufferedReader in = new BufferedReader(new InputStreamReader(sock
					.getInputStream()));
			while ((inputLine = in.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(inputLine, "|");
				String playerId;
				switch (Integer.parseInt(st.nextToken())) {
				case 0:
					//format 0|<player id>|<10 rows>...
					Server.serverConsole.write("ServerThread: READY(0) message received");
					playerId = st.nextToken();
					Server.gameEngine.addPlayer(playerId, Board.deserialize(st.nextToken()));
					while (!Server.gameEngine.isGameReady()) continue;
					oppOut = (PrintWriter) Server.outputters.get(getOtherThreadNum());
					MsgUtils.sendTurnMessage(myOut, Server.gameEngine.isMyTurn(playerId));
					break;
				case 1:
					//format 1|<player id>|<x coordinate>|<y coordinate>
					playerId = st.nextToken();
					String x = st.nextToken();
					String y = st.nextToken();
					Server.serverConsole.write("ServerThread: MOVE(1) message received, coordinates: "+x+", "+y);
					MsgUtils.sendIsHitMessage(myOut, Server.gameEngine.move(playerId,x,y));
					MsgUtils.sendMoveNotifyMessage(oppOut, x, y);
					break;
				}
			}
			Server.gameEngine.resetGame();
			myOut.close();
			in.close();
			sock.close();

		} catch (IOException e) {
			Server.serverConsole.write("ServerThread: " + e.getMessage());
		}
	}

	private String getOtherThreadNum() {
		if (threadNum == 1) {
			return "2";
		} else {
			return "1";
		}
	}

}