package com.cyberdyne.heartsclient.controller;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.IQTypeFilter;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.pubsub.Node;

import com.cyberdyne.heartsclient.listeners.QueryListener;
import com.cyberdyne.heartsclient.model.Room;
import com.cyberdyne.heartsclient.providers.ConfigurationForm;
import com.cyberdyne.heartsclient.providers.MUGSetup;
import com.cyberdyne.heartsclient.providers.MUGSetupProvider;
import com.cyberdyne.heartsclient.utils.ConstantData;
import com.cyberdyne.heartsclient.view.LobbyView;

public class LobbyManager {
	/**
	 * @uml.property  name="roomCreated"
	 */
	String roomCreated = null;

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
	 * @uml.property  name="presenceFilter"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	PacketFilter presenceFilter = new PacketTypeFilter(Presence.class);
	
	/**
	 * @uml.property  name="mug"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	PacketFilter mug = new PacketExtensionFilter("game","http://jabber.org/protocol/mug");
	/**
	 * @uml.property  name="mugHearts"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	PacketFilter mugHearts = new PacketExtensionFilter("http://jabber.org/protocol/mug/hearts");
	/**
	 * @uml.property  name="orFilter"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	PacketFilter orFilter = new OrFilter(mug, mugHearts);
	/**
	 * @uml.property  name="andFilter"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	PacketFilter andFilter = new AndFilter(mug,presenceFilter);
	
	/**
	 * @uml.property  name="lobbyView"
	 * @uml.associationEnd  multiplicity="(1 1)" inverse="roomManager:com.cyberdyne.heartsclient.view.LobbyView"
	 */
	LobbyView lobbyView;

	/**
	 * @uml.property  name="queryListener"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private QueryListener queryListener;

	public LobbyManager() {
		this.lobbyView = new LobbyView (this);
		this.queryListener = new QueryListener(this.lobbyView);
	}

	public Room createRoom(String nameRoom, int numBot) {
		
		PacketCollector collector = connectionManager.getConnection().createPacketCollector(andFilter);

		MUGSetupProvider roomProv = new MUGSetupProvider();
		
		Presence pres = new Presence(Presence.Type.available);
		
		pres.setFrom(userName+"@"+ConstantData.host);
		pres.setTo(nameRoom+"@"+ConstantData.hostService+ "/" + userName);

		/*quando aggiungiamo un'estensione, smack si occupa di utilizzare il metodo toXml della relativa estensione
		 * per "appendere" l'estensione al pacchetto desiderato (in questo caso Presence)
		 * L'estensione in questo caso e'mugcreateroom
		 */
		MUGSetup mugSetup = new MUGSetup();
		mugSetup.setElementName("game");
		mugSetup.setNameSpace("http://jabber.org/protocol/mug");
		mugSetup.setVar("http://jabber.org/protocol/mug/hearts");
		pres.addExtension(mugSetup);
		
		/*creo oggetto di tipo stanza*/
		Room room = Room.createRoom(nameRoom);
		String myRole = null;
		String myAffiliation = null;

		/*invio il pacchetto di richiesta di creazione nuova stanza*/
		connectionManager.getConnection().sendPacket(pres);
		System.out.println("pacchetto per creazione stanza "+pres.toXML());
		
		/*Utilizziamo Collector perche' e' bloccante. Conviene davvero utilizzarlo? Si blocca
		l'esecuzione del programma fin quando non arriva il pacchetto che stiamo aspettando*/
		int count = 0;
		while (count<2) {
			/*chiamata bloccante*/
			Packet packet = collector.nextResult();
			MUGSetup content = (MUGSetup)packet.getExtension("game", "http://jabber.org/protocol/mug");
			if(content.getStatus() != null)
				Room.getRoomInstance().setStatus(content.getStatus());
			if(content.getItemRole() != null){
				Room.getRoomInstance().setMyRole(content.getItemRole());
				myRole = content.getItemRole();
			}
			if(content.getItemAffiliation()!=null){
				Room.getRoomInstance().setMyAffiliation(content.getItemAffiliation());
				myAffiliation = content.getItemAffiliation();
			}
			if(content.getBotNum()!=null)
				Room.getRoomInstance().setBotNum(content.getBotNum());

			count++;
		}
		
		collector.cancel();
		Room.getRoomInstance().addPlayer(myRole, myAffiliation, userName);
		/*Qui inviamo la configurazione della stanza al server*/
	  	ConfigurationForm config = new ConfigurationForm();
    	config.setRoomName(nameRoom);
    	config.setNumBot(numBot);
    	/*imposto il numero di bot che ho scelto, indipendentemente dal valore di default del sistema*/
    	Room.getRoomInstance().setBotNum(Integer.toString(numBot));
    	
    	config.setFrom(ConnectionManager.getConnectionManager().getConnection().
    			getAccountManager().getAccountAttribute("username")+"@"+ConstantData.host);
    	config.setTo(nameRoom+"@"+ConstantData.hostService);
    	config.setType(IQ.Type.SET);
    	config.setPacketID("create2");
    	
    	System.out.println("FORM DA INVIARE");
    	System.out.println(config.toXML());
    	ConnectionManager.getConnectionManager().getConnection().sendPacket(config);
    	
