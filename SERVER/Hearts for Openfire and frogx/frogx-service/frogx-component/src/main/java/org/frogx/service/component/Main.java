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

import java.io.File;

import org.frogx.service.component.container.PluginManager;

import org.jivesoftware.whack.ExternalComponentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * @author  Laiho
 */
public class Main {
	
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
	/**
	 * Name of the application (for help and the fallback directory)
	 */
	public final static String appName = "frogx-component";
	
	/**
	 * Error Codes
	 */
	public final static int DOMAIN_ERROR = 1;
	public final static int PASSWD_ERROR = 2;
	public final static int OPTIONS_ERROR = 3;
	public final static int CONNECTION_ERROR = 4;
	
	/**
	 * Starting attributes.
	 */
	protected int port = 5275;
	protected String domain = null;
	protected String passwd = null;
	protected String server = null;
	protected String subdomain = "gaming";
	protected String description = "Multi-User Gaming Service";
	protected File homeDir = null;
	protected File pluginDir = null;
	
	/**
	 * Multi-User Gaming infrastructure
	 * @uml.property  name="mugManager"
	 * @uml.associationEnd  
	 */
	private DefaultMUGManager mugManager;
	/**
	 * @uml.property  name="pluginManager"
	 * @uml.associationEnd  
	 */
	private PluginManager pluginManager;
	
	
	public void applyProperties() {
		if (System.getProperty("frogx.component.home") != null) {
			homeDir = new File(System.getProperty("frogx.component.home"));
		}
		else if (System.getProperty("frogx.component.bin.dir") != null) {
			File binDir = new File(System.getProperty("frogx.component.bin.dir"));
			homeDir = binDir.getParentFile();
		}
	}
	
	public boolean parseArguments(String[] args) {
		try {
			Options options = new Options();
			options.addOption("p", "port", true, 
					"use this port for connecting with the server (default is 5275)");
			options.addOption("s", "server", true, 
					"specifies the server address");
			options.addOption("d", "domain", true, 
					"specifies the XMPP domain of the server");
			options.addOption(null, "home", true, 
					"specifies the home directory of frogx");
			options.addOption(null, "plugin-directory", true, 
					"specifies the directory of the plugins");
			options.addOption("k", "key", true, 
					"the password for the handshake with the server");
			options.addOption("g", "subdomain", true, 
					"use this subdomain for the gaming service (default is \"gaming\")");
			options.addOption("h", "help", false, 
					"print this message");
			
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(options, args);
			HelpFormatter formatter = new HelpFormatter();
			
			if (cmd.hasOption('p')) {
				port = Integer.valueOf(cmd.getOptionValue('p'));
			}
			if (cmd.hasOption('d')) {
				domain = cmd.getOptionValue('d');
			}
			if (cmd.hasOption('s')) {
				server = cmd.getOptionValue('s');
			}
			if (cmd.hasOption('k')) {
				passwd = cmd.getOptionValue('k');
			}
			if (cmd.hasOption('g')) {
				subdomain = cmd.getOptionValue('g');
			}
			if (cmd.hasOption("home")) {
				homeDir = new File(cmd.getOptionValue("home"));
			}
			if (cmd.hasOption("plugin-directory")) {
				pluginDir = new File(cmd.getOptionValue("plugin-directory"));
			}
			
			if (cmd.hasOption("h")) {
				formatter.printHelp(
						appName + " [options] --domain=frogx.org --key=password", 
						options);
				System.exit(0);
			}
			if (domain == null) {
				System.err.println("Domain not initialized!");
				formatter.printHelp(
						appName + " [options] --domain=frogx.org --key=password", 
						options);
				System.exit(DOMAIN_ERROR);
			}
			if (passwd == null) {
				System.err.println("Password not initialized!");
				formatter.printHelp(
						appName + " [options] --domain=frogx.org --key=password", 
						options);
				System.exit(PASSWD_ERROR);
			}
		}
		catch (ParseException e) {
			System.err.println("Error while parsing application parameters!");
			e.printStackTrace(System.err);
			System.exit(OPTIONS_ERROR);
		}
		catch (NumberFormatException e) {
			System.err.println("Can't parse the specified port!");
			System.exit(OPTIONS_ERROR);
		}
		return true;
	}
	
	public void start() {
		// verify the necessary parameters
		if (passwd == null || subdomain == null) {
			throw new IllegalStateException("Password or Subdomain is not initialized!");
		}
		if (server == null && domain == null) {
			throw new IllegalStateException("Server and Domain are not initialized!");
		}
		
		// verify environment
		if (homeDir == null) {
			homeDir = new File(System.getProperty("user.home") 
					+ File.separator + "." + appName);
		}
		System.setProperty("frogx.component.home", homeDir.getAbsolutePath());
		if (pluginDir == null) {
			pluginDir = new File(homeDir, "plugins");
		}
		
		// initialize component manager
		final ExternalComponentManager componentManager;
		if (server != null) {
			componentManager = new ExternalComponentManager(server, port);
		}
		else {
			componentManager = new ExternalComponentManager(domain, port);
		}
		if (domain != null && domain.trim().length() > 0) {
			componentManager.setServerName(domain);
		}
		componentManager.setSecretKey(subdomain, passwd);
		componentManager.setMultipleAllowed(subdomain, false);
		
		// setup multi-user gaming service
		mugManager = new DefaultMUGManager(componentManager, subdomain, description);
		pluginManager = new PluginManager(mugManager, pluginDir);
		
		try {
			log.debug("Frogx directory: " + System.getProperty("frogx.component.home"));
			log.debug("Plugin directory: " + pluginManager.getPluginDirectory());
			pluginManager.start();
			componentManager.addComponent(subdomain, mugManager.getMultiUserGamingService());
			
			// Quick trick to ensure that this application will be running for ever. To stop the
			// application you will need to kill the process
			while (true) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			log.error("Can't connect to the XMPP.", e);
			System.exit(CONNECTION_ERROR);
		}
	}
	
	public static void main(String[] args) {
		Main main = new Main();
		main.applyProperties();
		main.parseArguments(args);
		main.start();
	}
}
