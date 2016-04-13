package HBF;

public class Match {
	private int id;
	
	private MatchType type;
	
	private int tournamentId;
	
	private Team team1;
	private Team team2;
	
	private int team1Score = 0;
	private int team2Score = 0;
	
	private boolean played = false;
	
	public static MatchType toMatchType(String type) {
		if(type.equals("p")) return MatchType.SEEDINGMATCH;
		if(type.equals("k")) return MatchType.QUARTERFINALE;
		if(type.equals("s")) return MatchType.SEMIFINAL;
		if(type.equals("f")) return MatchType.FINAL;
		if(type.equals("jk")) return MatchType.JAYS_QUARTERFINAL;
		if(type.equals("js")) return MatchType.JAYS_SEMIFINAL;
		if(type.equals("jf")) return MatchType.JAYS_FINAL;
		return null;
	}
	
	public Match(int id, int tournamentId, MatchType type, Team t1, Team t2) {
		this.id = id;
		this.tournamentId = tournamentId;
		this.type = type;
		team1 = t1;
		team2 = t2;
	}
	
	public MatchType getType() {
		return type;
	}
	
	public int id() {
		return id;
	}
	
	public int getTournamentId() {
		return tournamentId;
	}
	
	public void setResult(int t1Score, int t2Score) {
		team1Score = t1Score;
		team2Score = t2Score;
		played = true;
	}
	
	public boolean hasWinner() {
		if(played)
			return team1Score != team2Score;
		else return played;
	}
	
	public Team getWinner() {
		if(played) {
			if(team1Score > team2Score) return team1;
			if(team2Score > team1Score) return team2;
		}
		return null;
	}
	
	public Team getLoser() {
		if(played) {
			if(team1Score < team2Score) return team1;
			if(team2Score < team1Score) return team2;
		}
		return null;
	}
	
	public boolean hasUser(int userId) {
		if(
				team1.hasUser(userId) ||
				team2.hasUser(userId)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean didUserWin(int userId) throws Exception {
		if(hasUser(userId)) {
			return getWinner().hasUser(userId);
		} else {
			throw new Exception("User not in match");
		}
	}
}
