package com.cyberdyne.heartsclient.providers;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

public class MUGMessageProvider	implements PacketExtensionProvider {
		
		public MUGMessageProvider(){
			
		}
		/* 
		 * Questa classe si occupa di effettuare il parse dell'XML 
		 */
		public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
			MUGMessage move = new MUGMessage();

			boolean done = false; 
			while(!done){
				int eventType = parser.next();
				if (eventType == XmlPullParser.START_TAG){
					move.setElementName("turn");
					move.setNameSpace("http://jabber.org/protocol/mug#user");
					if (parser.getName().equalsIgnoreCase("move")){
						move.setMoveElementName("move");
						move.setMoveNameSpace("http://jabber.org/protocol/mug/hearts");
						move.setTurnType(parser.getAttributeValue(null, "turntype"));
						move.setCard1(parser.getAttributeValue(null, "card1"));
						move.setCard2(parser.getAttributeValue(null, "card2"));
						move.setCard3(parser.getAttributeValue(null, "card3"));
						move.setSender(parser.getAttributeValue(null, "sender"));
					}
					
					else if (parser.getName().equalsIgnoreCase("matchend")){
						move.setMoveElementName("matchend");
						move.setTurnType("matchend");
						move.setMoveNameSpace("http://jabber.org/protocol/mug/hearts");
						move.setScore("north", parser.getAttributeValue(null, "scoreN"));
						move.setScore("east", parser.getAttributeValue(null, "scoreE"));
						move.setScore("south", parser.getAttributeValue(null, "scoreS"));
						move.setScore("west", parser.getAttributeValue(null, "scoreW"));

						
					}
				}
				else if (eventType == XmlPullParser.END_TAG){
					if (parser.getName().equalsIgnoreCase("turn"))
						done = true;
				}
			}
			return move;
			}
}
