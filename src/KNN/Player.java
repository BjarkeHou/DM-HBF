package KNN;

public class Player {
	public int id;
	public String name;
	public TournamentResult[] tournaments;
	public int presentMMR;
	
	
	public Player(int id, TournamentResult[] tournaments, int presentMMR) {
		this.id = id;
		this.tournaments = tournaments;
		this.presentMMR = presentMMR;
	}
	
	public void print() {
		System.out.println("----------------------------------------");
		System.out.println("Name: " + name);
		System.out.println("Present MMR: " + presentMMR);
		System.out.println("Tournament Results:");
		for(TournamentResult t : tournaments) {
			t.print();
		}
		System.out.println("----------------------------------------");
	}
}
