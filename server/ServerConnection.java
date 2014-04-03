package server;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;

import shared.Connection;
import shared.Message;

public class ServerConnection extends Connection {
	
	private Server server;
	private boolean hasToRun;

	public ServerConnection(Socket socket, Server server) {
		super(socket);
		this.server = server;
		this.hasToRun = false;
	}
	
	public void kill(){
		this.hasToRun = false;
	}

	@Override
	public void run() {
		this.hasToRun = true;
		while (this.hasToRun) {
			Message message = null;
			try{
				message = new Message(this.receive());
			} catch (IOException e) {
				this.server.dropConnection(this);
				this.kill();
				return;
			} catch (NullPointerException e) {
				this.kill();
				return;
			}
			message.set("Datetime", (new Date()).toString());
			message.set("SourceIP", this.getSourceIP());
			message.set("SourcePort", ""+this.getSourcePort());
			server.addMsg(message);

			for (ServerConnection c : server.getConns()) {
				if (c != this) {
					try {
						c.send(message.encode()+'\n');
					} catch (IOException e) {
						server.dropConnection(this);
						this.kill();
						return;
					}
				}
			}
		}
	}
}
