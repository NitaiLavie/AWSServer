package com.nsslawsproject.awsserver;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientTask extends Thread{
	private Socket mClientSocket;
	private ServerAction mAction;
	private AtomicInteger mThreadCounter;
	private boolean mReject;
	
	public ClientTask(Socket clientSocket, AtomicInteger threadCounter) {
		this(clientSocket, threadCounter, false);
	}
	
	public ClientTask(Socket clientSocket, AtomicInteger threadCounter, boolean reject) {
		 this(clientSocket, new TanhAction(), threadCounter, reject);
	}
	
	public ClientTask(Socket clientSocket, TanhAction tanhAction, AtomicInteger threadCounter) {
		this(clientSocket, tanhAction, threadCounter, false);
	}
	
	public ClientTask(Socket clientSocket, ServerAction serverAction, AtomicInteger threadCounter, boolean reject) {
		 mClientSocket = clientSocket;
		 mAction = serverAction;
		 mThreadCounter = threadCounter;
		 mReject = reject;
	}
	

	public void setAction(ServerAction serverAction) {
		mAction = serverAction;
	}

	@Override
	public void run() {
		super.run();
		if(!mReject) {
			// the task is accepted!
			mThreadCounter.incrementAndGet();
			BufferedWriter buff = null;
			try {
				buff = new BufferedWriter(new OutputStreamWriter(mClientSocket.getOutputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			mAction.execute();
			try {
				buff.write("done\n");
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
				buff.write("rejected!\n");
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
