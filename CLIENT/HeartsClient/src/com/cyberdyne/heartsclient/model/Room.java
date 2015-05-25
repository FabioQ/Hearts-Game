package com.cyberdyne.heartsclient.model;

import java.util.Hashtable;
import java.util.Vector;

public class Room {
	
	private static Room room = null;
	
	/**
	 * @uml.property  name="status"
	 */
	private String status = null;
	/**
	 * @uml.property  name="nameRoom"
	 */
	private String nameRoom = null;
	/**
	 * @uml.property  name="myAffiliation"
	 */
	private String myAffiliation= null;
	/**
	 * @uml.property  name="myRole"
	 */
	private String myRole = null;
	/**
	 * @uml.property  name="prevRole"
	 */
	private String prevRole = null;
	/**
	 * @uml.property  name="nextRole"
	 */
	private String nextRole = null;
	/**
	 * @uml.property  name="nextNextRole"
	 */
	private String nextNextRole = null;
	/**
	 * @uml.property  name="botNum"
	 */
	private String botNum = null;
	/**
	 * @uml.property  name="players"
	 * @uml.associationEnd  qualifier="username:java.lang.String com.cyberdyne.heartsclient.model.Player"
	 */
	private Hashtable players;
    /**
	 * @uml.property  name="playersNumber"
	 */
    private int playersNumber = 0;
    /**
	 * @uml.property  name="spectatorsNumber"
	 */
    private int spectatorsNumber = 0;
    /**
	 * @uml.property  name="game"
	 * @uml.associationEnd  inverse="room:com.cyberdyne.heartsclient.model.Game"
	 */
    private Game game;
    /**
	 * @uml.property  name="playersByRole"
	 * @uml.associationEnd  qualifier="role:java.lang.String java.lang.String"
	 */
    private Hashtable playersByRole;
	
	public Room(String name) {
		this.nameRoom = name;
		this.players = new Hashtable();
		this.playersByRole = new Hashtable();
	}
	
	public static synchronized Room createRoom(String name) {
		
		room = new Room(name);
		
		return room;
	}
	
	public static Room getRoomInstance(){
		return room;
	}
	
	public static void deleteRoomInstance(){
		room = null;
	}
	
	public void addPlayer(String role, String affiliation, String username){
		System.out.println("ROOM: aggiungo utente: "+ username);
		if((this.players.containsKey(username)) == false){
			Player aPlayer = new Player (role, affiliation, username);
			this.players.put(username, aPlayer);
			if (role.equalsIgnoreCase("spectator"))
				this.spectatorsNumber++;
			else {
				this.playersNumber++;
				this.playersByRole.put(role, username);
			}
		}
	}
	
	public Player getPlayer(String username){
		return (Player) this.players.get(username);

	}
	
	public void removePlayer(String username){
		String role = ((Player)this.players.get(username)).getRole();
                if (((Player)this.players.get(username)).getRole().equalsIgnoreCase("spectator"))
                    this.spectatorsNumber--;
                else this.playersNumber--;
                
		this.players.remove(username);
		
		if (!role.equalsIgnoreCase("spectator"))
			this.playersByRole.remove(role);
	}
	
	/**
	 * @return
	 * @uml.property  name="status"
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 * @uml.property  name="status"
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * @return
	 * @uml.property  name="myAffiliation"
	 */
	public String getMyAffiliation() {
		return myAffiliation;
	}
	
	/**
	 * @param affiliation
	 * @uml.property  name="myAffiliation"
	 */
	public void setMyAffiliation(String affiliation) {
		this.myAffiliation = affiliation;
	}

	/**
	 * @param role
	 * @uml.property  name="myRole"
	 */
	public void setMyRole(String role) {
		this.myRole = role;
		if(role.equalsIgnoreCase("north")){ 
			setPrevRole("west");
			setNextRole("east");
			setNextNextRole("south");
		}
		if(role.equalsIgnoreCase("east")) {
			setPrevRole("north");
			setNextRole("south");
			setNextNextRole("west");
		}
		if(role.equalsIgnoreCase("south")) {
			setPrevRole("east");
			setNextRole("west");
			setNextNextRole("north");
		}
		if(role.equalsIgnoreCase("west")) {
			setPrevRole("south");
			setNextRole("north");
			setNextNextRole("east");
		}
	}

	/**
	 * @return
	 * @uml.property  name="myRole"
	 */
	public String getMyRole() {
		return this.myRole;
	}

	/**
	 * @return
	 * @uml.property  name="nameRoom"
	 */
	public String getNameRoom() {
		return nameRoom;
	}

	/**
	 * @param nameRoom
	 * @uml.property  name="nameRoom"
	 */
	public void setNameRoom(String nameRoom) {
		this.nameRoom = nameRoom;
	}

	/**
	 * @param botNum
	 * @uml.property  name="botNum"
	 */
	public void setBotNum(String botNum) {
		this.botNum = botNum;
	}

	/**
	 * @return
	 * @uml.property  name="botNum"
	 */
	public String getBotNum() {
		return botNum;
	}
        
    /**
	 * @return
	 * @uml.property  name="playersNumber"
	 */
    public int getPlayersNumber(){
        return this.playersNumber;
        
    }
	
    /**
	 * @return
	 * @uml.property  name="spectatorsNumber"
	 */
    public int getSpectatorsNumber(){
        return this.spectatorsNumber;
    }
    
    /**
	 * @return
	 * @uml.property  name="players"
	 */
    public Hashtable getPlayers(){
    	return this.players;
    }

	/**
	 * @return
	 * @uml.property  name="prevRole"
	 */
	public String getPrevRole() {
		return prevRole;
	}

	/**
	 * @param prevRole
	 * @uml.property  name="prevRole"
	 */
	public void setPrevRole(String prevRole) {
		this.prevRole = prevRole;
	}

	/**
	 * @return
	 * @uml.property  name="game"
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * @param game
	 * @uml.property  name="game"
	 */
	public void setGame(Game game) {
		this.game = game;
	}

	/**
	 * @return
	 * @uml.property  name="nextRole"
	 */
	public String getNextRole() {
		return nextRole;
	}

	/**
	 * @param nextRole
	 * @uml.property  name="nextRole"
	 */
	public void setNextRole(String nextRole) {
		this.nextRole = nextRole;
	}

	/**
	 * @return
	 * @uml.property  name="nextNextRole"
	 */
	public String getNextNextRole() {
		return nextNextRole;
	}

	/**
	 * @param nextNextRole
	 * @uml.property  name="nextNextRole"
	 */
	public void setNextNextRole(String nextNextRole) {
		this.nextNextRole = nextNextRole;
	}

	public String getPlayersByRole(String role) {
		
		return (String)playersByRole.get(role);
	}


	
	
}