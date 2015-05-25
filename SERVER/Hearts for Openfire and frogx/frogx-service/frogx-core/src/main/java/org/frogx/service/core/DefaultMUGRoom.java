/**
 * DefaultMUGRoom - A Multi-User Gaming room.
 * Some parts are inspired by the LocalMUCRoom of the Openfire XMPP
 * server.
 * 
 * Copyright (C) 2004-2008 Jive Software. All rights reserved.
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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.component.ComponentException;
import org.xmpp.forms.FormField;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;
import org.xmpp.packet.Presence.Type;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.frogx.service.api.MUGManager;
import org.frogx.service.api.MUGMatch;
import org.frogx.service.api.MUGOccupant;
import org.frogx.service.api.MUGRoom;
import org.frogx.service.api.MUGService;
import org.frogx.service.api.MultiUserGame;
import org.frogx.service.api.exception.CannotBeInvitedException;
import org.frogx.service.api.exception.ConflictException;
import org.frogx.service.api.exception.ForbiddenException;
import org.frogx.service.api.exception.GameConfigurationException;
import org.frogx.service.api.exception.NotFoundException;
import org.frogx.service.api.exception.RequiredPlayerException;
import org.frogx.service.api.exception.RoomLockedException;
import org.frogx.service.api.exception.ServiceUnavailableException;
import org.frogx.service.api.exception.UnauthorizedException;
import org.frogx.service.api.exception.UnsupportedGameException;
import org.frogx.service.api.exception.UserAlreadyExistsException;
import org.frogx.service.api.util.LocaleUtil;
import org.frogx.service.core.iq.IQOwnerHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The first implementation of a  {@see  MUGRoom} . A game room manages its occupants, sends game moves, invitations, presence information,...
 * @author  G&uuml;nther Nie&szlig;
 */
public class DefaultMUGRoom implements MUGRoom {
	
	private static final Logger log = LoggerFactory.getLogger(DefaultMUGRoom.class);
	
	/**
	 * This represents the privacy type of a   {@see   MUGRoom}  .
	 * @author   G&uuml;nther Nie&szlig;
	 */
	public enum Anonymity {
		
		/**
		 * @uml.property  name="nonAnonymous"
		 * @uml.associationEnd  
		 */
		nonAnonymous,
		
		/**
		 * @uml.property  name="semiAnonymous"
		 * @uml.associationEnd  
		 */
		semiAnonymous, 
		
		/**
		 * @uml.property  name="fullyAnonymous"
		 * @uml.associationEnd  
		 */
		fullyAnonymous;
	}
	
	/**
	 * The service hosting the room.
	 * @uml.property  name="mugService"
	 * @uml.associationEnd  
	 */
	private MUGService mugService;
	
	/**
	 * The ComponentManager provides a logging utility, localized Strings and allows to send XML stanzas.
	 * @uml.property  name="mugManager"
	 * @uml.associationEnd  
	 */
	private MUGManager mugManager;
	
	/**
	 * @uml.property  name="locale"
	 * @uml.associationEnd  
	 */
	private LocaleUtil locale;
	
	/**
	 * The game which can be played in this room
	 * @uml.property  name="game"
	 * @uml.associationEnd  
	 */
	MultiUserGame game;
	
	/**
	 * The running match represents the game logic and game state.
	 * @uml.property  name="match"
	 * @uml.associationEnd  
	 */
	MUGMatch match;
	
	/**
	 * The name of the room which is used in the JID address of the room.
	 * @uml.property  name="name"
	 */
	private String name;
	
	/**
	 * The natural language name of the room.
	 * @uml.property  name="naturalLanguageName"
	 */
	private String naturalLanguageName;
	
	/**
	 * Description of the room. The owner can change the description using the room configuration form.
	 * @uml.property  name="description"
	 */
	private String description;
	
	/**
	 * A public room means that the room is searchable and visible. This means that the room can be located using disco or search requests.
	 * @uml.property  name="publicRoom"
	 */
	private boolean publicRoom;
	
	/**
	 * Moderated rooms enables the owner to kick users, revoke roles and save and reload the match.
	 * @uml.property  name="moderated"
	 */
	private boolean moderated;
	
	/**
	 * In a member-only room a user cannot enter without being on the member list. This can be done by inviting the user to a room.
	 * @uml.property  name="membersOnly"
	 */
	private boolean membersOnly;
	
