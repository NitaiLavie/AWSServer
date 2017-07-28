package awsServer;

import java.io.IOException;
import java.net.ServerSocket;

public class AWSServer {
	
	
	public static void main(String [] args) {
		try {
			ServerSocket serverSocket = new ServerSocket(8060);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