    	this.queryListener.setRoom(Room.getRoomInstance());
    	System.out.println("STAMPO LO STATUS DELLA ROOM DAL CREATE:" + room.getStatus() );
		
	return Room.getRoomInstance();
	}

	
	/*questo metodo invia una richiesta per ottenere la lista delle stanze sul server. Restituisce un array
	 * con i nomi delle stanze
	 */
	public Vector getRoomList() {
		
		DiscoverItems roomListQuery = new DiscoverItems();
		roomListQuery.setPacketID("disco2");
		roomListQuery.setTo(ConstantData.hostService);
		roomListQuery.setDefaultXmlns("http://jabber.org/protocol/disco#items");
		roomListQuery.setFrom(userName+"@"+ConstantData.host);
		
		System.out.println("PACCHETTO DI PROVA per richiesta lista stanze");
		System.out.println(roomListQuery.toXML());
		/*System.out.println("PACCHETTO DI PROVA per richiesta form");
		System.out.println(roomForm.toXML());*/
		
		ConnectionManager.getConnectionManager().getConnection().sendPacket(roomListQuery);
		
		/*mi metto in ascolto sincrono (bloccante) in attesa di una risposta*/
		PacketFilter iqFilter = new PacketTypeFilter(IQ.class);
		PacketFilter iqTypeFilter = new IQTypeFilter(IQ.Type.RESULT);
		PacketFilter packetIdFilter = new PacketIDFilter ("disco2");
		PacketFilter andFilter = new AndFilter (iqFilter, iqTypeFilter, packetIdFilter);
		
		PacketCollector collector = connectionManager.getConnection().createPacketCollector(andFilter);
		Packet packet = collector.nextResult();
		System.out.println("LISTA DELLE STANZE");
		System.out.println(packet.toXML());
		collector.cancel();
		
		DiscoverItems roomList = (DiscoverItems) packet;
		Iterator<DiscoverItems.Item> it = roomList.getItems();
		Vector rooms = new Vector();
		while (it.hasNext()) {
			DiscoverItems.Item i = it.next();
			System.out.println(i.getName());
			rooms.add(i.getName());
		}
		return rooms;
	}
	
	public void joinRoom(String roomName) {
		
		Presence pres = new Presence(Presence.Type.available);
		
		pres.setFrom(userName+"@"+ConstantData.host);
		pres.setTo(roomName+"@"+ConstantData.hostService+ "/" + userName);
		MUGSetup mugSetup = new MUGSetup();
		mugSetup.setElementName("game");
		mugSetup.setNameSpace("http://jabber.org/protocol/mug");
		mugSetup.setVar("http://jabber.org/protocol/mug/hearts");
		pres.addExtension(mugSetup);
		connectionManager.getConnection().sendPacket(pres);
	}

	/*metodo utilizzato per attendere i giocatori*/
	
	public Vector getUserList(String room){

		DiscoverItems userListQuery = new DiscoverItems();
		userListQuery.setTo(room+"@conference.tauron");
		userListQuery.setFrom(userName+"@"+ConstantData.host);
		userListQuery.setType(Type.GET);
		
		ConnectionManager.getConnectionManager().getConnection().sendPacket(userListQuery);

		
		PacketFilter iqFilter = new PacketTypeFilter(IQ.class);
		PacketFilter iqTypeFilter = new IQTypeFilter(IQ.Type.RESULT);
		PacketFilter andFilter = new AndFilter (iqFilter, iqTypeFilter);
		
		PacketCollector collector = connectionManager.getConnection().createPacketCollector(andFilter);
		Packet packet = collector.nextResult();
		System.out.println("LISTA UTENTI");
		System.out.println(packet.toXML());
		collector.cancel();
		
		DiscoverItems userList = (DiscoverItems) packet;
		Iterator<DiscoverItems.Item> it = userList.getItems();
		Vector users = new Vector();
		while (it.hasNext()) {
			
			DiscoverItems.Item i = it.next();
			System.out.println(i.getEntityID());
			String id = i.getEntityID();
			  int index = id.indexOf("/");
              String userName = null;
              if (index!=-1)
                  userName = id.substring(index+1);
              
			users.add(userName);
		}
		return users;
	}

	public void setJoinedRoom(Room aRoom) {
		this.queryListener.setRoom(Room.getRoomInstance());
	}
	
	public void getUserStatus(String user){
		DiscoverItems userInfo = new DiscoverItems();
        userInfo.setPacketID("disco2");
        userInfo.setTo("lobby@conference.tauron/"+user);
        userInfo.setType(Type.GET);
        userInfo.setFrom("lobby@conference.tauron/"+userName);
        ConnectionManager.getConnectionManager().getConnection().sendPacket(userInfo);
	}
	public void getRoomStatus(String user){
		DiscoverItems userInfo = new DiscoverItems();
        userInfo.setPacketID("disco3");
        userInfo.setTo("lobby@conference.tauron/"+user);
        userInfo.setType(Type.GET);
        userInfo.setFrom("lobby@conference.tauron/"+userName);
        ConnectionManager.getConnectionManager().getConnection().sendPacket(userInfo);
	}
}