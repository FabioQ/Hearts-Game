package com.cyberdyne.heartsclient.view;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListModel;

import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverItems;

import com.cyberdyne.heartsclient.controller.ChatManager;
import com.cyberdyne.heartsclient.controller.ConnectionManager;
import com.cyberdyne.heartsclient.controller.GameManager;
import com.cyberdyne.heartsclient.controller.LobbyManager;
import com.cyberdyne.heartsclient.model.Room;

public class LobbyView {
	
	/**
	 * @uml.property  name="roomManager"
	 * @uml.associationEnd  multiplicity="(1 1)" inverse="lobbyView:com.cyberdyne.heartsclient.controller.LobbyManager"
	 */
	LobbyManager roomManager;
	
	/**
	 * @uml.property  name="list"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JList list;

	/**
	 * @uml.property  name="list2"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JList list2;
	/**
	 * @uml.property  name="roomList"
	 */
	private Vector roomList = new Vector();
	/**
	 * @uml.property  name="frame"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JFrame frame;
	/**
	 * @uml.property  name="frameInfo"
	 * @uml.associationEnd  
	 */
	private JFrame frameInfo;
	/**
	 * @uml.property  name="nameRoom"
	 */
	private String nameRoom = "";
	/**
	 * @uml.property  name="numBot"
	 */
	private int numBot;
	/**
	 * @uml.property  name="textStanza"
	 * @uml.associationEnd  
	 */
	private JTextField  textStanza;
	/**
	 * @uml.property  name="botList"
	 * @uml.associationEnd  
	 */
	private JComboBox botList;
	/**
	 * @uml.property  name="botString" multiplicity="(0 -1)" dimension="1"
	 */
	private String botString[];
	/**
	 * @uml.property  name="nameJoinedRoom"
	 */
	private String nameJoinedRoom = "";
	/**
	 * @uml.property  name="connectionManager"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	ConnectionManager connectionManager = ConnectionManager.getConnectionManager();
	/**
	 * @uml.property  name="userName"
	 */
	String userName = connectionManager.getConnection().getAccountManager().getAccountAttribute("username");

	/**
	 * @uml.property  name="userList"
	 */
	private Vector userList;
	public LobbyView(LobbyManager roomManager){
		
		this.roomManager = roomManager;
		initializeFrame();
	}

	private void initializeFrame() {
		 
		roomList = roomManager.getRoomList();
		
		userList = roomManager.getUserList("lobby");
		
	       frame = new JFrame("Lobby"+" "+userName);
	       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	       Container contentPane = frame.getContentPane();
	       list = new JList(roomList);
	       list2 = new JList(userList);
	       JToolBar toolbar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);
	       JButton refreshList = new JButton("Aggiorna Liste");
	       refreshList.setToolTipText("Clicca per aggiornare la lista delle stanze");
	       refreshList.setPreferredSize(new Dimension(190, 30));
	       JButton createRoom = new JButton("Crea Nuova Stanza");
	       createRoom.setToolTipText("Clicca per creare una nuova stanza");
	       createRoom.setPreferredSize(new Dimension(190, 30));
	       JButton logout = new JButton("Logout");
	       logout.setToolTipText("Clicca per fare il logout dell'utente corrente");
	       logout.setPreferredSize(new Dimension(190, 30));
	       toolbar.add(refreshList);
	       toolbar.addSeparator(new Dimension(15, 30));
	       toolbar.add(createRoom);
	       toolbar.addSeparator(new Dimension(15, 30));
	       toolbar.add(logout);
	       toolbar.setPreferredSize(new Dimension(200, 30));
	       toolbar.setFloatable(false);
	       frame.getContentPane().add(toolbar ,BorderLayout.PAGE_START);
	       JScrollPane scrollPane1 = new JScrollPane(list);
	       JScrollPane scrollPane2 = new JScrollPane(list2);
	       JLabel label1 = new JLabel("Lista Stanze");
	       JLabel label2 = new JLabel("Lista Utenti");
	       label1.setOpaque(true);
	       label2.setOpaque(true);
	       label1.setBackground(Color.lightGray);
	       label2.setBackground(Color.lightGray);
	       Font font = label1.getFont();
	       label1.setFont(font.deriveFont(font.getStyle()^Font.BOLD));
	       scrollPane1.setColumnHeaderView(label1);
	       Font font2 = label2.getFont();
	       label2.setFont(font2.deriveFont(font2.getStyle()^Font.BOLD));
	       scrollPane2.setColumnHeaderView(label2);
	       Box box = Box.createHorizontalBox();
	       box.add(scrollPane1);
	       box.add(scrollPane2);
	       contentPane.add(box, BorderLayout.CENTER);
	       frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
	       frame.setSize(600,400);
	       frame.setVisible(true);
	       frame.setResizable(false);

		
		refreshList.addActionListener(new ActionListener(){
		 	   public void actionPerformed(ActionEvent ae)
		 	  {
		 		   frame.dispose();
		 		   initializeFrame();

		 		  
		 	  }
		});
		
