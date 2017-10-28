package com.nsslawsproject.awsserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class AWSServer {
	
	private static final int mServerPort = 7777;
	public static final long ServerID = System.currentTimeMillis();
	
	public static void main(String [] args) {
		boolean run = true;
		AtomicInteger threadCounter = new AtomicInteger(0);
		ReadWriteInt updateInterval = new ReadWriteInt(30000);
		ReadWriteInt threadCountLimit = new ReadWriteInt(100);
		
		
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
					if(threadCounter.get() < threadCountLimit.get()) {
						new ClientTask(clientSocket, new TanhAction() ,threadCounter, runningAverage, updateInterval, threadCountLimit).start();
					}
					else {
						new ClientTask(clientSocket, threadCounter, runningAverage, updateInterval, threadCountLimit, true).start();
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
