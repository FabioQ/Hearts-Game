package com.cyberdyne.heartsclient.controller;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.cyberdyne.heartsclient.utils.ConstantData;


/**
 * @author  Laiho
 * questa classe istanzia un oggetto singleton. istanzia una connessione di tipo XMPP
 */
public class ConnectionManager {
	
	/**
	 * @uml.property  name="connection"
	 */
	private XMPPConnection connection;
	/**
	 * @uml.property  name="connectionManager"
	 * @uml.associationEnd  
	 */
	private static ConnectionManager connectionManager = null;
//	private static final String serverAddress = "tauron.mentalsmash.org";
//	private static final int serverPort = 5222;
	
	public ConnectionManager() {
    	ConnectionConfiguration config = new ConnectionConfiguration(ConstantData.serverAddress, ConstantData.serverPort);
    	connection = new XMPPConnection(config);
    	try {
			connection.connect();
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			System.out.println("Connection failed\n");
			e.printStackTrace();
		}
	}
	
	/**
	 * @return
	 * @uml.property  name="connectionManager"
	 */
	public static synchronized ConnectionManager getConnectionManager() {
		if (connectionManager == null)
			connectionManager = new ConnectionManager();
		return connectionManager;
	}

	/**
	 * @return
	 * @uml.property  name="connection"
	 */
	public XMPPConnection getConnection() {
		return connection;
	}

	/**
	 * @param connection
	 * @uml.property  name="connection"
	 */
	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
	}
}
