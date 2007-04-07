package backend.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import javax.net.ServerSocketFactory;

import backend.engine.GameEngine;

public class Server {
	
	protected static int com_port;  //port for the server to listen/respond on
	protected static ServerConsole serverConsole;
	protected static GameEngine gameEngine;
	protected static HashMap<String, PrintWriter> outputters = new HashMap<String, PrintWriter>(2);
	
	public static void main (String args[]) {
		com_port = Integer.parseInt(args[0]);
		gameEngine = new GameEngine();
		serverConsole = new ServerConsole();
		serverConsole.pack();
        serverConsole.setLocation(200,150);
        serverConsole.setVisible(true);
		try {
    		ServerSocketFactory sf = ServerSocketFactory.getDefault();
			ServerSocket com_sock = sf.createServerSocket(com_port);
			
			int numConnections = 0;
        	while (numConnections < 2) {
        		Socket tempSock = com_sock.accept();
        		numConnections++;
        		PrintWriter out = new PrintWriter(tempSock.getOutputStream(), true);
        		outputters.put(String.valueOf(numConnections), out);
        		new ServerThread(tempSock, numConnections).start();
        	}
        	
		} catch (IOException e) {
			serverConsole.write("Server: Exception " + e);
      		System.out.println("Exception " + e);
    	}
	}
	
}