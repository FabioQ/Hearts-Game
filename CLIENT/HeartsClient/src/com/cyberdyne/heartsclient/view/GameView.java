package com.cyberdyne.heartsclient.view;


import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import org.jivesoftware.smack.XMPPConnection;

import com.cyberdyne.heartsclient.controller.ConnectionManager;
import com.cyberdyne.heartsclient.controller.GameManager;
import com.cyberdyne.heartsclient.listeners.MainListener;
import com.cyberdyne.heartsclient.model.Room;
import com.cyberdyne.heartsclient.model.Score;

import ch.aplu.jcardgame.CardGame;

public class GameView{
	/**
	 * @uml.property  name="roomUpdateCounter"
	 */
	private int roomUpdateCounter = 0;
	
	/**
	 * @uml.property  name="gameManager"
	 * @uml.associationEnd  multiplicity="(1 1)" inverse="gameView:com.cyberdyne.heartsclient.controller.GameManager"
	 */
	private GameManager gameManager;
	/**
	 * @uml.property  name="mainList"
	 * @uml.associationEnd  multiplicity="(1 1)" inverse="tonotify:com.cyberdyne.heartsclient.listeners.MainListener"
	 */
	private MainListener mainList;
	/**
	 * @uml.property  name="aRoom"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Room aRoom;
	/**
	 * @uml.property  name="table"
	 * @uml.associationEnd  multiplicity="(1 1)" inverse="gameView:com.cyberdyne.heartsclient.view.Table"
	 */
	private Table table;
	/**
	 * @uml.property  name="connection"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	XMPPConnection connection = ConnectionManager.getConnectionManager().getConnection();
	/**
	 * @uml.property  name="userName"
	 */
	String userName = connection.getAccountManager().getAccountAttribute("username");

	public GameView(GameManager gameManager, Room aRoom) {
		/*this.table = new Table();*/
		this.aRoom = Room.getRoomInstance();
		this.mainList = new MainListener (Room.getRoomInstance(), this);
		this.mainList.listen();
		this.table = new Table(this);
		this.gameManager = gameManager;
		System.out.println("Mio ruolo "+ Room.getRoomInstance().getMyRole());
		System.out.println("Mio user "+ this.userName);

		if((Room.getRoomInstance().getMyRole()!=null)&&(!Room.getRoomInstance().getMyRole().equalsIgnoreCase("none"))
				&&(!Room.getRoomInstance().getMyRole().equalsIgnoreCase("spectator")))
			this.table.addPlaceHolder(this.userName, Room.getRoomInstance().getMyRole());
		}

	public void notifyUserConnection(String user) {
		 /*utilizzato dal main listener per notificare la connessione di
         * un nuovo utente
          */
		 System.out.println("Metodo notify user connection");
         System.out.println("Nella stanza " + Room.getRoomInstance().getNameRoom());
         System.out.println("Si e' appena connesso l'utente con username "+ user);
    	 System.out.println("Ha ruolo: " +Room.getRoomInstance().getPlayer(user).getRole());

         System.out.println("Numero di utenti connessi: " + Room.getRoomInstance().getPlayersNumber());

         if(!(Room.getRoomInstance().getPlayer(user).getRole().equalsIgnoreCase("none")) &&
        		 !(Room.getRoomInstance().getPlayer(user).getRole().equalsIgnoreCase("spectator"))){
        	 
        	 System.out.println("Aggiunge il placeholder");
        	 /*aggiungo il segnaposto*/
     		this.table.addPlaceHolder(user, Room.getRoomInstance().getPlayer(user).getRole());

         }
         /*
          * CONTROLLO SE POSSIAMO INIZIARE IL GIOCO
          */
         if((Room.getRoomInstance().getPlayersNumber()==4) && (!Room.getRoomInstance().getMyRole().equalsIgnoreCase("spectator"))) 
        	{
        	 
        	 if(Room.getRoomInstance().getPlayer(user).getRole().equalsIgnoreCase("spectator"))
        		 gameManager.startGame(Room.getRoomInstance(), false);
        	
        	 else {
        		 gameManager = new GameManager();
        		 gameManager.startGame(Room.getRoomInstance(), true);
        	 }
        	 
       	  /* !!GAME STARTED!! */
       	  /*System.out.println("Il gioco puo' partire. Invio messaggio di start");
       	  gameManager = new GameManager();
       	  gameManager.startGame(Room.getRoomInstance());*/
        	 
        	 
        	 
        	}
         
         if ((Room.getRoomInstance().getPlayersNumber()>=4) && (Room.getRoomInstance().getMyRole().equalsIgnoreCase("spectator"))){
          	 gameManager = new GameManager();
        	 gameManager.startGameSpectator(Room.getRoomInstance());
         }
	}

