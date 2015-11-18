package evolution;

import java.util.Comparator;

public class ParetoMinimizeFit implements Comparator {
    public int compare(Object x, Object y) {
    	if( x instanceof Individual && y instanceof Individual ) {
    		if( MOFitnessSmaller((Individual)x,(Individual)y ) ) {
    			return -1;
    		} else if ( MOFitnessBigger((Individual)x,(Individual)y ) ) {
    			return 1;
    		} else {
        		return 0;
        	}
        } else {
        	throw new ClassCastException();
        }
    }
    
    public boolean MOFitnessSmaller(Individual x, Individual y)
    {
    	boolean smaller=true;
    	for (int i=0;i<x.getSortedMOFitness().length;i++)
    	{
    		if (x.getSortedMOFitness()[i]<y.getSortedMOFitness()[i])
    		{
    			smaller=true;
    			break;
    		}
    		else if (x.getSortedMOFitness()[i]>y.getSortedMOFitness()[i])
    		{
    			smaller=false;
    			break;
    		}
    	}
    	return smaller && !x.getSortedMOFitness().equals(y.getSortedMOFitness());
    }
    
    public boolean MOFitnessBigger(Individual x, Individual y)
    {
    	boolean bigger=true;
    	for (int i=0;i<x.getSortedMOFitness().length;i++)
    	{
    		if (x.getSortedMOFitness()[i]>y.getSortedMOFitness()[i])
    		{
    			bigger=true;
    			break;
    		}
    		else if (x.getSortedMOFitness()[i]<y.getSortedMOFitness()[i])
    		{
    			bigger=false;
    			break;
    		}
    	}
    	return bigger && !x.getSortedMOFitness().equals(y.getSortedMOFitness());
    }
    
    public boolean equals( Object obj ) {
    	return this.getClass() == obj.getClass();
    }
}