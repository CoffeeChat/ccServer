package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

public class ConnectionHandler implements Runnable {

	private Socket sSocket;
	private String handle;
	
	PrintWriter toClient;
	BufferedReader fromClient;
	
	public ConnectionHandler(Socket ss) {
		sSocket = ss;
		handle = "~Anon" + ((Integer) (new Random().nextInt(25565) + 10000)).toString();
		
		try {
			toClient = new PrintWriter(sSocket.getOutputStream(), true);
			fromClient = new BufferedReader(new InputStreamReader(sSocket.getInputStream()));
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {		
			//MotD
			toClient.println("Heya! You are now using this server under a random alias. If you wish to have a name, type \"/n <NAME>\". Enjoy yourself!");
			
			String message = "";
			while((message = fromClient.readLine()) != null) {
				JServer.serverParse(this, message);
			}	
			sSocket.close();
		} catch(SocketException se) {
			System.out.println("Timed out.");
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} finally {
			System.out.println("This happens after a disconnect.");
			String dcUsername = JServer.disconnectUser(this);
			JServer.broadcast("User " + dcUsername + " has disconnected.");
		}
		return;
	}
	
	public String getHandle() {
		return handle;
	}
	
	public void setHandle(String h) {
		handle = h;
	}
}
