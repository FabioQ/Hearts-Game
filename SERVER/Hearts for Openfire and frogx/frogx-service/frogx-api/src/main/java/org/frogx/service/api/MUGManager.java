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

import org.frogx.service.api.MUGService;
import org.frogx.service.api.MultiUserGame;
import org.frogx.service.api.util.LocaleUtil;
import org.xmpp.component.ComponentException;
import org.xmpp.packet.Packet;


/**
 * The MUGManager is the interface for {@see MultiUserGame} plugins
 * to register and unregister their support within {@see MUGService}
 * components.
 * 
 * @author G&uuml;nther Nie&szlig;
 */
public interface MUGManager {
	
	/**
	 * Returns true if a {@see MultiUserGame} is configured for this
	 * server.
	 * 
	 * @param namespace The namespace (disco#info) of the MultiUserGame.
	 * @return True if the game is registered for this server.
	 */
	public boolean isGameRegistered(String namespace);
	
	/**
	 * Registers a {@see MultiUserGame} at the multi-user game plugin
	 * which handles the {@see MUGService}.
	 * 
	 * @param namespace The namespace of the MultiUserGame which can be
	 *     discovered via disco#info queries.
	 * @param game The MultiUserGame which should be registered.
	 */
	public void registerMultiUserGame(String namespace, MultiUserGame game);
	
	/**
	 * Unregisters a {@see MultiUserGame} at the multi-user game plugin
	 * which handles the {@see MUGService}. For example if the Plugin is
	 * getting removed or if the game should no longer offered.
	 * 
	 * @param namespace The namespace (disco#info) of the MultiUserGame.
	 */
	public void unregisterMultiUserGame(String namespace);
	
	public String getServerName();
	
	public LocaleUtil getLocaleUtil();
	
	public void sendPacket(MUGService mugService, Packet packet) throws ComponentException;
}
