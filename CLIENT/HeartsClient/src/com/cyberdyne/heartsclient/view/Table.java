package com.cyberdyne.heartsclient.view;


import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.cyberdyne.heartsclient.model.Room;
import com.cyberdyne.heartsclient.utils.ConvertCard;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.CardAdapter;
import ch.aplu.jcardgame.CardGame;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;
import ch.aplu.jcardgame.RowLayout;
import ch.aplu.jcardgame.StackLayout;
import ch.aplu.jcardgame.TargetArea;
import ch.aplu.jgamegrid.GGExitListener;
import ch.aplu.jgamegrid.Location;
import ch.aplu.jgamegrid.ToolBar;
import ch.aplu.jgamegrid.ToolBarAdapter;
import ch.aplu.jgamegrid.ToolBarItem;
import ch.aplu.jgamegrid.ToolBarText;
import ch.aplu.util.Monitor;

public class Table extends CardGame{

	/**
	 * @author   vincenzoalaia
	 */
	public enum Suit {/**
	 * @uml.property  name="cLUBS"
	 * @uml.associationEnd  
	 */
	CLUBS, /**
	 * @uml.property  name="dIAMONDS"
	 * @uml.associationEnd  
	 */
	DIAMONDS, /**
	 * @uml.property  name="sPADES"
	 * @uml.associationEnd  
	 */
	SPADES, /**
	 * @uml.property  name="hEARTS"
	 * @uml.associationEnd  
	 */
	HEARTS}
	/**
	 * @author   vincenzoalaia
	 */
	public enum Rank {/**
	 * @uml.property  name="aCE"
	 * @uml.associationEnd  
	 */
	ACE, /**
	 * @uml.property  name="kING"
	 * @uml.associationEnd  
	 */
	KING, /**
	 * @uml.property  name="qUEEN"
	 * @uml.associationEnd  
	 */
	QUEEN, /**
	 * @uml.property  name="jACK"
	 * @uml.associationEnd  
	 */
	JACK, /**
	 * @uml.property  name="tEN"
	 * @uml.associationEnd  
	 */
	TEN, /**
	 * @uml.property  name="nINE"
	 * @uml.associationEnd  
	 */
	NINE, /**
	 * @uml.property  name="eIGHT"
	 * @uml.associationEnd  
	 */
	EIGHT, /**
	 * @uml.property  name="sEVEN"
	 * @uml.associationEnd  
	 */
	SEVEN, /**
	 * @uml.property  name="sIX"
	 * @uml.associationEnd  
	 */
	SIX, /**
	 * @uml.property  name="fIVE"
	 * @uml.associationEnd  
	 */
	FIVE, /**
	 * @uml.property  name="fOUR"
	 * @uml.associationEnd  
	 */
	FOUR, /**
	 * @uml.property  name="tHREE"
	 * @uml.associationEnd  
	 */
	THREE, /**
	 * @uml.property  name="tWO"
	 * @uml.associationEnd  
	 */
	TWO}

	/**
	 * @uml.property  name="handLocations"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	private final Location[] handLocations =
	{
			new Location(400, 80),
			new Location(720, 300),
			new Location(400, 520),
			new Location(80, 300),

			new Location(400, 300)
	};

	/**
	 * @uml.property  name="bidLocations"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	private final Location[] bidLocations =
	{
			new Location(420, 260),
			new Location(460, 300),
			new Location(380, 340),
			new Location(340, 300),
	};

	/**
	 * @uml.property  name="stockLocations"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	private final Location[] stockLocations =
	{
			new Location(300, 100),
			new Location(700, 200),
			new Location(500, 500),
			new Location(100, 400),
	};

	/**
	 * @uml.property  name="north"
	 * @uml.associationEnd  
	 */
	private ToolBarText north;
	/**
	 * @uml.property  name="south"
	 * @uml.associationEnd  
	 */
	private ToolBarText south;
	/**
	 * @uml.property  name="west"
	 * @uml.associationEnd  
	 */
	private ToolBarText west;
	/**
	 * @uml.property  name="east"
	 * @uml.associationEnd  
	 */
	private ToolBarText east;
	/**
	 * @uml.property  name="northTB"
	 * @uml.associationEnd  
	 */
	private ToolBar northTB;
	/**
	 * @uml.property  name="southTB"
	 * @uml.associationEnd  
	 */
	private ToolBar southTB;
	/**
	 * @uml.property  name="westTB"
	 * @uml.associationEnd  
	 */
	private ToolBar westTB;
	/**
	 * @uml.property  name="eastTB"
	 * @uml.associationEnd  
	 */
	private ToolBar eastTB;

