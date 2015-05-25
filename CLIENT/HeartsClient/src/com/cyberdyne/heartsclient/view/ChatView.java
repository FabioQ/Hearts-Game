
package com.cyberdyne.heartsclient.view;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import org.jivesoftware.smack.XMPPException;

import com.cyberdyne.heartsclient.controller.ChatManager;

/**
 *
 * @author dleonard
 */
public class ChatView implements ActionListener {
    /**
	 * @uml.property  name="lobbyChatManager"
	 * @uml.associationEnd  multiplicity="(1 1)" inverse="roomChatView:com.cyberdyne.heartsclient.controller.ChatManager"
	 */
    private final ChatManager lobbyChatManager;
    /**
	 * @uml.property  name="toclient"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private JTextField toclient = new JTextField();
    /**
	 * @uml.property  name="display"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private JTextArea display = new JTextArea();
    /**
	 * @uml.property  name="send"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private JButton send = new JButton("Invia");
    /**
	 * @uml.property  name="input"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private JPanel input;
    /**
	 * @uml.property  name="frame"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private JFrame frame;
    /**
	 * @uml.property  name="scrollPane"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private JScrollPane scrollPane;
    /**
	 * @uml.property  name="roomName"
	 */
    private String roomName;
    /**
	 * @uml.property  name="toCreate"
	 */
    private boolean toCreate;

    public ChatView(ChatManager aThis) {
        
        this.lobbyChatManager = aThis;
        //this.setBounds(500, 0, 500, 300);
        initializeChat("");
    }
    
    
    public ChatView(ChatManager chatManager, String roomName, boolean toCreate) {
    	this.lobbyChatManager = chatManager;
    	this.roomName = roomName;
    	this.toCreate = toCreate;
        initializeChat(this.roomName);

	}


	private void initializeChat(String room){
		if(room.equalsIgnoreCase(""))
			frame = new JFrame("Chat Lobby");
		else 
			frame = new JFrame("Chat stanza: "+ this.roomName);
        toclient.setMaximumSize(toclient.getPreferredSize());
        display.setMaximumSize(display.getPreferredSize());
        display.setEditable(false);
       
    input = new JPanel();
    input.setMaximumSize(input.getPreferredSize());
    input.setLayout(new BorderLayout());
    input.setBorder(new TitledBorder("Messaggio"));
    input.add(toclient, BorderLayout.CENTER);
    input.add(send, BorderLayout.EAST);
 
    JPanel output = new JPanel();
    output.setMaximumSize(output.getPreferredSize());
    output.setLayout(new BorderLayout());
    output.setBorder(new TitledBorder("Conversazione"));
    display.setText("");
    scrollPane = new JScrollPane(display, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    output.add(scrollPane, BorderLayout.CENTER);
    Dimension dim = new Dimension(500, 200);
    output.setMaximumSize(dim);
 
    JPanel gabung = new JPanel();
    gabung.setLayout(new GridLayout(2, 1));
    gabung.add(input);
    gabung.add(output);
 
    frame.getContentPane().add(gabung, BorderLayout.NORTH);
 
    //setTitle("Chat");
    //setSize(500, 300);
    //setVisible(true);
    //frame.add(output, BorderLayout.NORTH);
    frame.add(input, BorderLayout.SOUTH);
    frame.add(output);
    frame.setSize(600, 400);
    frame.setBounds(620, 0, 600, 400);
    frame.setVisible(true);
    toclient.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent ae) {
                try {
                	
                    lobbyChatManager.getMucLobby().sendMessage(toclient.getText());
                } catch (XMPPException ex) {
                    Logger.getLogger(ChatView.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                toclient.setText("");
            }
        });

    
    }
    
    public void writeReceivedMessage(String sender, String message){
        display.append(sender + " dice: " + message + "\n");
        display.setCaretPosition(display.getDocument().getLength());
    }

    
   

    public void actionPerformed(ActionEvent ae) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
 
 

 
 

}