	public void notifyRoomUpdate() {
			
          System.out.println("Stato stanza "+ Room.getRoomInstance().getStatus());
          System.out.println("Mio ruolo "+ Room.getRoomInstance().getMyRole());
          int[] myHand = new int[13];
          int[] handN = new int[13];
          int[] handE = new int[13];
          int[] handS = new int[13];
          int[] handW = new int[13];

          int i = 0;
          
          if(Room.getRoomInstance().getStatus().equalsIgnoreCase("active")){
        	  /*ho ricevuto le carte in mano*/
        	  System.out.println("Ho ricevuto le carte");
        	  
        	  Iterator iterator = Room.getRoomInstance().getGame().getHands().keySet().iterator();
        	  while(iterator.hasNext()){
	 			String card = (String)iterator.next();
	 			int cardInt = Integer.parseInt(card);
	 			myHand[i] = cardInt;
	 			System.out.println("Carta: "+card);
	 			i++;
	 			
	 		}
                  i=0;
                  iterator = Room.getRoomInstance().getGame().getHands("north").keySet().iterator();
        	  while(iterator.hasNext()){
	 			String card = (String)iterator.next();
	 			int cardInt = Integer.parseInt(card);
	 			handN[i] = cardInt;
	 			System.out.println("Carta: "+card);
	 			i++;
	 			
	 		}
                  i=0;
                  iterator = Room.getRoomInstance().getGame().getHands("east").keySet().iterator();
        	  while(iterator.hasNext()){
	 			String card = (String)iterator.next();
	 			int cardInt = Integer.parseInt(card);
	 			handE[i] = cardInt;
	 			System.out.println("Carta: "+card);
	 			i++;
	 			
	 		}
                  i=0;
                  iterator = Room.getRoomInstance().getGame().getHands("south").keySet().iterator();
        	  while(iterator.hasNext()){
	 			String card = (String)iterator.next();
	 			int cardInt = Integer.parseInt(card);
	 			handS[i] = cardInt;
	 			System.out.println("Carta: "+card);
	 			i++;
	 			
	 		}
                  i=0;
                  iterator = Room.getRoomInstance().getGame().getHands("west").keySet().iterator();
        	  while(iterator.hasNext()){
	 			String card = (String)iterator.next();
	 			int cardInt = Integer.parseInt(card);
	 			handW[i] = cardInt;
	 			System.out.println("Carta: "+card);
	 			i++;
	 			
	 		}
        	  System.out.println("GAMEVIEW...sto per uscire dal metodo notify room update e faccio il dealing");
        	  if(!Room.getRoomInstance().getMyRole().equalsIgnoreCase("spectator"))
        		  this.table.dealing(Room.getRoomInstance().getMyRole(), myHand, handN, handE, handS, handW);
        	  else {
        		  System.out.println("GAMEVIEW...sto per uscire dal metodo notify room update e faccio il dealing");
        		  this.table.dealingSpectator(Room.getRoomInstance().getMyRole(), handN, handE, handS, handW);
        	  }
          }
          
          if((Room.getRoomInstance().getStatus().equalsIgnoreCase("active"))&&(Room.getRoomInstance().getMyRole().equalsIgnoreCase("north"))) {
        	  table.playFirstTurn();          
        	  }
	}

