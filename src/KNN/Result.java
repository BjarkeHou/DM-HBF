package KNN;

public class Result {
	public int MMR;
	public double distance;
	public int id;
	
	public Result(int id, int MMR, double dist) {
		this.id = id;
		this.MMR = MMR;
		distance = dist;
	}
}