	/**
	 * @uml.property  name="myRoleInt"
	 */
	private int myRoleInt; 
	/**
	 * @uml.property  name="myPrevRoleInt"
	 */
	private int myPrevRoleInt;
	/**
	 * @uml.property  name="myNextRoleInt"
	 */
	private int myNextRoleInt;

	/**
	 * @uml.property  name="numCarte"
	 */
	private int numCarte = 0;

	/**
	 * @uml.property  name="cardsToPass" multiplicity="(0 -1)" dimension="1"
	 */
	private int[] cardsToPass = new int[3];
	/**
	 * @uml.property  name="cardToPlay"
	 */
	private int cardToPlay = 1;
	/**
	 * @uml.property  name="isStandardTurn"
	 */
	private boolean isStandardTurn = false;

	/**
	 * @uml.property  name="cardList"
	 */
	private ArrayList<Card> cardList = new ArrayList<Card>();
	/**
	 * @uml.property  name="incamingCards"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="ch.aplu.jcardgame.Card"
	 */
	private ArrayList<Card> incamingCards = new ArrayList<Card>();


	/**
	 * @uml.property  name="converter"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private ConvertCard converter;

	/**
	 * @uml.property  name="hand"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	private Hand[] hand;
	/**
	 * @uml.property  name="bids"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	private Hand[] bids = new Hand[4];
	/**
	 * @uml.property  name="stocks"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	private Hand[] stocks = new Hand[4];

	/**
	 * @uml.property  name="deck"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Deck deck = new Deck(Suit.values(), Rank.values(), "cover");

	/**
	 * @uml.property  name="gameView"
	 * @uml.associationEnd  multiplicity="(1 1)" inverse="table:com.cyberdyne.heartsclient.view.GameView"
	 */
	private GameView gameView;
	
	/**
	 * @uml.property  name="usernameNorth"
	 */
	private String usernameNorth = "";
	/**
	 * @uml.property  name="usernameEast"
	 */
	private String usernameEast = "";
	/**
	 * @uml.property  name="usernameSouth"
	 */
	private String usernameSouth = "";
	/**
	 * @uml.property  name="usernameWest"
	 */
	private String usernameWest = "";
	
	private boolean spectatorDealing = false;

	public Table(GameView gameView){

		super(800, 600, 30);

		this.addExitListener(new GGExitListener()
		{
			public boolean notifyExit()
			{
				exit();
				return true;
			}
		});
		
		initSortBtn();
		
		setTitle("Stanza: "+gameView.getaRoomName()+" - Giocatore: "+gameView.userName);
		setStatusText("In attesa di altri giocatori...");
		this.converter = new ConvertCard();
		this.gameView = gameView;
		initBids();
		initStocks();
	}

	private void exit()
	{
		
		//int msg = 
			//JOptionPane.showMessageDialog(null, "L'applicazione verra' chiusa");
		JOptionPane.showMessageDialog(this, "L'applicazione verra' chiusa. Grazie per aver giocato!", "Attenzione!", JOptionPane.WARNING_MESSAGE);
		//int msg = JOptionPane.showConfirmDialog(this, "Sei sicuro di voler uscire dall'applicazione?", "Uscita", JOptionPane.OK_OPTION);
		
		//if (msg == JOptionPane.OK_OPTION) 
			System.exit(0);
			
		
	}
	
	public void closeTable(String userType){
		String cause;
		if (userType.equalsIgnoreCase("owner"))
			cause = "il creatore della stanza";
		else
			cause = "un giocatore";
		
		JOptionPane.showMessageDialog
		(this, "Si è appena scollegato "+ cause + ". Il tavolo verrà distrutto", "Attenzione!", JOptionPane.WARNING_MESSAGE);
		getFrame().dispose();
	}

