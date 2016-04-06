package HBF;

public class Match {
	private Team team1;
	private Team team2;
	
	private int team1Score = 0;
	private int team2Score = 0;
	
	private boolean played = false;
	
	public Match(Team t1, Team t2) {
		team1 = t1;
		team2 = t2;
	}
	
	public void setResult(int t1Score, int t2Score) {
		team1Score = t1Score;
		team2Score = t2Score;
		played = true;
	}
	
	public boolean hasWinner() {
		if(played)
			return team1Score == team2Score;
		else return played;
	}
	
	public Team getWinner() {
		if(played) {
			if(team1Score > team2Score) return team1;
			if(team2Score > team1Score) return team2;
		}
		return null;
	}
}
