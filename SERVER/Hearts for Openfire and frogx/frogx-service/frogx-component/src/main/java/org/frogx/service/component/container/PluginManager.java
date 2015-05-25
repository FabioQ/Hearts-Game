/**
 * PluginManager - Some parts are used and inspired by the Openfire XMPP server.
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

import java.io.*;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.ZipFile;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.frogx.service.api.MUGManager;
import org.frogx.service.api.MultiUserGame;

import org.frogx.service.core.DefaultMultiUserGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author  Laiho
 */
public class PluginManager {
	
	private static final Logger log = LoggerFactory.getLogger(PluginManager.class);
	
	/**
	 * @uml.property  name="pluginDirectory"
	 */
	private File pluginDirectory;
	private Map<String, File> pluginFiles;
	private Map<String, PluginClassLoader> classloaders;
	private Map<String, MultiUserGame> games;
	
	/**
	 * @uml.property  name="mugManager"
	 * @uml.associationEnd  
	 */
	private MUGManager mugManager;
	
	private ScheduledExecutorService executor = null;
	/**
	 * @uml.property  name="pluginMonitor"
	 * @uml.associationEnd  
	 */
	private PluginMonitor pluginMonitor;
	
	public PluginManager(MUGManager mugManager, File pluginDirectory) {
		this.mugManager = mugManager;
		this.pluginDirectory = pluginDirectory;
		this.pluginFiles = new HashMap<String, File>();
		this.classloaders = new HashMap<String, PluginClassLoader>();
		this.games = new HashMap<String, MultiUserGame>();
		this.pluginMonitor = new PluginMonitor();
	}
	
	/**
	 * Starts plugins and the plugin monitoring service.
	 */
	public void start() {
		executor = new ScheduledThreadPoolExecutor(1);
		executor.scheduleWithFixedDelay(pluginMonitor, 0, 5, TimeUnit.SECONDS);
	}
	
	/**
	 * Shuts down all running plugins.
	 */
	public void shutdown() {
		// Stop the plugin monitoring service.
		if (executor != null) {
			executor.shutdown();
		}
	}
	
	/**
	 * @param pluginDirectory
	 * @uml.property  name="pluginDirectory"
	 */
	public void setPluginDirectory(File pluginDirectory) {
		this.pluginDirectory = pluginDirectory;
	}
	
	/**
	 * @return
	 * @uml.property  name="pluginDirectory"
	 */
	public File getPluginDirectory() {
		return pluginDirectory;
	}
	
	public void loadPlugin(String pluginName, File pluginDir) {
		try {
			File pluginConfig = new File(pluginDir, "frogx.xml");
			if (pluginConfig.exists()) {
				SAXReader saxReader = new SAXReader();
				saxReader.setEncoding("UTF-8");
				Document xmlConfig = saxReader.read(pluginConfig);
				Element rootEl = xmlConfig.getRootElement();
				
				Element element = rootEl.element("type");
				if (element == null || "match".equalsIgnoreCase(element.getText())) {
					element = rootEl.element("match");
					if (element != null && element.getText().trim().length() > 0) {
						PluginClassLoader pluginLoader = new PluginClassLoader(mugManager);
						pluginLoader.addDirectory(pluginDir);
						ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
						Thread.currentThread().setContextClassLoader(pluginLoader);
						DefaultMultiUserGame game = new DefaultMultiUserGame(pluginDir, mugManager);
						game.setMatchClassloader(pluginLoader);
						classloaders.put(pluginName, pluginLoader);
						games.put(pluginName, game);
						mugManager.registerMultiUserGame(game.getNamespace(), game);
						Thread.currentThread().setContextClassLoader(oldLoader);
					}
				}
			}
		}
		catch (Exception e) {
			log.error("Error while loading plugin.", e);
		}
	}
	
	/**
	 * A service that monitors the plugin directory for plugins. It periodically
	 * checks for new plugin JAR files and extracts them if they haven't already
	 * been extracted. Then, any new plugin directories are loaded.
	 */
	private class PluginMonitor implements Runnable {
		
		/**
		 * Tracks if the monitor is currently running.
		 */
		private boolean running = false;
		
		/**
		 * True when it's the first time the plugin monitor process runs. This is helpful for
		 * bootstrapping purposes.
		 */
		private boolean firstRun = true;
		
