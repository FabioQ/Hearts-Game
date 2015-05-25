package com.cyberdyne.heartsclient.utils;

import java.util.Hashtable;

public class ConvertCard {

	private static Hashtable<Integer, Integer> dataToGui = new Hashtable<Integer, Integer>();
	private static Hashtable<Integer, Integer> guiToData = new Hashtable<Integer, Integer>();
	/**
	 * @uml.property  name="newCard"
	 */
	private int newCard;

	public ConvertCard() {
		createTableGui(dataToGui);
		createTableData(guiToData);
	}
	private Hashtable<Integer, Integer> createTableGui (Hashtable<Integer, Integer> dataToGui) {

		//Tabelle di conversione: a sinistra carte nostre, a destra carte di gigi

		//CLUBS
		//CLUBS
	       dataToGui.put(13, 0); //Ace
	       dataToGui.put(12, 1); //King
	       dataToGui.put(11, 2); //Queen
	       dataToGui.put(10, 3); //Jack
	       dataToGui.put(9, 4); //10
	       dataToGui.put(8, 5); //9
	       dataToGui.put(7, 6); //8
	       dataToGui.put(6, 7); //7
	       dataToGui.put(5, 8); //6
	       dataToGui.put(4, 9); //5
	       dataToGui.put(3, 10); //4
	       dataToGui.put(2, 11); //3
	       dataToGui.put(1, 12); //2

	       //DIAMONDS
	       dataToGui.put(26, 13); //Ace
	       dataToGui.put(25, 14); //King
	       dataToGui.put(24, 15); //Queen
	       dataToGui.put(23, 16); //Jack
	       dataToGui.put(22, 17); //10
	       dataToGui.put(21, 18); //9
	       dataToGui.put(20, 19); //8
	       dataToGui.put(19, 20); //7
	       dataToGui.put(18, 21); //6
	       dataToGui.put(17, 22); //5
	       dataToGui.put(16, 23); //4
	       dataToGui.put(15, 24); //3
	       dataToGui.put(14, 25); //2

	       //SPADES
	       dataToGui.put(39, 26); //Ace
	       dataToGui.put(38, 27); //King
	       dataToGui.put(37, 28); //Queen
	       dataToGui.put(36, 29); //Jack
	       dataToGui.put(35, 30); //10
	       dataToGui.put(34, 31); //9
	       dataToGui.put(33, 32); //8
	       dataToGui.put(32, 33); //7
	       dataToGui.put(31, 34); //6
	       dataToGui.put(30, 35); //5
	       dataToGui.put(29, 36); //4
	       dataToGui.put(28, 37); //3
	       dataToGui.put(27, 38); //2

	       //HEARTS
	       dataToGui.put(52, 39); //Ace
	       dataToGui.put(51, 40); //King
	       dataToGui.put(50, 41); //Queen
	       dataToGui.put(49, 42); //Jack
	       dataToGui.put(48, 43); //10
	       dataToGui.put(47, 44); //9
	       dataToGui.put(46, 45); //8
	       dataToGui.put(45, 46); //7
	       dataToGui.put(44, 47); //6
	       dataToGui.put(43, 48); //5
	       dataToGui.put(42, 49); //4
	       dataToGui.put(41, 50); //3
	       dataToGui.put(40, 51); //2

		return dataToGui;
	}

	private Hashtable<Integer, Integer> createTableData (Hashtable<Integer, Integer> guiToData) {

		//Tabelle di conversione: a sinistra carte nostre, a destra carte di gigi

		//CLUBS
	       guiToData.put(0, 13); //Ace
	       guiToData.put(1, 12); //King
	       guiToData.put(2, 11); //Queen
	       guiToData.put(3, 10); //Jack
	       guiToData.put(4, 9); //10
	       guiToData.put(5, 8); //9
	       guiToData.put(6, 7); //8
	       guiToData.put(7, 6); //7
	       guiToData.put(8, 5); //6
	       guiToData.put(9, 4); //5
	       guiToData.put(10, 3); //4
	       guiToData.put(11, 2); //3
	       guiToData.put(12, 1); //2

	       //DIAMONDS
	       guiToData.put(13, 26); //Ace
	       guiToData.put(14, 25); //King
	       guiToData.put(15, 24); //Queen
	       guiToData.put(16, 23); //Jack
	       guiToData.put(17, 22); //10
	       guiToData.put(18, 21); //9
	       guiToData.put(19, 20); //8
	       guiToData.put(20, 19); //7
	       guiToData.put(21, 18); //6
	       guiToData.put(22, 17); //5
	       guiToData.put(23, 16); //4
	       guiToData.put(24, 15); //3
	       guiToData.put(25, 14); //2

	       //SPADES
	       guiToData.put(26, 39); //Ace
	       guiToData.put(27, 38); //King
	       guiToData.put(28, 37); //Queen
	       guiToData.put(29, 36); //Jack
	       guiToData.put(30, 35); //10
	       guiToData.put(31, 34); //9
	       guiToData.put(32, 33); //8
	       guiToData.put(33, 32); //7
	       guiToData.put(34, 31); //6
	       guiToData.put(35, 30); //5
	       guiToData.put(36, 29); //4
	       guiToData.put(37, 28); //3
	       guiToData.put(38, 27); //2

	       //HEARTS
	       guiToData.put(39, 52); //Ace
	       guiToData.put(40, 51); //King
	       guiToData.put(41, 50); //Queen
	       guiToData.put(42, 49); //Jack
	       guiToData.put(43, 48); //10
	       guiToData.put(44, 47); //9
	       guiToData.put(45, 46); //8
	       guiToData.put(46, 45); //7
	       guiToData.put(47, 44); //6
	       guiToData.put(48, 43); //5
	       guiToData.put(49, 42); //4
	       guiToData.put(50, 41); //3
	       guiToData.put(51, 40); //2
	       
	       return guiToData;
	}

	public int formDataToGui(int card) {
		newCard = (Integer) dataToGui.get(card);
		return newCard;
	}
	
	public int formGuiToData(int card) {
		newCard = (Integer) guiToData.get(card);
		return newCard;
	}
}
