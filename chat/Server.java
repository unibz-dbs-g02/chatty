package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import shared.Connection;
import shared.Message;
import shared.ServerConnection;

public class Server extends Thread {

	
	private ArrayList<ServerConnection> conns = new ArrayList<>();
	private ArrayList<Message> history = new ArrayList<>();
	private ServerSocket ss;
	private int port;

	public static void main(String[] args) {
		(new Server(4001)).start();
	}

	public Server(int port) {
		System.out.println("Initializing...");
		this.port = port;
		try {
			this.ss = new ServerSocket(this.port);
		} catch (IOException e) {
			System.err.println("FATAL ERROR! Unable to bind socket server to port, exiting." + e.getMessage());
			return;
		}
	}

	public void run() {
		// add shutdown hook to properly close the socket server 
		Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                try {
					ss.close();
				} catch (IOException e) {
					System.err.println("FATAL ERROR! An I/O error occurred while closing the socket server." + e.getMessage());
				}
            }
        });
		
		System.out.println("Server started.");
		
		while (true) {
			try {
				Socket s = ss.accept();

				ServerConnection c = new ServerConnection(s, this);
				
				for (Message m : this.history) {
					c.send(m.encode()+"\n");
				}
				c.start();

				conns.add(c);
			} catch (IOException e) {
				System.err.println("WARNING. An I/O error occurred while waiting for a connection. Trying with another socket." + e.getMessage());
			}
		}


	}
	
	public void dropConnection(Connection c){
		this.conns.remove(c);
	}

	public ArrayList<ServerConnection> getConns() {
		return conns;
	}

	public ArrayList<Message> getHistory() {
		return history;
	}

	public void addMsg(Message msg) {
		history.add(msg);
	}
	
}
