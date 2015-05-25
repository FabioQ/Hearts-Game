/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyberdyne.heartsclient.listeners;


import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PacketExtensionFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import com.cyberdyne.heartsclient.controller.ConnectionManager;
import com.cyberdyne.heartsclient.model.Room;
import com.cyberdyne.heartsclient.providers.MUGMessage;
import com.cyberdyne.heartsclient.providers.MUGSetup;
import com.cyberdyne.heartsclient.view.GameView;

/**
 *
 * @author dleonard
 */
public class MainListener {
    
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
	 * @uml.property  name="presenceFilter"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private PacketFilter presenceFilter = new PacketTypeFilter(Presence.class);	
    /**
	 * @uml.property  name="mug"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private PacketFilter mug = new PacketExtensionFilter("game","http://jabber.org/protocol/mug");
    /**
	 * @uml.property  name="mugHearts"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private PacketFilter mugHearts = new PacketExtensionFilter("http://jabber.org/protocol/mug/hearts");
    /**
	 * @uml.property  name="orFilter"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private PacketFilter orFilter = new OrFilter(mug, mugHearts);
    /**
	 * @uml.property  name="statusUserFilter"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private PacketFilter statusUserFilter = new AndFilter(mug,presenceFilter);
    
    /**
	 * @uml.property  name="messageFilter"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private PacketFilter messageFilter = new PacketTypeFilter(Message.class);
    /**
	 * @uml.property  name="mugTurn"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private PacketFilter mugTurn = new PacketExtensionFilter("turn","http://jabber.org/protocol/mug#user");
    /**
	 * @uml.property  name="turnFilter"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private PacketFilter turnFilter = new AndFilter(mugTurn,messageFilter); 
    
    /**
	 * @uml.property  name="room"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private Room room;
    /**
	 * @uml.property  name="tonotify"
	 * @uml.associationEnd  multiplicity="(1 1)" inverse="mainList:com.cyberdyne.heartsclient.view.GameView"
	 */
    private GameView tonotify;
    
