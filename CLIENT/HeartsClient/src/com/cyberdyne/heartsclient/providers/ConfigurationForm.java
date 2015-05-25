package com.cyberdyne.heartsclient.providers;

import org.jivesoftware.smack.packet.IQ;

public class ConfigurationForm extends IQ {
	
	/**
	 * @uml.property  name="roomName"
	 */
	private String roomName;
	/**
	 * @uml.property  name="numBot"
	 */
	private int numBot;

	/**
	 * @return
	 * @uml.property  name="numBot"
	 */
	public int getNumBot() {
		return numBot;
	}

	/**
	 * @param numBot
	 * @uml.property  name="numBot"
	 */
	public void setNumBot(int numBot) {
		this.numBot = numBot;
	}

	/**
	 * @return
	 * @uml.property  name="roomName"
	 */
	public String getRoomName() {
		return roomName;
	}

	/**
	 * @param roomName
	 * @uml.property  name="roomName"
	 */
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	
	/* TODO  NELLE CONFIGURAZIONE DEL GIOCO CI ANDRA' IL NUMERO DI BOT DA INVIARE AL SERVER 
	 * N.B QUESTA E' LA CONFIGURAZIONE PER IL TICTACTOE
	 */
	@Override
	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<query xmlns=\"http://jabber.org/protocol/mug#owner\"> " +
		 "<options> " +
		 "<x xmlns=\"jabber:x:data\" type=\"submit\"> " +
		 "<field var=\"FORM_TYPE\"> " +
		 "<value>http://jabber.org/protocol/mug#matchconfig</value> " +
		 "</field> " +
		 "<field var=\"mug#roomconfig_roomname\">" +
		 "<value>").append(getRoomName()).append("</value>" +
		 "</field>" +
		 "<field var=\"mug#roomconfig_matchdesc\">" +
		 "<value></value>" +
		 "</field>" +
		 "<field var=\"mug#roomconfig_moderated\">" +
		 "<value>0</value>" +
		 "</field>" +
		 "<field var=\"mug#roomconfig_allowinvites\">" +
		 "<value>0</value>" +
		 "</field>" +
		 "<field var=\"mug#roomconfig_maxusers\">" +
		 "<value>10</value>" +
		 "</field>" +
		 "<field var=\"mug#roomconfig_publicroom\">" +
		 "<value>1</value>" +
		 "</field>" +
		 "<field var=\"mug#roomconfig_membersonly\">" +
		 "<value>0</value>" +
		 "</field>" +
		 "<field var=\"mug#roomconfig_anonymity\">" +
		 "<value>non-anonymous</value>" +
		 "</field>" +
		 "<field var=\"mug#roomconfig_passwordprotectedroom\">" +
		 "<value>0</value>" +
		 "</field>" +
		 "</x>" +
		 "<options xmlns=\"http://jabber.org/protocol/mug/hearts\">" +
		 "<x xmlns=\"jabber:x:data\" type=\"submit\">" +
		 "<field var=\"FORM_TYPE\">" +
		 "<value>http://jabber.org/protocol/mug/hearts#heartsconfig</value>" +
		 "</field>" +
		 /*"<field var=\"mug/Hearts#config_rows\">" +
		 "<value>3</value>" +
		 "</field>" +
		 "<field var=\"mug/Hearts#config_cols\">" +
		 "<value>3</value>" +
		 "</field>" +
		 "<field var=\"mug/Hearts#config_strike\">" +
		 "<value>3</value>" +
		 "</field>" +
		 "<field var=\"mug/Hearts#config_first\">" +
		 "<value>x</value>" +
		 "</field>" +*/
		 "<field var=\"mug/hearts#config_bot\">" +
		 "<value>").append(getNumBot()).append("</value>" +
		 "</field>" +
		 "</x>" +
		 "</options>" +
		 "</options>" +
		 "</query>");
		
		return buf.toString();
	}

}
