package com.nsslawsproject.awsserver;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class PingReceiver extends Thread {
	// the ping receiver is one of the backbone components of the server.
	// it is responsible for answering the liveness test that the aws ELB 
	// sends to each connected EC2 instance.
	// the ping receiver is listening on port 6666 which is different than the 
	// socket use to listen for client connection to seperate the internal and 
	// the external traffic.
	
	public PingReceiver() {
		this.setPriority(Thread.MAX_PRIORITY); // this thread's priority is top priority since it's an essential component of the system
	}
	
	private boolean mRun = true; // keep running the ping receiver while mRun is true
	
	@Override
	public void run() {
		super.run();
		
		try {
			ServerSocket serverSocket = new ServerSocket(6666); // listening on port 6666 for ELB pings
			Socket pingSocket = null;
			BufferedWriter buf = null;
			while(mRun) {
				pingSocket = serverSocket.accept();
				buf = new BufferedWriter(new OutputStreamWriter(pingSocket.getOutputStream()));
				buf.write("ACK\n"); // returning ACK to the ELB although this is not needed...
				buf.flush();
				buf.close();
				pingSocket.close();
			}
			serverSocket.close();
		} catch (IOException e) {
			// in case of an exception the ping receiver will stop
			e.printStackTrace();
		}
	}
	
	public void halt() {
		// halt the ping receiver by changing mRun to false and stopping the main loop
		mRun = false;
	}

}