	private void initSortBtn()
	{
		ToolBar toolBar = new ToolBar(this);
		final ToolBarItem sortBtn = new ToolBarItem("sprites/abbandona.gif", 2);
		toolBar.addItem(sortBtn);
		toolBar.show(new Location(730, 550));
		toolBar.addToolBarListener(new ToolBarAdapter()
		{
			public void leftPressed(ToolBarItem item)
			{
				sortBtn.show(1);
			}

			public void leftReleased(ToolBarItem item)
			{
				sortBtn.show(0);
				int msg = JOptionPane.showConfirmDialog(null, "Sei sicuro di voler uscire dalla stanza?", "Esci", JOptionPane.OK_CANCEL_OPTION);
				if (msg == JOptionPane.OK_OPTION){
					getFrame().dispose();
					gameView.disconnectPlayer();
				}
			}
		});
	}

	public void addPlaceHolder(String userName, String role) {

		if(role.equalsIgnoreCase("north")){
			this.north = new ToolBarText(userName, 20);
			northTB = new ToolBar(this);
			northTB.addItem(this.north);
			northTB.show(new Location(610, 10));
			this.usernameNorth=userName;
		}


		else if(role.equalsIgnoreCase("east")){
			this.east = new ToolBarText(userName, 20);
			eastTB = new ToolBar(this);
			eastTB.addItem(this.east);
			eastTB.show(new Location(650, 510));
			this.usernameEast=userName;

		}


		else if(role.equalsIgnoreCase("south")){
			this.south = new ToolBarText(userName, 20);
			southTB = new ToolBar(this);
			southTB.addItem(this.south);
			southTB.show(new Location(100, 570));
			this.usernameSouth=userName;

		}


		else if(role.equalsIgnoreCase("west")){
			this.west = new ToolBarText(userName, 20);
			westTB = new ToolBar(this);
			westTB.addItem(this.west);
			westTB.show(new Location(10, 70));
			this.usernameWest=userName;
		}
		/*setStatusText("Il giocatore "+ userName +" si e' unito al tavolo!");*/


	}
	
	public void deletePlaceHolder(String rolePlayerString){
		if (rolePlayerString.equalsIgnoreCase("north"))
			northTB.removeAllItems();
		else if (rolePlayerString.equalsIgnoreCase("east"))
			eastTB.removeAllItems();
		else if (rolePlayerString.equalsIgnoreCase("south"))
			southTB.removeAllItems();
		else if (rolePlayerString.equalsIgnoreCase("west"))
			westTB.removeAllItems();
	}
	
	public void dealing(String role, int[] myCards, int[] nCards, int[] eCards, int[] sCards, int[] wCards){
		if (role.equalsIgnoreCase("north")){
			this.myRoleInt = 0;
			this.myPrevRoleInt = 3;
			this.myNextRoleInt = 1;

		}

		else if (role.equalsIgnoreCase("east")){
			this.myRoleInt = 1;
			this.myPrevRoleInt = 0;
			this.myNextRoleInt = 2;

		}

		else if (role.equalsIgnoreCase("south")){
			this.myRoleInt = 2;
			this.myPrevRoleInt = 1;
			this.myNextRoleInt = 3;
		}


		else if (role.equalsIgnoreCase("west")){
			this.myRoleInt = 3;
			this.myPrevRoleInt = 2;
			this.myNextRoleInt = 0;
		}


		this.hand = deck.dealingOut(4, 0);

		if(!role.equalsIgnoreCase("north")){
			for (int j =0; j <13; j++){
				hand[0].insert(converter.formDataToGui(nCards[j]), true);
			}
		}

		if(!role.equalsIgnoreCase("east")){
			for (int j =0; j <13; j++){
				hand[1].insert(converter.formDataToGui(eCards[j]), true);
			}
		}

		if(!role.equalsIgnoreCase("south")){
			for (int j =0; j <13; j++){
				hand[2].insert(converter.formDataToGui(sCards[j]), true);
			}
		}

		if(!role.equalsIgnoreCase("west")){
			for (int j =0; j <13; j++){
				hand[3].insert(converter.formDataToGui(wCards[j]), true);
			}
		}

		for(int j=0; j<13; j++)
			hand[this.myRoleInt].insert(converter.formDataToGui(myCards[j]), true);

		for (int i = 0; i < 4; i++)
		{
			//layout a righe con coordinate x,y e l'ampiezza della mano e angolazione
			hand[i].setView(this, new RowLayout(handLocations[i], 400, 90 * i));

			if(i!=this.myRoleInt)
				hand[i].setVerso(true);
			hand[i].sort(Hand.SortType.SUITPRIORITY, true);
			hand[i].draw();

		}
		
		setStatusText("Distribuzione carte...");

		initBids();
		initStocks();


	}
	
