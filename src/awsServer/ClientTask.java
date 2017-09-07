package awsServer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientTask extends Thread{
	private Socket mClientSocket;
	private ServerAction mAction;

	public ClientTask(Socket clientSocket) {
		 this(clientSocket, new TanhAction());
	}
	
	public ClientTask(Socket clientSocket, ServerAction serverAction) {
		 mClientSocket = clientSocket;
		 mAction = serverAction;
	}

	@Override
	public void run() {
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
		
	}

}
