package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
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
				serverParse(message);
			}	
			sSocket.close();
		} catch(SocketException se) {
			System.out.println("Timed out.");
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Parses strings that affect anything server-side (names, private chat, etc). 
	 */
	protected void serverParse(String strToParse) {
		String[] messageSplit = strToParse.split(" ");
		String messageWithoutLead;
		StringBuilder sb = new StringBuilder();
		for(String s : Arrays.copyOfRange(messageSplit, 1, messageSplit.length)) {
			sb.append(s + " ");
		}
		messageWithoutLead = sb.toString() + "\b";
		
		System.out.println(messageWithoutLead);
		
		//Note: the first element references the / commands. If it doesn't, it is a normal message.
		switch(messageSplit[0]) {
			case "/n":
			case "/name":
				if(messageSplit.length > 1) {
					if(messageSplit[1].matches("[a-zA-z0-9]+")) {
						this.handle = messageSplit[1];
						toClient.println("Changed name to " + messageSplit[1] + ".");
					} else {
						toClient.println("Name can only contain letters and numbers.");
					}		
				} else {
					toClient.println("Did not specify name./nUsage: \"/n <String>\"");
				}
				break;
			
			//Gets number and names of users.
			case "/u":
			case "/users":
				
				break;
				
			//Private case: used to send a broadcast message.
			case "/z":
				toClient.println(messageWithoutLead);
				break;
				
			//Broadcast to all users.
			default:
				JServer.broadcast(strToParse, this.handle);
		}
	}
}