		public void run() {
			// If the task is already running, return.
			synchronized (this) {
				if (running) {
					return;
				}
				running = true;
			}
			
			File[] jars = pluginDirectory.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					String fileName = pathname.getName().toLowerCase();
					return fileName.endsWith(".jar");
				}
			});
			
			if (jars == null) {
				return;
			}
			
			for (File jarFile : jars) {
				String pluginName = jarFile.getName().substring(0,
						jarFile.getName().length() - 4).toLowerCase();
				// See if the JAR has already been exploded.
				File dir = new File(pluginDirectory, pluginName);
				// Store the JAR/WAR file that created the plugin folder
				pluginFiles.put(pluginName, jarFile);
				// If the JAR hasn't been exploded, do so.
				if (!dir.exists()) {
					unzipPlugin(pluginName, jarFile, dir);
				}
				// See if the JAR is newer than the directory. If so, the plugin
				// needs to be unloaded and then reloaded.
				else if (jarFile.lastModified() > dir.lastModified()) {
					// If this is the first time that the monitor process is running, then
					// plugins won't be loaded yet. Therefore, just delete the directory.
					if (firstRun) {
						int count = 0;
						// Attempt to delete the folder for up to 5 seconds.
						while (!deleteDir(dir) && count < 5) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
							}
						}
					}
					else {
						deleteDir(dir);
					}
					// If the delete operation was a success, unzip the plugin.
					if (!dir.exists()) {
						unzipPlugin(pluginName, jarFile, dir);
					}
				}
				
				File[] dirs = pluginDirectory.listFiles(new FileFilter() {
					public boolean accept(File pathname) {
						return pathname.isDirectory();
					}
				});
				
				// Load all plugins that need to be loaded.
				for (File dirFile : dirs) {
					// If the plugin hasn't already been started, start it.
					if (dirFile.exists() && !games.containsKey(dirFile.getName())) {
						loadPlugin(dirFile.getName(), dirFile);
					}
				}
			}
			running = false;
		}
		
		/**
		 * Unzips a plugin from a JAR file into a directory. If the JAR file
		 * isn't a plugin, this method will do nothing.
		 *
		 * @param pluginName the name of the plugin.
		 * @param file the JAR file
		 * @param dir the directory to extract the plugin to.
		 */
		@SuppressWarnings("unchecked")
		private void unzipPlugin(String pluginName, File file, File dir) {
			try {
				ZipFile zipFile = new JarFile(file);
				// Ensure that this JAR is a plugin.
				if (zipFile.getEntry("frogx.xml") == null) {
					return;
				}
				dir.mkdir();
				// Set the date of the JAR file to the newly created folder
				dir.setLastModified(file.lastModified());
				log.debug("PluginManager: Extracting plugin: " + pluginName);
				for (Enumeration e = zipFile.entries(); e.hasMoreElements();) {
					JarEntry entry = (JarEntry)e.nextElement();
					File entryFile = new File(dir, entry.getName());
					// Ignore any manifest.mf entries.
					if (entry.getName().toLowerCase().endsWith("manifest.mf")) {
						continue;
					}
					if (!entry.isDirectory()) {
						entryFile.getParentFile().mkdirs();
						FileOutputStream out = new FileOutputStream(entryFile);
						InputStream zin = zipFile.getInputStream(entry);
						byte[] b = new byte[512];
						int len;
						while ((len = zin.read(b)) != -1) {
							out.write(b, 0, len);
						}
						out.flush();
						out.close();
						zin.close();
					}
				}
				zipFile.close();
				
				// The lib directory of the plugin may contain Pack200 versions of the JAR
				// file. If so, unpack them.
				unpackArchives(new File(dir, "lib"));
			}
			catch (Exception e) {
				log.error("An error occurred while extracting a plugin.", e);
			}
		}
		
		/**
		 * Converts any pack files in a directory into standard JAR files. Each
		 * pack file will be deleted after being converted to a JAR. If no
		 * pack files are found, this method does nothing.
		 *
		 * @param libDir the directory may containing pack files.
		 */
		private void unpackArchives(File libDir) {
			// Get a list of all packed files in the lib directory.
			File [] packedFiles = libDir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".pack");
				}
			});
			
			if (packedFiles == null) {
				// Do nothing since no .pack files were found
				return;
			}
			
			// Unpack each.
			for (File packedFile : packedFiles) {
				try {
					String jarName = packedFile.getName().substring(0,
							packedFile.getName().length() - ".pack".length());
					// Delete JAR file with same name if it exists
					File jarFile = new File(libDir, jarName);
					if (jarFile.exists()) {
						jarFile.delete();
					}
					
					InputStream in = new BufferedInputStream(new FileInputStream(packedFile));
					JarOutputStream out = new JarOutputStream(new BufferedOutputStream(
							new FileOutputStream(new File(libDir, jarName))));
					Pack200.Unpacker unpacker = Pack200.newUnpacker();
					// Call the unpacker
					unpacker.unpack(in, out);
					
					in.close();
					out.close();
					packedFile.delete();
				}
				catch (Exception e) {
					log.error("An error occurred while extracting an archive.", e);
				}
			}
		}
		
	}
	
	/**
	 * Deletes a directory.
	 *
	 * @param dir the directory to delete.
	 * @return true if the directory was deleted.
	 */
	private boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] childDirs = dir.list();
			// Always try to delete JAR files first since that's what will
			// be under contention. We do this by always sorting the lib directory
			// first.
			List<String> children = new ArrayList<String>(Arrays.asList(childDirs));
			Collections.sort(children, new Comparator<String>() {
				public int compare(String o1, String o2) {
					if (o1.equals("lib")) {
						return -1;
					}
					if (o2.equals("lib")) {
						return 1;
					}
					else {
						return o1.compareTo(o2);
					}
				}
			});
			for (String file : children) {
				boolean success = deleteDir(new File(dir, file));
				if (!success) {
					log.debug("PluginManager: Plugin removal: could not delete: " + new File(dir, file));
					return false;
				}
			}
		}
		boolean deleted = !dir.exists() || dir.delete();
		if (deleted) {
			// Remove the JAR/WAR file that created the plugin folder
			//pluginFiles.remove(dir.getName());
		}
		return deleted;
	}
}