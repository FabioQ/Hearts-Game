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
package org.frogx.service.api;


import java.util.Collection;


import org.dom4j.Element;
import org.frogx.service.api.exception.ConflictException;
import org.frogx.service.api.exception.GameConfigurationException;
import org.frogx.service.api.exception.InvalidTurnException;
import org.frogx.service.api.exception.RequiredPlayerException;
import org.xmpp.component.ComponentException;


/**
 * A MUGMatch provides the game logic, handles the state of the game
 * and provides roles which can be assigned by an {@see MUGOccupant}
 * to become a player.
 * 
 * @author G&uuml;nther Nie&szlig;
 */
public interface MUGMatch {
	
	
	/**
	 * The Status of a   {@see   MUGMatch}   is part of the game state within a  {@see   MUGRoom}   and describes if the game is running, paused or can be configured. For example only in matches with an active status moves are permitted, newly created rooms have to be configured before a   {@see   MUGOccupant}  is allowed to join the room,...
	 * @author   G&uuml;nther Nie&szlig;
	 */
	public enum Status {
		
		/**
		 * @uml.property  name="created"
		 * @uml.associationEnd  
		 */
		created,
		
		/**
		 * @uml.property  name="active"
		 * @uml.associationEnd  
		 */
		active, 
		
		/**
		 * @uml.property  name="paused"
		 * @uml.associationEnd  
		 */
		paused, 
		
		/**
		 * @uml.property  name="inactive"
		 * @uml.associationEnd  
		 */
		inactive;
	}
	
	/**
	 * If a user joins the room as a spectator or a player releases
	 * the game role this function is called.
	 * 
	 * @param occupant The room occupant who wants to watch the game.
	 */
	public void addSpectator(MUGOccupant occupant);
	
	/**
	 * An occupant which have no role yet can reserve any free game role.
	 * 
	 * @param occupant The room occupant who wants to reserve a role.
	 * @return The name of the reserved role or null if no role could be reserved.
	 */
	public String reserveFreeRole(MUGOccupant occupant);
	
	/**
	 * An occupant which have no role yet can try to reserve a game role.
	 * If the role isn't available this method returns null.
	 * 
	 * @param occupant The room occupant who wants to reserve a role.
	 * @param roleName The name of the role which wants the occupant reserve.
	 * @throws ConflictException If the role is reserved by another occupant.
	 * @throws GameConfigurationException If the role doesn't exist.
	 */
	public void reserveRole(MUGOccupant occupant, String roleName) 
		throws ConflictException, GameConfigurationException;
	
	/**
	 * Get a collection of the players in the match.
	 * 
	 * @return A collection of the players in the match.
	 */
	public Collection<MUGOccupant> getPlayers();
	
	/**
	 * Get the role of an {@see MUGOccupant}.
	 * 
	 * @param player The owner of the role.
	 * @return The name of the role which is reserved by the player.
	 */
	public String getRole(MUGOccupant player);
	
	/**
	 * Get a collection of the available free roles in the match.
	 * 
	 * @return A collection of free roles.
	 */
	public Collection<String> getFreeRoles();
	
	/**
	 * Get xml element which represents the actual match state. This state
	 * is used for the presence and delivered to the room occupants.
	 * 
	 * @return A xml element with the match state.
	 */
	public Element getState();
	
	/**
	 * The room occupants want to start the game. If the match is not yet
	 * ready to start because some required roles are not assigned a
	 * RequiredPlayerException is released. If no playable configuration
	 * is available the GameConfigurationException triggered.
	 * 
	 * @throws RequiredPlayerException This Exception is thrown if a 
	 * required role isn't assigned by a occupant.
	 * @throws GameConfigurationException This Exception is thrown if no
	 * playable configuration is available.
	 */
	public void start() throws RequiredPlayerException, GameConfigurationException;
	
	/**
	 * A player (an occupant reserved a game role) want to make a turn.
	 * 
	 * @param player The MUGOccupant which wants to make the turn.
	 * @param moves A collection of the xml elements for the turn.
	 * @throws RequiredPlayerException This Exception is thrown if a 
	 * required role isn't assigned by a occupant.
	 * @throws GameConfigurationException This Exception is thrown if
	 * the match hasn't started yet.
	 * @throws InvalidTurnException If the turn is invalid.
	 * @throws ComponentException 
	 */
	public void processTurn(MUGOccupant player, Collection<Element> moves)
		throws RequiredPlayerException, GameConfigurationException, 
		InvalidTurnException, ComponentException;
	
	/**
	 * A MUGOccupant want to release his role.
	 * 
	 * @param player The MUGOccupant which wants to release his role.
	 */
	public void releaseRole(MUGOccupant player);
	
	/**
	 * Get a collection of xml elements which were delivered to the owner if he 
	 * wants to configure the match
	 * 
	 * @return xml elements which describes what the owner can configure
	 */
	public Collection<Element> getConfigurationForm();
	
	/**
	 * Configure the match with help of the ConfigurationFrom get by
	 * getConfigurationForm(). If the Collection of configure elements
	 * is null the default configuration is applied.
	 * 
	 * @param config A collection of xml elements which describes the new 
	 * 		configuration or null if the default configuration is applied.
	 */
	public void setConfiguration(Collection<Element> config);
	
	/**
	 * In some games the owner can request a constructed match by
	 * sending an starting match state.
	 * If the game didn't support that just throw an 
	 * IllegalArgumentException or an UnsupportedGameException if it
	 * is not implemented yet.
	 * 
	 * @param state The starting match state.
	 */
	public void setConstructedState(Element state);
	
	/**
	 * Get the current status of the match.
	 * 
	 * @return The pressent match status.
	 */
	public Status getStatus();
	
	/**
	 * An {@see MUGOccupant} leaves the match.
	 * This means an occupant is neither a player nor a spectator anymore.
	 * 
	 * @param occupant The occupant who leaves the match.
	 */
	public void leave(MUGOccupant occupant);
	
	/**
	 * This method is called when the match will be destroyed.
	 */
	public void destroy();
}
