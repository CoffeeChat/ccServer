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
				ServerSocket sSocket = new ServerSocket(80);
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
	
	public static boolean broadcast(String msg, String handle) {
		System.out.println("Broadcasting " + msg + " to " + userList.size() + " people.");
		for(ConnectionHandler ch : userList) {
			ch.serverParse("/z <" + handle + ">: " + msg);
		}
		
		return true;
	}
}
