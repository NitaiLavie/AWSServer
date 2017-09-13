package com.nsslawsproject.awsserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientTask extends Thread{
	private Socket mClientSocket;
	private ServerAction mAction;
	private AtomicInteger mThreadCounter;
	private boolean mReject;
	private int mCores;
	private int mTimeout;
	private int mIterations;
	private ReadWriteInt mUpdateInterval;
	
	public ClientTask(Socket clientSocket, AtomicInteger threadCounter, ReadWriteInt interval) {
		this(clientSocket, threadCounter, interval, false);
	}
	
	public ClientTask(Socket clientSocket, AtomicInteger threadCounter, ReadWriteInt interval, boolean reject) {
		 this(clientSocket, new TanhAction(), threadCounter, interval, reject);
	}
	
	public ClientTask(Socket clientSocket, ServerAction serverAction, AtomicInteger threadCounter, ReadWriteInt interval) {
		this(clientSocket, serverAction, threadCounter, interval, false);
	}
	
	public ClientTask(Socket clientSocket, ServerAction serverAction, AtomicInteger threadCounter, ReadWriteInt interval, boolean reject) {
		 mClientSocket = clientSocket;
		 mAction = serverAction;
		 mThreadCounter = threadCounter;
		 mReject = reject;
		 mUpdateInterval = interval;
		 this.setPriority(Thread.MIN_PRIORITY);
	}
	

	public void setAction(ServerAction serverAction) {
		mAction = serverAction;
	}

	@Override
	public void run() {
		super.run();
		if(!mReject) {
			// the task is accepted!
			int threadCount = mThreadCounter.incrementAndGet();
			BufferedWriter buffWrite = null;
			BufferedReader buffRead = null;
			
			try {
				buffWrite = new BufferedWriter(new OutputStreamWriter(mClientSocket.getOutputStream()));
				buffRead = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
				mCores = Integer.parseInt(buffRead.readLine());
				mTimeout = Integer.parseInt(buffRead.readLine());
				mIterations = Integer.parseInt(buffRead.readLine());
				mUpdateInterval.set(Integer.parseInt(buffRead.readLine()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			mAction.execute(mCores, mTimeout, mIterations);
			try {
				buffWrite.write(AWSServer.ServerID+":"+threadCount+": done\n");
				buffWrite.flush();
			} catch (IOException e) {
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
				buff.write(AWSServer.ServerID+": rejected!\n");
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
