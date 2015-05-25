package com.cyberdyne.heartsclient.providers;

import org.jivesoftware.smack.packet.PacketExtension;

public class MUGMessage implements PacketExtension {
	
	/**
	 * @uml.property  name="elementName"
	 */
	private String elementName;
	/**
	 * @uml.property  name="moveElementName"
	 */
	private String moveElementName;
	/**
	 * @uml.property  name="nameSpace"
	 */
	private String nameSpace;
	/**
	 * @uml.property  name="moveNameSpace"
	 */
	private String moveNameSpace;
	/**
	 * @uml.property  name="turnType"
	 */
	private String turnType;
	/**
	 * @uml.property  name="card1"
	 */
	private String card1;
	/**
	 * @uml.property  name="card2"
	 */
	private String card2;
	/**
	 * @uml.property  name="card3"
	 */
	private String card3;
	/**
	 * @uml.property  name="sender"
	 */
	private String sender;
	/**
	 * @uml.property  name="scoreN"
	 */
	private String scoreN;
	/**
	 * @uml.property  name="scoreE"
	 */
	private String scoreE;
	/**
	 * @uml.property  name="scoreS"
	 */
	private String scoreS;
	/**
	 * @uml.property  name="scoreW"
	 */
	private String scoreW;


	/**
	 * @return
	 * @uml.property  name="elementName"
	 */
	@Override
	public String getElementName() {
		return this.elementName;
	}
	
	/**
	 * @param elementName
	 * @uml.property  name="elementName"
	 */
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}
	
	public String getScore(String role){
		String score = "";
		if (role.equalsIgnoreCase("north"))
			score = scoreN;
		else if (role.equalsIgnoreCase("east"))
			score = scoreE;
		else if (role.equalsIgnoreCase("south"))
			score = scoreS;
		else if (role.equalsIgnoreCase("west"))
				score = scoreW;
		
		return score;
	}
	
	public void setScore(String role, String score){
		if (role.equalsIgnoreCase("north"))
			this.scoreN = score;
		else if (role.equalsIgnoreCase("east"))
			this.scoreE = score;
		else if (role.equalsIgnoreCase("south"))
			this.scoreS = score;
		else if (role.equalsIgnoreCase("west"))
			this.scoreW = score;
	}
	
	@Override
	public String getNamespace() {
		return this.nameSpace;
	}
	
	/**
	 * @param nameSpace
	 * @uml.property  name="nameSpace"
	 */
	public void setNameSpace(String nameSpace){
		this.nameSpace = nameSpace;
	}

	/**
	 * @return
	 * @uml.property  name="moveElementName"
	 */
	public String getMoveElementName() {
		return moveElementName;
	}

	/**
	 * @param moveElementName
	 * @uml.property  name="moveElementName"
	 */
	public void setMoveElementName(String moveElementName) {
		this.moveElementName = moveElementName;
	}

	/**
	 * @return
	 * @uml.property  name="moveNameSpace"
	 */
	public String getMoveNameSpace() {
		return moveNameSpace;
	}

	/**
	 * @param moveNameSpace
	 * @uml.property  name="moveNameSpace"
	 */
	public void setMoveNameSpace(String moveNameSpace) {
		this.moveNameSpace = moveNameSpace;
	}

	/**
	 * @return
	 * @uml.property  name="turnType"
	 */
	public String getTurnType() {
		return turnType;
	}

	/**
	 * @param turnType
	 * @uml.property  name="turnType"
	 */
	public void setTurnType(String turnType) {
		this.turnType = turnType;
	}

	/**
	 * @return
	 * @uml.property  name="card1"
	 */
	public String getCard1() {
		return card1;
	}

	/**
	 * @param card1
	 * @uml.property  name="card1"
	 */
	public void setCard1(String card1) {
		this.card1 = card1;
	}

	/**
	 * @return
	 * @uml.property  name="card2"
	 */
	public String getCard2() {
		return card2;
	}

	/**
	 * @param card2
	 * @uml.property  name="card2"
	 */
	public void setCard2(String card2) {
		this.card2 = card2;
	}

	/**
	 * @return
	 * @uml.property  name="card3"
	 */
	public String getCard3() {
		return card3;
	}

	/**
	 * @param card3
	 * @uml.property  name="card3"
	 */
	public void setCard3(String card3) {
		this.card3 = card3;
	}
	
	

	/**
	 * @return
	 * @uml.property  name="sender"
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * @param sender
	 * @uml.property  name="sender"
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}

	@Override
	public String toXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">").append("<")
		.append(getMoveElementName()).append(" xmlns=\"").append(getMoveNameSpace()).append("\" turntype=\"").append(getTurnType()).
		append("\" card1=\"").append(getCard1()).append("\" card2=\"").append(getCard2()).append("\" card3=\"").append(getCard3()).
		/*append("\" sender=\"").append(getSender()).*/append("\"/>").append("</").append(getElementName()).append(">");
		return buf.toString();
	}
}