	public void notifyUserDisconnection(String userName, String rolePlayer, String affiliationPlayer) {
		 /*utilizzato dal main listener per notificare la disconnessione di
         * un  utente
          */    
   	 	System.out.println("Nella stanza " + Room.getRoomInstance().getNameRoom());
        System.out.println("Si e' appena disconnesso l'utente con username "+ userName);
        System.out.println("Il giocatore "+ userName + "aveva affiliation player di "+ affiliationPlayer);
        System.out.println("Il giocatore "+ userName + "aveva role di "+ rolePlayer);
        
        if (affiliationPlayer.equalsIgnoreCase("owner")){
        	/*chiudo il tavolo*/
        	this.table.closeTable("owner");
        	Room.deleteRoomInstance();
        	this.mainList.removeMainListener();
        }
        
        else if ((Room.getRoomInstance().getStatus().equalsIgnoreCase("active"))&&(!rolePlayer.equalsIgnoreCase("spectator"))){
        	/*chiudo il tavolo*/
        	this.table.closeTable(affiliationPlayer);
        	Room.deleteRoomInstance();
        	this.mainList.removeMainListener();

        }
        
        else if (Room.getRoomInstance().getStatus().equalsIgnoreCase("inactive")){
        	/*elimino il placeholder*/
        	this.table.deletePlaceHolder(rolePlayer);
        	if(userName.equalsIgnoreCase(this.userName)) {
        		Room.deleteRoomInstance();
            	this.mainList.removeMainListener();
        	}
        		
        }
	}

	public void notifyIncomingCards() {
		int[] cards = new int[3];
		/*metodo utilizzato per notificare che sono state passate le tre carte del primo turno*/
 		Iterator iterator = Room.getRoomInstance().getGame().getIncomingCards().keySet().iterator();
 		int i = 0;
 		while(iterator.hasNext()){
 			 String card = (String)Room.getRoomInstance().getGame().getIncomingCards().get((String)iterator.next());
 			 System.out.println("Carta -> " + card);
 			 cards[i] = Integer.parseInt(card);
 			 i++;
 		}
 		table.moveMyIncomingCards(cards);
 		
 		if (!Room.getRoomInstance().getMyRole().equalsIgnoreCase("north"))
 			table.playFirstTurn();

	}
	
	public void notifyFirstTurn(String role, String string, String string2, String string3) {
		int[] cards = new int[3];
		cards[0]= Integer.parseInt(string); 
		cards[1]= Integer.parseInt(string2); 
		cards[2]= Integer.parseInt(string3); 
		table.moveFirstTurnCards(role, cards);
	}

	public void notifyPlayedCard(String card1, String userName) {
		String role = Room.getRoomInstance().getPlayer(userName).getRole();
		table.moveStandardTurnCard(card1, role);
		
		System.out.println("Sono entrato nel notifyPlayerCard");
 		
 		System.out.println("Il giocatore "+userName);
		System.out.println("ha giocato la carta " +card1);
 		
 		String turnTaker = Room.getRoomInstance().getGame().getTurnTaker();
 		boolean amITaker = false;
 		
 		if(turnTaker!=null)
 			table.transferToStock(Room.getRoomInstance().getPlayer(turnTaker).getRole());
 		
 		if (turnTaker!=null && connection.getAccountManager().getAccountAttribute("username").equalsIgnoreCase(turnTaker)){
 			/*questa carta appena giocata � quella del quarto turno e c'� un vincitore
 			 * se entro qui, significa che sono io, e quindi posso giocare*/
 			System.out.println("Ultimo turno, ho preso io la mano ");
 			playStandardTurn();			
 		}
 		
 		else if((Room.getRoomInstance().getPrevRole().equalsIgnoreCase(Room.getRoomInstance().getPlayer(userName).getRole()))&&
 				(!Room.getRoomInstance().getGame().isLastTurn())){
			System.out.println("Sono il giocatore con ruolo " + Room.getRoomInstance().getMyRole());
			System.out.println("E' il mio turno");
 			playStandardTurn();			
		}
	}
	
