package org.frogx.service.games.hearts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;


public class CardsController {
	public static Logger log;
	static {
	      log = Logger.getLogger("MyLogger");
	}
	
	private boolean hearts = false;
	private Role role;
	public ArrayList<ArrayList<Integer>> gameHands;
	public int [] onTable;
	
	public boolean isHearts() {
		return hearts;
	}
	
	public boolean checkHearts(int card) {
		if (hearts)
			return true;
		if (getSeed(card) == 400) {
			hearts = true;
			return true;
		}
		else return false;
	}

	public void cardPlayed (int role, ArrayList<Integer> hand, int card) {
		log.warning("Card Played -> role: " + role);
		if (getSeed(card) == 400) {this.hearts = true; log.info("Card Player: HEARTS!!");}
		this.firstPlayer = (role%40)+10;
		log.warning("Card Played: carta giocata -> " + cardToString(card));
		hand.remove(hand.indexOf(card));
		Collections.sort(hand);
		log.warning("Card Played: hand size: " + hand.size());
	}
	
	private ArrayList<Integer> hand = new ArrayList<Integer>(13);
	public ArrayList<ArrayList<Integer>> shuffledHands;
	private int firstPlayer;
	private MatchEndingController matchController;
	
	public CardsController (ArrayList<ArrayList<Integer>> hands, int[] onTable) {
		this.matchController = new MatchEndingController();
		this.shuffledHands = new ArrayList<ArrayList<Integer>>();
		this.firstPlayer = 0;
		this.gameHands = hands; 
		this.onTable = onTable;
	}
	
	public ArrayList<Object> matchEnding() {
			log.info("\t\tmatchEnding(): chiamo matchController:getStandings()");
			int[] standings = matchController.getStandings();
			Role winner = Role.valueOf(((matchController.getWinner()*10)+10));
			ArrayList<Object> results = new ArrayList<Object>();
			results.add(winner);
			results.add(standings);
			return results;
	}
	
	private static int cardValue(int card) {
		if (card == 37) {
			return 13;
		}
		if (card <= 39) {
			return 0;
		}
		else
			return 1;
	}
	
	// returns card seed
	public int getSeed(int card) {
		if (card > 0) {
			if (card <= 13) {
				// clubs
				return 100;
			}
			if (card > 13 && card <= 26) {
				// diamonds
				return 200;
			}
			if (card > 26 && card <= 39) {
				// spades
				return 300;
			}
			if (card > 39) {
				// hearts
				return 400;
			}
			else return 0;
		}
		else return 0;
	}
	
	// deals cards
	//@SuppressWarnings("rawtypes")
	
	public ArrayList<ArrayList<Integer>> cardsDealer() {
		log.fine("Start\n");
		ArrayList<Integer> deck = new ArrayList<Integer>(52);
		for (int i = 1; i < 53; i++) {
			deck.add(i-1, i);
		}
		log.info("Deck populated\n");
		Collections.shuffle(deck);
		log.info("Deck shuffled\n");
		int counter = 0;
		for (int i = 0; i < 4; i++) {
			hand = new ArrayList<Integer>(13);
			for (int j = 0; j < 13 ; j++, counter++) {
				hand.add(deck.get(counter));	
			}
			log.info("Sort\n");
			Collections.sort(hand);
			this.shuffledHands.add(hand);
		}
		return this.shuffledHands;
	}
	
	// returns the winner of the hand
	public int handWinner(int[] cards, int seedCard) {
		log.warning("HandWinner");
		int winner = seedCard;
		int seed = getSeed(cards[seedCard]);
		log.warning("HandWinner seedCard: " + seedCard);
		if (getSeed(cards[0]) == seed && cards[0] >= cards[winner]) {
			winner = 0;
		}
		if (getSeed(cards[1]) == seed && cards[1] > cards[winner]) {
			winner = 1;
		}
		if (getSeed(cards[2]) == seed && cards[2] > cards[winner]) {
			winner = 2;
		}
		if (getSeed(cards[3]) == seed && cards[3] > cards[winner]) {
			winner = 3;
		}
		log.warning("HandWinner winner: " + cards[winner]);
		log.warning("HandWinner card: " + (cardToString(cards[winner])));
		this.firstPlayer = winner;
		role = Role.valueOf((winner+1)*10);
		log.warning("HandWinner winner: " + role.toString());
		log.warning("Points of winner: " + matchController.setStandings(winner, calculatePoints(cards)));
		return winner;
	}
	
	
	// returns points of the hand
	public int calculatePoints(int[] cards) {
		int points = 0;
		for (int i = 0; i < 4; i++) {
			points += cardValue(cards[i]);
		}
		return points;
	}
	
