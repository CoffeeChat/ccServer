package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

public class JServer {
	
	protected static ArrayList<ConnectionHandler> userList = new ArrayList<ConnectionHandler>();
	
	public static void main(String[] args) {
		
		AutoFeedHistory.getLogger();
		
		try {
			Socket cSocket = null;
			while(true) {
				ServerSocket sSocket = new ServerSocket(52682);
				System.out.println("Pending connections on port 52682...");
				cSocket = sSocket.accept();
				System.out.println(cSocket.getInetAddress() + " attempting to connect...");
				Runnable ch = new ConnectionHandler(cSocket);
				userList.add((ConnectionHandler) ch);
				new Thread(ch).start();
				sSocket.close();
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Parses strings that affect anything server-side (names, private chat, etc). 
	 */
	static void serverParse(ConnectionHandler user, String strToParse) {
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
						user.setHandle(messageSplit[1]);
						user.toClient.println("Changed name to " + messageSplit[1] + ".");
					} else {
						user.toClient.println("Name can only contain letters and numbers.");
					}		
				} else {
					user.toClient.println("Did not specify name./nUsage: \"/n <String>\"");
				}
				break;
			
			//Gets number and names of users.
			case "/u":
			case "/users":
				int noOfAnons = 0;
				for(int x = 0; x < JServer.userList.size(); x++) {
					if(JServer.userList.get(x).getHandle().charAt(0) == '~') {
						noOfAnons++;
					} else {
						user.toClient.println(JServer.userList.get(x).getHandle());
					}
				}
				user.toClient.println(noOfAnons + " anonymous user(s).");
				break;

			//Broadcast to all users.
			default:
				JServer.broadcast(strToParse, user.getHandle());
		}
	}
	
	/**
	 * Messages sent via clients.
	 * @param msg
	 * @param handle
	 * @return
	 */
	static boolean broadcast(String msg, String handle) {
		System.out.println("Broadcasting " + msg + " to " + userList.size() + " people.");
		for(ConnectionHandler ch : userList) {
			ch.toClient.println("<" + handle + ">: " + msg);
		}
		AutoFeedHistory.getLogger().pushMessageToFeed("<" + handle + ">: " + msg);
		
		return true;
	}
	
	/**
	 * Messages broadcasted by server.
	 * @param msg
	 * @return
	 */
	static boolean broadcast(String msg) {
		System.out.println("Broadcasting " + msg + " to " + userList.size() + " people.");
		for(ConnectionHandler ch : userList) {
			ch.toClient.println(msg);
		}
		
		return true;
	}
	
	public static String disconnectUser(ConnectionHandler ch) {
		if(userList.remove(ch)) {
			return ch.getHandle();
		} else {
			System.out.println("User " + ch.getHandle() + " was not found in the user list.");
			return "USERNOTFOUND";
		}
	}
}
