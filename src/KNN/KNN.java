package KNN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KNN {
	ArrayList<Player> data;
	int k;
	
	public void init(ArrayList<Player> data, int k) {
		this.data = data;
		this.k = k;
	}
	
	public int[] calc(Player player) {
		ArrayList<Result> results = new ArrayList<Result>();
		for(Player point : data) {
			results.add(distanceBetween(point, player));
		}
		results.sort(new DistanceComparator());
		
		int[] ids = new int[k];
		for(int i = 0; i < k; i++) {
			ids[i] = results.get(i).id;
		}
		
		return ids;
	}
	
	private Result distanceBetween(Player to, Player from) {
		if(to.tournaments.length != from.tournaments.length) return null;
		
		double temp = 0;
		for(int i = 0; i < to.tournaments.length; i++) {
			temp += Math.pow(to.tournaments[i].mmrChange-from.tournaments[i].mmrChange, 2);
			temp += Math.pow(to.tournaments[i].day-from.tournaments[i].day, 2);
		}
		
		double dist = Math.sqrt(temp);
				
		return new Result(to.id, to.presentMMR, dist);
	}
	
//	private Course findMajority(Course[] c) {
//		HashMap<Course, Integer> count = new HashMap<Course, Integer>();
//		for(int i = 0; i<c.length; i++) {
//			if(count.containsKey(c[i])) {
//				count.replace(c[i],	count.get(c[i])+1);
//			} else {
//				count.put(c[i], 1);
//			}
//		}
//	
//		Map.Entry<Course, Integer> maxEntry = null;
//		for(Map.Entry<Course, Integer> entry : count.entrySet()) {
//			if(maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
//				maxEntry = entry;
//			}
//		}
//		
//		return maxEntry.getKey();
//	}
	
	private int findAverageMMR(Player[] players) {
		int mmrAverage = 0;
		for(Player p : players) {
			mmrAverage += p.presentMMR;
		}
		return mmrAverage/players.length;
	}
}