	public int nextFirstPlayer() {
		if (firstPlayer > 4) {
			log.info("Next player: " + (Role.valueOf(this.firstPlayer)));
			return this.firstPlayer;
		}
		else {
			log.info("Next player: " + (Role.valueOf((this.firstPlayer+1)*10)));
			return (this.firstPlayer+1)*10;
		}
	}

	public ArrayList<ArrayList<Integer>> passCards(int[][] cards) {
		log.info("PassCards init");
		String cardsToLog = new String();
		for (int i = 0; i < 4; i++) {
			log.info("PassCards: hand" + i + " size " + this.shuffledHands.get(i).size());
			for (int j = 0; j < 3; j++) {
				cardsToLog += cardToString(cards[i][j]) + " ";
			}
		}
		log.info("Received Cards: " + cardsToLog);
		for (int i = 0; i < 4; i++) {
			//log.info("PassCards 1");
			ArrayList<Integer> currentHand = new ArrayList<Integer>(13);
			currentHand = this.shuffledHands.get(i);
			//log.info("PassCards 2");
			for (int j = 0; j < 3; j++) {
				//log.info("\t|"+ i + "|" +j+ "| PassCards CardToTake: "+ cardToString(cards[(i+3)%4][j]));
				//log.info("PassCards CardToGive: "+ cardToString(cards[i][j]));
				//log.info("PassCards Index of the card: "+ currentHand.indexOf(cards[i][j]) + " - Size: " + currentHand.size() );
				currentHand.add(cards[(i+3)%4][j]);
				if (currentHand.contains(cards[i][j])) {
					log.info("PassCards ok");
					currentHand.remove(currentHand.indexOf(cards[i][j]));
				}
				else log.info("PassCards fail");
			}
			Collections.sort(currentHand);
		}
		if (this.shuffledHands.get(0).get(0) == 1) {
			log.info("PassCards 2 di fiori a: north");
			this.firstPlayer = 0;
		}
		if (this.shuffledHands.get(1).get(0) == 1) {
			log.info("PassCards 2 di fiori a: east");
			this.firstPlayer = 1;
		}
		if (this.shuffledHands.get(2).get(0) == 1) {
			log.info("PassCards 2 di fiori a: south");
			this.firstPlayer = 2;
		}
		if (this.shuffledHands.get(3).get(0) == 1) {
			log.info("PassCards 2 di fiori a: west");
			this.firstPlayer = 3;
		}
		
		return shuffledHands;
 	}
	
	public String cardToString(int value) {
		switch (value) {
			case 1: return "2 fiori";
			case 2: return "3 fiori";
			case 3: return "4 fiori";
			case 4: return "5 fiori";
			case 5: return "6 fiori";
			case 6: return "7 fiori";
			case 7: return "8 fiori";
			case 8: return "9 fiori";
			case 9: return "10 fiori";
			case 10: return "J fiori";
			case 11: return "Q fiori";
			case 12: return "K fiori";
			case 13: return "A fiori";
			case 14: return "2 quadri";
			case 15: return "3 quadri";
			case 16: return "4 quadri";
			case 17: return "5 quadri";
			case 18: return "6 quadri";
			case 19: return "7 quadri";
			case 20: return "8 quadri";
			case 21: return "9 quadri";
			case 22: return "10 quadri";
			case 23: return "J quadri";
			case 24: return "Q quadri";
			case 25: return "K quadri";
			case 26: return "A quadri";
			case 27: return "2 picche";
			case 28: return "3 picche";
			case 29: return "4 picche";
			case 30: return "5 picche";
			case 31: return "6 picche";
			case 32: return "7 picche";
			case 33: return "8 picche";
			case 34: return "9 picche";
			case 35: return "10 picche";
			case 36: return "J picche";
			case 37: return "Q picche";
			case 38: return "K picche";
			case 39: return "A picche";
			case 40: return "2 cuori";
			case 41: return "3 cuori";
			case 42: return "4 cuori";
			case 43: return "5 cuori";
			case 44: return "6 cuori";
			case 45: return "7 cuori";
			case 46: return "8 cuori";
			case 47: return "9 cuori";
			case 48: return "10 cuori";
			case 49: return "J cuori";
			case 50: return "Q cuori";
			case 51: return "K cuori";
			case 52: return "A cuori";
			default: return "Error";
		}
	}
	
}