	public static Object randomValue(Hashtable table) {
        Object value = null;
        Collection values = table.keySet();
        int numElements = values.size();
        int randomIndex = (int)(Math.random() * numElements);
        Iterator iter = values.iterator();
        int index = 0;
        while (iter.hasNext()) {
              value = iter.next();
              if (index == randomIndex) {
                    return value;
              }
              index++;
        }
        return null;
  }  
	
	public void playFirstTurn(int[] playedCards) {
    	/*try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String card1 = "";
		String card2 = "";
		String card3 = "";
	
    	Hashtable hands = aRoom.getGame().getHands();
    	card1 = (String) GameView.randomValue(hands);
		card2 = (String) GameView.randomValue(hands);
    	card3 = (String) GameView.randomValue(hands);
    	while (card1.equalsIgnoreCase(card2))
    		card2 = (String) GameView.randomValue(hands);
    	while (card1.equalsIgnoreCase(card3))
    		card3 = (String) GameView.randomValue(hands);
    	while (card2.equalsIgnoreCase(card3))
    		card3 = (String) GameView.randomValue(hands);*/
    	System.out.println("Qui invio le carte");
    	gameManager.sendMove("firstturn", Integer.toString(playedCards[0]), 
    			Integer.toString(playedCards[1]), Integer.toString(playedCards[2]));
	}
	
	private void playStandardTurn() {
		
		table.playStandardTurn();
	}

	public void notifyFirstTurnEnd() {
		
		if (!Room.getRoomInstance().getMyRole().equalsIgnoreCase("spectator")){
			table.endFirstTurn();
		}else {
			table.endFirstTurnSpectator();
		}
		
		if(Room.getRoomInstance().getGame().getHands().containsKey("1")){
			/*significa che ho il due di fiori e che devo iniziare a giocare*/
			playStandardTurn();
			
		}
		
	}

	public boolean sendStandardTurn(int cardToPlay) {
		boolean sendOK = false;
		
		if (gameManager.checkCard(cardToPlay)){
			gameManager.sendMove("standard", String.valueOf(cardToPlay), "", "");
			sendOK= true;
		}
		
		return sendOK;
		
	}

	public String getaRoomName() {
		return Room.getRoomInstance().getNameRoom();
	}

	/**
	 * @param aRoom
	 * @uml.property  name="aRoom"
	 */
	public void setaRoom(Room aRoom) {
		this.aRoom = Room.getRoomInstance();
	}

	public void disconnectPlayer() {
		this.gameManager.exitGame();
		Room.deleteRoomInstance();
    	this.mainList.removeMainListener();
		
	}

	public void notifyMatchEnd(String score, String score2, String score3, String score4) {
		String myRole = Room.getRoomInstance().getMyRole();
		int myScore;
		int intScore = Integer.parseInt(score);
		int intScore2 = Integer.parseInt(score2);
		int intScore3 = Integer.parseInt(score3);
		int intScore4 = Integer.parseInt(score4);
		int minScore = intScore;
		String winner = "north";
		if(intScore2<=minScore){
			winner = "east";
			minScore = intScore2;
		}if (intScore3<=minScore) {
			winner="south";
			minScore=intScore3;
		}if (intScore4<=minScore) {
			winner="west";
			minScore=intScore4;
		}
		
		if (winner.equalsIgnoreCase(Room.getRoomInstance().getMyRole()))
			myScore = 4;
		else if (!winner.equalsIgnoreCase(Room.getRoomInstance().getMyRole())&&(!myRole.equalsIgnoreCase("spectator")))
			myScore = -1;
		else 
			myScore = 0;
		
		Score.getScoreInstance().setCurrentScore(myScore);
		this.mainList.removeMainListener();
		this.table.showScore(score, score2, score3, score4, winner);
		Room.deleteRoomInstance();
	}

	public void cardOnTable(String card, String role) {
		this.table.cardOnTable(card,role);
		
	}
}