	public void dealingSpectator(String role, int[] nCards, int[] eCards, int[] sCards, int[] wCards) {
		
		if (!this.spectatorDealing) {
			this.hand = deck.dealingOut(4, 0);
			if (!role.equalsIgnoreCase("north")) {
				for (int j = 0; j < (nCards.length); j++) {
					System.out.println("Lunghezza carte nord " + nCards.length);
					if (nCards[j] != 0)
						hand[0].insert(converter.formDataToGui(nCards[j]), true);

				}
			}
			if (!role.equalsIgnoreCase("east")) {
				for (int j = 0; j < (eCards.length); j++) {
					System.out.println("Lunghezza carte east " + eCards.length);
					if (eCards[j] != 0)
						hand[1].insert(converter.formDataToGui(eCards[j]), true);
				}
			}
			if (!role.equalsIgnoreCase("south")) {
				for (int j = 0; j < (sCards.length); j++) {
					System.out
							.println("Lunghezza carte south " + sCards.length);
					if (sCards[j] != 0)
						hand[2].insert(converter.formDataToGui(sCards[j]), true);
				}
			}
			if (!role.equalsIgnoreCase("west")) {
				for (int j = 0; j < (wCards.length); j++) {
					System.out.println("Lunghezza carte west " + wCards.length);
					if (wCards[j] != 0)
						hand[3].insert(converter.formDataToGui(wCards[j]), true);
				}
			}
			for (int i = 0; i < 4; i++) {
				//layout a righe con coordinate x,y e l'ampiezza della mano e angolazione
				hand[i].setView(this, new RowLayout(handLocations[i], 400,
						90 * i));

				//hand[i].sort(Hand.SortType.SUITPRIORITY, true);
				hand[i].setVerso(true);
				hand[i].draw();

			}
			setStatusText("Distribuzione carte...");
			this.spectatorDealing = true;
		}
		
		/*initBids();
		initStocks();*/
		
	}


	private void initBids()
	{
		for (int i = 0; i < 4; i++)
		{

			bids[i] = new Hand(deck);
			//la locazione del target di ogni giocatore e' impostata alla sua rispettiva bid location
			bids[i].setView(this, new StackLayout(bidLocations[i]));
		}
	}

	private void initStocks()
	{
		for (int i = 0; i < 4; i++)
		{
			stocks[i] = new Hand(deck);
			double rotationAngle;
			if (i == 0 || i == 2)
				rotationAngle = 0;
			else
				rotationAngle = 90;
			stocks[i].setView(this, new StackLayout(stockLocations[i], rotationAngle));
		}
	}

	public void transferToStock(String role)
	{
		String usernameWinner = Room.getRoomInstance().getPlayersByRole(role);
		int player = 0;

		if (role.equalsIgnoreCase("north"))
			player = 0;

		else if (role.equalsIgnoreCase("east"))
			player = 1;

		else if (role.equalsIgnoreCase("south"))
			player = 2;

		else if (role.equalsIgnoreCase("west"))
			player = 3;

		for (int i = 0; i < 4; i++)
		{
			bids[i].setTargetArea(new TargetArea(stockLocations[player]));
			Card card = bids[i].getLast();
			if (card == null)
				continue;
			bids[i].setVerso(false);
			//bids[i].transferNonBlocking(card, stocks[player]);
			bids[i].transfer(card, stocks[player], true);
			card.setVerso(true);
		}
		setStatusText("Il giocatore "+ usernameWinner + " ha preso la mano");
		//fermo momentaneamente il thread corrente e aspetto fino a che tutte le carte sono trasferite allo stock del giocatore
		//Monitor.putSleep();
		//stocks[player].draw();
	}

