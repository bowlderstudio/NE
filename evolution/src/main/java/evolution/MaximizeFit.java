package evolution;

import java.util.Comparator;

public class MaximizeFit implements Comparator {
    public int compare(Object x, Object y) {
    	if( x instanceof Individual && y instanceof Individual ) {
    		if( ((Individual)x).getFitness() < ((Individual)y).getFitness() ) {
    			return 1;
    		} else if ( ((Individual)x).getFitness() > ((Individual)y).getFitness() ) {
    			return -1;
    		} else {
        		return 0;
        	}
        } else {
        	throw new ClassCastException();
        }
    }
    
    public boolean equals( Object obj ) {
    	return this.getClass() == obj.getClass();
    }
}