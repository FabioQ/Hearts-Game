/**
 * Copyright (C) 2008-2009 Guenther Niess. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.frogx.service.core;


import org.xmpp.component.ComponentException;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

import org.dom4j.Element;
import org.dom4j.QName;
import org.frogx.service.api.MUGManager;
import org.frogx.service.api.MUGOccupant;
import org.frogx.service.api.MUGRoom;
import org.frogx.service.api.MUGService;

/**
 * The first implementation of an  {@see  MUGOccupant} . A Multi-User Gaming occupant represents an user who is associated with a game room. It handles information about this user such as the presence status, affiliation, nickname or real  {@see  JID}  and provides the utility to send him a  {@see  Packet} .
 * @author  G&uuml;nther Nie&szlig;
 */
public class DefaultMUGOccupant implements MUGOccupant {
	
	/**
	 * The game room which this occupant is associated.
	 * @uml.property  name="room"
	 * @uml.associationEnd  
	 */
	private MUGRoom room;
	
	/**
	 * The real Jabber ID is the account on which this user is
	 * signed on.
	 */
	private JID realJID;
	
	/**
	 * The nickname of this occupant within the game room.
	 */
	private String nick;
	
	/**
	 * The users last presence status within the game room.
	 * @uml.property  name="presence"
	 */
	private Presence presence;
	
	/**
	 * The affiliation represents the authorization status of the occupant.
	 * @uml.property  name="affiliation"
	 * @uml.associationEnd  
	 */
	private MUGOccupant.Affiliation affiliation;
	
	/**
	 * The   {@see   ComponentManager}   provides a utility for logging and routing XMPP stanzas.
	 * @uml.property  name="mugManager"
	 * @uml.associationEnd  
	 */
	private MUGManager mugManager;
	
	/**
	 * Create an occupant of a game room and try to register with the 
	 * {@see net.sf.openfire.mug.lib.MUGMatch}.
	 * 
	 * @param room The occupant is participating in this room.
	 * @param realJID The JID of the user account on which this user is signed on.
	 * @param nickname Every participant has a nickname in a game room.
	 * @param affiliation The affiliation represents the authorization status of 
	 * the occupant.
	 * @param presence The users last presence status within the game room.
	 * @param componentManager A {@see ComponentManager} utility for logging and 
	 * sending XMPP Stanzas.
	 */
	public DefaultMUGOccupant(MUGRoom room, JID realJID, String nickname,
			MUGOccupant.Affiliation affiliation, Presence presence,
			MUGManager mugManager) {
		// Initialize the occupant
		this.mugManager = mugManager;
		this.room = room;
		this.nick = nickname;
		this.affiliation = affiliation;
		this.realJID = realJID;
		setPresence(presence);
		if ((room.getMatch() != null) && !hasRole()) {
			room.getMatch().reserveFreeRole(this);
			if (!hasRole())
				room.getMatch().addSpectator(this);
		}
	}
	
	/**
	 * Called when the occupant is leaving the game room.
	 * Leave the match and clean things up.
	 */
	public void destroy() {
		room.getMatch().leave(this);
		room = null;
		realJID = null;
		nick = null;
		presence = null;
		affiliation = null;
	}
	
	/**
	 * Get the nickname of this occupant.
	 * 
	 * @return The nickname of this occupant.
	 */
	public String getNickname() {
		return nick;
	}
	
	/**
	 * Set a new nickname for the occupants attributes.<br>
	 * <b>Note:</b>
	 * A {@see MUGRoom} manage its occupants and therefore
	 * use {@see MUGRoom#changeNickname(String, String, Presence)}
	 * to ensure the room handles the occupant with the new nickname
	 * correctly.
	 * 
	 * @param nickname The new nickname which should be used.
	 */
	public void changeNickname(String nickname) {
		nick = nickname;
		presence.setFrom(getRoomAddress());
	}
	
	/**
	 * Obtain the {@see JID} representing this occupant in a room.
	 * The Jabber ID has the form: room@service/nickname.
	 *
	 * @return The Jabber ID that represents this occupant in the room.
	 */
	public JID getRoomAddress() {
		return new JID(room.getName(),room.getMUGService().getDomain(), nick);
	}
	
	/**
	 * Get the real {@see JID} of this occupant.
	 * The real Jabber ID is the account on which this user is
	 * signed on.
	 * 
	 * @return The Jabber ID of the user.
	 */
	public JID getUserAddress() {
		return realJID;
	}
	
	/**
	 * Sets a new presence information for the occupant.<br> <b>Note:</b> This doesn't include to announce the new presence in the room.
	 * @param presence  The new presence for the occupant.
	 * @uml.property  name="presence"
	 */
	public void setPresence(Presence presence) {
		Element element = presence.getElement().element(
				QName.get("game", MUGService.mugNS));
		if (element != null) {
			presence.getElement().remove(element);
		}
		this.presence = presence;
		this.presence.setFrom(getRoomAddress());
	}
	
	/**
	 * Obtain the actual presence information for this occupant including information about his affiliation and game role.
	 * @return  The current presence.
	 * @uml.property  name="presence"
	 */
	public Presence getPresence() {
		Presence extendedPresence = presence.createCopy();
		Element gameElement = extendedPresence.addChildElement("game", MUGService.mugNS);
		Element item = gameElement.addElement("item");
		item.addAttribute("affiliation", affiliation.name());
		if (hasRole())
			item.addAttribute("role", getRoleName());
		if (room.isNonAnonymous())
			item.addAttribute("jid", getUserAddress().toString());
		return extendedPresence;
	}
	
	/**
	 * Set a new  {@see  Affiliation}  of this occupant. The affiliation represents the authorization.
	 * @param newAffiliation  The new affiliation of this occupant.
	 * @uml.property  name="affiliation"
	 */
	public void setAffiliation(Affiliation newAffiliation) {
		affiliation = newAffiliation;
		setPresence(presence);
	}
	
	/**
	 * Get the  {@see  Affiliation}  of this occupant. The affiliation represents the authorization.
	 * @return  The affiliation of this occupant.
	 * @uml.property  name="affiliation"
	 */
	public Affiliation getAffiliation() {
		return affiliation;
	}
	
	/**
	 * Return true if the occupant has a game role and therefore is
	 * a player in the match.
	 * It returns false if the occupant is a spectator.
	 * 
	 * @return True if the occupant is a player or false otherwise.
	 */
	public boolean hasRole() {
		String roleName = getRoleName();
		return ((roleName != null) && 
				(roleName.trim().length() > 0) &&
				!"none".equals(roleName));
	}
	
	/**
	 * Get the name of the role which this occupant is reserved.
	 * 
	 * @return The name of the occupants role.
	 */
	public String getRoleName() {
		return ((room != null) && (room.getMatch() != null)) ? 
				room.getMatch().getRole(this) : null;
	}
	
	/**
	 * Get the {@see MUGRoom} associated with this occupant.
	 * Each occupant object represents one participant in one 
	 * game room.
	 * 
	 * @return The game room which this occupant is participating.
	 */
	public MUGRoom getGameRoom() {
		return room;
	}
	
	/**
	 * Sends a packet to the occupant.
	 *
	 * @param packet The packet to send.
	 */
	public void send(Packet packet) throws ComponentException {
		if (packet == null) {
			return;
		}
		packet.setTo(realJID);
		
		mugManager.sendPacket(room.getMUGService(), packet);
	}
}