	/**
	 * A List of bare JIDs, that are game room's members.
	 */
	private List<String> members = new ArrayList<String>();
	
	/**
	 * The privacy type of this room. This describes who is able to see the occupants real JIDs.
	 * @uml.property  name="anonymity"
	 * @uml.associationEnd  
	 */
	private Anonymity anonymity;
	
	/**
	 * Some rooms may restrict the occupants that are able to send invitations.
	 * Sending an invitation in a members-only room adds the invitee to the members list.
	 */
	private boolean canOccupantsInvite;
	
	/**
	 * This describes if the room occupants are public available via a disco items query.
	 */
	private boolean canDiscoverOccupants;
	
	/**
	 * The password that every occupant should provide in order to enter the room.
	 * @uml.property  name="password"
	 */
	private String password = null;
	
	/**
	 * The max. number of occupants who are able to join the room.
	 * @uml.property  name="maxOccupants"
	 */
	private int maxOccupants;
	
	/**
	 * The occupants of the room accessible by the occupants nickname.
	 * @uml.property  name="occupants"
	 */
	private Map<String,MUGOccupant> occupants = new ConcurrentHashMap<String, MUGOccupant>();
	
	/**
	 * A list of the occupants nicknames who want to start the match.
	 */
	private List<String> startMatch = new ArrayList<String>();
	
	/**
	 * The bare JID of the room owner.
	 * @uml.property  name="owner"
	 */
	private String owner;
	
	/**
	 * The IQ handler for the owner namespace. It helps to configure the room.
	 * @uml.property  name="iqOwnerHandler"
	 * @uml.associationEnd  
	 */
	private IQOwnerHandler iqOwnerHandler;
	
	
	/**
	 * Create a game room.
	 * 
	 * @param service The {@see MUGService} which hosts this room.
	 * @param componentManager A {@see ComponentManager} provides a utility for sending 
	 *         packages and logging.
	 * @param roomName The room name which is used in the {@see JID} address of the room.
	 * @param game A {@see MultiUserGame} which can be played within the room.
	 * @param creator The {@see JID} of the user who is creating this room.
	 */
	public DefaultMUGRoom(MUGService service, 
			MUGManager mugManager, String roomName, 
			MultiUserGame game, JID creator) {
		this.mugService = service;
		this.mugManager = mugManager;
		this.locale = mugManager.getLocaleUtil();
		this.name = roomName;
		this.naturalLanguageName = roomName;
		this.description = roomName;
		this.game = game;
		this.owner = creator.toBareJID();
		this.iqOwnerHandler = new IQOwnerHandler(service,mugManager, this);
		loadDefaultValues();
		this.match = game.createMatch(this);
		log.debug(locale.getLocalizedString("mug.room.debug.create") + roomName);
	}
	
	public void destroy() {
		if (match != null)
			game.destroyMatch(this);
		match = null;
		game = null;
		name = null;
		description = null;
		password = null;
		if (occupants != null)
			occupants.clear();
		mugManager = null;
	}
	
	/**
	 * @return
	 * @uml.property  name="name"
	 */
	public String getName() {
		return name;
	}
	
	public MUGService getMUGService() {
		return mugService;
	}
	
	public JID getJID() {
		return new JID(getName(), getMUGService().getAddress().getDomain(), null);
	}
	
	/**
	 * @return
	 * @uml.property  name="game"
	 */
	public MultiUserGame getGame() {
		return game;
	}
	
	/**
	 * @return
	 * @uml.property  name="match"
	 */
	public MUGMatch getMatch() {
		return match;
	}
	
	/**
	 * @return
	 * @uml.property  name="occupants"
	 */
	public Collection<MUGOccupant> getOccupants() {
		return Collections.unmodifiableCollection(occupants.values());
	}
	
	/**
	 * @return
	 * @uml.property  name="naturalLanguageName"
	 */
	public String getNaturalLanguageName() {
		return naturalLanguageName;
	}
	
	/**
	 * @return
	 * @uml.property  name="description"
	 */
	public String getDescription() {
		return description;
	}
	
	public boolean isLocked() {
		return match.getStatus() == MUGMatch.Status.created;
	}
	
	public boolean isPasswordProtected() {
		return password != null && password.trim().length() > 0;
	}
	
