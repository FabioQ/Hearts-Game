package com.cyberdyne.heartsclient.controller;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;

import com.cyberdyne.heartsclient.view.LoginView;

public class LoginManager {
	/**
	 * @uml.property  name="connectionManager"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	ConnectionManager connectionManager = ConnectionManager.getConnectionManager();
	/**
	 * @uml.property  name="loginView"
	 * @uml.associationEnd  multiplicity="(1 1)" inverse="loginManager:com.cyberdyne.heartsclient.view.LoginView"
	 */
	LoginView loginView;
	
	public LoginManager() {
		this.loginView = new LoginView(this);
		
	}
	
	/*
	 * Metodo per il Login, ritorna 0=ok, 1=err
	 */
	public int LoginAccount(String userName, String password) {
		try {
			connectionManager.getConnection().login(userName, password);


		} catch (XMPPException e) {
			e.printStackTrace();
			System.out.println("Errore: "+e.getMessage());
			return 1;
		}
		return 0;
	}
}

