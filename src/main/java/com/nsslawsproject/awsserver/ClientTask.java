package com.nsslawsproject.awsserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientTask extends Thread{
	private Socket mClientSocket; // the socket the client connect to - getting when constructed
	private ServerAction mAction; // the action that is t be done - the "task"
	private AtomicInteger mThreadCounter; // the server's thread counter - bring updated by all the client tasks
	private boolean mReject; // when false - a regular client task, when true - a degenerated version the rejects the task
	private int mCores; // number of cores to stress on - received from the client
	private int mTimeout; // stress timeout - received from the client
	private int mIterations; // the number of iteration to run - received from the client
	private ReadWriteInt mUpdateInterval; // the update interval to send metrics to aws cloud - received from the client
	private ReadWriteInt mLimit;
	private RunningAverageCalculator mRunningAverage;
	
	public ClientTask(Socket clientSocket, AtomicInteger threadCounter, RunningAverageCalculator runningAverage, ReadWriteInt interval, ReadWriteInt limit) {
		this(clientSocket, threadCounter, runningAverage, interval, limit, false);
	}
	
	public ClientTask(Socket clientSocket, AtomicInteger threadCounter, RunningAverageCalculator runningAverage, ReadWriteInt interval, ReadWriteInt limit, boolean reject) {
		 this(clientSocket, new TanhAction(), threadCounter, runningAverage, interval, limit, reject);
	}
	
	public ClientTask(Socket clientSocket, ServerAction serverAction, AtomicInteger threadCounter, RunningAverageCalculator runningAverage, ReadWriteInt interval, ReadWriteInt limit) {
		this(clientSocket, serverAction, threadCounter, runningAverage, interval, limit, false);
	}
	
	public ClientTask(Socket clientSocket, ServerAction serverAction, AtomicInteger threadCounter, RunningAverageCalculator runningAverage, ReadWriteInt interval, ReadWriteInt limit, boolean reject) {
		 mClientSocket = clientSocket;
		 mAction = serverAction;
		 mThreadCounter = threadCounter;
		 mReject = reject;
		 mUpdateInterval = interval;
		 mLimit = limit;
		 mRunningAverage = runningAverage;
		 this.setPriority(Thread.MIN_PRIORITY); // client task thread are set to the lowest priority since they are the less important that server backbone threads
	}
	

	public void setAction(ServerAction serverAction) {
		// setting the type of task to run
		mAction = serverAction;
	}

	@Override
	public void run() {
		super.run();
		if(!mReject) {
			// the task is accepted!
			int threadCount = mThreadCounter.incrementAndGet(); // a new thread has started - increase thread count
			BufferedWriter buffWrite = null;
			BufferedReader buffRead = null;
			
			try {
				buffWrite = new BufferedWriter(new OutputStreamWriter(mClientSocket.getOutputStream()));
				buffRead = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
				
				// getting parameters from the client - this is to let us to configure the server from the client side
				mCores = Integer.parseInt(buffRead.readLine());
				mTimeout = Integer.parseInt(buffRead.readLine());
				mIterations = Integer.parseInt(buffRead.readLine());
				mUpdateInterval.set(Integer.parseInt(buffRead.readLine()));
				mLimit.set(Integer.parseInt(buffRead.readLine()));
				
				// run the client task
				mAction.execute(mCores, mTimeout, mIterations);
				
				// sending a response back to the client with important information
				buffWrite.write("done,"+AWSServer.ServerID+","+mRunningAverage.getRunningAverage()+"\n");
				buffWrite.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			try {
				buffWrite.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				mClientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mThreadCounter.decrementAndGet();
		} else {
			// the task is rejected!
			BufferedWriter buff = null;
			try {
				buff = new BufferedWriter(new OutputStreamWriter(mClientSocket.getOutputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				// sending a response back to the client with important information + rejection
				buff.write("rejected,"+AWSServer.ServerID+","+mRunningAverage.getRunningAverage()+"\n");
				buff.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				buff.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				mClientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
