package HBF;

public class User {
	private int MMR = 1200;
	private int id;
	
	public User(int id) {
		this.id = id;
	}
	
	public int id() {
		return id;
	}
	
	public int getMMR() {
		return MMR;
	}
	
	public void addMMR(int change) {
		MMR += change;
	}
}
