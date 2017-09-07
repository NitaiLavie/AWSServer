package awsServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AWSServer {
	
	private static final int mServerPort = 7777;
	
	public static void main(String [] args) {
		boolean run = true;
		PingReceiver pinger = new PingReceiver();
		pinger.start();
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
			try {
				serverSocket = new ServerSocket(mServerPort);
				while(run) {
					clientSocket = serverSocket.accept();
					new ClientTask(clientSocket).start();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				pinger.stopReceiver();
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