	public void moveMyIncomingCards(int[] cards) {


		for (int i = 0; i<3; i++){
			//	System.out.println("ENTRO NEL FOR 1");

			Card card = hand[this.myPrevRoleInt].getCard(converter.formDataToGui(cards[i]));
			incamingCards.add(card);
		}

		for (int i = 0; i<3; i++) {
			System.out.println("ENTRO NEL FOR 2");
			if(this.myRoleInt==0)
				incamingCards.get(i).setVerso(false);
			else
				incamingCards.get(i).setVerso(true);
			incamingCards.get(i).transfer(hand[this.myRoleInt], true);
			//hand[this.myRoleInt].sort(Hand.SortType.SUITPRIORITY, true);
		}
	}

	public void playFirstTurn() {
		
		setStatusText("Seleziona tre carte da passare al giocatore alla tua sinistra...");
		System.out.println("E' il mio turno, gioco il first turn.");
		System.out.println(this.myRoleInt);

		hand[this.myRoleInt].addCardListener(new CardAdapter()
		{
			public void leftDoubleClicked(Card card)
			{
				if(!isStandardTurn){
					System.out.println("HO CLICCATO");
					cardsToPass[numCarte] = converter.formGuiToData(card.getCardNumber());
					System.out.println("Cliccata la carta: " + converter.formGuiToData(card.getCardNumber()));
					setStatusText("Hai selezionato la carta: " + hand[myRoleInt].getCard(converter.formDataToGui(converter.formGuiToData(card.getCardNumber()))));
					//Controllo doppio click carta!!!!!
					if(!cardList.contains(card)) {
						cardList.add(card);
						card.setVerso(true);
						numCarte++;
					}
					
					if (numCarte == 3){
						for(int i = 0; i < 3; i++) {
							//if(hand[myNextRoleInt].getCard(cardList.get(i).getCardNumber())!=null){
							//								Card cardToRemove = hand[myNextRoleInt].getCard(cardList.get(i).getCardNumber());
							//								hand[myNextRoleInt].remove(cardToRemove, true);
							//	}
							cardList.get(i).setVerso(true);
							cardList.get(i).transfer(hand[myNextRoleInt], true);
						}
						hand[myRoleInt].setTouchEnabled(false);
						gameView.playFirstTurn(cardsToPass);
					}
				}
			}
		});

		hand[this.myRoleInt].setTouchEnabled(true);
	}

	public void moveFirstTurnCards(String role, int[] cards) {
		
		int to = 0;
		int from = 0;

		if (role.equalsIgnoreCase("north")){
			from = 0;
			to = 1;
		}

		else if (role.equalsIgnoreCase("east")){
			from = 1;
			to = 2;
		}
		else if (role.equalsIgnoreCase("south")){
			from = 2;
			to = 3;
		}
		else if (role.equalsIgnoreCase("west")){
			from = 3;
			to = 0;
		}

		for (int i = 0; i<3; i++){

			Card card = hand[from].getCard(converter.formDataToGui(cards[i]));
			//			int intCard = card.getCardNumber();
			//			if(hand[to].getCard(intCard)!=null){
			//				hand[to].remove(card, true);
			//			}
			card.setVerso(true);
			card.transfer(hand[to], true);
		}
	}
	/* METODO CHE MI SCOPRE LE CARTE SCAMBIATE AL PRIMO TURNO! */
	public void endFirstTurn() {
		for(int i = 0; i<4; i++){
			hand[i].setTargetArea(new TargetArea(bidLocations[i]));
		}
		hand[this.myRoleInt].setVerso(false);
		/*modifica*/
		hand[this.myRoleInt].sort(Hand.SortType.SUITPRIORITY, true);
		/*modifica*/
		isStandardTurn = true;
	}
	
