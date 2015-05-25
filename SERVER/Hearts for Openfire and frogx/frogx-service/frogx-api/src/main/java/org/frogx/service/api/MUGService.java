/**
 * Copyright (C) 2008 Guenther Niess. All rights reserved.
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
package org.frogx.service.api;

import java.util.Collection;
import java.util.Map;


import org.dom4j.Element;
import org.frogx.service.api.exception.NotAllowedException;
import org.frogx.service.api.exception.UnsupportedGameException;
import org.frogx.service.api.MultiUserGame;
import org.xmpp.component.Component;
import org.xmpp.packet.JID;


/**
 * A Multi-User Gaming service is a XMPP component which  manages game rooms and users.
 * @author   G&uuml;nther Nie&szlig;
 */
public interface MUGService extends Component {
	
	/**
	 * The xml namespace of the multi-user game service.
	 */
	public static final String mugNS = "http://jabber.org/protocol/mug";
	
	/**
	 * The Suffix of the owner xml namespace.
	 */
	public static final String mugOwnerNS = "http://jabber.org/protocol/mug#owner";
	
	/**
	 * The Suffix of the user xml namespace.
	 */
	public static final String mugUserNS = "http://jabber.org/protocol/mug#user";
	
	/**
	 * Get the name of the service e.g. gaming for the domain 
	 * gaming.example.com.
	 * 
	 * @return the name of the multi-user game service.
	 */
	public String getName();
	
	/**
	 * Get the domain of the service e.g. gaming.example.com.
	 * 
	 * @return the domain of the multi-user game service.
	 */
	public String getDomain();
	
	/**
	 * Get the JID of the multi-user game service with the full service domain.
	 * 
	 * @return the domain of the service.
	 */
	public JID getAddress();
	
	/**
	 * Get a collection of the administrators JIDs.
	 * 
	 * @return A collection of the administrators JIDs.
	 */
	public Collection<JID> getAdmins();
	
	/**
	 * Get a human readable description of the multi-user game service.
	 * @return   the description of the service.
	 * @uml.property  name="description"
	 */
	public String getDescription();
	
	/**
	 * Assign the human readable description of multi-user game service. This short description of service is used for disco queries and such.
	 * @param description   Description of the service.
	 * @uml.property  name="description"
	 */
	public void setDescription(String description);
	
	/**
	 * Get true if the the multi-user game service is started and running.
	 * 
	 * @return true if the service is running, otherwise false.
	 */
	public boolean isServiceEnabled();
	
	/**
	 * Retuns the number of existing Rooms on the server (i.e. active or not, 
	 * in memory or not).
	 *
	 * @return the number of rooms provided by the service.
	 */
	public int getNumberRooms();
	
	/**
	 * Get the number of active multi-user rooms.
	 * 
	 * @return the number of rooms.
	 */
	public int getNumberActiveRooms();
	
	/**
	 * Get the number of saved multi-user rooms.
	 * 
	 * @return the number of rooms.
	 */
	public int getNumberSavedRooms();
	
	/**
	 * Retuns the total number of user sessions on this service.
	 *
	 * @return the number of user sessions on the server.
	 */
	public int getNumberUserSessions();
	
	/**
	 * Registers a new MultiUserGame implementation to the service.
	 * 
	 * @param namespace the MultiUserGame disco feature namespace.
	 * @param gameClass the class which implements the MultiUserGame.
	 */
	public void registerMultiUserGame(String namespace, MultiUserGame gameClass);
	
	/**
	 * Unregisters a MultiUserGame implementation.
	 * 
	 * @param namespace the MultiUserGame disco feature namespace.
	 */
	public void unregisterMultiUserGame(String namespace);
	
	/**
	 * Get the games supported by the service.
	 * 
	 * @return the namespaces and classes that are supported.
	 */
	public Map<String, MultiUserGame> getSupportedGames();
	
	/**
	 * Obtains a game room.
	 * 
	 * @param roomName the name of the room.
	 * @param gameNamespace the namespace of the game played within the room.
	 * @param userJID the user which wants to get the room.
	 * @return the game room for the given name.
	 * @throws NotAllowedException if the room doesn't exist yet and there are 
	 * restrictions on this service to create a new room
	 * @throws UnsupportedGameException if the game isn't supported by this service.
	 */
	public MUGRoom getGameRoom(String roomName, String gameNamespace, JID userJID) 
		throws NotAllowedException, UnsupportedGameException;
	
	/**
	 * Get a collection of all game rooms hosted by the service.
	 * 
	 * @return a collection of game rooms.
	 */
	public Collection<MUGRoom> getGameRooms();
	
	/**
	 * If a game room with the specified name exists this method returns true.
	 * 
	 * @param roomName the name of the game room.
	 * @return true if the room already exists otherwise false.
	 */
	public boolean hasRoom(String roomName);
	
	/**
	 * Add an additional service discovery identity to the service.
	 * 
	 * @param name the name of the identity.
	 * @param type the type of the identity (see XMPP Registrar for more details).
	 * @param category the category of the identity (see XMPP Registrar for 
	 * more details).
	 */
	public void addExtraIdentity(String category, String name, String type);
	
	/**
	 * Remove an additional service discovery identity from the service 
	 * (if exists).
	 * 
	 * @param name the name of the identity.
	 */
	public void removeExtraIdentity(String name);
	
	/**
	 * Obtains a collection of additional service discovery identities.
	 * 
	 * @return a collection of additional service discovery identities.
	 */
	public Collection<Element> getExtraIdentities();
	
	/**
	 * Add additional service discovery features for this service.
	 * 
	 * @param feature the namespace of the feature which wants to be added.
	 */
	public void addExtraFeature(String feature);
	
	/**
	 * Remove an additional service discovery feature from the service.
	 * 
	 * @param feature the namespace of the feature which wants to be removed.
	 */
	public void removeExtraFeature(String feature);
	
	/**
	 * Obtains a collection of additional service discovery features.
	 * 
	 * @return a collection of additional service discovery features.
	 */
	public Collection<String> getExtraFeatures();
	
	/**
	 * Remove a game room from this service.
	 * 
	 * @param roomName The name of the room.
	 */
	public void removeGameRoom(String roomName);
}