	/**
	 * @return
	 * @uml.property  name="password"
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * @return
	 * @uml.property  name="membersOnly"
	 */
	public boolean isMembersOnly() {
		return membersOnly;
	}
	
	/**
	 * @return
	 * @uml.property  name="moderated"
	 */
	public boolean isModerated() {
		return moderated;
	}
	
	/**
	 * @return
	 * @uml.property  name="publicRoom"
	 */
	public boolean isPublicRoom() {
		return publicRoom;
	}
	
	public boolean isNonAnonymous() {
		return (anonymity == Anonymity.nonAnonymous);
	}
	
	public boolean isSemiAnonymous() {
		return (anonymity == Anonymity.semiAnonymous);
	}
	
	public boolean isFullyAnonymous() {
		return (anonymity == Anonymity.fullyAnonymous);
	}
	
	public boolean canOccupantsInvite() {
		return canOccupantsInvite;
	}
	
	public boolean canDiscoverOccupants() {
		return (!isLocked() && canDiscoverOccupants);
	}
	
	/**
	 * @return
	 * @uml.property  name="owner"
	 */
	public JID getOwner() {
		return new JID(owner);
	}
	
	/**
	 * @return
	 * @uml.property  name="maxOccupants"
	 */
	public int getMaxOccupants() {
		return maxOccupants;
	}
	
	public int getOccupantsCount() {
		return occupants.size();
	}
	
	public int getPlayersCount() {
		return match.getPlayers().size();
	}
	
	public Collection<String> getExtraFeatures() {
		// We don't have additional disco features
		return null;
	}
	
	public Collection<FormField> getExtraExtendedDiscoFields() {
		// We don't have additional extended disco fields
		return null;
	}
	
	public String getUID() {
		return getJID().toString();
	}
	
	/**
	 * Load the default room configuration.
	 */
	protected void loadDefaultValues() {
		//TODO: Read a adjustable default room configuration from DB
		canDiscoverOccupants = true;
		canOccupantsInvite = false;
		maxOccupants = 10;
		membersOnly = false;
		moderated = false;
		publicRoom = true;
		anonymity = Anonymity.semiAnonymous;
	}
	
	protected Presence getPresence() {
		Presence presence = new Presence();
		presence.setFrom(getJID());
		Element game = presence.addChildElement("game", MUGService.mugNS);
		Element statusElement = game.addElement("status");
		statusElement.addText(match.getStatus().name());
		if (match == null || match.getState() == null) {
			if (match == null)
				log.warn("[MUG] No match in room " + getJID() + "!");
			if (match.getState() == null)
				log.debug("[MUG] No game state available in room " + getJID());
		}
		else
			game.add(match.getState().createCopy());
		
		return presence;
	}
	
	protected void resetStartMatch() {
		startMatch.clear();
	}
	
	protected void sendBroadcastPacket(Packet packet, MUGOccupant sender) throws ComponentException {
		packet.setFrom(sender.getRoomAddress());
		
		for (MUGOccupant recipient : occupants.values()) {
			recipient.send(packet);
		}
	}
	
	private void onlyOwner(JID user) throws ForbiddenException {
		// Check Permissions: only owners, admins or the MUG service are ok
		if (!mugService.getAdmins().contains(user.toBareJID()) && 
				!owner.equals(user.toBareJID()) &&
				!getJID().equals(user))
			throw new ForbiddenException();
	}
	
	/**
	 * Set a human readable name of this room.
	 * @param name  the human readable name of this room.
	 * @uml.property  name="naturalLanguageName"
	 */
	public void setNaturalLanguageName(String name) {
		naturalLanguageName = name;
	}
	
	public void setAllowInvites(boolean canOccupantsInvite) {
		this.canOccupantsInvite = canOccupantsInvite;
	}
	
	/**
	 * @param membersOnly
	 * @uml.property  name="membersOnly"
	 */
	public void setMembersOnly(boolean membersOnly) {
		this.membersOnly = membersOnly;
	}
	
	public void setMUGService(MUGService service) {
		this.mugService = service;
	}
	
	/**
	 * @param password
	 * @uml.property  name="password"
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * @param publicRoom
	 * @uml.property  name="publicRoom"
	 */
	public void setPublicRoom(boolean publicRoom) {
		this.publicRoom = publicRoom;
	}
	
