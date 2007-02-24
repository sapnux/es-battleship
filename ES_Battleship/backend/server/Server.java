package backend.server;

import java.io.IOException;
import java.net.ServerSocket;

import javax.net.ServerSocketFactory;

import backend.engine.GameEngine;

public class Server {
	
	protected static int com_port;  //port for the server to listen/respond on
	protected static ServerConsole serverConsole;
	protected static GameEngine gameEngine;
	
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
			
			
        	while (true) {
        		new ServerThread(com_sock.accept()).start();
        	}
        	
		} catch (IOException e) {
			serverConsole.write("Server: Exception " + e);
      		System.out.println("Exception " + e);
    	}
	}
	
}