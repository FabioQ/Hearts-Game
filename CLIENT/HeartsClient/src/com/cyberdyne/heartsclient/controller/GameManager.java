package com.cyberdyne.heartsclient.controller;


import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Presence;

import com.cyberdyne.heartsclient.model.Game;
import com.cyberdyne.heartsclient.model.Room;
import com.cyberdyne.heartsclient.providers.MUGMessage;
import com.cyberdyne.heartsclient.providers.MUGSetup;
import com.cyberdyne.heartsclient.utils.ConstantData;
import com.cyberdyne.heartsclient.view.GameView;

public class GameManager {
	
	/**
	 * @uml.property  name="aRoom"
	 * @uml.associationEnd  readOnly="true"
	 */
	private Room aRoom;
	/**
	 * @uml.property  name="game"
	 * @uml.associationEnd  
	 */
	private Game game;
	/**
	 * @uml.property  name="gameView"
	 * @uml.associationEnd  inverse="gameManager:com.cyberdyne.heartsclient.view.GameView"
	 */
	private GameView gameView;
	/**
	 * @uml.property  name="connectionManager"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	ConnectionManager connectionManager = ConnectionManager.getConnectionManager();
	/**
	 * @uml.property  name="userName"
	 */
	String userName = connectionManager.getConnection().getAccountManager().getAccountAttribute("username");

	public GameManager(Room aRoom) {
		//this.aRoom = Room.getRoomInstance();
		this.gameView = new GameView(this, Room.getRoomInstance());
		
		
	}
	
	public GameManager(){
		
	}
	

	public void startGameSpectator(Room roomInstance) {
		// TODO Auto-generated method stub
		this.game = new Game(Room.getRoomInstance());
	}
	
	public void startGame(Room aRoom, boolean newGame){
		//this.aRoom = Room.getRoomInstance();
		if (newGame) /*inserito per test*/
			this.game = new Game(Room.getRoomInstance());
		
		Message mess = new Message();
		
		mess.setFrom(userName+"@"+ConstantData.host);
		mess.setTo(Room.getRoomInstance().getNameRoom()+"@"+ConstantData.hostService);
		
		MUGSetup mugSetup = new MUGSetup();
		mugSetup.setElementName("start");
		mugSetup.setNameSpace("http://jabber.org/protocol/mug#user");
		mugSetup.setVar("");
		mess.addExtension(mugSetup);
		connectionManager.getConnection().sendPacket(mess);
		System.out.println(mess.toXML());
	}
	
	public void sendMove(String turnType, String card1, String card2, String card3){
		
		Message mess = new Message();
		
		mess.setFrom(userName+"@"+ConstantData.host);
		mess.setTo(Room.getRoomInstance().getNameRoom()+"@"+ConstantData.hostService);
		mess.setType(Type.chat);
		
		MUGMessage mugMessage = new MUGMessage();
		mugMessage.setElementName("turn");
		mugMessage.setNameSpace("http://jabber.org/protocol/mug#user");
		mugMessage.setMoveElementName("move");
		mugMessage.setMoveNameSpace("http://jabber.org/protocol/mug/hearts");
		mugMessage.setTurnType(turnType);
		mugMessage.setCard1(card1);
		mugMessage.setCard2(card2);
		mugMessage.setCard3(card3);
		//mugMessage.setSender("");
		mess.addExtension(mugMessage);
		connectionManager.getConnection().sendPacket(mess);
		if (turnType.equalsIgnoreCase("firstturn")){
			System.out.println("Ho inviato le carte al giocatore alla mia sinistra");
			System.out.println(mess.toXML());
			//CANCELLAZIONE DELLE CARTE CHE PASSO AL MIO COMPAGNO DI FIANCO
			game.removeCards(card1, card2, card3);
		}
		if (turnType.equalsIgnoreCase("standard")){
			System.out.println("Ho giocato la carta "+card1);
			System.out.println(mess.toXML());
			game.removeCards(card1, null, null);
		}
	}
	
	public void exitGame(){
		Presence pres = new Presence(Presence.Type.unavailable);
		pres.setFrom(userName+"@"+ConstantData.host);
		pres.setTo(Room.getRoomInstance().getNameRoom()+"@"+ConstantData.hostService);
		connectionManager.getConnection().sendPacket(pres);
	}

	public boolean checkCard(int cardToPlay) {
		int myCardSuit = this.getCardSuit(cardToPlay);
		boolean cardOK = false;
		
		/*se non ci sono carte a terra ed è il primo turno di gioco*/
		if((this.game.getTalonSize() == 0)&&(this.game.isFirstTurn())){
			/*devo buttare il due di fiori*/
			if (cardToPlay == 1)
				cardOK = true;
		}
			
		/* se non ci sono carte a terra e non sto giocando il primo turno*/
		else if ((this.game.getTalonSize()==0) && (!this.game.isFirstTurn())){
			/*posso giocare qualsiasi carta io voglia, a meno che i cuori non siano 
			 * ancora stati infranti
			 */
			if (myCardSuit != 4)
				cardOK = true;
			
			else if ((myCardSuit == 4) && (this.game.isBrokenHearts())){
				cardOK = true;
			}
			
			else if((myCardSuit==4)&&(!this.game.isBrokenHearts())&&(!this.game.checkSuitPresence(1))
					&&(!this.game.checkSuitPresence(2))&&(!this.game.checkSuitPresence(3))
					){
				cardOK = true;
			}
		}
		
		else if (this.game.getTalonSize() != 0){
			if(myCardSuit == this.game.getFirstCardSuit()){
				cardOK = true;
			}
			
			else if((myCardSuit != this.game.getFirstCardSuit()) && 
					(!this.game.checkSuitPresence(this.game.getFirstCardSuit()))){
				//if (myCardSuit != 4)
					cardOK = true;
				
				/*else if ((myCardSuit == 4) && (this.game.isBrokenHearts())){
					cardOK = true;
				}
				
				else if((myCardSuit==4)&&(!this.game.isBrokenHearts())&&(!this.game.checkSuitPresence(1))
						&&(!this.game.checkSuitPresence(2))&&(!this.game.checkSuitPresence(3))
						){
					cardOK = true;
				}*/
			}				
			
		}
		
		return cardOK;
	}
	
	public int getCardSuit (int cardToPlay){
		int cardSuit = 0;
		if (cardToPlay<=13)
			cardSuit = 1;
		else if (cardToPlay<=26)
			cardSuit = 2;
		else if (cardToPlay<=39)
			cardSuit = 3;
		else
			cardSuit = 4;
		
		return cardSuit;
	}

}