	/**
	 * @param moderated
	 * @uml.property  name="moderated"
	 */
	public void setModerated(boolean moderated) {
		this.moderated = moderated;
	}
	
	/**
	 * @param description
	 * @uml.property  name="description"
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @param maxOccupants
	 * @uml.property  name="maxOccupants"
	 */
	public void setMaxOccupants(int maxOccupants) {
		this.maxOccupants = maxOccupants;
	}
	
	/**
	 * Set the privacy type of this room.
	 * @param anonymity  describes who can see the real JID of occupants.
	 * @uml.property  name="anonymity"
	 */
	public void setAnonymity(Anonymity anonymity) {
		this.anonymity = anonymity;
	}
	
	public void addMember(JID newMember, MUGOccupant occupant) throws ForbiddenException {
		onlyOwner(occupant.getUserAddress());
		
		String bareJID = newMember.toBareJID();
		if (!members.contains(bareJID))
			members.add(bareJID);
	}
	
	public void broadcastPresence(MUGOccupant occupant) {
		Presence presence = occupant.getPresence().createCopy();
		for (MUGOccupant otherOccupant: occupants.values() ) {
			if (otherOccupant.equals(occupant))
				continue;
			try {
				// In semi-anonymous rooms we must add the real JID for room owners
				if ((anonymity == Anonymity.semiAnonymous) && 
						MUGOccupant.Affiliation.owner.equals(otherOccupant.getAffiliation())) {
					Presence extPresence = presence.createCopy();
					Element frag = extPresence.getChildElement("game", MUGService.mugNS);
					frag.element("item").addAttribute("jid", occupant.getUserAddress().toBareJID());
					otherOccupant.send(extPresence);
				}
				else {
					otherOccupant.send(presence);
				}
			}
			catch (ComponentException e) {
				log.error(locale.getLocalizedString("mug.room.error.presence")
						+ otherOccupant.getUserAddress(), e);
			}
		}
	}
	
	public void broadcastRoomPresence() {
		Packet roomPresence = getPresence();
		for (MUGOccupant recipient : occupants.values()) {
			try {
				recipient.send(roomPresence);
			} catch (ComponentException e) {
				log.error(locale.getLocalizedString("mug.room.error.presence")
						+ recipient.getUserAddress(), e);
			}
		}
	}
	
	public void broadcastTurn(Collection<Element> moves, MUGOccupant sender) throws ComponentException {
		Element rawMessage = DocumentFactory.getInstance().createDocument().addElement("message");
		Element turn = rawMessage.addElement("turn", MUGService.mugNS + "#user");
		
		if (moves != null) {
			for (Element move : moves) {
				move.setParent(null);
				turn.add(move);
			}
		}
			
		Message message = new Message(rawMessage, false);
		sendBroadcastPacket(message, sender);
	}
	
	public MUGOccupant changeNickname(String oldNick, String newNick, Presence newPresence) throws NotFoundException,
			ConflictException {
		MUGOccupant occupant = occupants.get(oldNick.toLowerCase());
		
		if (occupant == null)
			throw new NotFoundException();
		
		if (occupants.containsKey(newNick.toLowerCase()))
			throw new ConflictException();
		
		// Refresh start counter
		if (startMatch.contains(oldNick.toLowerCase())) {
			startMatch.remove(oldNick.toLowerCase());
			startMatch.add(newNick.toLowerCase());
		}
		
		// Submit changing
		occupants.remove(oldNick.toLowerCase());
		occupants.put(newNick.toLowerCase(), occupant);
		occupant.changeNickname(newNick);
		
		// Update presence
		if (newPresence != null)
			occupant.setPresence(newPresence);
		
		// Inform the occupants about the change
		Presence presence = occupant.getPresence().createCopy();
		presence.setFrom(new JID(getName(), mugService.getDomain(), oldNick));
		Element gameElement = presence.getChildElement("game", MUGService.mugNS);
		Element item = gameElement.element("item");
		item.addAttribute("nick", newNick);
		for (MUGOccupant otherOccupant: occupants.values() ) {
			// In semi-anonymous rooms we must add the real JID for room owners
			if ((anonymity == Anonymity.semiAnonymous) && 
					MUGOccupant.Affiliation.owner.equals(otherOccupant.getAffiliation())) {
				gameElement.element("item").addAttribute("jid", occupant.getUserAddress().toBareJID());
			}
			try {
				otherOccupant.send(presence);
			}
			catch (ComponentException e) {
				log.error(locale.getLocalizedString("mug.room.error.presence")
						+ otherOccupant.getUserAddress(), e);
			}
		}
		return occupant;
	}
	
