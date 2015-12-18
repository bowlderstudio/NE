package evolution;

import java.util.*;

public class StandardEvolution extends Evolution
{
	private Population population;

	private Individual globalBestSolution=null;
	
	private List<Double> perfQ = new Vector<Double>();	// double
	
	public StandardEvolution(Environment e, String propertyFile)
	{
		super(e,propertyFile);
		population=new Population(propertyFile);
		
		globalBestSolution=new Individual(geneType,e.getFullChromoLength());
		
		//initialize best fitness values
		globalBestSolution.setFitness(0);
	}
	
	public void evolve() {
		population.initializePopulation(environ.getFullChromoLength());
		
		for (int i=0;i<this.generationNumber;i++)
		{
			evalPopulation();
			
			if( skipRecombination ) {
				// skip recombination if we have just perturb the population
				// a strategy to escape local optima
				skipRecombination = false;
			}
			else 
			{   
				population.qsort();
				population.recombination();
				population.mutation();
			}
			evolutionProcess.add(new EvolutionRecord(i,globalBestSolution.getFitness()));
		}
	}
	
	public void evalPopulation()
	{
		population.evaluationReset();
		evaluationPerform();
	}
	
	private void evaluationPerform()
	{
		Individual bestSolution=new Individual(geneType,environ.getFullChromoLength());
		
		bestSolution.setFitness(0);
		
		for (int i=0;i<population.getPopulationSize();i++)
		{
			population.getIndividual(i).setFitness(environ.evalSolution(population.getIndividual(i)));
			
			if(population.getIndividual(i).getFitness() > bestSolution.getFitness()) { 
				bestSolution = (Individual)population.getIndividual(i).clone();
			}
		}

		if(bestSolution.getFitness() > globalBestSolution.getFitness()) { 
			globalBestSolution = (Individual)bestSolution.clone(); 
		}
		
		perfQ.add(0, new Double(bestSolution.getFitness()));

		// Used for increamental evolution
		if(bestSolution.getFitness() >= environ.getMaxFitness() )
		{
			globalBestSolution.setFitness(0.0);
			perfQ.clear();
			environ.nextTask();
		}

		// if performance stagnates, do something
		if( perfQ.size() >= this.stagnationNumber) {
			if(bestSolution.getFitness() <= ((Double)perfQ.get(this.stagnationNumber-1)).doubleValue()) {
				handleStagnation();
			}
		}
	}
		
	/**
	 * Performace stagnates
	 */
	private void handleStagnation() 
	{
		perfQ.clear();   
		newDeltaPhase();    
	}

	/**
	 * Implement perturb on best solution
	 */
	private void newDeltaPhase() {
		System.out.println( "DELTA started");
		skipRecombination = true;
		population.deltify(globalBestSolution);
	}
}