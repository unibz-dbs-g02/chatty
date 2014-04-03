package server;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;


public class ServerGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private Server server;
	private JList list;
	private String[] clients = {"dsvsvwv","asd2d2","vervrvw","qsdada","aafqef","dvavdvavas","asfasfvad","asa"};
	
	public ServerGUI(Server server){
		super();
		this.server = server;
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Chatty server session - listening on port " + this.server.getPort());
		this.setSize(333, 666);
		this.setLocationRelativeTo(null);
		this.setBackground(new Color(255, 255, 255));

		JPanel main = new JPanel();
		main.setBackground(new Color(255, 255, 255));
		
		JButton killServer = new JButton("Kill Server");
		killServer.addActionListener(new KillServerAction());
		
		
		
		this.list = new JList(clients);
		this.list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		this.list.setVisibleRowCount(-1);
		
		main.add(this.list);
		main.add(killServer);

		this.add(main);
		this.setVisible(true);


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
	
}
