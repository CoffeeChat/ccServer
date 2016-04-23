package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simple easily-removable auto-logger that captures non-command user input and logs/timestamps the boundaries
 * between a given amount of messages. Each log is put into a "msg" directory where the message can be read.
 * @author Matt Imel
 *
 */
class AutoFeedHistory implements Runnable {
	
	/**
	 * The start time when the next log file will be made.
	 */
	private String startTime;
	
	/**
	 * The history of messages sent from 
	 */
	private String[] messageHistory;
	
	/**
	 * Where in messageHistory the next empty array position is; used in conjunction
	 * with messageHistory to load messages.
	 */
	private int next;
	
	/**
	 * An additional amount of spaces that the messageHistory allows
	 * for messages put while file writing is taking place.
	 */
	private int msgBuffer;
	
	/**
	 * The logger itself. 
	 */
	private static AutoFeedHistory logger;
	
	private AutoFeedHistory(int messageMaximum, int messageBuffer) {
		this.msgBuffer = messageBuffer;
		messageHistory = new String[messageMaximum + messageBuffer];
		startTime = new SimpleDateFormat("HH.mm.ss").format(new Date());
		new Thread(this).start();
	}
	
	static AutoFeedHistory getLogger() {
		if(logger == null) {
			logger = new AutoFeedHistory(10, 5);
		}
		return logger;
	}
	
	/**
	 * Sends a message to the autofeed.
	 */
	synchronized void pushMessageToFeed(String msg) {
		messageHistory[next] = msg;
		next++;
	}

	/**
	 * Pushes the entirety of message history to a text file,
	 * with each entry delimited by \r\n.
	 */
	@Override
	public void run() {
		while(true) {
			//Halts thread until next reaches 
			while(next < messageHistory.length - msgBuffer) {
				try {
					Thread.sleep(1000);
				} catch(InterruptedException ie) {
					break;
				}	
			}
			File f = new File("ChatLog[" + startTime + new SimpleDateFormat(" - HH.mm.ss -- MM-dd-yyy").format(new Date()) + "].txt");
			
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(f));
				for(int x = 0; x < next; x++) {
					bw.write(messageHistory[x] + "\r\n");
					messageHistory[x] = null;
				}
				
				next = 0;
				startTime = new SimpleDateFormat("HH.mm.ss").format(new Date());
				
				bw.close();
			} catch(IOException ioe) {
				System.out.println("FileLogger has encountered an error; Logger now stopping.");
				return;
			}
		}
	}
}