    /**
	 * @uml.property  name="statusUserListener"
	 * @uml.associationEnd  
	 */
    private PacketListener statusUserListener;
    /**
	 * @uml.property  name="messageListener"
	 * @uml.associationEnd  
	 */
    private PacketListener messageListener;
    
    
    public MainListener (Room room, GameView tonotify){
        this.room = Room.getRoomInstance(); 
        this.tonotify = tonotify;
    }
    
    
    public void listen (){
        System.out.println("Listener Avviato");
        this.statusUserListener = new PacketListener() {
        private int messageCounter = 0;

        public void processPacket(Packet packet) {
            /*questo metodo viene invocato per ogni pacchetto in entrata. Se è stato specificato un filtro
             * come nel nostro caso, allora il metodo verrà invocato esclusivamente quando ci sono pacchetti
             * che superano il filtro
             */
            MUGSetup content = (MUGSetup)packet.getExtension("game", "http://jabber.org/protocol/mug");
            String sender = packet.getFrom();
            
            String role = null;
            String affiliation = null;
            
            boolean fromUser = false;
            int index = sender.indexOf("/");
            String userName = null;
            if (index!=-1){
                fromUser = true;
                userName = sender.substring(index+1);
            }
            /*controllo il tipo di pacchetto ricevuto: se trovo il campo status si tratta di un pacchetto che
             * indica lo stato della stanza. Provvedo quindi ad aggiornare i campi dell'oggetto room.
             */
            /*TODO saranno necessari altri campi utili oltre a status e bot ?*/
            
            if(content.getStatus() != null){
            	System.out.println("STATO STANZA DA MESSAGGIO:" + content.getStatus());
            	Room.getRoomInstance().setStatus(content.getStatus());
            	System.out.println("Nuovo stato stanza : " + Room.getRoomInstance().getStatus());
            	System.out.println("NUMERO BOTS CONNESSI:" + content.getBotNum());
                if(content.getBotNum()!=null) {
                	
                	String numeroBot = content.getBotNum();
                	Room.getRoomInstance().setBotNum(numeroBot);
                    int intNumBot = Integer.parseInt(numeroBot);
                    for (int i=0; i<intNumBot; i++) {
                    	if (i==0)
                    		role="west";
                    	if (i==1)
                    		role="south";
                    	if (i==2)
                    		role="east";
                    	System.out.println("STATO STANZA NEL LOOP" +Room.getRoomInstance().getStatus() );
                    	if ((Room.getRoomInstance().getStatus().equalsIgnoreCase("inactive")) ||
                    			((Room.getRoomInstance().getStatus().equalsIgnoreCase("active"))&&
                    					(Room.getRoomInstance().getMyRole().equalsIgnoreCase("spectator")))) {
                    		Room.getRoomInstance().addPlayer(role, role, role);
                    		System.out.println("AGGIUNTO GIOCATORE");
                    		try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                    		System.out.println("NOTIFICO PRESENZA NUOVO GIOCATORE");

                    		tonotify.notifyUserConnection(role);
                    	}
                    }
                }
                
                if (((content.getStatus().equalsIgnoreCase("active"))&&
                		(!Room.getRoomInstance().getMyRole().equalsIgnoreCase("spectator"))&&(messageCounter==0))||
                		((content.getStatus().equalsIgnoreCase("active"))&&
                				(Room.getRoomInstance().getMyRole().equalsIgnoreCase("spectator")))){
                	messageCounter++;
                	System.out.println("MAIN LISTENER: set delle mani dei giocatori");
                	/*room.getGame().setHands(content.getPlayerCards(room.getMyRole()), room.getMyRole());*/
                	if (content.getPlayerCards("north")==null)
                		System.out.println("getPlayerCards di nord restituisce null");
                	if (content.getPlayerCards("east")==null)
                		System.out.println("getPlayerCards di east restituisce null");
                	if (content.getPlayerCards("south")==null)
                		System.out.println("getPlayerCards di south restituisce null");
                	if (content.getPlayerCards("west")==null)
                		System.out.println("getPlayerCards di west restituisce null");
                	
                	System.out.println("OK ,non ci sono vettori null..per�...");
                	
                	if(Room.getRoomInstance().getGame()==null)
                		System.out.println("MAIN LISTENER...GetGame Null");
                	
                	Room.getRoomInstance().getGame().setHands(content.getPlayerCards("north"), "north");
                	Room.getRoomInstance().getGame().setHands(content.getPlayerCards("east"), "east");
                	Room.getRoomInstance().getGame().setHands(content.getPlayerCards("south"), "south");
                	Room.getRoomInstance().getGame().setHands(content.getPlayerCards("west"), "west");
                	System.out.println("Main Listener: SET HANDS OK");
                	
                	if (Room.getRoomInstance().getMyRole().equalsIgnoreCase("spectator")) {
                		if((content.getOnTableN()!=null) &&((Room.getRoomInstance().getPlayersByRole("north"))!=null)) {
                        	System.out.println("Main Listener: CARD ON TABLE N " + content.getOnTableN());
                			Room.getRoomInstance().getGame().setPlayedCard(content.getOnTableN(), Room.getRoomInstance().getPlayersByRole("north"));
                			tonotify.cardOnTable(content.getOnTableN(),"north");
                		}
                		if(content.getOnTableE()!=null && ((Room.getRoomInstance().getPlayersByRole("east"))!=null)) {
                        	System.out.println("Main Listener: CARD ON TABLE E " + content.getOnTableE());
                			Room.getRoomInstance().getGame().setPlayedCard(content.getOnTableE(), Room.getRoomInstance().getPlayersByRole("east"));
                			tonotify.cardOnTable(content.getOnTableE(),"east");
                		}
                		if(content.getOnTableS()!=null && ((Room.getRoomInstance().getPlayersByRole("south"))!=null)) {
                        	System.out.println("Main Listener: CARD ON TABLE S " + content.getOnTableS());
                			Room.getRoomInstance().getGame().setPlayedCard(content.getOnTableS(), Room.getRoomInstance().getPlayersByRole("south"));
                			tonotify.cardOnTable(content.getOnTableS(),"south");
                		}
                		if(content.getOnTableW()!=null && ((Room.getRoomInstance().getPlayersByRole("west"))!=null)) {
                        	System.out.println("Main Listener: CARD ON TABLE W " + content.getOnTableW());
                			Room.getRoomInstance().getGame().setPlayedCard(content.getOnTableW(), Room.getRoomInstance().getPlayersByRole("west"));
                			tonotify.cardOnTable(content.getOnTableW(),"west");
                		}
                		
                		
                		
                	}
                	  /*notificare update stanza*/
                	tonotify.notifyRoomUpdate();
                }
                /*notificare update stanza*/
                /*tonotify.notifyRoomUpdate();*/
            }
            
            /*altrimenti (se non trovo il campo status) sicuramente il mittente e' un utente specifico*/        
            else if (fromUser == true){
            /*controllo di che tipo di messaggio di presence si tratta (available, unavailable)*/
            	
                if(((Presence)packet).getType() != null){
                    if(((Presence)packet).getType() == Presence.Type.unavailable)
                    {
                        /*TODO devo eliminare il giocatore dalla lista dei giocatori della stanza*/
                    	String rolePlayer = Room.getRoomInstance().getPlayer(userName).getRole();
                    	String affiliationPlayer = Room.getRoomInstance().getPlayer(userName).getAffiliation();
                    	Room.getRoomInstance().removePlayer(userName);
                        /*notifico il logout di un giocatore*/
                        tonotify.notifyUserDisconnection(userName, rolePlayer, affiliationPlayer);
                    }
                    
                    else {/*si dovrebbe trattare di un messaggio assimilabile al tipo "available"*/
                        /*quindi considero un nuovo utente entrato nella stanza*/
	                        if(content.getItemRole() != null){
	                            	role = content.getItemRole();
	                            	if(userName.equalsIgnoreCase(myUserName)){
	                            		Room.getRoomInstance().setMyRole(role);
	                            	}
	                        }
	                        if(content.getItemAffiliation()!=null){
	                        	affiliation = content.getItemAffiliation();
	                        	if(userName.equalsIgnoreCase(userName)){
	                        		Room.getRoomInstance().setMyAffiliation(affiliation);
	                        	}
	                        }
	                        /*aggiungo il giocatore nella stanza*/
	                        Room.getRoomInstance().addPlayer(role, affiliation, userName);
	                        /*notifico la presenza di un nuovo giocatore*/
	                        tonotify.notifyUserConnection(userName);
                     }
                }
            }
        }
        };
        
         	this.messageListener = new PacketListener() {
            public void processPacket(Packet packet) {
                /*questo metodo viene invocato per ogni pacchetto in entrata. Se è stato specificato un filtro
                 * come nel nostro caso, allora il metodo verrà invocato esclusivamente quando ci sono pacchetti
                 * che superano il filtro
                 */
                MUGMessage content = (MUGMessage)packet.getExtension("turn", "http://jabber.org/protocol/mug#user");
                String sender = packet.getFrom();
                
                boolean fromUser = false;
                int index = sender.indexOf("/");
                String userName = null;
                if (index!=-1){
                    fromUser = true;
                    userName = sender.substring(index+1);
                }
                
                String bot = content.getSender();
                if ((bot!=null) && (!bot.equalsIgnoreCase(""))){
                	/*significa che � un messaggio di un bot*/
                	userName = bot;
                }
                System.out.println(userName);
                System.out.println(content.getTurnType());
                
                /*controllo il tipo di turno*/
                if(content.getTurnType() != null){
                	if (content.getTurnType().equalsIgnoreCase("firstturn")){
                		/*se si tratta del primo turno, cerco il messaggio proveniente dall'utente alla mia destra*/
                		if(Room.getRoomInstance().getPlayer(userName).getRole().equalsIgnoreCase(Room.getRoomInstance().getPrevRole())){
                    		System.out.println("Ho ricevuto le carte del giocatore alla mia destra: ");
                    		Room.getRoomInstance().getGame().setIncomingCards(content.getCard1(), content.getCard2(), content.getCard3());
                			tonotify.notifyIncomingCards();
                		}
                		if(Room.getRoomInstance().getPlayer(userName).getRole().equalsIgnoreCase("west")){
                			Room.getRoomInstance().getGame().joinIncomingCards();
                			tonotify.notifyFirstTurnEnd();
                		}
                		
                		if((Room.getRoomInstance().getPlayer(userName).getRole().equalsIgnoreCase(Room.getRoomInstance().getNextRole()) ||
                				Room.getRoomInstance().getPlayer(userName).getRole().equalsIgnoreCase(Room.getRoomInstance().getNextNextRole())) ||
                				(Room.getRoomInstance().getMyRole().equalsIgnoreCase("spectator"))){
                			tonotify.notifyFirstTurn(Room.getRoomInstance().getPlayer(userName).getRole(),content.getCard1(), content.getCard2(), content.getCard3());
                		}
                	}
                	else if (content.getTurnType().equalsIgnoreCase("standard")){
                		/*se si tratta del turno di gioco standard, mi interessano i messaggi di tutti gli utenti del tavolo*/
                		Room.getRoomInstance().getGame().setPlayedCard(content.getCard1(), userName);
                		tonotify.notifyPlayedCard(content.getCard1(), userName);
                		
                	}
                	
                	/*aggiungo la gestione del messaggio di fine partita*/
                	else if (content.getTurnType().equalsIgnoreCase("matchend")){
                		tonotify.notifyMatchEnd(content.getScore("north"),content.getScore("east"),
                				content.getScore("south"),content.getScore("west"));
                		
                	}
                }
            }
            };
   
        /*registro il listener alla connessione*/
        connectionManager.getConnection().addPacketListener(this.statusUserListener, statusUserFilter);
        connectionManager.getConnection().addPacketListener(messageListener, turnFilter);
    }
    
    public void removeMainListener(){
    	connectionManager.getConnection().removePacketListener(this.statusUserListener);
    	connectionManager.getConnection().removePacketListener(this.messageListener);
    	
    	
    	System.out.println("CHIUSO IL MAIN LISTENER");
    }
}
