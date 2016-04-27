package KNN;

import java.util.Comparator;

public class DistanceComparator implements Comparator<Result>{

	@Override
	public int compare(Result x, Result y) {
		if(x.distance < y.distance) return -1;
		else if(x.distance == y.distance) return 0;
		else return 1;
	}
	
}
