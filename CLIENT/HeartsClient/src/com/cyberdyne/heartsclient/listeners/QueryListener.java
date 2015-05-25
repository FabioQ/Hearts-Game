package com.cyberdyne.heartsclient.listeners;

import java.util.Iterator;
import java.util.Vector;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.filter.ToContainsFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.packet.DiscoverItems;

import com.cyberdyne.heartsclient.controller.ConnectionManager;
import com.cyberdyne.heartsclient.model.Room;
import com.cyberdyne.heartsclient.model.Score;
import com.cyberdyne.heartsclient.view.LobbyView;

public class QueryListener {
	
	/**
	 * @uml.property  name="connectionManager"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private ConnectionManager connectionManager = ConnectionManager.getConnectionManager();
    /**
	 * @uml.property  name="myUserName"
	 */
    private String myUserName = connectionManager.getConnection().getAccountManager().getAccountAttribute("username");
	
    /**
	 * @uml.property  name="discoverFilter"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private PacketFilter discoverFilter = new PacketTypeFilter(IQ.class);
    /**
	 * @uml.property  name="toFilter"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private PacketFilter toFilter = new ToContainsFilter("lobby@conference.tauron/");
    /**
	 * @uml.property  name="andFilter"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private PacketFilter andFilter = new AndFilter(discoverFilter,toFilter);
    /**
	 * @uml.property  name="tonotify"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private LobbyView tonotify;
    
    /**
	 * @uml.property  name="room"
	 * @uml.associationEnd  
	 */
    private Room room;
    
    public QueryListener (LobbyView tonotify){
        this.tonotify = tonotify;
        listen();
    }
    

	private void listen() {
		PacketListener queryListener = new PacketListener() {
	        public void processPacket(Packet packet) {
	            /*questo metodo viene invocato per ogni pacchetto in entrata. Se e' stato specificato un filtro
	             * come nel nostro caso, allora il metodo verra' invocato esclusivamente quando ci sono pacchetti
	             * che superano il filtro
	             */
	        	DiscoverItems discoItems = (DiscoverItems) packet;
	        	String sender = discoItems.getFrom();
	        	int index = sender.indexOf("/");
	            String userName = null;
	            if (index!=-1)
	                userName = sender.substring(index+1);
	            
	            if(sender.contains("lobby@conference.tauron/")){
	            	if (discoItems.getPacketID().equalsIgnoreCase("disco2") &&
			            	 discoItems.getType().equals(Type.GET)){
	            		//un utente richiede informazioni riguardo l'utente
	            		String userStatus = null;
		            	discoItems.setFrom("lobby@conference.tauron"+"/"+myUserName);
		            	discoItems.setTo("lobby@conference.tauron"+"/"+userName);
		            	discoItems.setType(Type.RESULT);
		            	if(Room.getRoomInstance()!=null){
			            	System.out.println("STATUS STANZA 2 ");
		            		System.out.println(Room.getRoomInstance().getStatus());
		            	}
		            	
		            	if(Room.getRoomInstance()==null || Room.getRoomInstance().getStatus().equalsIgnoreCase("")){
		            		userStatus="libero";
		            	}else if(Room.getRoomInstance().getStatus().equalsIgnoreCase("inactive") || Room.getRoomInstance().getStatus().equalsIgnoreCase("created")){
		            		userStatus = "in attesa";
		            	}else if (Room.getRoomInstance().getStatus().equalsIgnoreCase("active")){
		            		userStatus = "occupato";
		            	}
		            	String myScore;
		            	myScore = Integer.toString(Score.getScoreInstance().getCurrentScore());
		            	discoItems.setNode(userStatus+"@"+myScore);
		            	System.out.println(discoItems.toXML());
		            	connectionManager.getConnection().sendPacket(discoItems);	
	            	}
	            	else if(discoItems.getPacketID().equalsIgnoreCase("disco2") &&
			            	 discoItems.getType().equals(Type.RESULT)){
	            		if(discoItems.getNode()!=null){
	            			String statusUser = discoItems.getNode();
	            			tonotify.statusUser(statusUser);
	            		}
	            	}
	            	
	            	else if(discoItems.getPacketID().equalsIgnoreCase("disco3") &&
			            	 discoItems.getType().equals(Type.GET)){
	            		String roomStatus = null;
		            	discoItems.setFrom("lobby@conference.tauron"+"/"+myUserName);
		            	discoItems.setTo("lobby@conference.tauron"+"/"+userName);
		            	discoItems.setType(Type.RESULT);
		            	//System.out.println("STATUS STANZA 2 ");
		            	if(Room.getRoomInstance()!=null){
			            	System.out.println("STATUS STANZA 2 ");
		            		System.out.println(Room.getRoomInstance().getStatus());
		            	}

		            	if(Room.getRoomInstance()==null || Room.getRoomInstance().getStatus().equalsIgnoreCase("inactive") || Room.getRoomInstance().getStatus().equalsIgnoreCase("created"))
		            			roomStatus = "non avviata";
		            	else if (Room.getRoomInstance().getStatus().equalsIgnoreCase("active"))
		            			roomStatus = "avviata";
		            	
		            	discoItems.setNode(roomStatus);
		            	System.out.println(discoItems.toXML());
		            	connectionManager.getConnection().sendPacket(discoItems);	
	            	}
	            	
	            	else if(discoItems.getPacketID().equalsIgnoreCase("disco3") &&
			            	 discoItems.getType().equals(Type.RESULT)){
	            		if(discoItems.getNode()!=null){
	            			String statusRoom = discoItems.getNode();
	            			
	            			tonotify.statusRoom(statusRoom);
	            		}
	            	}
	            }
	        }
		};
		
	    connectionManager.getConnection().addPacketListener(queryListener, discoverFilter);
		
	}
	
	/**
	 * @param aRoom
	 * @uml.property  name="room"
	 */
	public void setRoom(Room aRoom){
		this.room = Room.getRoomInstance();
		System.out.println("ZIO CANE LO STATO E':" + Room.getRoomInstance().getStatus() );

	}
	public Room getRoom() {
		return Room.getRoomInstance();
	}
	

}
