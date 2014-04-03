package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import shared.Message;

public class Client extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private int port;
	private JTextArea history;
	private JTextField msgInput;
	private String server, user;
	private ClientConnection connection = null;
	private SendMessageAction sendMessageAction;
	private boolean hasToReconnect = false;

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
		this.setSize(620, 440);
		this.setLocationRelativeTo(null);
		this.setBackground(new Color(220,220,220));

		JPanel main = new JPanel();
		main.setBackground(new Color(220,220,220));
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		this.history = new JTextArea(10,10);
		this.history.setEditable(false);
		this.history.setBackground(new Color(255,255,255));
		this.history.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		this.history.setLineWrap(true);
		this.history.setWrapStyleWord(true);
		this.history.setForeground(new Color(0,0,180));
		this.history.setFont(new Font("Monospaced",Font.PLAIN,12));
		DefaultCaret caret = (DefaultCaret)this.history.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);		
		
		this.msgInput = new JTextField(10);
		this.msgInput.setBackground(new Color(255, 220, 220));
		this.msgInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
		this.msgInput.setForeground(new Color(120,0,0));
		this.msgInput.setFont(new Font("Monospaced",Font.PLAIN,12));


		JScrollPane scroll = new JScrollPane(this.history);
		main.add(scroll);
		main.add(this.msgInput);
		
		this.add(main);
		this.setVisible(true);
		
		this.connect();
	}

	private void connect() {
		try {
			if(this.connection != null){
				this.connection.kill();
			}
			this.connection = new ClientConnection(this.server, this.port, this);
			this.connection.start();
			this.hasToReconnect = false;
			this.msgInput.addActionListener(this.sendMessageAction);
			this.msgInput.setBackground(new Color(220, 255, 220));
			this.msgInput.setForeground(new Color(0,120,0));
		} catch (UnknownHostException e) {
			this.showError("WARNING. The server might have changed IP address. " + e.getMessage()+'\n');
		} catch (IOException e) {
			this.showError("WARNING. " + e.getMessage()+'\n');
		}
	}

	public void addMessage(Message message) {
		this.history.setEditable(true);
		this.history.append("[" + message.get("Datetime") + "] " + message.get("User") + ": " + message.get("Message") + '\n');
		this.history.setEditable(false);
	}
	
	public void showError(String error){
		this.history.setEditable(true);
		this.history.append(error);
		this.history.setEditable(false);		
	}
	
	public void clearHistory(){
		this.history.setEditable(true);
		this.history.setText("");
		this.history.setEditable(false);
	}
	
	public void reconnect(){
		this.hasToReconnect = true;
		this.msgInput.removeActionListener(this.sendMessageAction);
		this.msgInput.setBackground(new Color(255, 220, 220));
		this.msgInput.setForeground(new Color(120,0,0));
		while(this.hasToReconnect){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				return;
			}
			this.connect();
		}
	}

	private class SendMessageAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(msgInput.getText().length()>0){
				try {
					connection.send("User[" + user + "]Message[" + msgInput.getText() + "]");
				} catch (IOException ex) {
					clearHistory();
					showError("WARNING. You are disconnected.\n"+ex.getMessage()+'\n');
					reconnect();
				}
				history.setEditable(true);
				history.append("["+(new Date()).toString() + "] You: " + msgInput.getText() + '\n');
				history.setEditable(false);
				msgInput.setText("");
			}
		}

	}

}
