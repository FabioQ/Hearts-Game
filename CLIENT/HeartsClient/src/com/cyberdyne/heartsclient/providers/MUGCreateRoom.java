package com.cyberdyne.heartsclient.providers;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

public class MUGCreateRoom implements PacketExtension {
	
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

	@Override
	public String getElementName() {
		return "game";
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
		if (itemRole==null){
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
		return "http://jabber.org/protocol/mug";
	}

	@Override
	public String toXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\" var=\"").append(getVar()).append("\"/>");
		return buf.toString();
	}

	public String getVar() {
		return "http://jabber.org/protocol/mug/hearts";
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

	
}