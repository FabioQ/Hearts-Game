/**
 * Copyright (C) 2008-2009 Guenther Niess. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.frogx.service.games.hearts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.*;
import org.dom4j.DocumentFactory;
import java.io.*;

import org.dom4j.Element;
import org.frogx.service.api.MUGManager;
import org.frogx.service.api.MUGMatch;
import org.frogx.service.api.MUGOccupant;
import org.frogx.service.api.MUGRoom;
import org.frogx.service.api.exception.ConflictException;
import org.frogx.service.api.exception.GameConfigurationException;
import org.frogx.service.api.exception.InvalidTurnException;
import org.frogx.service.api.exception.RequiredPlayerException;
import org.frogx.service.api.exception.UnsupportedGameException;
import org.xmpp.component.ComponentException;
import org.xmpp.forms.DataForm;
import org.xmpp.forms.FormField;
//import org.xmpp.packet.Packet;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.frogx.service.games.hearts.CardsController;

public class Match implements MUGMatch {
	
/*@@@@@@@@@@@@@@@@@ LOGGER @@@@@@@@@@@@@@@@@*/	
	
//	private static final Logger log = LoggerFactory.getLogger(Match.class);
	public static Logger log;
	static {
	   try {
	      boolean append = true;
	      DateFormat dateFormat = new SimpleDateFormat("HH-mm-ss");
	      Date date = new Date();
	      FileHandler fh = new FileHandler("/home/laiho/openfire/logs/Hearts/" + dateFormat.format(date), append);
	//      FileHandler fh = new FileHandler("C:/Users/cruz/log_as/" + dateFormat.format(date), append);
	      fh.setFormatter(new SimpleFormatter());
	      log = Logger.getLogger("MyLogger");
	      log.addHandler(fh);
	   }
	  catch (IOException e) {
	     e.printStackTrace();
	    }
	}
	
