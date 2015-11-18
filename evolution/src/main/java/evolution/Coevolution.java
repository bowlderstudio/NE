package evolution;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class Coevolution extends Evolution{

	private Population[] population;
	private int popNumber;

	private Individual[] currentBestSolution=null;
	private Individual[] phaseBestSolution=null;
	private Individual[] globalBestSolution=null;
	
	private List perfQ = new Vector();	// double
	private static boolean SKIP = false;	// skip recombination;
	
	public Coevolution(Environment e)
	{
		super(e);
		popNumber=e.getPopulationNumber();
		population=new Population[popNumber];
		currentBestSolution=new Individual[popNumber];
		phaseBestSolution=new Individual[popNumber];
		globalBestSolution=new Individual[popNumber];
		
		for (int i=0;i<popNumber;i++)
		{
			population[i]=new Population(Config.POP_SIZE);
		
			currentBestSolution[i]=new Individual(e.getGeneType(),e.getSubChromoLength(i));
			phaseBestSolution[i]=new Individual(e.getGeneType(),e.getSubChromoLength(i));
			globalBestSolution[i]=new Individual(e.getGeneType(),e.getSubChromoLength(i));
			
			if (Config.MIN)
			{
				currentBestSolution[i].setFitness(100000);
				phaseBestSolution[i].setFitness(100000);
				globalBestSolution[i].setFitness(100000);
			}
			else
			{
				currentBestSolution[i].setFitness(0);
				phaseBestSolution[i].setFitness(0);
				globalBestSolution[i].setFitness(0);
			}
		}
	}
	
	public void evolve(int maxGeneration)
	{
		for (int i=0;i<popNumber;i++)
		{
			population[i].initializePopulation(environ.getGeneType(),environ.getSubChromoLength(i));
		}
		
		//initialize multifitness of individuals in the populations
		for (int i=0;i<popNumber;i++)
			for (int j=0;j<population[i].getPopulationSize();j++)
			{
				population[i].getIndividual(j).setMOFitnessSize(Config.ARCHIVE_SIZE);
				
				if (Config.MIN)
				{
					for (int k=0;k<Config.ARCHIVE_SIZE;k++)
						population[i].getIndividual(j).setMOFitness(k,100000);
				}
				else
				{
					for (int k=0;k<Config.ARCHIVE_SIZE;k++)
						population[i].getIndividual(j).setMOFitness(k,0);
				}
			}
		
		for (int i=0;i<maxGeneration;i++)
		{
			if (evaluationNumbers>=Config.MAX_EVALUATIONS)
			{
				break;
			}
//			System.out.println("generation"+(generation++)+" start."+"	global best fitnes="+globalBestSolution[0].getFitness());
			testRecord=new Hashtable();
			testRecord.put("generations", new Integer(i+1));
			
			evalPopulation(i % popNumber);
			
			
			if( SKIP ) {
				SKIP = false;// skip recombination if we have just perturb the population
			}
			else if (Config.GROUP_REPRODUCTION && (i % popNumber==popNumber-1))
			{
				for (int j=0;j<popNumber;j++)
				{
					populationSorting(population[j]);
					population[j].recombination();
					population[j].mutation();
				}
			}
			else if (!Config.GROUP_REPRODUCTION)
			{
				populationSorting(population[i % popNumber]); 
				population[i % popNumber].recombination();
				population[i % popNumber].mutation();
			}
			testRecord.put("evaluationNumbers", new Integer(evaluationNumbers));
			testRecord.put("bestFitness", new Double(currentBestSolution[0].getFitness()));
			testData.add(testRecord);
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
			for (int k=0;k<Config.ARCHIVE_SIZE;k++)
			{
				if (evaluationNumbers++>=Config.MAX_EVALUATIONS)
				{
					return;
				}
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
		Individual[] solution=new Individual[popNumber];
		for (int i=0;i<popNumber;i++)
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