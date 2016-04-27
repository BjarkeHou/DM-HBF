package KNN;

import java.util.Date;

public class TournamentResult {
	public int mmrChange;
	public double day;
	public Date date;
	
	public TournamentResult(int mmrChange, int day) {
		this.mmrChange = mmrChange;
		WeekDay wd = new WeekDay();
		switch (day) {
		case 1:
			this.day = wd.MONDAY;
			break;
		case 2:
			this.day = wd.TUESDAY;
			break;
		case 3:
			this.day = wd.WEDNESDAY;
			break;
		case 4:
			this.day = wd.THURSDAY;
			break;
		default:
			break;
		}
	}
	
	public void print() {
		System.out.println("\tDay: " + day + " - MMR Change: " + mmrChange);
	}
}