	public void handleOwnerIQ(IQ iq, MUGOccupant occupant) throws ForbiddenException, 
			IllegalArgumentException, GameConfigurationException, UnsupportedGameException {
		iqOwnerHandler.handleIQ(iq, occupant);
	}
	
	public void invite(JID recipient, String reason, MUGOccupant invitor) throws ForbiddenException, 
			CannotBeInvitedException {
		if (canOccupantsInvite() || MUGOccupant.Affiliation.owner.equals(invitor.getAffiliation())) {
			Message message = new Message();
			message.setFrom(getJID());
			message.setTo(recipient);
			
			Element gameElement = message.addChildElement("game", MUGService.mugNS + "#user");
			Element invite = gameElement.addElement("invited");
			invite.addAttribute("var", game.getNamespace());
			if (invitor.getUserAddress() != null) {
				invite.addAttribute("from", invitor.getUserAddress().toBareJID());
			}
			if (reason != null && reason.length() > 0) {
				invite.addElement("reason").setText(reason);
			}
			if (isPasswordProtected()) {
				gameElement.addElement("password").setText(getPassword());
			}
			
			try {
				// TODO: Remove debug output
				log.debug("[MUG]: Sending: " + message.toXML());
				
				mugManager.sendPacket(mugService, message);
			}
			catch (Exception e) {
				log.error(locale.getLocalizedString("mug.room.error.invite"), e);
				throw new CannotBeInvitedException();
			}
		}
		else {
			throw new ForbiddenException();
		}
	}
	
	public MUGOccupant join(String nick, String passwd, JID fullJID, Presence presence) throws
			ServiceUnavailableException, RoomLockedException, UserAlreadyExistsException,
			UnauthorizedException, ForbiddenException, ComponentException {
		
		DefaultMUGOccupant occupant = null;
		boolean isOwner = true;
		
		// Check permission
		try {
			onlyOwner(fullJID);
		}
		catch (ForbiddenException e) {
			isOwner = false;
		}
		
		// Check capacity
		if (getMaxOccupants() > 0 && 
				getOccupantsCount() >= getMaxOccupants() &&
				!isOwner)
			throw new ServiceUnavailableException();
		
		// Check if the room is locked
		if (isLocked()) {
			if (!isOwner) {
				throw new RoomLockedException();
			}
		}
		
		// Check if the nickname is already used in the room
		if (occupants.containsKey(nick.toLowerCase())) {
			if (occupants.get(nick.toLowerCase()).getUserAddress().toBareJID().equals(fullJID.toBareJID())) {
				//TODO: Nickname exists in room, and belongs to this user, maybe kick the previous instance.
				// The new instance will "take over" the previous role or handle two user instances.
				// Participants in the room shouldn't notice anything has occurred.
				throw new UserAlreadyExistsException();
			}
			else
				throw new UserAlreadyExistsException();
		}
		
		// Check password
		if (isPasswordProtected()) {
			if (password == null || !password.equals(getPassword())) {
				throw new UnauthorizedException();
			}
		}
		
		// Set affiliation
		MUGOccupant.Affiliation affiliation;
		if (isOwner) {
			affiliation = MUGOccupant.Affiliation.owner;
		}
		else if (members.contains(fullJID.toBareJID())) {
			affiliation = MUGOccupant.Affiliation.member;
		}
		else if (isMembersOnly()) {
			throw new ForbiddenException();
		}
		else {
			affiliation = MUGOccupant.Affiliation.none;
		}
		
		// Add the new occupant
		occupant = new DefaultMUGOccupant(this, fullJID, nick, affiliation, presence, mugManager);
		occupants.put(nick.toLowerCase(), occupant);
		
		// Send presence of the room itself (room and match information)
		occupant.send(getPresence());
		
		// Send presence of existing occupants to new occupant
		for (MUGOccupant otherOccupant : occupants.values() ) {
			if (otherOccupant.equals(occupant))
				continue;
			// In semi-anonymous rooms we must add the real JID for room owners
			if ((anonymity == Anonymity.semiAnonymous) && isOwner) {
				Presence pres = otherOccupant.getPresence().createCopy();
				Element frag = pres.getChildElement("game", MUGService.mugNS);
				frag.element("item").addAttribute("jid", otherOccupant.getUserAddress().toBareJID());
				occupant.send(pres);
			}
			else {
				occupant.send(otherOccupant.getPresence());
			}
		}
		
		// Broadcast the presence of the new occupant
		broadcastPresence(occupant);
		
		// Confirm and welcome the new occupant by his presence in the room
		occupant.send(occupant.getPresence());
		
		return occupant;
	}
	
