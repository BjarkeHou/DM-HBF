package Apriori;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class Apriori {

    public List<ItemSet> apriori( int[][] data, float supportThreshold ) {
    	List<ItemSet> returnList = new ArrayList<ItemSet>();
        int k;
        Hashtable<ItemSet, Integer> frequentItemSets = generateFrequentItemSetsLevel1( data, supportThreshold );
        System.out.println(frequentItemSets.size());
        for (k = 1; frequentItemSets.size() > 0; k++) {
            System.out.print( "Finding frequent itemsets of length " + (k + 1) + "…" );
            frequentItemSets = generateFrequentItemSets( supportThreshold, data, frequentItemSets );
            
            for(ItemSet key : frequentItemSets.keySet()) {
            	returnList.add(key);
            }

            System.out.println( " found " + frequentItemSets.size() );
        }
        // TODO: create association rules from the frequent itemsets

        // TODO: return something useful
        return returnList;
    }

    private Hashtable<ItemSet, Integer> generateFrequentItemSets( float supportThreshold, int[][] data,
                    Hashtable<ItemSet, Integer> lowerLevelItemSets ) {
        // TODO: first generate candidate itemsets from the lower level itemsets
    	Hashtable<ItemSet, Integer> newItemSets = new Hashtable<ItemSet, Integer>();
    	ItemSet[] oldItemSetsArray = lowerLevelItemSets.keySet().toArray(new ItemSet[lowerLevelItemSets.size()]);
    	
    	for(int i = 0; i < oldItemSetsArray.length-1; i++) {
    		for(int j = i+1; j < oldItemSetsArray.length; j++) {
    			ItemSet joinedSet = joinSets(oldItemSetsArray[i], oldItemSetsArray[j]);
    			
    			if(joinedSet == null) continue;
    			
    			newItemSets.put(joinedSet, 0);
    			
    			for(int[] row : data) {
    				boolean existsIn = true;
    	    		for(int item : joinedSet.set) {
    	    			if(!Arrays.asList(row).contains(item)) {
    	    				existsIn = false;
    	    				break;
    	    			}
    	    		}
    	    		if(existsIn) {
    	    			joinedSet.andAnotherOne();
    	    			newItemSets.replace(joinedSet, joinedSet.getOccurenses());
    	    		}
    	    	}
    		}
    	}
    	
    	ArrayList<ItemSet> badSets = new ArrayList<ItemSet>();
    	for(ItemSet key : newItemSets.keySet()) {
    		float supp = countSupport(key.set, data);
    		if(supp < supportThreshold) {
    			badSets.add(key);
    		}
    	}
    	
    	for(ItemSet set : badSets) {
    		newItemSets.remove(set);
    	}
    	
        return newItemSets;
    }

    private ItemSet joinSets( ItemSet first, ItemSet second ) {
        if(first.set.length != second.set.length) return null;
        
        Arrays.sort(first.set);
        Arrays.sort(second.set);
        int[] joinedSet = new int[first.set.length+1];
        for(int i = 0; i < first.set.length-1; i++) {
        	if(first.set[i] != second.set[i]) return null;
        	joinedSet[i] = first.set[i];
        }
        
        joinedSet[joinedSet.length-2] = first.set[first.set.length-1];
        joinedSet[joinedSet.length-1] = second.set[second.set.length-1];
        return new ItemSet(joinedSet);
    }

    private Hashtable<ItemSet, Integer> generateFrequentItemSetsLevel1( int[][] data, float supportThreshold ) {
    	Hashtable<ItemSet, Integer> freqItemSets = new Hashtable<ItemSet, Integer>();
    	for(int[] row : data) {
    		for(int item : row) {
    			int[] _item = new int[1];
    			_item[0] = item;
    			ItemSet itemSet = new ItemSet(_item);
    			if(freqItemSets.containsKey(itemSet)) {
    				itemSet.andAnotherOne();
    				freqItemSets.replace(itemSet, itemSet.getOccurenses());
    			} else {
    				itemSet.andAnotherOne();
    				freqItemSets.put(itemSet, itemSet.getOccurenses());
    			}
    		}
    	}
    	
    	ArrayList<ItemSet> badSets = new ArrayList<ItemSet>();
    	for(ItemSet key : freqItemSets.keySet()) {
    		float supp = countSupport(key.set, data);
    		if(supp < supportThreshold) {
    			badSets.add(key);
    		}
    	}
    	
    	for(ItemSet set : badSets) {
    		freqItemSets.remove(set);
    	}
    	
        return freqItemSets;
    }

    private float countSupport( int[] set, int[][] data ) {
        // Assumes that items in ItemSets and transactions are both unique
    	int occurenses = 0;
    	for(int[] row : data) {
    		int count = 0;
    		for(int i = 0; i < set.length; i++) {
    			if(contains(row, set[i])) {
    				count++;
    			} else {
    				break;
    			}
    		}
    		if(count == set.length) {
    			occurenses++;
    		}
    	}
    	
        // TODO: return something useful
    	float x = (occurenses*100)/data.length;
        return x;
    }
    
    private static boolean contains(int[] arr, int item) {
        for (int n : arr) {
           if (item == n) {
              return true;
           }
        }
        return false;
     }

}
