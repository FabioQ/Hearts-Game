package org.frogx.service.games.hearts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;


public class Bot {
	
	//final private static String NAMESPACE = "http://jabber.org/protocol/mug/hearts";
	private Role role;
	private CardsController cardsController;
	private ArrayList<Integer> hand;
	public static Logger log;
	static {
	      log = Logger.getLogger("MyLogger");
	}
	
	Bot(Role role, CardsController controller) {
		log.warning("Bot " + role.name() + " created");
		this.role = role;
		this.hand = null;
		this.cardsController = controller;
	
	}

	public ArrayList<Integer> getHand() {
		return this.hand;
	}

	public void setHand(ArrayList<Integer> hand) {
		this.hand = hand;
	}
	
	private ArrayList<Integer> generateRandomCards(ArrayList<Integer> botHand, int number, int seed) {
		log.warning("generateRandomCards init");
		ArrayList<Integer> randomCards = new ArrayList<Integer>(number);
		ArrayList<Integer> playable = botHand;
		if (number != 3) {
			log.warning("generateRandomCards playCard");
			playable = playableCards(seed, botHand);
			int aNumber = (int)((Math.random()*100)%playable.size());
			log.info("generateRandomCards: " + cardsController.cardToString(playable.get(aNumber)));
			randomCards.add(playable.get(aNumber));
		}
		
		else {
			for (int i = 0; i < number; i++) {
				int aNumber = (int) (((Math.random()*100)%this.hand.size()));
			//	log.info("aNumber... " + cardsController.cardToString(this.hand.get(aNumber)));
				while (!(playable.contains(this.hand.get(aNumber))) || randomCards.contains(this.hand.get(aNumber))) {
					aNumber = (int) (((Math.random()*100)%this.hand.size()));
					log.info("generateRandomCards: " + cardsController.cardToString(this.hand.get(aNumber)));
				}
				randomCards.add(this.hand.get(aNumber));
			}
		}
		log.info("generateRandomCard: card " + randomCards.get(0));
		return randomCards;
	}
	
	public ArrayList<Integer> passCards() {
		log.warning("BOT: " + this.role.name());
		ArrayList<Integer> cardsToGive = generateRandomCards(this.hand, 3, 0);
		Collections.sort(cardsToGive);
		ArrayList<Integer> cardsToReturn = new ArrayList<Integer>();
		cardsToReturn.add(this.hand.get(this.hand.indexOf(cardsToGive.get(2))));
	//	log.warning("PassCards: " + cardsController.cardToString(this.hand.get(this.hand.indexOf(cardsToGive.get(2)))) + " passed");
		cardsToReturn.add(this.hand.get(this.hand.indexOf(cardsToGive.get(1))));
	//	log.warning("PassCards: " + cardsController.cardToString(this.hand.get(this.hand.indexOf(cardsToGive.get(1)))) + " passed");
		cardsToReturn.add(this.hand.get(this.hand.indexOf(cardsToGive.get(0))));
	//	log.warning("PassCards: " + cardsController.cardToString(this.hand.get(this.hand.indexOf(cardsToGive.get(0)))) + " passed");	
		Collections.sort(this.hand);	
		return cardsToReturn;
		
	}
	
	public void getCards(int[] cards) {
		this.hand.add(cards[0]);
		this.hand.add(cards[1]);
		this.hand.add(cards[2]);
		Collections.sort(this.hand);
		return;
	}
	
	private ArrayList<Integer> playableCards(int seed, ArrayList<Integer> hand) {
		log.warning("PlayableCards seed: " + seed);
		ArrayList<Integer> playable = new ArrayList<Integer>();
		log.warning("PlayableCards: hand size " + this.hand.size());
		if (this.hand.size() == 13) {
			if (this.hand.contains(1)) {
				playable.add(1);
				return playable;
			}
		}
		for (int i = 0; i < hand.size(); i++) {
			if (cardsController.getSeed(hand.get(i)) == seed) {
				playable.add(hand.get(i));
			}
		}
		if (playable.size() == 0) {
			if (hand.size() != 13) {
				for (int i = 0; i < hand.size(); i++) {
					if (cardsController.isHearts()) {
						log.info("Playable Card: isHearts dovrebbe essere true... " + cardsController.isHearts());
						playable.add(hand.get(i));
					}
					else if (cardsController.getSeed(hand.get(i)) != 400) {
						log.info("Playable Card: isHearts dovrebbe essere false... " + cardsController.isHearts());
						log.info("PlayableCards - HAND: " + i + ") " + cardsController.cardToString(hand.get(i)) + " seed: " + cardsController.getSeed(hand.get(i)));
						playable.add(hand.get(i));
					}
				}
			}
			else
				for (int i = 0; i < hand.size(); i++) {
					if ((hand.get(i) != 37) && (cardsController.getSeed(hand.get(i)) != 400)) {
						playable.add(hand.get(i));
					}
				}
		}
		
		if (playable.size() == 0) {
			log.info("PlayableCards only Hearts!");
			for (int i = 0; i < hand.size(); i++) {
				playable.add(hand.get(i));
			}
		}
		for (int i = 0; i < hand.size(); i++) {
			log.info("PlayableCards - HAND: " + i + ") " + cardsController.cardToString(hand.get(i)) + " seed: " + cardsController.getSeed(hand.get(i)));
		}
		for (int i = 0; i < playable.size(); i++) {
			log.info("PlayableCards - GOOD " + i + ")" + cardsController.cardToString(playable.get(i)));
		}
		return playable;
	}
	
	public int playCard(int seed) {
		log.warning("PlayCard: seed " + seed + " Role: " + this.role.name());
		//log.warning("PlayCard");
		if (this.hand.contains(1)) {
		//	this.hand.remove(this.hand.indexOf(1));
		//	Collections.sort(this.hand);
			log.warning("PlayCard: " + cardsController.cardToString(1));
			return 1;
		}
		else {
			log.warning("PlayCard");
			ArrayList<Integer> index = generateRandomCards(this.hand, 1, seed);
			log.info("PlayCard: seed " + seed + " index " + index.get(0));
			int card = this.hand.get(this.hand.indexOf(index.get(0)));
		//	this.hand.remove(this.hand.indexOf(card));
		//	Collections.sort(this.hand);
			log.warning("PlayCard: " + cardsController.cardToString(card));
			return card;
		}
	}
	

}

