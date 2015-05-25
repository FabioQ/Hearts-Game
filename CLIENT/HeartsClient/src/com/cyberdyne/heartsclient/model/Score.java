package com.cyberdyne.heartsclient.model;


import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;

import com.cyberdyne.heartsclient.controller.ConnectionManager;

public class Score {
	
	private static Score score = null;
	/**
	 * @uml.property  name="currentScore"
	 */
	private int currentScore;
	
	public Score() {
		currentScore = Integer.parseInt(ConnectionManager.getConnectionManager().getConnection().getAccountManager().getAccountAttribute("name"));
	}
	
	public static synchronized Score getScoreInstance() {
		if (score == null)
			score = new Score();
		return score;
	}
	
	/**
	 * @return
	 * @uml.property  name="currentScore"
	 */
	public int getCurrentScore() {
		return currentScore;
	}

	/**
	 * @param matchScore
	 * @uml.property  name="currentScore"
	 */
	public void setCurrentScore(int matchScore) {
		this.currentScore = currentScore+matchScore;
		Registration reg = new Registration();
        reg.setType(IQ.Type.SET);
        reg.setTo(ConnectionManager.getConnectionManager().getConnection().getServiceName());
        Map<String, String> map = new HashMap<String, String>();
        map.put("username",ConnectionManager.getConnectionManager().getConnection().getAccountManager().getAccountAttribute("username"));
        map.put("password",ConnectionManager.getConnectionManager().getConnection().getAccountManager().getAccountAttribute("password"));
        map.put("name",Integer.toString(this.currentScore));
        reg.setAttributes(map);
        ConnectionManager.getConnectionManager().getConnection().sendPacket(reg);
	}
}
