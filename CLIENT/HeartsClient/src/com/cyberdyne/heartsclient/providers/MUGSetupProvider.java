package com.cyberdyne.heartsclient.providers;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Vector;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

public class MUGSetupProvider implements PacketExtensionProvider {
	
	public MUGSetupProvider(){
		
	}
	/* 
	 * Questa classe si occupa di effettuare il parse dell'XML 
	 */
	public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
		MUGSetup response = new MUGSetup();

		boolean done = false; 
		while(!done){
			Vector cards = new Vector();


			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG){
				//System.out.println("ENTRASTARTAG");
				//System.out.println(parser.getName());
				if (parser.getName().equalsIgnoreCase("status")){
					response.setElementName("game");
					response.setNameSpace("http://jabber.org/protocol/mug");
					response.setVar("http://jabber.org/protocol/mug/hearts");
					//System.out.println("e' status");
					parser.next();
					//System.out.println("STATUS: "+parser.getText());
					response.setStatus(parser.getText());
					/*response.setStatus(parser.getText());*/
					
				}
				else if (parser.getName().equalsIgnoreCase("bots")){
					response.setElementName("game");
					response.setNameSpace("http://jabber.org/protocol/mug");
					response.setVar("http://jabber.org/protocol/mug/hearts");
					//System.out.println("e' next");
					parser.next();
					//System.out.println("NEXT"+parser.getText());
					response.setBotNum(parser.getText());
					System.out.println(parser.getText());
				}
				
					
				else if (parser.getName().equalsIgnoreCase("item")){
					response.setElementName("game");
					response.setNameSpace("http://jabber.org/protocol/mug");
					response.setVar("http://jabber.org/protocol/mug/hearts");
					//System.out.println("e' item");
					//System.out.println(parser.getAttributeValue(null, "affiliation"));
					response.setItemAffiliation(parser.getAttributeValue(null, "affiliation"));
					//System.out.println(parser.getAttributeValue(null, "role"));
					response.setItemRole(parser.getAttributeValue(null, "role"));
					/*response.setStatus(parser.getText());*/
				}
			
				
				/*se lo stato della stanza è active devo recuperare le carte che il server invia*/
				else if ((response.getStatus()!=null)&&(response.getStatus().equalsIgnoreCase("active"))&&
						parser.getName().equalsIgnoreCase("playerN")){
					
					System.out.println("ELEMENT NAME DOPO IF "+ parser.getName().toString());
				
					cards = response.getPlayerNCards();
					String cardValue = null;
					for (int i=1; i<=13;i++){
						cardValue = parser.getAttributeValue(null, "Card"+i);
						/*MODIFICA*/
						if(cardValue.equalsIgnoreCase("0"))
							cardValue = null;
						/*MODIFICA*/
						
						cards.add(cardValue);
					}
					/* MODIFICA */
					String cardOnTable = null;
					cardOnTable = parser.getAttributeValue(null, "ontable");
					if(cardOnTable!=null) {
						if(cardOnTable.equalsIgnoreCase("0"))
							cardOnTable = null;
						response.setOnTableN(cardOnTable);
					}
				}
				
				else if ((response.getStatus()!=null)&&(response.getStatus().equalsIgnoreCase("active"))&&
						parser.getName().equalsIgnoreCase("playerE")){
					
					System.out.println("ELEMENT NAME DOPO IF "+ parser.getName().toString());
				
					cards = response.getPlayerECards();
					String cardValue = null;
					for (int i=1; i<=13;i++){
						cardValue = parser.getAttributeValue(null, "Card"+i);
						/*MODIFICA*/
						if(cardValue.equalsIgnoreCase("0"))
							cardValue = null;
						/*MODIFICA*/
						cards.add(cardValue);
					}
					/* MODIFICA */
					String cardOnTable = null;
					cardOnTable = parser.getAttributeValue(null, "ontable");
					if(cardOnTable!=null) {
						if(cardOnTable.equalsIgnoreCase("0"))
							cardOnTable = null;
						response.setOnTableE(cardOnTable);
					}
				}
				
				else if ((response.getStatus()!=null)&&(response.getStatus().equalsIgnoreCase("active"))&&
						parser.getName().equalsIgnoreCase("playerS")){
					
					System.out.println("ELEMENT NAME DOPO IF "+ parser.getName().toString());
				
					cards = response.getPlayerSCards();
					String cardValue = null;
					for (int i=1; i<=13;i++){
						cardValue = parser.getAttributeValue(null, "Card"+i);
						/*MODIFICA*/
						if(cardValue.equalsIgnoreCase("0"))
							cardValue = null;
						/*MODIFICA*/
						cards.add(cardValue);
					}
					/* MODIFICA */
					String cardOnTable = null;
					cardOnTable = parser.getAttributeValue(null, "ontable");
					if(cardOnTable!=null) {
						if(cardOnTable.equalsIgnoreCase("0"))
							cardOnTable = null;
						response.setOnTableS(cardOnTable);
					}
				}
				
				else if ((response.getStatus()!=null)&&(response.getStatus().equalsIgnoreCase("active"))&&
						parser.getName().equalsIgnoreCase("playerW")){
					
					System.out.println("ELEMENT NAME DOPO IF "+ parser.getName().toString());
				
					cards = response.getPlayerWCards();
					String cardValue = null;
					for (int i=1; i<=13;i++){
						cardValue = parser.getAttributeValue(null, "Card"+i);
						/*MODIFICA*/
						if(cardValue.equalsIgnoreCase("0"))
							cardValue = null;
						/*MODIFICA*/
						cards.add(cardValue);
					}
					/* MODIFICA */
					String cardOnTable = null;
					cardOnTable = parser.getAttributeValue(null, "ontable");
					if(cardOnTable!=null) {
						if(cardOnTable.equalsIgnoreCase("0"))
							cardOnTable = null;
						response.setOnTableW(cardOnTable);
					}
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