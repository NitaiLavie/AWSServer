package com.nsslawsproject.awsserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class AWSServer {
	
	private static final int mServerPort = 7777;
	private static final int mTreadCountLimit = 100;
	private static AtomicInteger mActionItterations = new AtomicInteger((int) 2e6);
	
	public static void setActionItterations(int itterations) {
		mActionItterations.set(itterations);
	}
	
	public static void main(String [] args) {
		boolean run = true;
		AtomicInteger threadCounter = new AtomicInteger(0);
		ReadWriteInt updateInterval = new ReadWriteInt(30000);
		
		PingReceiver pinger = new PingReceiver();
		pinger.start();
		
		RunningAverageCalculator runningAverage = new RunningAverageCalculator(threadCounter);
		runningAverage.start();
		
		AwsCloudWatchUpdater updater = new AwsCloudWatchUpdater(runningAverage, updateInterval);
		boolean connected = false;
		
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
			try {
				serverSocket = new ServerSocket(mServerPort);
				while(run) {
					clientSocket = serverSocket.accept();
					if(!connected) {
						updater.start();
						connected = true;
					}
					if(threadCounter.get() <= mTreadCountLimit) {
						new ClientTask(clientSocket, new StressAction() ,threadCounter, updateInterval).start();
					}
					else {
						new ClientTask(clientSocket, threadCounter, updateInterval, true).start();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				pinger.halt();
				updater.halt();
				runningAverage.halt();
				try {
					if(serverSocket!=null) {
						serverSocket.close();
					}
					if(clientSocket!=null) {
						clientSocket.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	
}
