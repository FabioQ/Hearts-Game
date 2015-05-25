package com.cyberdyne.heartsclient.providers;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

public class MUGCreateRoomProvider implements PacketExtensionProvider {
	
	public MUGCreateRoomProvider(){
		
	}
	/* 
	 * Questa classe si occupa di effettuare il parse dell'XML 
	 */
	public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
		MUGCreateRoom response = new MUGCreateRoom();
		boolean done = false; 
		while(!done){
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG){
				//System.out.println("ENTRASTARTAG");
				//System.out.println(parser.getName());
				if (parser.getName().equalsIgnoreCase("status")){
					//System.out.println("e' status");
					parser.next();
					//System.out.println("STATUS: "+parser.getText());
					response.setStatus(parser.getText());
					/*response.setStatus(parser.getText());*/
				}
				else if (parser.getName().equalsIgnoreCase("bots")){
					//System.out.println("e' next");
					parser.next();
					//System.out.println("NEXT"+parser.getText());
					response.setBotNum(parser.getText());
					System.out.println(parser.getText());
				}
				else if (parser.getName().equalsIgnoreCase("item")){
					//System.out.println("e' item");
					//System.out.println(parser.getAttributeValue(null, "affiliation"));
					response.setItemAffiliation(parser.getAttributeValue(null, "affiliation"));
					//System.out.println(parser.getAttributeValue(null, "role"));
					response.setItemRole(parser.getAttributeValue(null, "role"));
					/*response.setStatus(parser.getText());*/
				}		
			}	
			else if (eventType == XmlPullParser.END_TAG){
				if (parser.getName().equalsIgnoreCase("game"))
					done = true;
			}
		}
		return response;
		}
}