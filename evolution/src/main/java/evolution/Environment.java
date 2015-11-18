package evolution;

import java.util.*;

public interface Environment {

	public double evalSolution(Individual individual);
	public double evalSolution(Individual[] individual);

	public int getGeneType();
	public int getFullChromoLength();
	public int getSubChromoLength(int i);
	public Vector createFullSolution();
	public Vector createSubSolution(int i);
	public double getMaxFitness();
	public int getPopulationNumber();
	public void nextTask();
}