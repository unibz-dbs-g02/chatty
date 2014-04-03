package shared;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Connection extends Thread {
	
	private String sourceIP;
	private int sourcePort;
	private DataOutputStream outputStream;
	private DataInputStream inputStream;
	private ExecutorService executorServer = Executors.newCachedThreadPool();
	protected Socket socket;

	public Connection(Socket socket) {
		this.socket = socket;
		this.sourceIP = socket.getInetAddress().toString();
		this.sourcePort = socket.getPort();
		try {
			this.outputStream = new DataOutputStream(socket.getOutputStream());
			this.inputStream = new DataInputStream(socket.getInputStream());
		} catch (Exception e) {
			System.err.println("WARNING. An I/O error occurred while creating the I/O streams of the socket.\n" + e.getMessage());
		}
	}

	public void send(String message) throws IOException{
		if (message == null) {
			return;
		}
		outputStream.writeUTF(message);
	}

	public void close() {
		try {
			this.outputStream.close();
			this.inputStream.close();
			this.executorServer.shutdown();
		} catch (IOException e) {
			System.err.println("WARNING. An I/O error occurred while closing the socket.\n" + e.getMessage());
		}
	}

	public String receive() throws IOException{
		return inputStream.readUTF();
	}
	
	public String getSourceIP(){
		return this.sourceIP;
	}
	
	public int getSourcePort(){
		return this.sourcePort;
	}
}
