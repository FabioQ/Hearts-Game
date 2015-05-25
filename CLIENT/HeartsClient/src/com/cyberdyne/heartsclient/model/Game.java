package com.cyberdyne.heartsclient.model;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class Game {
	/**
	 * @uml.property  name="room"
	 * @uml.associationEnd  readOnly="true" inverse="game:com.cyberdyne.heartsclient.model.Room"
	 */
	private Room room;
	/**
	 * @uml.property  name="hands"
	 * @uml.associationEnd  qualifier="card:java.lang.String java.lang.String"
	 */
	private Hashtable hands = new Hashtable();
        /**
		 * @uml.property  name="handsN"
		 * @uml.associationEnd  qualifier="card:java.lang.String java.lang.String"
		 */
        private Hashtable handsN = new Hashtable();
        /**
		 * @uml.property  name="handsE"
		 * @uml.associationEnd  qualifier="card:java.lang.String java.lang.String"
		 */
        private Hashtable handsE = new Hashtable();
        /**
		 * @uml.property  name="handsS"
		 * @uml.associationEnd  qualifier="card:java.lang.String java.lang.String"
		 */
        private Hashtable handsS = new Hashtable();
        /**
		 * @uml.property  name="handsW"
		 * @uml.associationEnd  qualifier="card:java.lang.String java.lang.String"
		 */
        private Hashtable handsW = new Hashtable();
        
	/**
	 * @uml.property  name="talon"
	 * @uml.associationEnd  qualifier="valueOf:java.lang.String java.lang.String"
	 */
	private Hashtable talon = new Hashtable();
	/**
	 * @uml.property  name="incomingCards"
	 * @uml.associationEnd  qualifier="card3:java.lang.String java.lang.String"
	 */
	private Hashtable incomingCards = new Hashtable();
	/**
	 * @uml.property  name="firstCard"
	 */
	private String firstCard;
	
	/**
	 * @uml.property  name="firstCardSuit"
	 */
	private int firstCardSuit;
	
	/**
	 * @uml.property  name="possibleTakers"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
	 */
	Vector possibleTakers = new Vector();
	
	/**
	 * @uml.property  name="lastTurn"
	 */
	private boolean lastTurn = false;
	/**
	 * @uml.property  name="secondTurn"
	 */
	private boolean secondTurn = false;
	/**
	 * @uml.property  name="turnTaker"
	 */
	private String turnTaker = null;
	
	private boolean firstTurn = true;
	
	private boolean brokenHearts = false;
	
	public Game(Room aRoom){
		//this.room = Room.getRoomInstance();
		Room.getRoomInstance().setGame(this);
	}

	/**
	 * @return
	 * @uml.property  name="incomingCards"
	 */
	public Hashtable getIncomingCards() {
		return incomingCards;
	}
	
	/**
	 * @return
	 * @uml.property  name="hands"
	 */
	public Hashtable getHands(){
		return this.hands;
	}
        
        public Hashtable getHands(String role){
            
               if(role.equalsIgnoreCase("north"))
                   return this.handsN;
                   
               else if(role.equalsIgnoreCase("east"))
                   return this.handsE;

               else if(role.equalsIgnoreCase("south"))
                   return this.handsS;

               else if(role.equalsIgnoreCase("west"))
                   return this.handsW;
               
               return null;

        }

	public void setIncomingCards(String card1, String card2, String card3) {
		this.incomingCards.put(card1, card1);
		this.incomingCards.put(card2, card2);
		this.incomingCards.put(card3, card3);
		
		this.setSecondTurn(true);
//		this.hands.put(card1, card1);
//		this.hands.put(card2, card2);
//		this.hands.put(card3, card3);

	}
	
	

	public void joinIncomingCards() {
		  Iterator iterator = this.incomingCards.keySet().iterator();
	       while(iterator.hasNext()) {
	           String card = (String)iterator.next();
	           this.hands.put(card, card);
	       }
	}
	//Rimuove le carte dalla mano del giocatore quando gioca
	public void removeCards(String card1, String card2, String card3){
		if(card1!=null)
			this.hands.remove(card1);
		if(card2!=null)
			this.hands.remove(card2);
		if(card3!=null)
			this.hands.remove(card3);
	}
	
	public void setHands(Vector cards, String role){
		System.out.println("GAME SET HANDS per il ruolo: "+ role);
		Iterator iterator = cards.iterator();

                
                if(role.equalsIgnoreCase(Room.getRoomInstance().getMyRole())){
                    while(iterator.hasNext()){
                    	String card = (String)iterator.next();
 			this.hands.put(card, card);
                    }
                }
                
                else if(role.equalsIgnoreCase("north")){
                    while(iterator.hasNext()){
                    	String card = (String)iterator.next();
                    	System.out.println("iterazione di set HandsN, card: " + card);
                    	if(card!=null)
                    		this.handsN.put(card, card);
                    }
                }
                
                else if(role.equalsIgnoreCase("east")){
                    while(iterator.hasNext()){
                    	String card = (String)iterator.next();
                    	System.out.println("iterazione di set HandsE, card: " + card);
                    	if(card!=null)
                    		this.handsE.put(card, card);
                    }
                } 
                
                else if(role.equalsIgnoreCase("south")){
                    while(iterator.hasNext()){
                    	String card = (String)iterator.next();
                    	System.out.println("iterazione di set HandsS, card: " + card);
                    	if(card!=null)
                    		this.handsS.put(card, card);
                    }
                }
                
                else if(role.equalsIgnoreCase("west")){
                    while(iterator.hasNext()){
                    	String card = (String)iterator.next();
                    	System.out.println("iterazione di set HandsW, card: " + card);
                    	if(card!=null)
                    		this.handsW.put(card, card);
                    }
                }
	}
	
	
	
	/*metodo utilizzato quando viene giocata una carta*/
	public void setPlayedCard(String card1, String player){
		int length;
		this.firstTurn = false;
		
		this.lastTurn = false;
		length = this.talon.size();
		
		System.out.println("-------------LUNGHEZZA DEL TALON "+ length);
		int cardSuit;
		int i;
		
		i = Integer.parseInt(card1);
		if (i<=13)
			cardSuit = 1;
		else if (i<=26)
			cardSuit = 2;
		else if (i<=39)
			cardSuit = 3;
		else{
			cardSuit = 4;
			this.brokenHearts = true;
		}
		
		if (length == 0){
			/*si tratta della prima carta buttata in questa mano e ci prendiamo il seme*/
			this.setTurnTaker(null);
			firstCard = card1;
			this.firstCardSuit = cardSuit;
			
			
		}
		
		this.talon.put(card1, player);
		
		length = this.talon.size();

		if (length == 4){
			this.possibleTakers.removeAllElements();
			/*in questo caso � stata appena buttata l'ultima carta*/
			this.lastTurn = true;
			/*devo calcolare il vincitore*/
			//mi vedo tutti i semi delle carte che sono state buttate a terra
			Iterator iterator = talon.keySet().iterator();
	 		while(iterator.hasNext()){
	 			int suit;
	 			String card = (String)iterator.next();
	 			int cardNum = Integer.parseInt(card);
	 			if (cardNum<=13)
	 				suit = 1;
				else if (cardNum<=26)
					suit = 2;
				else if (cardNum<=39)
					suit = 3;
				else
					suit = 4;
	 			//se la carta buttata corrisponde al seme iniziale, potrebbe essere una presa
	 			if (suit==this.firstCardSuit) 
	 				possibleTakers.add(card);
	 		}
	 		int maxValue = 0;
	 		Iterator iteratorVett = possibleTakers.iterator();
	 		while(iteratorVett.hasNext()){
	 			String cardVett = (String)iteratorVett.next();
	 			int cardVettInt = Integer.parseInt(cardVett);
	 			if (cardVettInt>=maxValue)
	 				maxValue=cardVettInt;

	 		}
			this.setTurnTaker((String) this.talon.get(String.valueOf(maxValue)));
			System.out.println("il vincitore �: "+(String) this.talon.get(String.valueOf(maxValue)));
			
			this.talon.clear();
		}
	}
	
	public boolean checkSuitPresence(int suit){
		boolean suitIsPresent = false;
		Iterator iterator = this.hands.keySet().iterator();
 		while(iterator.hasNext() && !suitIsPresent){
 			int suitToCompare;
 			String cardToCompare = (String)iterator.next();
 			int cardNumToCompare = Integer.parseInt(cardToCompare);
 			if (cardNumToCompare<=13)
 				suitToCompare = 1;
			else if (cardNumToCompare<=26)
				suitToCompare = 2;
			else if (cardNumToCompare<=39)
				suitToCompare = 3;
			else
				suitToCompare = 4;
 			
 			if (suitToCompare == suit)
 				suitIsPresent = true;
 				
 		}
 		
 		return suitIsPresent;
 		
		
	}

	/**
	 * @param turnTaker
	 * @uml.property  name="turnTaker"
	 */
	public void setTurnTaker(String turnTaker) {
		this.turnTaker = turnTaker;
	}

	/**
	 * @return
	 * @uml.property  name="turnTaker"
	 */
	public String getTurnTaker() {
		return turnTaker;
	}

	/**
	 * @return
	 * @uml.property  name="lastTurn"
	 */
	public boolean isLastTurn() {
		return lastTurn;
	}

	/**
	 * @param lastTurn
	 * @uml.property  name="lastTurn"
	 */
	public void setLastTurn(boolean lastTurn) {
		this.lastTurn = lastTurn;
	}
	/**
	 * @param b
	 * @uml.property  name="secondTurn"
	 */
	private void setSecondTurn(boolean b) {
		this.secondTurn = b;
		
	}
	
	/**
	 * @return
	 * @uml.property  name="secondTurn"
	 */
	public boolean isSecondTurn(){
		return this.secondTurn;
	}
	
	public int getTalonSize(){
		return this.talon.size();
	}
	
	public boolean isFirstTurn(){
		return this.firstTurn;
	}

	public boolean isBrokenHearts(){
		return this.brokenHearts;
	}
	
	public int getFirstCardSuit(){
		return this.firstCardSuit;
	}
}
