package com.feerbox.client.registers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

public class KismetClient implements Runnable {
	// Declare variables
	final private String host;
	final private int port;
	private Socket socket;
	private BufferedReader fromServer;
	private BufferedWriter toServer;
	private  PrintWriter log = null;

	public KismetClient() {
		this.port = 2501;
		this.host = "localhost";
		try {
			this.log = new PrintWriter(new File("kismet.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public KismetClient(String h, int p) {
		this.port = p;
		this.host = h;
	}

	public boolean connectToServer() {
		try {
			// Open a socket connection. Think of this as the door that lets the
			// kismet data enter our program
			socket = new Socket(this.host, this.port);
			// Create BufferReader/Writer for talking with Kismet Server
			fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			toServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			// Announce connection to console
			log.println("Connected to " + this.host + ":" + this.port);
			// Send Kismet commands
			toServer.write("!0 REMOVE TIME\r\n");
			//toServer.write("!0 ENABLE BSSID bssid,channel,type\r\n");
			toServer.write("!0 ENABLE CLIENT mac,signal_dbm\r\n");
			// Flush the output stream
			toServer.flush();
			// Begin a new thread
			Thread thread = new Thread(this);
			thread.start();
			// Respond with valid connection
			return true;
		} catch (IOException ex) {
			// Respond with invalid connection
			log.println("Cannot connect to kismet server, it is probably down.");
			return false;
		}
	}

	public void disconnectFromServer() {
		try {
			// Close the socket connection
			socket.close();
			log.println("You disconnected from the local server.");
		} catch (Exception ex) {
			log.println(ex);
		}
	}


	@Override
	public void run() {
		try {
			while (true) {
				// grab next kismet message
				String kismetData = fromServer.readLine();
				log.println(kismetData);
			}
		} catch (Exception ex) {
			log.println(ex);
		}
	}
}
