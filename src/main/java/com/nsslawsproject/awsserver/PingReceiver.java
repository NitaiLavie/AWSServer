package com.nsslawsproject.awsserver;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class PingReceiver extends Thread {
	
	private boolean mRun = true;
	
	@Override
	public void run() {
		super.run();
		
		try {
			ServerSocket serverSocket = new ServerSocket(6666);
			Socket pingSocket = null;
			BufferedWriter buf = null;
			while(mRun) {
				pingSocket = serverSocket.accept();
				buf = new BufferedWriter(new OutputStreamWriter(pingSocket.getOutputStream()));
				buf.write("ACK\n");
				buf.flush();
				buf.close();
				pingSocket.close();
			}
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void halt() {
		mRun = false;
	}

}
