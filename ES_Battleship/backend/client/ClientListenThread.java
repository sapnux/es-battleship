package backend.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

public class ClientListenThread extends Thread {
	private Socket sock = null;
	private boolean stop = false;

	public ClientListenThread(Socket s) {
		sock = s;
	}

	public void stopListener() {
		stop = true;
	}
	
	public void run() {
		String inputLine = "";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(sock
					.getInputStream()));
			while (!stop) {
				inputLine = in.readLine();
				if (inputLine == null) continue;
				System.out.println("from CLS: "+inputLine);
				StringTokenizer st = new StringTokenizer(inputLine, "|");
				switch (Integer.parseInt(st.nextToken())) {
				case 2:
					break;
				case 1:
					break;
				case 3:
					System.out.println("received message");
					break;
				}
			}
			in.close();
			sock.close();

		} catch (IOException e) {
			
		}
	}
}
