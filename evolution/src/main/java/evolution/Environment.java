package evolution;

import java.util.*;

public abstract class Environment {

	public abstract double evalSolution(Individual individual);
	public abstract double evalSolution(Individual[] individual);

	public final int bitsLen=16;
	protected int geneType; 
	public abstract int getDimension();
	public abstract Vector createFullSolution();
	public abstract Vector createSubSolution(int i);
	public abstract double getMaxFitness();
	public abstract int getPopulationNumber();
	public abstract void nextTask();
	
	public Environment(String propertyFile) {
		Properties p = Utils.loadProperties(propertyFile);
		if (p == null)
			System.exit(1);
		geneType=Integer.parseInt(p.getProperty("gene_type", "0"));
			
	}
	
	public int getSubChromoLength(int i)
	{
		int geneLen=0;
		
		if (geneType==Individual.BITS_GENE) {
			geneLen=bitsLen;
		} else {
			geneLen=1;
		}
		return (int)Math.ceil((double)geneLen*getDimension()/getPopulationNumber());
	}
	
	public int getFullChromoLength()
	{
		int geneLen=0;
		
		if (geneType==Individual.BITS_GENE) {
			geneLen=bitsLen;
		} else {
			geneLen=1;
		}
		return geneLen*getDimension();
	}
}