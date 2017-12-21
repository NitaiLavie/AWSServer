package com.nsslawsproject.awsserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class AWSServer {
	// this is the port we use to communicate with the traffic generator
	private static final int mServerPort = 7777;
	// each server gets a unique id determined to be the time the server has started
	public static final long ServerID = System.currentTimeMillis();
	
	public static void main(String [] args) {
		boolean run = true; // when true server is running, when false server will stop
		AtomicInteger threadCounter = new AtomicInteger(0); // holds the current amount of client threads in the system
		ReadWriteInt updateInterval = new ReadWriteInt(30000); // the interval between metric writes to aws cloud watch
		ReadWriteInt threadCountLimit = new ReadWriteInt(100); // the maximum amount of thread allowed in the server
		
		
		PingReceiver pinger = new PingReceiver(); // the component responsible for answering the aws ELB ping messages
		pinger.start();
		
		RunningAverageCalculator runningAverage = new RunningAverageCalculator(threadCounter); // the component responsible for calculating the running average of the thread count
		runningAverage.start();
		
		AwsCloudWatchUpdater updater = new AwsCloudWatchUpdater(runningAverage, updateInterval); // the component responsible for sending the running average to the aws cloud watch
		boolean connected = false; // before the server gets any traffic from the traffic generator this variable is false
		
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
			try {
				serverSocket = new ServerSocket(mServerPort);
				while(run) {
					clientSocket = serverSocket.accept();
					if(!connected) {
						updater.start(); // we start updating the aws could watch only after getting traffic for the first time
						connected = true;
					}
					if(threadCounter.get() < threadCountLimit.get()) {
						// in case the number of threads is lower than the limit start a valid client task
						new ClientTask(clientSocket, new TanhAction() ,threadCounter, runningAverage, updateInterval, threadCountLimit).start();
					}
					else {
						// else run a degenerated client task that rejects the task immediately
						new ClientTask(clientSocket, threadCounter, runningAverage, updateInterval, threadCountLimit, true).start();
					}
				}
			} catch (IOException e) {
				// in case there is an IO exception - continue to the next round
				e.printStackTrace();
			}
			finally {
				// at the end - close all connection and stop all server components
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
					// if can't close maybe they are already closed
					e.printStackTrace();
				}
			}
	}
	
}
