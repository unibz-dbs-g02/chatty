package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import shared.Connection;
import shared.Message;

public class Server extends Thread {

	
	private ArrayList<ServerConnection> connections = new ArrayList<>();
	private ArrayList<Message> history = new ArrayList<>();
	private ServerSocket socketServer;
	private int port;
	private boolean hasToRun;

	public static void main(String[] args) {
		(new Server(4001)).start();
	}

	public Server(int port) {
		this.hasToRun = false;
		this.port = port;
		try {
			this.socketServer = new ServerSocket(this.port);
		} catch (IOException e) {
			System.err.println("FATAL ERROR! Unable to bind socket server to port, exiting.\n" + e.getMessage()+"\n");
			return;
		}
	}

	public void run() {
		// add shutdown hook to properly close the socket server 
		Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                try {
					socketServer.close();
				} catch (IOException e) {
					System.err.println("FATAL ERROR! An I/O error occurred while closing the socket server.\n" + e.getMessage()+"\n");
				}
            }
        });
		
		new ServerGUI(this);
		
		this.hasToRun = true;
		while (this.hasToRun) {
			try {
				Socket s = socketServer.accept();

				ServerConnection c = new ServerConnection(s, this);
				
				c.send("User[SERVER]Message[Welcome!]Datetime["+(new Date())+"]\n");
				c.send("User[SERVER]Message[------BEGIN-CHAT-HISTORY------]Datetime["+(new Date())+"]\n");
				for (Message message : this.history) {
					c.send(message.encode()+"\n");
				}
				c.start();
				c.send("User[SERVER]Message[-------END-CHAT-HISTORY-------]Datetime["+(new Date())+"]\n");

				connections.add(c);
			} catch (IOException e) {
				System.err.println("WARNING. An I/O error occurred while waiting for a connection. Trying with another socket.\n" + e.getMessage()+"\n");
			}
		}
		try {
			this.socketServer.close();
			for(ServerConnection connection : this.connections){
				connection.send("User[SERVER]Message[THE SERVER IS GOING DOWN, YOU WILL BE DISCONNECTED]Datetime["+(new Date())+"]\n");
				connection.kill();
			}
			while(!this.connections.isEmpty()){
				this.connections.get(0).close();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					System.err.println("WARNING. Something interrupted the main server thread during the shutdown, restart needed.\n" + e.getMessage()+"\n");					
				}
			}
		} catch (IOException e) {
			System.err.println("WARNING. An I/O error occurred while closing the server.\n" + e.getMessage()+"\n");
		}
	}
	

	public void kill(){
		try {
			this.hasToRun = false;
			new Socket("127.0.0.1", this.port);
		} catch (IOException e) {
			System.err.println("WARNING. An I/O error occurred while initializing the shutdown procedure for the server.\n" + e.getMessage()+"\n");
		}
		return;
	}
	
	public void dropConnection(Connection c){
		this.connections.remove(c);
	}

	public ArrayList<ServerConnection> getConns() {
		return connections;
	}

	public ArrayList<Message> getHistory() {
		return history;
	}

	public void addMsg(Message msg) {
		history.add(msg);
	}
	
	public int getPort(){
		return this.port;
	}	
}
