package chat;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import shared.ClientConnection;
import shared.Message;

public class Client extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private int reconnected = 0;
	private int port;
	private JTextArea history;
	private JTextField msgInput;
	private String server, user;
	private ClientConnection connection;
	private SendMessageAction sendMessageAction;

	public static void main(String[] args) {
		new Client("127.0.0.1", "gino", 4001);
	}

	public Client(String server, String user, int port) {
		super();
		this.server = server;
		this.user = user;
		this.port = port;
		
		this.sendMessageAction = new SendMessageAction();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Chatty chat session - " + this.user + "@" + this.server + ":" + this.port);
		this.setSize(980, 385);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setBackground(new Color(255, 255, 255));

		JPanel main = new JPanel();
		main.setBackground(new Color(255, 255, 255));
		this.history = new JTextArea(20, 80);
		this.history.setEditable(false);
		this.history.setBackground(new Color(255, 255, 255));
		this.msgInput = new JTextField(80);
		this.msgInput.setBackground(new Color(255, 255, 255));
		this.msgInput.addActionListener(this.sendMessageAction);

		JScrollPane scroll = new JScrollPane(this.history);
		main.add(scroll);
		main.add(msgInput);

		this.add(main);
		this.setVisible(true);

		this.connect();
	}

	private void connect() {
		try {
			this.connection = new ClientConnection(this.server, this.port, this);
			this.connection.start();
		} catch (UnknownHostException e) {
			this.showError("FATAL ERROR: " + e.getMessage()+'\n');
		} catch (IOException e) {
			this.showError("FATAL ERROR: " + e.getMessage()+'\n');
		}
	}

	public void addMessage(Message m) {
		this.history.setEditable(true);
		this.history.append("[" + m.get("Datetime") + "] " + m.get("User") + ": " + m.get("Message") + '\n');
		this.history.setEditable(false);
	}
	
	public void showError(String error){
		this.history.setEditable(true);
		this.history.append(error);
		this.history.setEditable(false);		
	}
	
	public void reconnect(){
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			return;
		}
		this.connect();
	}

	private class SendMessageAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				connection.send("User[" + user + "]Message[" + msgInput.getText() + "]");
			} catch (IOException ex) {
				msgInput.setText("");
				showError("FATAL ERROR! You are disconnected.\n"+ex.getMessage()+'\n');
				reconnect();
			}
			history.setEditable(true);
			history.append("["+(new Date()).toString() + "] You: " + msgInput.getText() + '\n');
			history.setEditable(false);
			msgInput.setText("");
		}

	}

}
