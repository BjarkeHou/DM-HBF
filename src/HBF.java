import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import HBF.Match;
import HBF.Tournament;
import HBF.User;
import KNN.KNN;
import KNN.Player;
import KNN.TournamentResult;
import KNN.WeekDay;

public class HBF {
	public static HashMap<Integer, User> users = new HashMap<Integer, User>();
	public static HashMap<Integer, Tournament> tournaments = new HashMap<Integer, Tournament>();
	public static HashMap<Integer, Match> matches = new HashMap<Integer, Match>();
	
	public static HashMap<Integer, Integer> lostToArm = new HashMap<Integer, Integer>();
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
	
	public static void main(String args[]) throws Exception {
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
		
		ArrayList<int[]> participants = new ArrayList<int[]>();
		
		ArrayList<Float> bjarkeWinRates = new ArrayList<Float>();
		
		for(Map.Entry<Integer, Tournament> entry : tournaments.entrySet()) {
			Tournament t = entry.getValue();
			updateMMRInMatches(t.getMatches());
			
//			if(t.hasUser(986)) {
//				int numberOfMatches = 0;
//				int numberOfWins = 0;
//				
//				for(Match m : t.getMatches()) {
//					if(m.hasUser(986) && m.hasWinner()) {
//						numberOfMatches++;
//						if(m.didUserWin(986)) {
//							numberOfWins++;
//						}
//					}
//				}
//				
//				bjarkeWinRates.add((float)numberOfWins/numberOfMatches);
//			}
			
//			// APRIORI DATA PREPARATION 
//			if(t.getDate().before(new Date(115, 1, 1)) && t.getDate().getDay() != 2) continue;
//			int[] parts = new int[t.getTeams().size()*2];
//			for(int i = 0; i < parts.length; i+=2) {
//				parts[i] = t.getTeams().get(i/2).getPlayer1().id();
//				parts[i+1] = t.getTeams().get(i/2).getPlayer2().id();
//				
//				
//				
//			}
//			Arrays.sort(parts);
//			//System.out.println(Arrays.toString(parts));
//			participants.add(parts);
		}
		
//		for(Map.Entry<Integer, Integer> entry : lostToArm.entrySet()) {
//			int numberOfLosses = entry.getValue();
//			String name = users.get(entry.getKey()).getName();
//			
//			System.out.println(numberOfLosses + " - " + name);
//		}
		
//		for(Map.Entry<String, Integer> entry : db.getELOChangesForUser(986).entrySet()) {
//			System.out.println(entry.getKey() + " - " + entry.getValue());
//		}
				
//		ArrayList<User> sortedRatingList = new ArrayList<User>(users.values());
//		Collections.sort(sortedRatingList);
//		for(User u : sortedRatingList) {
//			if(u.getMMR() != 1200)
//				System.out.println(u.getName() + "\t"+u.getMMR());
//		}
		
		
		////////////////////////////////////////////////////////
		////////////////         APRIORI         ///////////////
		////////////////////////////////////////////////////////	
//		System.out.println("Participants size = " + participants.size());
//		Apriori apriori = new Apriori();
//		List<ItemSet> result = apriori.apriori(participants.toArray(new int[participants.size()][]), 10.0f);
//		for(ItemSet set : result) {
//			System.out.print("#"+set.getOccurenses());
//			for(int id : set.set) {
//				System.out.print(" - " + users.get(id).getName());
//			}
//			System.out.println();
//		}
		
//		for(float winRate : bjarkeWinRates) {
//			System.out.println(winRate);
//		}
		
		
		////////////////////////////////////////////////////////
		////////////////           KNN           ///////////////
		////////////////////////////////////////////////////////
		String[][] data = CSVFileReader.readDataFile("ratinghistory_turneringer.csv",";", "-",true);
		int k = 15;
		int numberOfTournaments = 15;
		int predictAfter = 20;
		
		int maxEarnedOnTournaments = 0;
		
		KNN kNN = new KNN();
		ArrayList<Player> players = new ArrayList<Player>();

		for(int i = 0; i < data.length; i++) {
			if(i+predictAfter >= data.length) break;
			if(Integer.parseInt(data[i][0]) == 1) continue;
			if(players.size() > 0)
				if(Integer.parseInt(data[i][0]) == players.get(players.size()-1).id) continue;
			
//			if(Integer.parseInt(data[i][0]) == Integer.parseInt(data[i+predictAfter][0])) {
//				int temp = 0;
//				for(int j = 0; j < 5; j++) {
//					temp += Math.abs(Integer.parseInt(data[i+j][4]));
//				}
//				
//				if(temp > maxEarnedOnTournaments) maxEarnedOnTournaments = temp;
//			}
			
			// New Player
			if(Integer.parseInt(data[i][0]) == Integer.parseInt(data[i+predictAfter][0])) {
				// Player has required number of tournaments
				TournamentResult[] tr = new TournamentResult[numberOfTournaments];
				for(int j = 0; j < numberOfTournaments; j++) {
					tr[j] = new TournamentResult(Integer.parseInt(data[i+j][4]), Integer.parseInt(data[i+j][3]));
				}
				
//				if(Integer.parseInt(data[i][0]) == 427) testPlayer = new Player(Integer.parseInt(data[i][0]), tr, calcMMRForTournaments(data, i, predictAfter));
//				if(Integer.parseInt(data[i][0]) == 986) testPlayer = new Player(Integer.parseInt(data[i][0]), tr, users.get(Integer.parseInt(data[i][0])).getMMR());
//				else 
					players.add(new Player(Integer.parseInt(data[i][0]), tr, calcMMRForTournaments(data, i, predictAfter)));
			}
		}
		
		kNN.init(players, k);
		
		int maxDiff = Integer.MIN_VALUE;
		double avgDiff = 0;
		double variance = 0; 
		
		for(Player p : players) {
			int[][] ids = kNN.calc(p);
			System.out.println("-----------------------------------");
			System.out.println("ID: " + p.id);
			System.out.println("Navn: " + p.name);
			System.out.println("MMR: " + p.presentMMR);
			
			int predictedMMR = 0;
			for(int i = 0; i < k; i++) {
				predictedMMR += ids[i][2];
			}
			predictedMMR /= k;
			System.out.println("Predicted MMR: " + predictedMMR);
			System.out.println("-----------------------------------");
			
			int deviation = Math.abs(predictedMMR-p.presentMMR); 
			if(maxDiff < deviation) maxDiff = deviation;
			avgDiff += deviation;
			variance += Math.pow(predictedMMR-p.presentMMR, 2);
		}
		
		avgDiff /= players.size();
		variance = Math.sqrt(variance/players.size());
		System.out.println("Average deviation = " + avgDiff);
		System.out.println("Max deviation = " + maxDiff);
		System.out.println("Standard Deviation = " + variance);
		System.out.println("Max earned on 5 tournaments: " + maxEarnedOnTournaments);
		
//		for(int[] id : ids) {
//			System.out.println("-----------------------------------");
//			System.out.println("ID: " + id[0]);
//			System.out.println("Distance: " + id[1]);
//			System.out.println("Navn: " + users.get(id[0]).getName());
//			System.out.println("MMR: " + id[2]);
//			
//		}
		
		
		long timeSpent = new Date().getTime() - startTime.getTime();
		System.out.println("\nTime: " + String.format("%02d min, %02d sec", 
			    TimeUnit.MILLISECONDS.toMinutes(timeSpent),
			    TimeUnit.MILLISECONDS.toSeconds(timeSpent) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeSpent))
			));
	}
	
	public static int calcMMRForTournaments(String[][] data, int index, int numberOfTournaments) {
		int returnValue = 1200;
		for(int i = 0; i < numberOfTournaments; i++) {
			returnValue += Integer.parseInt(data[index+i][4]);
		}
		return returnValue;
	}
}
