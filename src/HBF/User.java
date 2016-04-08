package HBF;

import java.util.Comparator;

public class User implements Comparator<User>, Comparable<User>{
	private int MMR = 1200;
	private int id;
	private String name;
	
	public User(int id) {
		this.id = id;
	}
	
	public User(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public int id() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public int getMMR() {
		return MMR;
	}
	
	public void adjustMMR(int change) {
		MMR += change;
	}

	@Override
	public int compareTo(User o) {
		if(o.getMMR() > this.MMR) return 1;
		if(o.getMMR() < this.MMR) return -1;
		return 0;
	}

	@Override
	public int compare(User o1, User o2) {
		if(o1.getMMR() > o2.getMMR()) return -1;
		if(o1.getMMR() < o2.getMMR()) return 1;
		return 0;
	}
}
