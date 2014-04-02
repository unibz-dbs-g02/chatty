package shared;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Connection extends Thread {
	
	private String sourceIP;
	private int sourcePort;
	private ObjectOutputStream o;
	private ObjectInputStream w;
	private ExecutorService ex = Executors.newCachedThreadPool();

	public Connection(Socket s) {
		this.sourceIP = s.getInetAddress().toString();
		this.sourcePort = s.getPort();
		try {
			this.o = new ObjectOutputStream(s.getOutputStream());
			this.w = new ObjectInputStream(s.getInputStream());
		} catch (Exception e) {
			System.err.println("WARNING. An I/O error occurred while creating the I/O streams of the socket.\n" + e.getMessage());
		}
	}

	public void send(String msg) throws IOException{
		if (msg == null) {
			return;
		}
		o.writeObject(msg);
	}

	@SuppressWarnings("deprecation")
	public void close() {
		try {
			this.o.close();
			this.w.close();
			this.ex.shutdown();
			this.stop();
		} catch (IOException e) {
			System.err.println("WARNING. An I/O error occurred while closing the socket.\n" + e.getMessage());
		}
	}

	public String receive() throws IOException{
		try {
			return (String) w.readObject();
		} catch (ClassNotFoundException e) {
			System.err.println("WARNING. An serialization error occurred while reading from the socket.\n" + e.getMessage());
			e.printStackTrace();
			this.close();
			return null;
		}
	}
	
	public String getSourceIP(){
		return this.sourceIP;
	}
	
	public int getSourcePort(){
		return this.sourcePort;
	}
}
