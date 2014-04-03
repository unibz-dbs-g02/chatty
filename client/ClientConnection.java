package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import shared.Connection;
import shared.Message;

public class ClientConnection extends Connection {

	private Client client;
	private boolean hasToRun;
	
	public ClientConnection(String host, int port, Client client) throws UnknownHostException, IOException{
		super(new Socket(host, port));
		this.client = client;
		this.hasToRun = false;
	}

	public ClientConnection(Socket socket, Client client) {
		super(socket);
		this.client = client;
		this.hasToRun = false;
	}
	
	public void kill(){
		super.close();
		this.hasToRun = false;
	}

	@Override
	public void run() {
		this.hasToRun = true;
		while (this.hasToRun && !this.socket.isClosed()) {
			try {
				Message message = new Message(this.receive());
				Thread.yield();
				this.client.addMessage(message);
			} catch (IOException e) {
				client.showError("WARNING. You are disconnected.\n"+e.getMessage()+'\n');
				client.reconnect();
			}
		}
	}
}