		logout.addActionListener(new ActionListener(){
		 	   public void actionPerformed(ActionEvent ae)
		 	  {
		 		  System.exit(0);	 		  
		 	  }
		});
		
		createRoom.addActionListener(new ActionListener(){
			JFrame frame2 = new JFrame("Crea Stanza");
		 	   public void actionPerformed(ActionEvent ae)
		 	  {
		 		  Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	               int w = dim.width;
	               int h = dim.height;
	               int x = (w/2)-(320/2);
	               int y = (h/2)-(200/2);
	               frame2.setLayout(null);
	               final String botString[] = {"0","1","2","3"};
	               textStanza = new JTextField(15);
	               JLabel nomeStanza = new JLabel();
	               nomeStanza.setText("Nome della stanza: (*)");
	               JLabel numeroBot = new JLabel();
	               numeroBot.setText("Numero di giocatori Virtuali: ");
	               botList = new JComboBox(botString);
	               botList.setSelectedIndex(0);
	               JButton crea = new JButton("Crea Stanza");
	               JButton annulla = new JButton("Annulla");
	               nomeStanza.setBounds(10,10,150,20);
	               textStanza.setBounds(150,10,140,25);
	               numeroBot.setBounds(10,50,200,20);
	               botList.setBounds(200,50,80,25);
	               frame2.getRootPane().setDefaultButton(crea);
	               crea.setBounds(180,90,100,30);
	               annulla.setBounds(40,90,100,30);
	               
	               frame2.add(textStanza);
	               frame2.add(nomeStanza);
	               frame2.add(numeroBot);
	               frame2.add(botList);
	               if (Room.getRoomInstance()==null)
	            	   frame2.add(crea);
	               frame2.add(annulla);
	               frame2.setVisible(true);
	               frame2.setSize(320, 150);
	               frame2.setLocation(x, y);
	               frame2.setResizable(false);
		 		  annulla.addActionListener(new ActionListener(){
				 	   public void actionPerformed(ActionEvent ae)
				 	  {
				 		   frame2.dispose();
				 	  }
		 		  });
		 		 crea.addActionListener(new ActionListener(){
				 	   public void actionPerformed(ActionEvent ae)
				 	  {
				 		  frame.dispose();
				 		  frame2.dispose();
				 		  nameRoom = textStanza.getText();
				 		  int indexBot = botList.getSelectedIndex();
				 		  numBot = Integer.parseInt(botString[indexBot]);
				 		  new ChatManager(nameRoom, true);
				 		  LobbyManager roomMng = new LobbyManager();
				 		  GameManager gameManager = new GameManager(roomMng.createRoom(nameRoom,numBot));
				 	  }
		 		  });
		 	  }
		});
		/* Double click per richiedere info utente */
		list2.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		    	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	            int w = dim.width;
	            int h = dim.height;
	            int x = (w/2)-(320/2);
	            int y = (h/2)-(200/2);
		        JList list2 = (JList)evt.getSource();
		        if (evt.getClickCount() == 2) {          // Double-click
		            // Get item index
		            int index = list2.locationToIndex(evt.getPoint());
		            ListModel dlm = list2.getModel();
		            Object item = dlm.getElementAt(index);
		            list2.ensureIndexIsVisible(index);
		            frameInfo = new JFrame("Utente: "+(String)item);
		            frameInfo.setLayout(null);
		            frameInfo.setLocation(x, y);
		            /* dovr� partire una richiesta per le info dell'utente da visualizzare nella finestra */
		            JButton indietro = new JButton("Indietro");
		            indietro.setBounds(110, 90, 100, 20);
		            frameInfo.add(indietro);
		            frameInfo.getRootPane().setDefaultButton(indietro);
		            frameInfo.setSize(320, 150);
		            frameInfo.setVisible(true);
		            frameInfo.setResizable(false);
		            //Creo pacchetto per richiedere il dettaglio dell'utente
		            roomManager.getUserStatus((String)item);
		            indietro.addActionListener(new ActionListener(){
		    	    	public void actionPerformed(ActionEvent ae)
		    	    	{
		    	    		frameInfo.dispose();
		    	    	}
		    	    });
		        }
		    }
		});
		/* Double click per richiedere info stanza */
		list.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		    	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	            int w = dim.width;
	            int h = dim.height;
	            int x = (w/2)-(320/2);
	            int y = (h/2)-(200/2);
		        JList list = (JList)evt.getSource();
		        if (evt.getClickCount() == 2) {          // Double-click
		            // Get item index
		            int index = list.locationToIndex(evt.getPoint());
		            ListModel dlm = list.getModel();
		            Object item = dlm.getElementAt(index);;
		            list.ensureIndexIsVisible(index);
		            nameJoinedRoom = (String)item;
		            frameInfo = new JFrame("Stanza: "+nameJoinedRoom);
		            frameInfo.setLayout(null);
		            frameInfo.setLocation(x, y);
		            /* dovr� partire una richiesta per le info della stanza da visualizzare nella finestra */
		            JButton indietro = new JButton("Indietro");
		            JButton joinRoom = new JButton("Joina Stanza");
		            indietro.setBounds(35, 90, 100, 20);
		            joinRoom.setBounds(190, 90, 100, 20);
		            frameInfo.add(indietro);
		            if (Room.getRoomInstance()==null)
		            	frameInfo.add(joinRoom);
		            frameInfo.setSize(320, 150);
		            frameInfo.setVisible(true);
		            frameInfo.setResizable(false);
		            frameInfo.getRootPane().setDefaultButton(joinRoom);
		            //Creo pacchetto per richiedere il dettaglio della stanza
		            Vector userList = new Vector();
		            userList = roomManager.getUserList(nameJoinedRoom);
		            System.out.println("UTENTE A CUI CHIEDO INFO: " + (String)userList.firstElement());
		            roomManager.getRoomStatus((String)userList.firstElement());
		            /*DiscoverInfo roomInfo = new DiscoverInfo();
		            roomInfo.setPacketID("disco3");
		            roomInfo.setTo((ConstantData.hostService));
		            roomInfo.setType(Type.GET);
		            roomInfo.setFrom(userName+"@"+ConstantData.host);
		            ConnectionManager.getConnectionManager().getConnection().sendPacket(roomInfo);*/
		            indietro.addActionListener(new ActionListener(){
		    	    	public void actionPerformed(ActionEvent ae)
		    	    	{
		    	    		frameInfo.dispose();
		    	    	}
		    	    });
		            joinRoom.addActionListener(new ActionListener(){
		    	    	public void actionPerformed(ActionEvent ae)
		    	    	{ 	
		    	    		new ChatManager(nameJoinedRoom, false);
		    	    		LobbyManager roomMng = new LobbyManager();
		    	    		Room aRoom = Room.createRoom(nameJoinedRoom);
					 		GameManager gameManager = new GameManager(Room.getRoomInstance());
					 		roomMng.setJoinedRoom(Room.getRoomInstance());
		    	    		roomMng.joinRoom(nameJoinedRoom);
		    	    		frameInfo.dispose();
		    	    		frame.dispose();
		    	    	}
		    	    });
		        }
		    }
		});
	}

	public void statusRoom(String statusRoom) {
		JLabel label = new JLabel("Stato stanza: ");
		JLabel status = new JLabel(statusRoom);
		if(statusRoom.equalsIgnoreCase("avviata")){
			status.setForeground(Color.RED);
		}else if(statusRoom.equalsIgnoreCase("non avviata")){
			status.setForeground(Color.GREEN);
		}
		label.setBounds(10,10,150,20);
		status.setBounds(160,10,150,20);
		frameInfo.add(label);
		frameInfo.add(status);
		frameInfo.repaint();
	}

	public void statusUser(String string) {
		int index = string.indexOf("@");
		String scoreStr = "0";
		String statusStr = "";
		System.out.println(string);
		if (index!=-1) {
            scoreStr = string.substring(index+1);
            statusStr = string.substring(0,index);
		}
		JLabel label = new JLabel("Stato utente: ");
		JLabel label2 = new JLabel("Punteggio");
		JLabel status = new JLabel(statusStr);
		JLabel score = new JLabel(scoreStr);
		if(statusStr.equalsIgnoreCase("libero")){
			status.setForeground(Color.GREEN);
		}else if(statusStr.equalsIgnoreCase("in attesa")){
			status.setForeground(Color.YELLOW);
		}else if(statusStr.equalsIgnoreCase("occupato")){
			status.setForeground(Color.RED);
		}
		label.setBounds(10,10,150,20);
		label2.setBounds(10, 40, 150, 20);
		status.setBounds(160,10,150,20);
		score.setBounds(160, 40, 150, 20);
		frameInfo.add(label);
		frameInfo.add(label2);
		frameInfo.add(status);
		frameInfo.add(score);
		frameInfo.repaint();
	}
}
