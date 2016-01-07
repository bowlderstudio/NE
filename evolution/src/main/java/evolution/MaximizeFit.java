package evolution;

import java.util.Comparator;

public class MaximizeFit implements Comparator<Individual> {
    public int compare(Individual x, Individual y) {
    	if( ((Individual)x).getFitness() < ((Individual)y).getFitness() ) {
			return 1;
		} else if ( ((Individual)x).getFitness() > ((Individual)y).getFitness() ) {
			return -1;
		} else {
    		return 0;
    	}
    }
    
    public boolean equals( Object obj ) {
    	return this.getClass() == obj.getClass();
    }
}