	public void endFirstTurnSpectator() {
		for(int i = 0; i<4; i++){
			hand[i].setTargetArea(new TargetArea(bidLocations[i]));
		}
		isStandardTurn = true;
	}

	public void playStandardTurn() {

		/*ystem.out.println("E' il mio turno, gioco.");
		System.out.println(this.myRoleInt);*/
		setStatusText("E' il tuo turno, scegli una carta da giocare!");

		hand[this.myRoleInt].addCardListener(new CardAdapter()
		{
			public void leftDoubleClicked(Card card)
			{
				System.out.println("HO CLICCATO");
				cardToPlay = converter.formGuiToData(card.getCardNumber());
				System.out.println("Cliccata la carta: " + converter.formGuiToData(card.getCardNumber()));
				/*setStatusText("Hai giocato la carta: "+ converter.formDataToGui(converter.formGuiToData(card.getCardNumber())));*/
				setStatusText("Hai giocato la carta: "+ card.getRank() + " " + card.getSuit());
				
				if(gameView.sendStandardTurn(cardToPlay))
					hand[myRoleInt].setTouchEnabled(false);
				else
					setStatusText("Non puoi giocare la carta selezionata!");
					
			}

		});

		hand[this.myRoleInt].setTouchEnabled(true);


	}

	public void moveStandardTurnCard(String card1, String role) {
		
		for(int i = 0; i<4; i++){
			hand[i].setTargetArea(new TargetArea(bidLocations[i]));
		}
		
		// TODO Auto-generated method stub
		int from = 0;
		System.out.println("MOVE 1");
		int cardToDraw = Integer.parseInt(card1);

		if (role.equalsIgnoreCase("north")){
			from = 0;
		}

		else if (role.equalsIgnoreCase("east")){
			from =1;
		}
		else if (role.equalsIgnoreCase("south")){
			from = 2;
		}
		else if (role.equalsIgnoreCase("west")){
			from = 3;
		}

		System.out.println("MOVE 2");

		Card card = hand[from].getCard(converter.formDataToGui(cardToDraw));

		System.out.println("MOVE 3");

		System.out.println("MOVE 7");

		card.transfer(bids[from], true);
		card.setVerso(false);
		System.out.println("MOVE 8");

	}

	public void showScore(String score, String score2, String score3, String score4, String winner) {
		String usernameWinner = "";
		if(winner.equalsIgnoreCase("north"))
			usernameWinner=usernameNorth;
		else if(winner.equalsIgnoreCase("east"))
			usernameWinner=usernameEast;
		else if(winner.equalsIgnoreCase("south"))
			usernameWinner=usernameSouth;
		else if(winner.equalsIgnoreCase("west"))
			usernameWinner=usernameWest;
		
		JOptionPane.showMessageDialog
		(this, "La partita è terminata, il vincitore è: "+usernameWinner+"\n i punteggi sono:\n"+usernameNorth+": "+score+"\n"+
				usernameEast+": "+score2+"\n"+usernameSouth+": "+score3+"\n"+usernameWest+": "+score4+"\n", "Partita Terminata!",
				JOptionPane.INFORMATION_MESSAGE);
		getFrame().dispose();
	}

	public void cardOnTable(String card, String role) {
		System.out.println("TABLE: entro nel cardOnTable, carta " + card + "ruolo " + role);
		int cardNum = Integer.parseInt(card);
		int from = 0;
		cardNum = converter.formDataToGui(cardNum);
		System.out.println("Carta per gui " + cardNum);
		
		if (role.equalsIgnoreCase("north")){
			from = 0;
		}

		else if (role.equalsIgnoreCase("east")){
			from =1;
		}
		else if (role.equalsIgnoreCase("south")){
			from = 2;
		}
		else if (role.equalsIgnoreCase("west")){
			from = 3;
		}
		bids[from].insert(cardNum, false);
		System.out.println("Insert OK");
		bids[from].draw();
		System.out.println("Draw OK");

		
	}
}