	public void sendPrivatePacket(Packet packet, MUGOccupant sender) throws NotFoundException, ComponentException {
		String nick = packet.getTo().getResource();
		MUGOccupant occupant = occupants.get(nick.toLowerCase());
		if (occupant != null) {
			packet.setFrom(sender.getRoomAddress());
			occupant.send(packet);
		}
		else {
			throw new NotFoundException();
		}
	}
	
	public void sendInvitationRejection(JID recipient, String reason, JID sender) throws ComponentException {
		Message message = new Message();
		message.setFrom(getJID());
		message.setTo(recipient);
		Element frag = message.addChildElement("game", MUGService.mugNS + "#user");
		frag.addElement("declined").addAttribute("from", sender.toBareJID());
		if (reason != null && reason.length() > 0) {
			frag.element("declined").addElement("reason").setText(reason);
		}
		
		// TODO: Remove debug output
		log.debug("[MUG]: Sending: " + message.toXML());
		
		mugManager.sendPacket(mugService, message);
	}
	
	public void sendTurn(Collection<Element> moves, MUGOccupant sender, MUGOccupant recipient) throws ComponentException {
		Element rawMessage = DocumentFactory.getInstance().createDocument().addElement("message");
		Element turn = rawMessage.addElement("turn", MUGService.mugNS + "#user");
		
		if (moves != null) {
			for (Element move : moves) {
				move.setParent(null);
				turn.add(move);
			}
		}
			
		Message message = new Message(rawMessage, false);
		message.setFrom(sender.getRoomAddress());
		recipient.send(message);
	}
	
	public boolean startMatch(MUGOccupant occupant) throws RequiredPlayerException, 
			GameConfigurationException, ComponentException {
		//TODO: Make this robust against changing roles or nicknames
		boolean started = false;
		if (occupant.hasRole()) {
			if (!startMatch.contains(occupant.getNickname().toLowerCase())) {
				startMatch.add(occupant.getNickname().toLowerCase());
			}
			
			// If all player sent a start, try to start.
			if (match != null && 
					match.getPlayers() != null && 
					match.getPlayers().size() == startMatch.size() ) {
				resetStartMatch();
				match.start();
				started = true;
			}
			
			// Reflect Start Message to other players
			Message startMessage = new Message();
			startMessage.setFrom(occupant.getRoomAddress());
			startMessage.addChildElement("start", MUGService.mugNS + "#user");
			
			for (MUGOccupant player : match.getPlayers()) {
				if (player == occupant)
					continue;
				player.send(startMessage);
			}
			
			// If the game has started, reflect the room state
			if (started)
				broadcastRoomPresence();
		}
		else
			throw new ForbiddenException();
		
		return started;
	}
	
	public void leave(MUGOccupant occupant) {
		boolean hasRole = occupant.hasRole();
		MUGMatch.Status matchStateBefore = match.getStatus();
		if (occupant.getPresence().getType() != Type.unavailable)
			occupant.setPresence(new Presence(Type.unavailable));
		
		// leave the match and inform the occupants about changes
		match.leave(occupant);
		broadcastPresence(occupant);
		try {
			occupant.send(occupant.getPresence());
		}
		catch (ComponentException e) {
			log.error(locale.getLocalizedString("mug.room.error.leave"), e);
		}
		
		// remove the occupant
		if (occupants.containsKey(occupant.getNickname().toLowerCase()))
			occupants.remove(occupant.getNickname().toLowerCase());
		occupant.destroy();
		occupant = null;
		
		// inform occupants about the changing match status
		if (!matchStateBefore.equals(match.getStatus()))
			broadcastRoomPresence();
		
		// reset start counter
		if (hasRole)
			resetStartMatch();
		
		// if he was the last, remove the room
		if (occupants.size() == 0) {
			mugService.removeGameRoom(name);
		}
	}
}
