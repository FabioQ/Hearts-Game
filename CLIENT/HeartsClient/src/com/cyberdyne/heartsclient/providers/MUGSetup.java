package com.cyberdyne.heartsclient.providers;

import java.util.Vector;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

public class MUGSetup implements PacketExtension {
	
	/**
	 * @uml.property  name="status"
	 */
	private String status;
	/**
	 * @uml.property  name="next"
	 */
	private String next;
	/**
	 * @uml.property  name="itemAffiliation"
	 */
	private String itemAffiliation;
	/**
	 * @uml.property  name="itemRole"
	 */
	private String itemRole;
	/**
	 * @uml.property  name="botNum"
	 */
	private String botNum;
	/**
	 * @uml.property  name="elementName"
	 */
	private String elementName;
	/**
	 * @uml.property  name="nameSpace"
	 */
	private String nameSpace;
	/**
	 * @uml.property  name="var"
	 */
	private String var;
	
	

	/**
	 * @uml.property  name="playerNCards"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
	 */
	private Vector playerNCards = new Vector();
	/**
	 * @uml.property  name="playerECards"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
	 */
	private Vector playerECards = new Vector();
	/**
	 * @uml.property  name="playerSCards"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
	 */
	private Vector playerSCards = new Vector();
	/**
	 * @uml.property  name="playerWCards"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
	 */
	private Vector playerWCards = new Vector();
	/**
	 * @uml.property  name="onTableN"
	 */
	private String onTableN = null;
	/**
	 * @uml.property  name="onTableE"
	 */
	private String onTableE = null;
	/**
	 * @uml.property  name="onTableS"
	 */
	private String onTableS = null;
	/**
	 * @uml.property  name="onTableW"
	 */
	private String onTableW = null;

	

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
	
	/**
	 * @return
	 * @uml.property  name="status"
	 */
	public String getStatus(){
		return this.status;
	}
	
	/**
	 * @param status
	 * @uml.property  name="status"
	 */
	public void setStatus(String status){
		this.status = status;
	}
	
	/**
	 * @return
	 * @uml.property  name="next"
	 */
	public String getNext(){
		return this.next;
	}
	
	/**
	 * @param next
	 * @uml.property  name="next"
	 */
	public void setNext(String next){
		this.next = next;
	}
	
	/**
	 * @param itemAffiliation
	 * @uml.property  name="itemAffiliation"
	 */
	public void setItemAffiliation(String itemAffiliation) {
		this.itemAffiliation = itemAffiliation;
	}

	/**
	 * @return
	 * @uml.property  name="itemAffiliation"
	 */
	public String getItemAffiliation() {
		return itemAffiliation;
	}

	/**
	 * @param itemRole
	 * @uml.property  name="itemRole"
	 */
	public void setItemRole(String itemRole) {
		if (itemRole==null || (itemRole.equalsIgnoreCase("none"))){
			this.itemRole = "spectator";
		}else {
			this.itemRole = itemRole;
		}
		
	}

	/**
	 * @return
	 * @uml.property  name="itemRole"
	 */
	public String getItemRole() {
		return itemRole;
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

	@Override
	public String toXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\" var=\"").append(getVar()).append("\"/>");
		return buf.toString();
	}

	/**
	 * @return
	 * @uml.property  name="var"
	 */
	public String getVar() {
		if (this.var!=null)
			return this.var;
		else
			return "";
	}
	
	/**
	 * @param var
	 * @uml.property  name="var"
	 */
	public void setVar(String var){
		this.var = var;
	}

	/**
	 * @param botNum
	 * @uml.property  name="botNum"
	 */
	public void setBotNum(String botNum) {
		this.botNum = botNum;
	}

	/**
	 * @return
	 * @uml.property  name="botNum"
	 */
	public String getBotNum() {
		return botNum;
	}
	
	/**
	 * @return
	 * @uml.property  name="playerNCards"
	 */
	public Vector getPlayerNCards() {
		return playerNCards;
	}

	/**
	 * @param playerNCards
	 * @uml.property  name="playerNCards"
	 */
	public void setPlayerNCards(Vector playerNCards) {
		this.playerNCards = playerNCards;
	}

	/**
	 * @return
	 * @uml.property  name="playerECards"
	 */
	public Vector getPlayerECards() {
		return playerECards;
	}

	/**
	 * @param playerECards
	 * @uml.property  name="playerECards"
	 */
	public void setPlayerECards(Vector playerECards) {
		this.playerECards = playerECards;
	}

	/**
	 * @return
	 * @uml.property  name="playerSCards"
	 */
	public Vector getPlayerSCards() {
		return playerSCards;
	}

	/**
	 * @param playerSCards
	 * @uml.property  name="playerSCards"
	 */
	public void setPlayerSCards(Vector playerSCards) {
		this.playerSCards = playerSCards;
	}

	/**
	 * @return
	 * @uml.property  name="playerWCards"
	 */
	public Vector getPlayerWCards() {
		return playerWCards;
	}

	/**
	 * @param playerWCards
	 * @uml.property  name="playerWCards"
	 */
	public void setPlayerWCards(Vector playerWCards) {
		this.playerWCards = playerWCards;
	}
	public Vector getPlayerCards(String role){
		if(role.equalsIgnoreCase("north"))
			return this.playerNCards;
		else if (role.equalsIgnoreCase("east"))
			return this.playerECards;
		else if(role.equalsIgnoreCase("south"))
			return this.playerSCards;
		else if(role.equalsIgnoreCase("west"))
			return this.playerWCards;
		else 
			return null;
	}

	/**
	 * @return
	 * @uml.property  name="onTableN"
	 */
	public String getOnTableN() {
		return onTableN;
	}

	/**
	 * @param onTableN
	 * @uml.property  name="onTableN"
	 */
	public void setOnTableN(String onTableN) {
		this.onTableN = onTableN;
	}

	/**
	 * @return
	 * @uml.property  name="onTableE"
	 */
	public String getOnTableE() {
		return onTableE;
	}

	/**
	 * @param onTableE
	 * @uml.property  name="onTableE"
	 */
	public void setOnTableE(String onTableE) {
		this.onTableE = onTableE;
	}

	/**
	 * @return
	 * @uml.property  name="onTableS"
	 */
	public String getOnTableS() {
		return onTableS;
	}

	/**
	 * @param onTableS
	 * @uml.property  name="onTableS"
	 */
	public void setOnTableS(String onTableS) {
		this.onTableS = onTableS;
	}

	/**
	 * @return
	 * @uml.property  name="onTableW"
	 */
	public String getOnTableW() {
		return onTableW;
	}

	/**
	 * @param onTableW
	 * @uml.property  name="onTableW"
	 */
	public void setOnTableW(String onTableW) {
		this.onTableW = onTableW;
	}
	
}