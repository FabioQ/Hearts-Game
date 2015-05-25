/**
 * Copyright (C) 2009 Guenther Niess. All rights reserved.
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
package org.frogx.service.component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.frogx.service.api.MUGManager;
import org.frogx.service.api.MUGPersistenceProvider;
import org.frogx.service.api.MUGService;
import org.frogx.service.api.MultiUserGame;
import org.frogx.service.api.util.LocaleUtil;

import org.frogx.service.component.util.DefaultLocaleUtil;
import org.frogx.service.core.DefaultMUGService;

import org.jivesoftware.whack.ExternalComponentManager;
import org.xmpp.component.ComponentException;
import org.xmpp.packet.Packet;


/**
 * @author  Laiho
 */
public class DefaultMUGManager implements MUGManager {
	
	/**
	 * @uml.property  name="locale"
	 * @uml.associationEnd  
	 */
	private LocaleUtil locale;
	
	private ExternalComponentManager componentManager;
	
	/**
	 * @uml.property  name="games"
	 */
	private Map<String, MultiUserGame> games;
	
	/**
	 * @uml.property  name="storage"
	 * @uml.associationEnd  
	 */
	private MUGPersistenceProvider storage;
	/**
	 * @uml.property  name="service"
	 * @uml.associationEnd  
	 */
	private MUGService service = null;
	
	public DefaultMUGManager(ExternalComponentManager componentManager,
			String subdomain, String description) {
		locale = new DefaultLocaleUtil();
		this.componentManager = componentManager;
		this.games = new ConcurrentHashMap<String, MultiUserGame>();
		this.storage = new DefaultPersistenceManager();
		this.service = new DefaultMUGService(subdomain, description, this,
				games, storage);
	}
	
	public MUGService getMultiUserGamingService() {
		return service;
	}
	
	public LocaleUtil getLocaleUtil() {
		return locale;
	}
	
	public String getServerName() {
		return componentManager.getServerName();
	}
	
	public boolean isGameRegistered(String namespace) {
		return (namespace != null && games.containsKey(namespace));
	}
	
	public void registerMultiUserGame(String namespace, MultiUserGame game) {
		if (namespace == null || game == null)
			throw new IllegalArgumentException("A namespace and game is needed in order to register a game");
		service.registerMultiUserGame(namespace, game);
		games.put(namespace, game);
	}
	
	public void unregisterMultiUserGame(String namespace) {
		if (namespace == null)
			throw new IllegalArgumentException("A namespace is needed in order to unregister a game");
		service.unregisterMultiUserGame(namespace);
		games.remove(namespace);
	}
	
	public void sendPacket(MUGService mugService, Packet packet)
			throws ComponentException {
		componentManager.sendPacket(mugService, packet);
	}
	
	/**
	 * @return
	 * @uml.property  name="games"
	 */
	public Map<String, MultiUserGame> getGames() {
		return games;
	}

}
