package com.cyberdyne.heartsclient.model;

public class Player {
	
	/**
	 * @uml.property  name="affiliation"
	 */
	private String affiliation;
	/**
	 * @uml.property  name="username"
	 */
	private String username;
	/**
	 * @uml.property  name="overallScore"
	 */
	private int overallScore;
	/**
	 * @uml.property  name="gameScore"
	 */
	private int gameScore;
	/**
	 * @uml.property  name="role"
	 */
	private String role; 

	public Player(String role, String affiliation, String username) {
		this.role = role;
		this.affiliation = affiliation;
		this.username = username;
		this.gameScore = 0;
		
	}
	
	public Player(){
		
	}
	
	/**
	 * @return
	 * @uml.property  name="role"
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role
	 * @uml.property  name="role"
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * @return
	 * @uml.property  name="affiliation"
	 */
	public String getAffiliation() {
		return affiliation;
	}

	/**
	 * @param affiliation
	 * @uml.property  name="affiliation"
	 */
	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	/**
	 * @return
	 * @uml.property  name="username"
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 * @uml.property  name="username"
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return
	 * @uml.property  name="overallScore"
	 */
	public int getOverallScore() {
		return overallScore;
	}

	/**
	 * @param overallScore
	 * @uml.property  name="overallScore"
	 */
	public void setOverallScore(int overallScore) {
		this.overallScore = overallScore;
	}

	/**
	 * @return
	 * @uml.property  name="gameScore"
	 */
	public int getGameScore() {
		return gameScore;
	}

	/**
	 * @param gameScore
	 * @uml.property  name="gameScore"
	 */
	public void setGameScore(int gameScore) {
		this.gameScore = gameScore;
	}
}
