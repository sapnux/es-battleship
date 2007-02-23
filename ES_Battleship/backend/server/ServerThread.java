package backend.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

public class ServerThread extends Thread {
	private Socket sock = null;

	public ServerThread(Socket s) {
		sock = s;
	}

	public void run() {
		String inputLine = "";
		Server.serverConsole.write("ServerThread: Query received");
		try {
			PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(sock
					.getInputStream()));
			while ((inputLine = in.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(inputLine, "|");
				switch (Integer.parseInt(st.nextToken())) {
				case 0:
					Server.serverConsole.write("ServerThread: TEST(0) message received");
					break;
				case 1:
					String x = st.nextToken();
					String y = st.nextToken();
					Server.serverConsole.write("ServerThread: MOVE(1) message received, coordinates: "+x+", "+y);
					String pack = "2|true";
					out.println(pack);
					break;
				case 3:
					Server.serverConsole.write("ServerThread: message 3 received");
					break;
				case 5:
					Server.serverConsole.write("ServerThread: message 5 received");
					break;
				}
			}
			out.close();
			in.close();
			sock.close();

		} catch (IOException e) {
			Server.serverConsole.write("ServerThread: " + e.getMessage());
		}
	}

}