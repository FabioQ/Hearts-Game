package org.frogx.service.games.hearts;

import java.util.logging.Logger;

public class MatchEndingController {
	private int[] standings = new int[4];
//	private static MatchEndingController matchEndingController;
	
	public static Logger log;
	static {
	      log = Logger.getLogger("MyLogger");
	}
	public int[] getStandings() {
		log.info("\t\t\tgetStandings(): ");
		return this.standings;
	}

	public MatchEndingController () {
		this.standings[0] = 0;
		this.standings[1] = 0;
		this.standings[2] = 0;
		this.standings[3] = 0;
	} 
	
	public int getWinner() {
		int winner = 0;
		for (int i = 0; i < 3; i++) {
			if (this.standings[i] < this.standings[winner]) {
				winner = i;
			}
		}
		return winner;
	}
	
	public int setStandings(int player, int points) {
		this.standings[player] += points;
		return this.standings[player];
	}
}