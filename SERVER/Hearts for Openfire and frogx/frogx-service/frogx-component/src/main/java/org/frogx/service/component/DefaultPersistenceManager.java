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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.frogx.service.api.MUGPersistenceProvider;

import org.xmpp.packet.JID;

public class DefaultPersistenceManager implements MUGPersistenceProvider {

	public Collection<JID> getServiceAdmins(String subdomain) throws Exception {
		List<JID> jids = new ArrayList<JID>();
		return jids;
	}

	public String getServiceProperty(String subdomain, String name)
			throws Exception {
		return null;
	}

	public void setServiceProperty(String subdomain, String name, String value)
			throws Exception {
		throw new IllegalStateException("Feature not implemented yet!");
	}

}
