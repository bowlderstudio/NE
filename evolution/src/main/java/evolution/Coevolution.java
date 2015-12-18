package evolution;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class Coevolution extends Evolution{

	private Population[] population;
	private Individual[][] archiveSolutions;
	private Individual[][] bestArchiveSolutions;
	private Individual[] currentBestSolution=null;
	private Individual[] phaseBestSolution=null;
	private Individual[] globalBestSolution=null;
	
	private List perfQ = new Vector();	// double
	
	public Coevolution(Environment e, String propertyFile)
	{
		super(e,propertyFile);
		population=new Population[populationNumber];
		globalBestSolution=new Individual[populationNumber];
		archiveSolutions=new Individual[getArchieveSize()][populationNumber];
		bestArchiveSolutions=new Individual[getArchieveSize()][populationNumber];
		
		for (int i=0;i<populationNumber;i++)
		{
			population[i]=new Population(propertyFile);
		
			globalBestSolution[i]=new Individual(geneType, e.getSubChromoLength(i));
			
			globalBestSolution[i].setFitness(0);
		}
		
		for (int i=0;i<getArchieveSize();i++)
		{
			for (int j=0;j<populationNumber;j++)
			{
				archiveSolutions[i][j]=new Individual(geneType,e.getSubChromoLength(i));
				bestArchiveSolutions[i][j]=new Individual(geneType,e.getSubChromoLength(i));
			}
		}
	}
	
	public void evolve()
	{
		int random;
		double fitness;
		
		for (int i=0;i<populationNumber;i++)
		{
			population[i].initializePopulation(environ.getSubChromoLength(i));
		}
		
		//initialize archive population with random individuals
		for (int i=0;i<getArchieveSize();i++)
		{
			for (int j=0;j<populationNumber;j++)
			{
				random = Math.abs(RandomSingleton.getInstance().nextInt())% population[j].getPopulationSize();
				archiveSolutions[i][j]=(Individual)population[j].getIndividual(random).clone();
			}
			fitness=environ.evalSolution(archiveSolutions[i]);
			for (int j=0;j<populationNumber;j++)
			{
				archiveSolutions[i][j].setFitness(fitness);
				bestArchiveSolutions[i][j]=(Individual)archiveSolutions[i][j].clone();
			}
		}
		
		//initialize multifitness of individuals in the populations
		for (int i=0;i<populationNumber;i++)
			for (int j=0;j<population[i].getPopulationSize();j++)
			{
				population[i].getIndividual(j).setMOFitnessSize(getArchieveSize());
				
				for (int k=0;k<getArchieveSize();k++)
					population[i].getIndividual(j).setMOFitness(k,0);
			}
		
		for (int i=0;i<generationNumber;i++)
		{
			int popIndex=(populationNumber==1)?0:(i % populationNumber);
			evalPopulation(popIndex);
			
			
			if( skipRecombination ) {
				skipRecombination = false;// skip recombination if we have just perturb the population
			}
			populationSorting(population[popIndex]); 
			population[popIndex].recombination();
			population[popIndex].mutation();
		}
	}
	
	public void evalPopulation(int i)
	{
		population[i].evaluationReset();
		evaluationPerform(i);
	}
	
	private void evaluationPerform(int i)
	{
		Individual[] solution=null;//save the best one between solution1 and solution2
		double fitness=0;
		
		for (int j=0;j<population[i].getPopulationSize();j++)
		{
			for (int k=0;k<getArchieveSize();k++)
			{
				//best combination
				if (Config.SELECTION_TYPE==Config.BESTSELECTION)
				{
					solution=bestCombination(i,k,population[i].getIndividual(j));
				}
				//random combination
				else if (Config.SELECTION_TYPE==Config.RANDOMSELECTION)
				{
					solution=randomCombination(i,population[i].getIndividual(j));
				}
				//mixed combination
				else if (Config.SELECTION_TYPE==Config.MIXEDSELECTION)
				{
					if (k==0)
						solution=bestCombination(i,k,population[i].getIndividual(j));
					else
						solution=randomCombination(i,population[i].getIndividual(j));
				}
				fitness=environ.evalSolution(solution);
				population[i].getIndividual(j).setMOFitness(k, fitness);
				
				for (int m=0;m<solution.length;m++)
				{
					solution[m].setFitness(fitness);
				}
				
				if( Config.MIN ) 
				{ 
					if( fitness < currentBestSolution[0].getFitness()) 
					{
						copySolution(solution,currentBestSolution);
					}
				} 
				else if( fitness > currentBestSolution[0].getFitness()) { 
					copySolution(solution,currentBestSolution);
				}
			}
		}

		
		if(Config.MIN) 
		{ 
			if(currentBestSolution[0].getFitness() < phaseBestSolution[0].getFitness()) 
			{
				copySolution(currentBestSolution,phaseBestSolution); 
			}
		} 
		else if(currentBestSolution[0].getFitness() > phaseBestSolution[0].getFitness())
		{ 
			copySolution(currentBestSolution,phaseBestSolution); 
		}
			  
		if(Config.MIN) 
		{ 
			if(phaseBestSolution[0].getFitness() < globalBestSolution[0].getFitness()) 
			{
				copySolution(phaseBestSolution,globalBestSolution); 
			}
		} 
		else if(phaseBestSolution[0].getFitness() > globalBestSolution[0].getFitness()) 
		{ 
			copySolution(phaseBestSolution,globalBestSolution); 
		}
		/*
		perfQ.add(0, new Double(phaseBestSolution[0].getFitness()));

		if( Config.MIN )
		{  
			if( currentBestSolution[0].getFitness() <= environ.getMaxFitness() )
			{
				phaseBestSolution[0].setFitness(1000000);
				perfQ.clear();
				environ.nextTask();
			}
		} 
		else if(currentBestSolution[0].getFitness() >= environ.getMaxFitness() )
		{
			phaseBestSolution[0].setFitness(0.0);
			perfQ.clear();
			environ.nextTask();
		}

		// if performance stagnates, do something
		if( perfQ.size() >= Config.NUM_STAGNATION) {
			if(Config.MIN) {
				if(currentBestSolution[0].getFitness() >= ((Double)perfQ.get(Config.NUM_STAGNATION-1)).doubleValue()) {
					handleStagnation();
				}
			} else if(currentBestSolution[0].getFitness() <= ((Double)perfQ.get(Config.NUM_STAGNATION-1)).doubleValue()) {
				handleStagnation();
			}
		}
		*/
	}
	
	public void copySolution(Individual[] source, Individual[] destination)
	{
		for (int i=0;i<destination.length;i++)
		{
			destination[i]=(Individual)source[i].clone();
		}
	}
	
	public Individual[] bestCombination(int popIndex, int arcIndex, Individual individual)
	{
		Individual[] solution=new Individual[populationNumber];
		for (int i=0;i<populationNumber;i++)
		{
			if (i==popIndex)
				solution[i]=individual;
			else
				solution[i]=population[i].selectBestIndividual(arcIndex);
		}
		return solution;
	}
	
	public Individual[] randomCombination(int index, Individual individual)
	{
		Individual[] solution=new Individual[popNumber];
		for (int i=0;i<popNumber;i++)
		{
			if (i==index)
				solution[i]=individual;
			else
				solution[i]=population[i].selectRandomIndividual();
		}
		return solution;
	}
	
//----------------------------------------------------------------------
// make a decision about what to do when performace stagnates
	private void handleStagnation() 
	{
		perfQ.clear();   
		newDeltaPhase();    
	}
	
//----------------------------------------------------------------------
// Start a new delta phase 
	private void newDeltaPhase() {
		System.out.println( "DELTA started");
		SKIP = true;
		for (int i=0;i<popNumber;i++)
		{
			population[i].deltify(globalBestSolution[i]);
		}
	}
}