package com.cyberdyne.heartsclient.controller;
import java.text.Normalizer;
import java.util.*;
import java.io.*;
 
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.workgroup.ext.forms.WorkgroupForm;

import com.cyberdyne.heartsclient.model.Room;
import com.cyberdyne.heartsclient.providers.MUGMessageProvider;
import com.cyberdyne.heartsclient.providers.MUGSetupProvider;
public class JabberSmackAPI /*implements MessageListener*/{
 
    /**
	 * @uml.property  name="connection"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    XMPPConnection connection = ConnectionManager.getConnectionManager().getConnection();
    /**
	 * @uml.property  name="aRoom"
	 * @uml.associationEnd  readOnly="true"
	 */
    private Room aRoom;
    /**
	 * @uml.property  name="gameManager"
	 * @uml.associationEnd  readOnly="true"
	 */
    private GameManager gameManager;
 
    public static void main(String args[]) throws XMPPException, IOException
    {
    // declare variables
    	//JabberSmackAPI c = new JabberSmackAPI();
    	//BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	//String msg;

 
    // turn on the enhanced debugger
    	XMPPConnection.DEBUG_ENABLED = true;
    	
    	MUGSetupProvider provider = new MUGSetupProvider();
    	ProviderManager.getInstance().addExtensionProvider("game", "http://jabber.org/protocol/mug", provider);
    	
    	MUGMessageProvider messProvider = new MUGMessageProvider();
    	ProviderManager.getInstance().addExtensionProvider("turn", "http://jabber.org/protocol/mug#user", messProvider);
    	
    	LoginManager loginManager = new LoginManager();
    	
    }
}