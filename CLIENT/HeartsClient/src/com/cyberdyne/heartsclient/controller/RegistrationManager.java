package com.cyberdyne.heartsclient.controller;
import java.util.HashMap;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.jivesoftware.smack.XMPPException;

import com.cyberdyne.heartsclient.view.RegistrationView;


public class RegistrationManager {
	/**
	 * @uml.property  name="connectionManager"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	ConnectionManager connectionManager = ConnectionManager.getConnectionManager();
	/**
	 * @uml.property  name="registrationView"
	 * @uml.associationEnd  multiplicity="(1 1)" inverse="registrationManager:com.cyberdyne.heartsclient.view.RegistrationView"
	 */
	RegistrationView registrationView;
	
	public RegistrationManager() {
		this.registrationView = new RegistrationView(this);
	}
	/*
	 * Metodo per la creazione di un nuovo account, ritorna 0=ok, 1=err
	 */
	public int createNewAccount(String userName, String password, String mail) {
		if (connectionManager.getConnection().getAccountManager().supportsAccountCreation()) {
			try {
				HashMap myData = new HashMap();
		    	myData.put("name", "0");
		    	myData.put("email", mail);
				connectionManager.getConnection().getAccountManager().createAccount(userName, password, myData);
			} catch (XMPPException e) {
				System.out.println("Epic fail\n");
				e.printStackTrace();
				System.out.println("Errore: "+e.getMessage());
				return 1;
			}
		}
		else { 
			System.out.println("Il server non supporta la registrazione\n");
			return 1;
		}
		return 0;
		
	}
}
