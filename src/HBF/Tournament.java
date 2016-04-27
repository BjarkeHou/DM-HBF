package HBF;

import java.util.ArrayList;
import java.util.Date;

public class Tournament {
	private int id;
	private Date date;
	private ArrayList<Team> teams;
	private ArrayList<Match> matches;
	private int[] points;
	
	public Tournament(int id, Date date, int[] points) {
		this.id = id;
		this.date = date;
		this.points = points;
		teams = new ArrayList<Team>();
		matches = new ArrayList<Match>();
	}
	
	public int id() {
		return id;
	}
	
	public void addTeam(Team t) {
		teams.add(t);
	}
	
	public void addTeams(ArrayList<Team> t) {
		teams = t;
	}
	
	public void addMatch(Match m) {
		matches.add(m);
	}
	
	public void addMatches(ArrayList<Match> m) {
		matches = m;
	}
	
	public ArrayList<Match> getMatches() {
		return matches;
	}
	
	public Match getMatch(int id) {
		for(Match m : matches) {
			if(m.id() == id) return m;
		}
		return null;
	}
	
	public ArrayList<Team> getTeams() {
		return teams;
	}
	
	public Team getTeamWithPlayer(int user_id) {
		for(Team t : teams) {
			if(t.getPlayer1().id() == user_id || t.getPlayer2().id() == user_id) return t;
		}
		return null;
	}
	
	public Date getDate() {
		return date;
	}
	
	public boolean hasUser(int userId) {
		for(Team t : teams) {
			if(t.hasUser(userId))
				return true;
		}
		return false;
	}
}
