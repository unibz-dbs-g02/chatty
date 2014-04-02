package shared;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;

import chat.Server;

public class ServerConnection extends Connection {
	
	private Server ss;

	public ServerConnection(Socket s, Server ss) {
		super(s);
		this.ss = ss;
	}

	@Override
	public void run() {
		while (true) {
			Message m = null;
			try{
				m = new Message(this.receive());
			} catch (IOException e) {
				this.ss.dropConnection(this);
				this.close();
				return;
			}
			m.set("Datetime", (new Date()).toString());
			m.set("SourceIP", this.getSourceIP());
			m.set("SourcePort", ""+this.getSourcePort());
			ss.addMsg(m);

			m.set("time", (new Date().toString()));
			for (ServerConnection c : ss.getConns()) {
				if (c != this) {
					try {
						c.send(m.encode()+'\n');
					} catch (IOException e) {
						ss.dropConnection(this);
						this.close();
						return;
					}
				}
			}
		}
	}
}