/*@@@@@@@@@@@@@@@@@ LOGGER @@@@@@@@@@@@@@@@@*/		
	
	
	final private static String NAMESPACE = "http://jabber.org/protocol/mug/hearts";
	
	private Boolean gameStarted = false;

	private MUGRoom room = null;
	
	private MUGOccupant playerN = null;
	private MUGOccupant playerE = null;
	private MUGOccupant playerS = null;
	private MUGOccupant playerW = null;
	private MUGOccupant player = null; // for broadcast
	
	private Role turn = Role.north;
	private int[][] cardSwap; 
	private CardsController cardsController = new CardsController(this.gameHands, this.onTable);
	//private BotsController botsController;
	private Role role = Role.none;
	private Collection<Element> botMoveToSend = new ArrayList<Element>();
	private Collection<Element> moves = null;
	private Element move = null;
	private int moveID = 1;
	private int botUsers = 0;
	private int turnCounter = 1;
	private ArrayList<ArrayList<Integer>> gameHands = new ArrayList<ArrayList<Integer>>();
	private int[] onTable;
	private int firstPlayerTurn;
	private String turnType;
	private Status status;
	private int firstTurn = 0;
	private Element stateElement = null;
	private DataForm configForm;
	private Element configElement;

	Bot botE = new Bot(Role.east, this.cardsController);
	Bot botS = new Bot(Role.south, this.cardsController);
	Bot botW = new Bot(Role.west, this.cardsController);
	
	/**
	 * Occupants who don't play. They are watching the match.
	 */
	private List<MUGOccupant> spectators = null;
	

	public Match(MUGRoom room, MUGManager mugManager) {
		this.room = room;
		this.spectators = new ArrayList<MUGOccupant>();
		this.playerN = null;
		this.playerE = null;
		this.playerS = null;
		this.playerW = null;
		this.botE = new Bot(Role.east, this.cardsController);
		this.botS = new Bot(Role.south, this.cardsController);
		this.botW = new Bot(Role.west, this.cardsController);
		this.turnCounter = 1;
		this.firstPlayerTurn = 0;
		this.status = Status.created;
		this.cardSwap = new int[4][3];
		this.onTable = new int[4];
		log.info("Room name: " + room.getName() + "\t Room description: "+ room.getDescription() + "\n");
		log.info("\tMatch: match\n");
		calculateStateElement();
		log.info("\tMatch: calculateStateElement done\n");
		initConfig();
		log.info("\tMatch: initConfig done\n");
	}
	

	public void destroy() {
		log.info("Destroy: seek");
		room = null;
		stateElement = null;
		spectators = null;
		playerN = null;
		playerE = null;
		playerS = null;
		playerW = null;
		botE = null;
		botS = null;
		botW = null;
		turnCounter = 0;
		this.cardSwap = null;
		this.onTable = null;
		this.firstPlayerTurn = 0;
		//room.destroy();
	}
	

	public Collection<MUGOccupant> getPlayers() {
		log.info("getPlayers: init");
		

		if (playerN == null && playerE == null && playerS == null && playerW == null)
			return null;
		
		Collection<MUGOccupant> players = new ArrayList<MUGOccupant>();
		
		if (playerN != null)
			players.add(playerN);
		if (playerE != null)
			players.add(playerE);		
		if (playerS != null)
			players.add(playerS);		
		if (playerW != null)
			players.add(playerW);
		log.info("getPlayers: done");
		return players;
		
	}
	

	public Collection<String> getFreeRoles() {
		log.info("getFreeRoles: init");
		if (playerN != null && 
				(playerE != null && botUsers < 3) &&
				(playerS != null && botUsers < 2) && 
				(playerW != null && botUsers < 1))
			return null;
		
		Collection<String> roles = new ArrayList<String>();
		if (playerN == null)
			roles.add(Role.north.name());
		if (playerE == null && botUsers < 3) //non deve essere restituito questo ruolo se ci sono 3 bot
			roles.add(Role.east.name());
		if (playerS == null && botUsers < 2)
			roles.add(Role.south.name());
		if (playerW == null && botUsers < 1)
			roles.add(Role.west.name());
		log.info("getFreeRoles: done");
		return roles;
	}

	public String getRole(MUGOccupant player) {
		log.info("getRole: init per player "+ player.getNickname());
		if (player.equals(playerN))
			return Role.north.name();
		else if (player.equals(playerE))
			return Role.east.name();
		else if (player.equals(playerS))
			return Role.south.name();
		else if (player.equals(playerW))
			return Role.west.name();
		else
			return null;
	}
	

	public void addSpectator(MUGOccupant occupant) {
		log.info("addSpectator: init");
		spectators.add(occupant);
	}

	public void reserveRole(MUGOccupant occupant, String roleName) 
			throws ConflictException, GameConfigurationException {
		log.info("reserveRole: init");
		
		if (roleName.equals(Role.north.name())) {
			if (playerN != null)
				throw new ConflictException();
			playerN = occupant;
			if (spectators.contains(occupant))
				spectators.remove(occupant);
		}
		if (roleName.equals(Role.east.name())) {
			//se uno spettatore vuole il ruolo East, deve assicurarsi che non ci siano 3 bot.
			if (playerE != null || botUsers > 2){
				throw new ConflictException();
			}
			else{
			playerE = occupant;
			if (spectators.contains(occupant))
				spectators.remove(occupant);
			}
		}
		if (roleName.equals(Role.south.name())) {
			//se uno spettatore vuole il ruolo South, deve assicurarsi che non ci siano 2 o 3 bot.
			if (playerS != null || botUsers > 1){
				throw new ConflictException();
			}
			else{
			playerS = occupant;
			if (spectators.contains(occupant))
				spectators.remove(occupant);
			}
		}
		if (roleName.equals(Role.west.name())) {
			//se uno spettatore vuole il ruolo West, deve assicurarsi che non ci siano 1, 2 o 3 bot.
			if (playerW != null || botUsers > 0){
				throw new ConflictException();
			}
			else{
			playerW = occupant;
			if (spectators.contains(occupant))
				spectators.remove(occupant);
			}
		}
		else {
			throw new GameConfigurationException();
		}
	}
	
	/**
	 * An occupant which have no role yet can reserve any free game role.
	 * 
	 * @param occupant The room occupant who wants to reserve a role.
	 * @return The name of the reserved role or null if no role could be reserved.
	 */
	public String reserveFreeRole(MUGOccupant occupant) {
		log.info("reserveFreeRole: init");
		if (playerN == null) {
			playerN = occupant;
			if (spectators.contains(occupant))
				spectators.remove(occupant);
			return Role.north.name();
		}
		//il ruolo di East viene assegnato solo se ci sono 0, 1 o 2 bot
		else if (playerE == null && botUsers < 3) {
			playerE = occupant;
			if (spectators.contains(occupant))
				spectators.remove(occupant);
			return Role.east.name();
		}
		//il ruolo di South viene assegnato solo se ci sono 0 o 1 bot
		else if (playerS == null && botUsers < 2) {
			playerS = occupant;
			if (spectators.contains(occupant))
				spectators.remove(occupant);
			return Role.south.name();
		}
		//il ruolo di West viene assegnato solo se ci sono 0 bot
		else if (playerW == null && botUsers < 1) {
			playerW = occupant;
			if (spectators.contains(occupant))
				spectators.remove(occupant);
			return Role.west.name();
		}
		else {
			return null;
		}
	}
	

	public Element getState() {
		log.info("getState: init");
		if (status == Status.active)
			calculateStateElement();
		return stateElement;
	}

	
	public void start() throws RequiredPlayerException, GameConfigurationException {
		log.info("start: init");
		if ((playerN == null) || 
			(playerE == null && botUsers < 3) ||
			(playerS == null && botUsers < 2) || 
			(playerW == null && botUsers < 1)) {
			throw new RequiredPlayerException();
			}
		status = Status.active;
	}
	
	
	public void configureBotHands() {
		log.info("\t Questo e' il primo turno");
		//controllo che ci sia almeno 1 bot a cui configurare la mano
		if (botUsers > 0) {
			//se la mano del botW e' vuota, non ho mai settato le mani ai bot e lo faccio
			log.info("\t Ci sono " + botUsers + " bot ");
			if (botW.getHand() == null){
				log.info("\t BotHands non settate, le configuro");
				if (botUsers == 3){
					log.info("\t configuro le mani dei 3 bot");
					botE.setHand(gameHands.get(1));
					botS.setHand(gameHands.get(2));
					botW.setHand(gameHands.get(3));
				}
				else if (botUsers == 2){
					log.info("\t configuro le mani dei 2 bot");
					botS.setHand(gameHands.get(2));
					botW.setHand(gameHands.get(3));
				}
				else if (botUsers == 1){
					log.info("\t configuro la mano dell'unico bot, west");
					botW.setHand(gameHands.get(3));
				}
			}
			else {
				log.info("botW hand not null, le mani sono state configurate");
			}
		}
		
	}
	
	
	public void processFirstTurn() throws InvalidTurnException, ComponentException {

		if (turnType.equalsIgnoreCase("firstturn")) {
			log.info("PFT\t Mi e' stata mandata una mossa firstturn, per lo scambio di carte.");
			log.info("PFT------------------------ MOSSA:"+ moveID +" ------------------------");
			int card1, card2, card3;
			int src = 0;
			card1 = Integer.valueOf(move.attributeValue("card1"));
			card2 = Integer.valueOf(move.attributeValue("card2"));
			card3 = Integer.valueOf(move.attributeValue("card3"));
			log.info("PFT\t - Le carte che voglio passare sono: "+card1+" - "+card2+" - "+card3);
			log.info("PFT\t - Mossa da cui ho estrapolato i dati: "+ move.asXML());
			if (role == Role.north) src = 0;
			else if (role == Role.east) src = 1;
			else if (role == Role.south) src = 2;
			else if (role == Role.west) src = 3;
			log.info("PFT\t Il mio ruolo, messo nel messaggio broadcastato e' = " + role.toString());
			log.info("PFT\t player: " + player.getNickname());
			cardSwap[src][0] = card1;
			cardSwap[src][1] = card2;
			cardSwap[src][2] = card3;
			room.broadcastTurn(moves, player);
			this.moveID++;
		}
		
		if (this.firstTurn == 0) {
			if (	((role == Role.west)&&(botUsers == 0)) ||
					((role == Role.south)&&(botUsers == 1)) ||
					((role == Role.east)&&(botUsers == 2)) ||
					((role == Role.north)&&(botUsers == 3)))
			{
				configureBotHands();
				if (role == Role.north && botUsers == 3) {
					log.info("PFT\t ruolo attuale North, bot 3, estraggo le carte da scambiare ai bot");
					swapCards(botE, cardSwap[1]);
					swapCards(botS, cardSwap[2]);
					swapCards(botW, cardSwap[3]);
					log.info("PFT\faccio il broadcast delle carte estratte");
					for (int h = 1; h < 4; h++) {
						Element botMove = DocumentFactory.getInstance().createDocument().addElement("move", NAMESPACE);
						botMove.addAttribute("turntype", "firstturn");
						botMove.addAttribute("card1", Integer.toString(cardSwap[h][0]));
						botMove.addAttribute("card2", Integer.toString(cardSwap[h][1]));
						botMove.addAttribute("card3", Integer.toString(cardSwap[h][2]));
						botMove.addAttribute("sender", (Role.valueOf((h+1)*10)).toString());
						log.info("PFT\t Il bot "+ (Role.valueOf((h+1)*10)).toString() +" invia a tutti la mossa:  "+ botMove.asXML());
						botMoveToSend.add(botMove);
						room.broadcastTurn(botMoveToSend, player);
						
					}
				}
				
				if (role == Role.east && botUsers == 2) {
					log.info("PFT\t ruolo attuale East, bot 2, estraggo le carte da scambiare ai bot");
					swapCards(botS, cardSwap[2]);
					swapCards(botW, cardSwap[3]);
					log.info("PFT\faccio il broadcast delle carte estratte");
					for (int h = 2; h < 4; h++) {
						Element botMove = DocumentFactory.getInstance().createDocument().addElement("move", NAMESPACE);
						botMove.addAttribute("turntype", "firstturn");
						botMove.addAttribute("card1", Integer.toString(cardSwap[h][0]));
						botMove.addAttribute("card2", Integer.toString(cardSwap[h][1]));
						botMove.addAttribute("card3", Integer.toString(cardSwap[h][2]));
						botMove.addAttribute("sender", (Role.valueOf((h+1)*10)).toString());
						log.info("PFT\t Il bot "+ (Role.valueOf((h+1)*10)).toString() +" invia a tutti la mossa:  "+ botMove.asXML());
						botMoveToSend.add(botMove);
						room.broadcastTurn(botMoveToSend, player);
						
					}
				}
		
				if (role == Role.south && botUsers == 1) {
					log.info("PFT\t ruolo attuale South, bot 1, estraggo le carte da scambiare ai bot");
					swapCards(botW, cardSwap[3]);
					log.info("PFT\faccio il broadcast delle carte estratte");
					Element botMove = DocumentFactory.getInstance().createDocument().addElement("move", NAMESPACE);
					botMove.addAttribute("turntype", "firstturn");
					botMove.addAttribute("card1", Integer.toString(cardSwap[3][0]));
					botMove.addAttribute("card2", Integer.toString(cardSwap[3][1]));
					botMove.addAttribute("card3", Integer.toString(cardSwap[3][2]));
					botMove.addAttribute("sender", Role.west.toString());
					log.info("PFT\t Il bot "+ Role.west.toString() +" invia a tutti la mossa:  "+ botMove.asXML());
					botMoveToSend.add(botMove);
					room.broadcastTurn(botMoveToSend, player);
					
				}
		
				log.info("PFT\t Finisce la prima mano, e' ora di scambiare le carte nelle strutture dati");
				this.firstTurn = 1;
				cardsController.passCards(cardSwap);
				log.info("PFT\t Carte Swappate");	
				//log.info("PFT:\t MOVEID = " + moveID);
				
				if (botUsers > 2) {
					log.info("PFT------------------------ MOSSA:"+ moveID +" ------------------------");
					botW.setHand(gameHands.get(3));
					log.info("PFT\t Carte scambiate e mano del bot West configurata e sincronizzata");
					log.info("PFT\tNext First Player: " + cardsController.nextFirstPlayer());
					this.moveID++;
				}
				
				if (botUsers > 1) {
					log.info("PFT------------------------ MOSSA:"+ moveID +" ------------------------");
					botS.setHand(gameHands.get(2));
					log.info("PFT\t Carte scambiate e mano del bot South configurata e sincronizzata");
					log.info("PFT\tNext First Player: " + cardsController.nextFirstPlayer());
					this.moveID++;
				}
				
				if (botUsers > 0) {
					log.info("PFT------------------------ MOSSA:"+ moveID +" ------------------------");
					botE.setHand(gameHands.get(1));
					log.info("PFT\t Carte scambiate e mano del bot East configurata e sincronizzata");
					log.info("PFT\tNext First Player: " + cardsController.nextFirstPlayer());
					this.moveID++;
				}
				this.firstTurn = 1;
			}
		}
		this.role = Role.valueOf(cardsController.nextFirstPlayer());
		log.info("PFT\tNext First Player: " + cardsController.nextFirstPlayer());
		//log.info("PFT\t Mi calcolo quale sara' il prossimo turno (in base al 2 di fiori), ovvero:   "+ role.toString());
		this.firstPlayerTurn = (turn.getValue() / 10) - 1;
		this.turnCounter = 1;
	}
	
	
	
	
	
	public void processStandardTurn() throws InvalidTurnException, ComponentException {
		log.info("PST: \t STANDARD TURN!");
		log.info("PST: \t Next Player: " + role.name());
		
		if (turnCounter == 1) {
			firstPlayerTurn = (role.getValue() / 10) - 1;
			log.info("\t Sono al primo turno della mano, configuro firsPlayerTurn = "+firstPlayerTurn);
		}
		
		if (turnCounter == 5) {
			log.info("\t Controllo chi ha vinto la mano");
			cardsController.handWinner(onTable, firstPlayerTurn);
			role = Role.valueOf(cardsController.nextFirstPlayer());
			log.info("\t Il giocatore che ha preso e deve lanciare e': "+ turn.toString());
			turnCounter = 1;
			resetTable();
		}
		
		
		if ((role == Role.north) || role == Role.east && botUsers < 3 || role == Role.south && botUsers < 2 || role == Role.west && botUsers < 1) {
			log.info("PST: \t GIOCA UN UMANO!");	
			if (moveID == 57) return;
			if (turnType.equalsIgnoreCase("standard")) {
				int playRoleID = ((role.getValue() % 40) + 10);
				log.info("\t Player ID: " + playRoleID);
				int card1 = Integer.valueOf(move.attributeValue("card1"));
				this.onTable[(role.getValue() / 10) - 1] = card1;
				//cardsController.checkHearts(card1);
				log.info("\t Turno normale, la carta giocata sul tavolo e': "+card1+" --- " + cardsController.cardToString(card1));
				log.info("\t Se ci sono bot, faccio buttare le carte in tavola a tutti i bot giocanti che non hanno gia giocato.");
				log.info("BROADCAST!" + move.asXML() + player.toString());
				room.broadcastTurn(moves, player);
				cardsController.cardPlayed(role.getValue(), gameHands.get((role.getValue()/10)-1), card1);
				role = Role.valueOf(cardsController.nextFirstPlayer());
				log.info("\t Aggiorno nextRoleID che diventa -> " + playRoleID);
				moveID++;
				turnCounter++;
				log.info("PST: ------------------------ MOSSA:"+ moveID +" ------------------------");
				log.info("PST: ------------------------ TURNO:"+ turnCounter +" ------------------------");
				if (turnCounter == 1) {
					firstPlayerTurn = (role.getValue() / 10) - 1;
					log.info("PST: \t Sono al primo turno della mano, configuro firsPlayerTurn = "+firstPlayerTurn);
				}
				
				if (turnCounter == 5) {
					log.info("PST: \t Controllo chi ha vinto la mano");
					cardsController.handWinner(onTable, firstPlayerTurn);
					role = Role.valueOf(cardsController.nextFirstPlayer());
					log.info("PST: \t Il giocatore che ha preso e deve lanciare e': "+ turn.toString());
					turnCounter = 1;
					resetTable();
				}
			}
		}
		
		
		
		// ************ BOT **********
		while ((role == Role.east && botUsers > 2) || (role == Role.south && botUsers > 1) || (role == Role.west && botUsers > 0)) {
			// GIOCA IL BOT
			log.info("PST: \t BOT TURN!");
			if (moveID == 57) return;
			
			if (turnCounter == 1) {
				firstPlayerTurn = (role.getValue() / 10) - 1;
				log.info("PST: \t Sono al primo turno della mano, configuro firsPlayerTurn = "+firstPlayerTurn);
			}
			
			if (turnCounter == 5) {
				log.info("PST: \t Controllo chi ha vinto la mano");
				cardsController.handWinner(onTable, firstPlayerTurn);
				role = Role.valueOf(cardsController.nextFirstPlayer());
				log.info("PST: \t Il giocatore che ha preso e deve lanciare e': "+ turn.toString());
				turnCounter = 1;
				resetTable();
			}

			if (role == Role.east && botUsers > 2) {
				moveID++;
				turnCounter++;
				int botCard = botE.playCard(cardsController.getSeed(onTable[firstPlayerTurn]));
				cardsController.cardPlayed(role.getValue(), gameHands.get((role.getValue()/10)-1), botCard);
				this.onTable[((role.getValue()) / 10) - 1] = botCard;
				log.info("PST: \tBot card played " + cardsController.cardToString(botCard));
				Element botMove = DocumentFactory.getInstance().createDocument().addElement("move", NAMESPACE);
				botMove.addAttribute("turntype", "standard");
				botMove.addAttribute("card1", Integer.toString(botCard));
				botMove.addAttribute("card2", "");
				botMove.addAttribute("card3", "");
				botMove.addAttribute("sender", role.toString());
				botMoveToSend.add(botMove);
				log.info("PST:\t La mossa broadcastata e': " + "\n\t" +botMove.toString()+"\n");
				room.broadcastTurn(botMoveToSend, player);
				role = Role.valueOf(cardsController.nextFirstPlayer());
				log.info("PST: \tIl giocatore che deve giocare dopo di me e' " + role.getValue());
				log.info("PST: ------------------------ MOSSA:"+ moveID +" ------------------------");
				log.info("PST: ------------------------ TURNO:"+ turnCounter +" ------------------------");
				if (turnCounter == 1) {
					firstPlayerTurn = (role.getValue() / 10) - 1;
					log.info("PST: \t Sono al primo turno della mano, configuro firsPlayerTurn = "+firstPlayerTurn);
				}
				
				if (turnCounter == 5) {
					log.info("PST: \t Controllo chi ha vinto la mano");
					cardsController.handWinner(onTable, firstPlayerTurn);
					role = Role.valueOf(cardsController.nextFirstPlayer());
					log.info("PST: \t Il giocatore che ha preso e deve lanciare e': "+ turn.toString());
					turnCounter = 1;
					resetTable();
				}
			}
			else if (role == Role.south && botUsers > 1) {
				moveID++;
				turnCounter++;
				int botCard = botS.playCard(cardsController.getSeed(onTable[firstPlayerTurn]));
				cardsController.cardPlayed(role.getValue(), gameHands.get((role.getValue()/10)-1), botCard);
				this.onTable[((role.getValue()) / 10) - 1] = botCard;
				log.info("PST: \tBot card played " + cardsController.cardToString(botCard));
				Element botMove = DocumentFactory.getInstance().createDocument().addElement("move", NAMESPACE);
				botMove.addAttribute("turntype", "standard");
				botMove.addAttribute("card1", Integer.toString(botCard));
				botMove.addAttribute("card2", "");
				botMove.addAttribute("card3", "");
				botMove.addAttribute("sender", role.toString());
				botMoveToSend.add(botMove);
				log.info("PST:\t La mossa broadcastata e': " + "\n\t" +botMove.toString()+"\n");
				room.broadcastTurn(botMoveToSend, player);
				role = Role.valueOf(cardsController.nextFirstPlayer());
				log.info("PST: \tIl giocatore che deve giocare dopo di me e' " + role.getValue());
				log.info("PST: ------------------------ MOSSA:"+ moveID +" ------------------------");
				log.info("PST: ------------------------ TURNO:"+ turnCounter +" ------------------------");
				if (turnCounter == 1) {
					firstPlayerTurn = (role.getValue() / 10) - 1;
					log.info("PST: \t Sono al primo turno della mano, configuro firsPlayerTurn = "+firstPlayerTurn);
				}
				
				if (turnCounter == 5) {
					log.info("PST: \t Controllo chi ha vinto la mano");
					cardsController.handWinner(onTable, firstPlayerTurn);
					role = Role.valueOf(cardsController.nextFirstPlayer());
					log.info("PST: \t Il giocatore che ha preso e deve lanciare e': "+ turn.toString());
					turnCounter = 1;
					resetTable();
				}
			}
			else if (role == Role.west && botUsers > 0) {
				moveID++;
				turnCounter++;
				int botCard = botW.playCard(cardsController.getSeed(onTable[firstPlayerTurn]));
				cardsController.cardPlayed(role.getValue(), gameHands.get((role.getValue()/10)-1), botCard);
				this.onTable[((role.getValue()) / 10) - 1] = botCard;
				log.info("PST: \tBot card played " + cardsController.cardToString(botCard));
				Element botMove = DocumentFactory.getInstance().createDocument().addElement("move", NAMESPACE);
				botMove.addAttribute("turntype", "standard");
				botMove.addAttribute("card1", Integer.toString(botCard));
				botMove.addAttribute("card2", "");
				botMove.addAttribute("card3", "");
				botMove.addAttribute("sender", role.toString());
				botMoveToSend.add(botMove);
				log.info("PST:\t La mossa broadcastata e': " + "\n\t" +botMove.toString()+"\n");
				room.broadcastTurn(botMoveToSend, player);
				role = Role.valueOf(cardsController.nextFirstPlayer());
				log.info("PST: \tIl giocatore che deve giocare dopo di me e' " + role.getValue());
				log.info("PST: ------------------------ MOSSA:"+ moveID +" ------------------------");
				log.info("PST: ------------------------ TURNO:"+ turnCounter +" ------------------------");
				if (turnCounter == 1) {
					firstPlayerTurn = (role.getValue() / 10) - 1;
					log.info("PST: \t Sono al primo turno della mano, configuro firsPlayerTurn = "+firstPlayerTurn);
				}
				
				if (turnCounter == 5) {
					log.info("PST: \t Controllo chi ha vinto la mano");
					cardsController.handWinner(onTable, firstPlayerTurn);
					role = Role.valueOf(cardsController.nextFirstPlayer());
					log.info("PST: \t Il giocatore che ha preso e deve lanciare e': "+ turn.toString());
					turnCounter = 1;
					resetTable();
				}
			}
		}
		
	}
	
	
	public void processLastTurn() throws InvalidTurnException, ComponentException {
		log.info("Prima di controllare moveID==57, stampo moveID --> " + moveID);
		if (moveID == 57) 
 		{
			log.info("\tMano finita! Calcolo il vincitore, assegno i punteggi e mando il messaggio di fine partita.");
			ArrayList winData = new ArrayList();
			int standings[];
			Role checkWinRole = null;
			Collection<Element> standingsData = new ArrayList<Element>();
			log.info("\tMi calcolo winData tramite cardsController.matchEnding()");
			winData = cardsController.matchEnding();
			if (winData != null)
			{
				checkWinRole = (Role) winData.get(0);
				standings = (int[]) winData.get(1);
				log.info("\tWinner: " + checkWinRole.toString());
				log.info("\tStandings: N " + standings[0] + " - E " + standings[1] + " - S " + standings[2] + " - W " + standings[3]);
				Element stats = DocumentFactory.getInstance().createDocument().addElement("matchend", NAMESPACE);
				stats.addAttribute("scoreN", Integer.toString(standings[0]));
				stats.addAttribute("scoreE", Integer.toString(standings[1]));
				stats.addAttribute("scoreS", Integer.toString(standings[2]));
				stats.addAttribute("scoreW", Integer.toString(standings[3]));
				log.info("\tIl messaggio in xml con i punteggi che mando a tutti e' :"+ stats.asXML().toString());
				standingsData.add(stats);
				room.broadcastTurn(standingsData, this.player);
				status = Status.inactive;
			}
 		}
	}
	
	
	
	
	public void processTurn(MUGOccupant player, Collection<Element> moves) throws InvalidTurnException, ComponentException {
		this.moves = moves;
		this.player = player;
		if (playerN == null || 
				(playerE == null && botUsers < 3) ||
				(playerS == null && botUsers < 2) || 
				(playerW == null && botUsers < 1)) {
			log.info("PT:\tNot enough player in match " + room.getJID());
			throw new RequiredPlayerException();
		}
		
		if (status != Status.active) {
			log.info("PT:\tMatch " + room.getJID() + " not yet started");
			throw new GameConfigurationException();
		}
		
		
		
		if (playerN != null && playerN.equals(player)) {
			this.role = Role.north;
		}
		else if (playerE != null && playerE.equals(player)) {
			this.role = Role.east;
		}
		else if (playerS != null && playerS.equals(player)) {
			this.role = Role.south;
		}		
		else if (playerW != null && playerW.equals(player)) {
			this.role = Role.west;
		}
		
		if (!player.hasRole() || role == Role.none) {
			log.info("A Spectator wants to send a move in match " + room.getJID());
			throw new GameConfigurationException();
		}
		
		if (moves == null || moves.size() != 1) {
			log.info("No valid move in match " + room.getJID());
			throw new InvalidTurnException();
		}
		
		move = moves.iterator().next();
		
		if (!move.getName().equals("move") || !move.getNamespaceURI().equals(NAMESPACE)) {
			log.info("No valid move in match " + room.getJID() + ": " + move.asXML());
			throw new InvalidTurnException();
		}
		
		log.info("PT:\tRuolo chiamante: " + role.toString());
		turnType = String.valueOf(move.attributeValue("turntype"));
		if (moveID <= 4) {
			processFirstTurn();
			log.info("Process Turn: \t primo turno finito");
			log.info("Process Turn: \t role " + role.getValue() + " " + role.name());
			log.info("Process Turn: \t this.role " + this.role.getValue() + " " + this.role.name());
			if ((this.role == Role.east && botUsers > 2) || (this.role == Role.south && botUsers > 1) || (this.role == Role.west && botUsers > 0)) {
				this.turnType = "standardturn";
				processStandardTurn();
			}
		}
		else { 
			if (moveID < 57) {
				processStandardTurn();
			}
			if (moveID == 57) {
				processLastTurn();
				log.info("Process Turn: \t partita finita!");
				leave(playerN);
				leave(playerE);
				leave(playerS);
				leave(playerW);
				this.destroy();
				room.destroy();
			}
		}
		return;
	}
	

	

	public void releaseRole(MUGOccupant player) {
		log.info("releaseRole: init");
		if (playerN != null && playerN.equals(player)) {
			playerN = null;
			if (status == Status.active)
				status = Status.paused;
			calculateStateElement();
		}
		else if (playerE != null && playerE.equals(player)) {
			playerE = null;
			if (status == Status.active)
				status = Status.paused;
			calculateStateElement();
		}
		else if (playerS != null && playerS.equals(player)) {
			playerS = null;
			if (status == Status.active)
				status = Status.paused;
			calculateStateElement();
		}
		else if (playerW != null && playerW.equals(player)) {
			playerW = null;
			if (status == Status.active)
				status = Status.paused;
			calculateStateElement();
		}
		if (!spectators.contains(player))
			spectators.add(player);
	}
	
	/**
	 * Calculate the xml representation of the present match state.
	 */
	private void calculateStateElement() {
		log.info("calculateStatElement: init");
		stateElement = DocumentFactory.getInstance().createDocument().addElement("state", NAMESPACE);
		stateElement.addElement("bots").addText(Integer.toString(botUsers));
		if (turn == Role.north)
			stateElement.addElement("turn").addText(Role.north.name());
		if (turn == Role.east)
			stateElement.addElement("turn").addText(Role.east.name());
		if (turn == Role.south)
			stateElement.addElement("turn").addText(Role.south.name());
		if (turn == Role.west)
			stateElement.addElement("turn").addText(Role.west.name());
		
		Element table = stateElement.addElement("table");
		if (status == Status.active){
			log.info("Room status: ACTIVE");
			
			if (!gameStarted) {
				gameHands = cardsController.cardsDealer();
				this.onTable[0] = 0;
				this.onTable[1] = 0;
				this.onTable[2] = 0;
				this.onTable[3] = 0;
				gameStarted = true;
			}
			Element field = table.addElement("playerN");
			for (int i = 1; i <= gameHands.get(0).size(); i++) {
				int cardToAdd = gameHands.get(0).get(i-1);
				field.addAttribute("Card"+ i, Integer.toString(cardToAdd));
			}
			if (gameHands.get(0).size() < 13) {
				for (int i = gameHands.get(0).size()+1; i <= 13; i++) {
					int cardToAdd = 0;
					log.severe("Card "+ i + " " + Integer.toString(cardToAdd));	
					field.addAttribute("Card"+i, Integer.toString(cardToAdd));
				}
			}
			field.addAttribute("ontable", Integer.toString(onTable[0]));

			
			Element field2 = table.addElement("playerE");
			
			for (int i = 1; i <= gameHands.get(1).size(); i++) {
				int cardToAdd = gameHands.get(1).get(i-1);
		
				field2.addAttribute("Card"+i, Integer.toString(cardToAdd));
			}
			if (gameHands.get(1).size() < 13) {
				for (int i = gameHands.get(1).size()+1; i <= 13; i++) {
					int cardToAdd = 0;
			
					field2.addAttribute("Card"+i, Integer.toString(cardToAdd));
				}
			}
			field2.addAttribute("ontable", Integer.toString(onTable[1]));
			
		
			Element field3 = table.addElement("playerS");
			for (int i = 1; i <= gameHands.get(2).size(); i++) {
				int cardToAdd = gameHands.get(2).get(i-1);
		
				field3.addAttribute("Card"+i, Integer.toString(cardToAdd));
			}
			if (gameHands.get(2).size() < 13) {
				for (int i = gameHands.get(2).size()+1; i <= 13; i++) {
					int cardToAdd = 0;
			
					field3.addAttribute("Card"+i, Integer.toString(cardToAdd));
				}
			}
			field3.addAttribute("ontable", Integer.toString(onTable[2]));
			
			
			Element field4 = table.addElement("playerW");
			for (int i = 1; i <= gameHands.get(3).size(); i++) {
				int cardToAdd = gameHands.get(3).get(i-1);
			//	log.severe("Card "+i+ " " + Integer.toString(cardToAdd));	
				field4.addAttribute("Card"+i, Integer.toString(cardToAdd));
			}
			if (gameHands.get(3).size() < 13) {
				for (int i = gameHands.get(3).size()+1; i <= 13; i++) {
					int cardToAdd = 0;
				//	log.severe("Card "+ i + " " + Integer.toString(cardToAdd));	
					field4.addAttribute("Card"+i, Integer.toString(cardToAdd));
				}
			}
			field4.addAttribute("ontable", Integer.toString(onTable[3]));
		}
		log.info(stateElement.asXML());
	}
	
	private void initConfig() {
		log.info("initConfig: init");
		configForm = new DataForm(DataForm.Type.form);
		configForm.setTitle("Configuration for Hearts");
		configForm.addInstruction("Default Configuration: "
				);

		
		FormField field = configForm.addField();
		field.setVariable("FORM_TYPE");
		field.setType(FormField.Type.hidden);
		field.addValue(NAMESPACE + "#heartsconfig");
	
		field = configForm.addField();
		field.setVariable("mug/hearts#config_bot");
		field.setType(FormField.Type.list_single);
		field.setLabel("Bots:");
		field.addValue(Integer.toString(botUsers));
	
		configElement = configForm.getElement();
		log.info(configElement.asXML());
	}
	

	private void reset() {
		log.info("reset: init");
		gameHands = new ArrayList<ArrayList<Integer>>();
		moveID = 1;
		onTable = new int [4];
		turn = Role.north;
		calculateStateElement();
	}
	

	public Collection<Element> getConfigurationForm() {
		log.info("getConfigurationForm: init");
		Collection<Element> result = new ArrayList<Element>();
		
		FormField field = configForm.getField("mug/hearts#config_bot");
		field.clearValues();
		field.addValue(Integer.toString(botUsers));
		
		configElement = configForm.getElement();
		result.add(configElement);
		return result;
	}
	
	public void setConfiguration(Collection<Element> config) {
		log.info("setConfiguration: init");
		Element formElement = null;
		
		if (config != null && !config.isEmpty())
			formElement = config.iterator().next();
		
		if (config != null && !config.isEmpty() && config.size() != 1)
			throw new GameConfigurationException();
		
		if (formElement != null) {
			if (!formElement.getName().equals("x") ||
					!formElement.getNamespaceURI().equals("jabber:x:data"))
				throw new GameConfigurationException();
		}
		
		status = Status.inactive;
		if (formElement != null) {
			int newBots = this.botUsers;
			try {
				DataForm completedForm = new DataForm(formElement);
				
				if (!DataForm.Type.submit.equals(completedForm.getType()))
					throw new GameConfigurationException();
				
				List<String> values;
				FormField field;
				field = completedForm.getField("mug/hearts#config_bot");
				if (field != null) {
					values = field.getValues();
					if (!values.isEmpty())
						newBots = Integer.parseInt(values.get(0));
				}
				log.info("setConfiguration" + config.toString());		
			}
			catch (Exception e) {
				reset();
				calculateStateElement();
				throw new GameConfigurationException();
			}
			botUsers = newBots;
		}
		reset();
		calculateStateElement();
	}
	

	public Status getStatus() {
		log.info("getStatus: init " + status.toString());
		return status;
	}
	

	public void setConstructedState(Element state) {
		log.info("setConstructedState: init");
		throw new UnsupportedGameException();
	}
	

	public void leave(MUGOccupant occupant) {
		log.info("leave: init");
		log.info("leave: di " + occupant.getNickname());
		if (spectators.contains(occupant))
			spectators.remove(occupant);
		if (playerN != null && playerN.equals(occupant)) {
			playerN = null;
			if (status == Status.active)
				status = Status.paused;
			calculateStateElement();
		}
		else if (playerE != null && playerE.equals(occupant)) {
			playerE = null;
			if (status == Status.active)
				status = Status.paused;
			calculateStateElement();
		}
		else if (playerS != null && playerS.equals(occupant)) {
			playerS = null;
			if (status == Status.active)
				status = Status.paused;
			calculateStateElement();
		}
		else if (playerW != null && playerW.equals(occupant)) {
			playerW = null;
			if (status == Status.active)
				status = Status.paused;
			calculateStateElement();
		}
	}
	
	private void resetTable() {
		this.onTable[0] = 0;
		this.onTable[1] = 0;
		this.onTable[2] = 0;
		this.onTable[3] = 0;
	}
	
	private void swapCards(Bot aBot, int anArray[]) {
	 ArrayList<Integer> cards = aBot.passCards();
	 anArray[0] = cards.get(0);
	 anArray[1] = cards.get(1);
	 anArray[2] = cards.get(2);
	 return;
	}
}