package shared;

import chat.*;

import java.io.IOException;
import java.net.*;

public class ClientConnection extends Connection {

	private Client client;
	
	public ClientConnection(String host, int port, Client c) throws UnknownHostException, IOException{
		super(new Socket(host, port));
		this.client = c;
	}

	public ClientConnection(Socket s, Client c) {
		super(s);
		this.client = c;
	}

	@Override
	public void run() {
		while (true) {
			try {
				this.client.addMessage(new Message(this.receive()));
			} catch (IOException e) {
				client.showError("FATAL ERROR! You are disconnected.\n"+e.getMessage()+'\n');
				client.reconnect();
			}
		}
	}
}
