package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class JServer {
	
	protected static ArrayList<ConnectionHandler> userList = new ArrayList<ConnectionHandler>();
	
	public static void main(String[] args) {
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
	 * Broadcasts a server message to all users.
	 * @param msg
	 * @return
	 */
	public static boolean broadcast(String msg) {
		System.out.println("Broadcasting " + msg + " to " + userList.size() + " people.");
		for(ConnectionHandler ch : userList) {
			ch.serverParse("/z <*Server>: " + msg);
		}
		
		return true;
	}
	
	public static boolean broadcast(String msg, String handle) {
		System.out.println("Broadcasting " + msg + " to " + userList.size() + " people.");
		for(ConnectionHandler ch : userList) {
			ch.serverParse("/z <" + handle + ">: " + msg);
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
