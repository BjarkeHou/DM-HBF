import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import HBF.Match;
import HBF.Team;
import HBF.Tournament;
import HBF.User;

public class DBConn {
	private String dbName = "hobofo_dk";
	private String userName = "hbf";
	private String password = "hbf";
	private java.sql.Connection conn;
	private int numberOfInvalidtMatches = 0;
	public boolean connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/"+dbName, userName, password);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public ArrayList<User> getUsers() {
		ArrayList<User> returnValue = new ArrayList<User>();
		String query = "SELECT bruger_id, navn FROM hbf_brugere";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				User user = new User(rs.getInt("bruger_id"), rs.getString("navn"));
				returnValue.add(user);
			}
		} catch (SQLException e) {
			// TODO: handle exception
		}
		return returnValue;
	}
	
	public User getUser(int id) {
		String query = "SELECT bruger_id, navn FROM hbf_brugere WHERE bruger_id = " + id;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				if(HBF.users.containsKey(rs.getInt("bruger_id")))
					return HBF.users.get(rs.getInt("bruger_id"));
				//return new User(rs.getInt("bruger_id"), rs.getString("navn"));
			}
		} catch (SQLException e) {
			// TODO: handle exception
		}
		return null;
	}
	
	public ArrayList<Tournament> getTournaments() {
		ArrayList<Tournament> returnValue = new ArrayList<Tournament>();
		String query = "SELECT turnering_id, date, point FROM valid_tournaments ORDER BY date ASC";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				
				int id = rs.getInt("turnering_id");
				Date date = rs.getDate("date");
				String pointString = rs.getString("point");
				pointString = pointString.replace("{", "");
				pointString = pointString.replace("}", "");
				String[] ps = pointString.split(",");
				int[] points = new int[ps.length];
//				for(int i = 0; i < ps.length; i++) {
//					points[i] = Integer.parseInt(ps[i]);
//				}
				returnValue.add(new Tournament(id, date, points));			
			}
			
			for(Tournament t : returnValue) {
				t.addMatches(getMatchesInTorunament(t.id()));
				t.addTeams(getTeamsInTournament(t.id()));
			}
			
			
		} catch (SQLException e) {
			// TODO: handle exception
		}
		return returnValue;
	}
	
	public ArrayList<Match> getMatchesInTorunament(int tournament_id) {
		
		ArrayList<Match> returnValue = new ArrayList<Match>();
		
		for(Map.Entry<Integer, Match> entry : HBF.matches.entrySet()) {
			Match m = entry.getValue();
			if(m.getTournamentId() == tournament_id)
				returnValue.add(m);
		}

		return returnValue;
	}
	
	public ArrayList<Match> getMatches() {
		
		ArrayList<Match> returnValue = new ArrayList<Match>();
		String query = "SELECT kamp_id, turnerings_id, hold1, hold2, resultat1, resultat2, type FROM all_valid_matches";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				int match_id = rs.getInt("kamp_id");
				Team team1 = getTeam(rs.getInt("turnerings_id"), rs.getInt("hold1"));
//				if(team1 == null) System.out.println("Team1 null");
				Team team2 = getTeam(rs.getInt("turnerings_id"), rs.getInt("hold2"));
//				if(team2 == null) System.out.println("Team2 null");
				String type = rs.getString("type");
				
				if(team1 == null || team2 == null) {
					numberOfInvalidtMatches++;
					continue;
				}
				Match m = new Match(match_id, rs.getInt("turnerings_id"), Match.toMatchType(type), team1, team2);
				m.setResult(rs.getInt("resultat1"), rs.getInt("resultat2"));
				returnValue.add(m);
			}
		} catch (SQLException e) {
			// TODO: handle exception
		}
		
		System.out.println("Number of invalid matches from getMatches: " + numberOfInvalidtMatches);
		numberOfInvalidtMatches = 0;
		return returnValue;
	}
	
public HashMap<String, Integer> getELOChangesForUser(int userId) {
		
		HashMap<String, Integer> returnValue = new HashMap<String, Integer>();
		String query = "SELECT maaned, points FROM rating_history_monthly WHERE bruger_id = " + userId + " ORDER BY maaned ASC";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				int eloChange = rs.getInt("points");
				String yearMonth = rs.getString("maaned");
				String year = yearMonth.substring(0, 4);
				String month = yearMonth.substring(5, 7);
				
				//Date date = new Date(Integer.parseInt(year)-1900, Integer.parseInt(month), 1);
				
				returnValue.put(yearMonth, eloChange);
			}
		} catch (SQLException e) {
			// TODO: handle exception
		}
		
		return returnValue;
	}
	
	public Team getTeam(int tournament_id, int user_id) {
		String query = "SELECT spiller, medspiller FROM hbf_spillere WHERE turnering_id = " + tournament_id + " AND spiller_id = " + user_id + " AND primaer = 1";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				User p1 = getUser(rs.getInt("spiller"));
				User p2 = getUser(rs.getInt("medspiller"));
				if(p1 == null || p2 == null) return null;
				return new Team(p1, p2);
			}
			
		} catch (SQLException e) {
			// TODO: handle exception
		}
		return null;
	}
	
	public ArrayList<Team> getTeamsInTournament(int tournament_id) {
		ArrayList<Team> returnValue = new ArrayList<Team>();
		String query = "SELECT spiller, medspiller FROM hbf_spillere WHERE turnering_id = " + tournament_id + " AND primaer = 1";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				User p1 = getUser(rs.getInt("spiller"));
				User p2 = getUser(rs.getInt("medspiller"));
				if(p1 == null || p2 == null) continue;
				returnValue.add(new Team(p1, p2));
			}
			
		} catch (SQLException e) {
			// TODO: handle exception
		}
		return returnValue;
	}
}
