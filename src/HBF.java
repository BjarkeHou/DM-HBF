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
	private static HashMap<Integer, User> users = new HashMap<Integer, User>();
	private static HashMap<Integer, Tournament> tournaments = new HashMap<Integer, Tournament>();
	private static HashMap<Integer, Match> matches = new HashMap<Integer, Match>();
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
				User winner1 = users.get(m.getWinner().getPlayer1().id());
				User winner2 = users.get(m.getWinner().getPlayer2().id());
				User loser1 = users.get(m.getLoser().getPlayer1().id());
				User loser2 = users.get(m.getLoser().getPlayer2().id());
				
				int winnerTeamMMR = (winner1.getMMR() + winner2.getMMR()) / 2;
				int loserTeamMMR = (loser1.getMMR() + loser2.getMMR()) / 2;
				int mmrChange = calculateMMR(winnerTeamMMR, loserTeamMMR);
				
				winner1.adjustMMR(mmrChange);
				winner2.adjustMMR(mmrChange);
				loser1.adjustMMR(-mmrChange);
				loser2.adjustMMR(-mmrChange);
				
				users.put(winner1.id(), winner1);
				users.put(winner2.id(), winner2);
				users.put(loser1.id(), loser1);
				users.put(loser2.id(), loser2);
			}
		}
	}
	
	public static void main(String args[]) {
		Date startTime = new Date();
		db = new DBConn();
		if(!db.connect()) return;
		
		for(User u : db.getUsers()) {
			users.put(u.id(), u);
		}
		
		for(Tournament t : db.getTournaments()) {
			tournaments.put(t.id(), t);
		}
		
		for(Match m : db.getMatches()) {
			matches.put(m.id(), m);
		}
		
		System.out.println("Number of users: " + users.size());
		System.out.println("Number of tournaments: " + tournaments.size());
		System.out.println("Number of matches: " + matches.size());
		
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
