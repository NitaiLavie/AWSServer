package awsServer;

import java.io.IOException;
import java.net.ServerSocket;

public class PingReceiver extends Thread {
	
	private boolean mRun = true;
	
	@Override
	public void run() {
		super.run();
		
		try {
			ServerSocket serverSocket = new ServerSocket(6666);
			while(mRun) {
				serverSocket.accept();
			}
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stopReceiver() {
		mRun = false;
	}

}
