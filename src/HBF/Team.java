package HBF;

public class Team {
	private User player1;
	private User player2;
	
	private int teamMMR;
	
	public Team(User p1, User p2) {
		player1 = p1;
		player2 = p2;
		
		teamMMR = (p1.getMMR() + p2.getMMR()) / 2;
	}
	
	public int getMMR() {
		return teamMMR;
	}
	
	public User getPlayer1() {
		return player1;
	}
	
	public User getPlayer2() {
		return player2;
	}
	
	public boolean hasUser(int userId) {
		if(player1.id() == userId || player2.id() == userId) 
			return true;
		else return false;
	}
}
