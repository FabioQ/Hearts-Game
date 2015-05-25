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

import org.xmpp.packet.JID;

/**
 * The MUGPersistenceProvider defines the data which must be provided
 * by a storage for Multi-User Gaming services.
 * 
 * @author G&uuml;nther Nie&szlig;
 */
public interface MUGPersistenceProvider {
	
	/**
	 * Get a collection of the bare JIDs of administrators of a
	 * XMPP component.
	 * 
	 * @return A collection of administrators bare JIDs.
	 */
	public Collection<JID> getServiceAdmins(String subdomain) throws Exception;
	
	/**
	 * Get a property value for a specific Multi-User Gaming component
	 * or null if none is present.
	 * 
	 * @param subdomain The subdomain of the Multi-User Gaming component.
	 * @param name The name of the property.
	 * @param value The value of the property.
	 */
	public void setServiceProperty(String subdomain, String name, String value) throws Exception;
	
	/**
	 * Get a property value for a specific Multi-User Gaming component
	 * or null if none is present.
	 * 
	 * @param subdomain The subdomain of the Multi-User Gaming component.
	 * @param name The name of the property.
	 * @return The value of the property or null.
	 */
	public String getServiceProperty(String subdomain, String name) throws Exception;
}
