package com.nsslawsproject.awsserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ConfigurationListener extends Thread {
	
	private boolean mRun;
	@Override
	public void run() {
		super.run();
		
		try {
			ServerSocket serverSocket = new ServerSocket(5555);
			Socket socket = null;
			BufferedReader buf = null;
			mRun = true;
			String configString;
			while(mRun) {
				socket = serverSocket.accept();
				buf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				configString = buf.readLine();
				AWSServer.setActionItterations(Integer.parseInt(configString));
				System.out.println("Server action iterrations configured to: " + configString);
				buf.close();
				socket.close();
				
				AWSServer.setActionItterations(Integer.parseInt(configString));
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
