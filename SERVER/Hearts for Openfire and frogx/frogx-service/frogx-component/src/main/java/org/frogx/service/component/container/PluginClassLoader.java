/**
 * PluginClassLoader - Some parts are used and inspired by the Openfire XMPP
 * server.
 * 
 * Copyright (C) 2004-2009 Jive Software. All rights reserved.
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
package org.frogx.service.component.container;


import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.frogx.service.api.MUGManager;
import org.frogx.service.component.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassLoader for plugins. It searches the plugin directory for classes
 * and JAR files, then constructs a class loader for the resources found.
 * Resources are loaded as follows:
 * <ul>
 * <li>Any JAR files in the <tt>lib</tt> will be added to the classpath.
 * <li>Any files in the classes directory will be added to the classpath.
 * </ul>
 *
 * @author Derek DeMoro
 */
public class PluginClassLoader extends URLClassLoader {
	
	private static final Logger log = LoggerFactory.getLogger(PluginClassLoader.class);
	
	public PluginClassLoader(MUGManager mugManager) {
		super(new URL[] {}, findParentClassLoader());
	}
	
	public void addDirectory(File directory) {
		try {
			// Add classes directory to classpath.
			File classesDir = new File(directory, "classes");
			if (classesDir.exists()) {
				addURL(classesDir.toURL());
			}
			
			// Add i18n directory to classpath.
			File i18nDir = new File(directory, "i18n");
			if(i18nDir.exists()){
				addURL(i18nDir.toURL());
			}
			
			// Add lib directory to classpath.
			File libDir = new File(directory, "lib");
			if (libDir.exists()) {
				File[] jars = libDir.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith(".jar") || name.endsWith(".zip");
					}
				});
				if (jars != null) {
					for (int i = 0; i < jars.length; i++) {
						if (jars[i] != null && jars[i].isFile()) {
							addURL(jars[i].toURL());
						}
					}
				}
			}
		}
		catch (MalformedURLException mue) {
			log.error("Error while loading '" + directory + "' to classpath.", mue);
		}
	}
	
	public void addURLFile(URL file) {
		addURL(file);
	}
	
	/**
	 * Locates the best parent class loader based on context.
	 *
	 * @return the best parent classloader to use.
	 */
	private static ClassLoader findParentClassLoader() {
		ClassLoader parent = Main.class.getClassLoader();
		if (parent == null) {
			parent = PluginClassLoader.class.getClassLoader();
		}
		if (parent == null) {
			parent = ClassLoader.getSystemClassLoader();
		}
		return parent;
	}
}
