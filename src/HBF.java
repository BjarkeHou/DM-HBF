import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import HBF.Match;
import HBF.Tournament;
import HBF.User;

public class HBF {
	public static HashMap<Integer, User> users = new HashMap<Integer, User>();
	public static HashMap<Integer, Tournament> tournaments = new HashMap<Integer, Tournament>();
	public static HashMap<Integer, Match> matches = new HashMap<Integer, Match>();
	private static DBConn db;
	
	public static int calculateMMR(int winnerMMR, int loserMMR) {
		float result = 1;
		float diff = winnerMMR-loserMMR;
		float expectedResult = 0.5f;
		if(diff > 285) expectedResult = 1f;
		else if(diff < -285) expectedResult = 0f;
		else expectedResult = (0.00175f * diff) + 0.5f;
		
		return (int)(30*(result-expectedResult));
	}
	
	public static void updateMMRInMatches(ArrayList<Match> matches) {
		for(Match m : matches) {
			if(m.hasWinner()) {
				int winnerTeamMMR = (m.getWinner().getPlayer1().getMMR() + m.getWinner().getPlayer2().getMMR()) / 2;
				int loserTeamMMR = (m.getLoser().getPlayer1().getMMR() + m.getLoser().getPlayer2().getMMR()) / 2;
				int mmrChange = calculateMMR(winnerTeamMMR, loserTeamMMR);
				
				m.getWinner().getPlayer1().adjustMMR(mmrChange);
				m.getWinner().getPlayer2().adjustMMR(mmrChange);
				m.getLoser().getPlayer1().adjustMMR(-mmrChange);
				m.getLoser().getPlayer2().adjustMMR(-mmrChange);
			}
		}
	}
	
	public static void main(String args[]) {
		Date startTime = new Date();
		db = new DBConn();
		if(!db.connect()) return;
		
		// Important order of calls:
		// 1. Users
		// 2. Matches
		// 3. Tournaments
		
		for(User u : db.getUsers()) {
			users.put(u.id(), u);
		}
		
		for(Match m : db.getMatches()) {
			matches.put(m.id(), m);
		}
		
		for(Tournament t : db.getTournaments()) {
			tournaments.put(t.id(), t);
		}
		
		System.out.println("Number of users: " + users.size());
		System.out.println("Number of matches: " + matches.size());
		System.out.println("Number of tournaments: " + tournaments.size());
		
		for(Map.Entry<Integer, Tournament> entry : tournaments.entrySet()) {
			Tournament t = entry.getValue();
			updateMMRInMatches(t.getMatches());
		}
				
		ArrayList<User> sortedRatingList = new ArrayList<User>(users.values());
		Collections.sort(sortedRatingList);
		for(User u : sortedRatingList) {
			System.out.println(u.getName() + "\t"+u.getMMR());
		}
		
		
		long timeSpent = new Date().getTime() - startTime.getTime();
		System.out.println("\nTime: " + String.format("%02d min, %02d sec", 
			    TimeUnit.MILLISECONDS.toMinutes(timeSpent),
			    TimeUnit.MILLISECONDS.toSeconds(timeSpent) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeSpent))
			));
	}
}
