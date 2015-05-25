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
package org.frogx.service.games;

import java.io.File;

import org.frogx.service.api.MUGManager;
import org.frogx.service.core.DefaultMultiUserGame;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author  Laiho
 */
public class OpenfirePlugin implements Plugin {
	
	private static final Logger log = LoggerFactory.getLogger(OpenfirePlugin.class);
	
	private static final String mugPluginName = "frogx-service";
	
	/**
	 * @uml.property  name="mugManager"
	 * @uml.associationEnd  
	 */
	private MUGManager mugManager = null;
	
	/**
	 * @uml.property  name="game"
	 * @uml.associationEnd  
	 */
	private DefaultMultiUserGame game = null;
	
	
	public void destroyPlugin() {
		if (game != null) {
			mugManager.unregisterMultiUserGame(game.getNamespace());
			game = null;
		}
	}
	
	public void initializePlugin(PluginManager pluginManager, File pluginDirectory) {
		mugManager = (MUGManager)pluginManager.getPlugin(mugPluginName);
		try {
			game = new DefaultMultiUserGame(pluginDirectory, mugManager);
			mugManager.registerMultiUserGame(game.getNamespace(), game);
		} catch (Exception e) {
			log.error("Can't register the game.", e);
		}
	}
	
}
