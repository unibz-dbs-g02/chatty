package server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class ServerGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private Server server;
	private JList<String> list;
	final DefaultListModel<String> listModel;
	
	public ServerGUI(Server server){
		super();
		this.server = server;
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Chatty server session - listening on port " + this.server.getPort());
		this.setSize(400, 300);
		this.setMinimumSize(new Dimension(400, 300));
		this.setLocationRelativeTo(null);
		this.setBackground(new Color(255, 255, 255));

		JPanel main = new JPanel();
		main.setBackground(new Color(255, 255, 255));
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		
		this.listModel = new DefaultListModel<String>();
		this.list = new JList<String>(this.listModel);
		this.list.setFixedCellHeight(20);
		this.list.setFixedCellWidth(160);

		JScrollPane scrollPane = new JScrollPane(this.list);
		scrollPane.setPreferredSize(new Dimension(165, 300));
		scrollPane.setBorder(BorderFactory.createTitledBorder("connected clients"));

		JPanel buttons = new JPanel();
		
		JButton killServer = new JButton("Kill Server");
		killServer.addActionListener(new KillServerAction());

		JButton kickClient = new JButton("Kick Client");
		kickClient.addActionListener(new KickClientAction());

		JButton banClient = new JButton("Ban IP Address");
		banClient.addActionListener(new BanClientAction());
		
		buttons.add(killServer);
		buttons.add(kickClient);
		buttons.add(banClient);
		
		main.add(scrollPane);
		main.add(buttons);

		this.add(main);
		this.setVisible(true);

	}
	
	public void updateClientList(){
		this.listModel.removeAllElements();
		for(ServerConnection serverConnection : server.getConnections()){
			this.listModel.addElement(serverConnection.getSourceIP()+":"+serverConnection.getSourcePort());
		}
	}
	
	public void exit(){
		this.dispose();
	}
	
	private class KillServerAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			server.kill();
			exit();
		}
	}
	
	private class KickClientAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			String value = list.getSelectedValue();
			String ip = value.substring(0,value.indexOf(':'));
			String port = value.substring(value.indexOf(':')+1);
			server.kickClient(ip, Integer.parseInt(port), false);
		}
	}
	
	private class BanClientAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			String value = list.getSelectedValue();
			String ip = value.substring(0,value.indexOf(':'));
			String port = value.substring(value.indexOf(':')+1);
			server.banClient(ip, Integer.parseInt(port));
		}
	}
	
}
