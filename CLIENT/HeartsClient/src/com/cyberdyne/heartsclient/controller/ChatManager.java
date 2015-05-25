/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyberdyne.heartsclient.controller;

import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;

import com.cyberdyne.heartsclient.view.ChatView;

/**
 *
 * @author dleonard
 */
public class ChatManager {

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
	 * @uml.property  name="mucLobby"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private MultiUserChat mucLobby;
    /**
	 * @uml.property  name="mucRoom"
	 * @uml.associationEnd  readOnly="true"
	 */
    private MultiUserChat mucRoom;
    /**
	 * @uml.property  name="history"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    DiscussionHistory history =  new DiscussionHistory();
    /**
	 * @uml.property  name="lobbyChatView"
	 * @uml.associationEnd  
	 */
    private ChatView lobbyChatView;
    /**
	 * @uml.property  name="roomChatView"
	 * @uml.associationEnd  inverse="lobbyChatManager:com.cyberdyne.heartsclient.view.ChatView"
	 */
    private ChatView roomChatView;
    /**
	 * @uml.property  name="roomName"
	 */
    private String roomName;
    /**
	 * @uml.property  name="toCreate"
	 */
    private boolean toCreate;
        
    public ChatManager() {
        
        this.lobbyChatView = new ChatView(this);
        joinLobbyChatRoom();
        addMessageListener();
        
    }
    public ChatManager(String roomName, boolean toCreate){
    	this.roomName = roomName;
    	this.toCreate = toCreate;
    	this.roomChatView = new ChatView(this, roomName, toCreate);
    	if(toCreate)
    		createChatRoom();
    	else
    		joinChatRoom();
    	
    	addRoomMessageListener();
    }

    private void addRoomMessageListener() {
    	
    	this.mucLobby.addMessageListener(new PacketListener() {

            @Override
            public void processPacket(Packet packet) {
                Message mess = (Message) packet;
                String sender = mess.getFrom();
                int index = sender.indexOf("/");
                String userName = null;
                if (index!=-1)
                    sender = sender.substring(index+1);
                
                System.out.println(sender+ " dice: " + mess.getBody());
                
                roomChatView.writeReceivedMessage(sender, mess.getBody());
            }
            

        });
		
	}
	private void joinChatRoom() {
		
		 this.mucLobby = new MultiUserChat(connectionManager.getConnection(), this.roomName+"@conference.tauron");
	        try {
	            history.setMaxStanzas(0);
	            this.mucLobby.join(userName, "",history, SmackConfiguration.getPacketReplyTimeout());
	        } catch (XMPPException ex) {
	            Logger.getLogger(ChatManager.class.getName()).log(Level.SEVERE, null, ex);
	        }
				
	}
	private void createChatRoom() {
		 this.mucLobby = new MultiUserChat(connectionManager.getConnection(), this.roomName+"@conference.tauron");
		 try {
			this.mucLobby.create(userName);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.mucLobby.sendConfigurationForm(new Form(Form.TYPE_SUBMIT));
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void joinLobbyChatRoom() {
        this.mucLobby = new MultiUserChat(connectionManager.getConnection(), "lobby@conference.tauron");
        try {
            history.setMaxStanzas(0);
            this.mucLobby.join(userName, "",history, SmackConfiguration.getPacketReplyTimeout());
        } catch (XMPPException ex) {
            Logger.getLogger(ChatManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void addMessageListener(){
        this.mucLobby.addMessageListener(new PacketListener() {

            @Override
            public void processPacket(Packet packet) {
                Message mess = (Message) packet;
                String sender = mess.getFrom();
                int index = sender.indexOf("/");
                String userName = null;
                if (index!=-1)
                    sender = sender.substring(index+1);
                
                System.out.println(sender+ " dice: " + mess.getBody());
                
                lobbyChatView.writeReceivedMessage(sender, mess.getBody());
            }
            

        });
                
    }
    
    /**
	 * @return
	 * @uml.property  name="mucLobby"
	 */
    public MultiUserChat getMucLobby(){
    	return this.mucLobby;
    }
           
}
