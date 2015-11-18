package evolution;

import java.util.*;

public class StandardEvolution extends Evolution
{
	private Population population;

	//private Individual localBestSolution=null;
	private Individual phaseBestSolution=null;
	private Individual globalBestSolution=null;
	
	private List perfQ = new Vector();	// double
	private static boolean SKIP = false;	// skip recombination;
	
	public StandardEvolution(Environment e)
	{
		super(e);
		
		population=new Population(Config.POP_SIZE);
		
		//localBestSolution=new Individual(e.getGeneType(),e.getFullChromoLength());
		phaseBestSolution=new Individual(e.getGeneType(),e.getFullChromoLength());
		globalBestSolution=new Individual(e.getGeneType(),e.getFullChromoLength());
		
		if (Config.MIN)
		{
			phaseBestSolution.setFitness(100000);
			globalBestSolution.setFitness(100000);
		}
		else
		{
			phaseBestSolution.setFitness(0);
			globalBestSolution.setFitness(0);
		}
		
	}
	
	public void evolve(int maxGeneration)
	{
		population.initializePopulation(environ.getGeneType(),environ.getFullChromoLength());
		
		for (int i=0;i<maxGeneration;i++)
		{
			if (evaluationNumbers>=Config.MAX_EVALUATIONS)
			{
				break;
			}
			//System.out.println("generation"+(++generation)+" start."+"	global best fitnes="+globalBestSolution.getFitness());
			testRecord=new Hashtable();
			testRecord.put("generations", new Integer(generation));
			
			evalPopulation();
			
			if( SKIP ) {
				SKIP = false;// skip recombination if we have just perturb the population
			}
			else 
			{   
				population.qsort();
				population.recombination();
				population.mutation();
			}
			testRecord.put("evaluationNumbers", new Integer(evaluationNumbers));
			testRecord.put("bestFitness", new Double(globalBestSolution.getFitness()));
			testData.add(testRecord);
		}
	}
	
	public void evalPopulation()
	{
		population.evaluationReset();
		evaluationPerform();
	}
	
	private void evaluationPerform()
	{
		Individual bestSolution=new Individual(environ.getGeneType(),environ.getFullChromoLength());
		
		if (Config.MIN)
		{
			bestSolution.setFitness(100000);
		}
		else
		{
			bestSolution.setFitness(0);
		}
		
		for (int i=0;i<population.getPopulationSize();i++)
		{
			if (evaluationNumbers++>=Config.MAX_EVALUATIONS)
			{
				return;
			}
			population.getIndividual(i).setFitness(environ.evalSolution(population.getIndividual(i)));
			
			if( Config.MIN ) 
			{ 
				if( population.getIndividual(i).getFitness() < bestSolution.getFitness()) 
				{
					bestSolution = (Individual)population.getIndividual(i).clone();
				}
			} 
			else if(population.getIndividual(i).getFitness() > bestSolution.getFitness()) { 
				bestSolution = (Individual)population.getIndividual(i).clone();
			}
		}
		
		if(Config.MIN) 
		{ 
			if(bestSolution.getFitness() < phaseBestSolution.getFitness()) 
			{
				phaseBestSolution = (Individual)bestSolution.clone(); 
			}
		} 
		else if(bestSolution.getFitness() > phaseBestSolution.getFitness())
		{ 
			phaseBestSolution = (Individual)bestSolution.clone(); 
		}
			  
		if(Config.MIN) 
		{ 
			if(phaseBestSolution.getFitness() < globalBestSolution.getFitness()) 
			{
				globalBestSolution = (Individual)phaseBestSolution.clone(); 
			}
		} 
		else if(phaseBestSolution.getFitness() > globalBestSolution.getFitness()) 
		{ 
			globalBestSolution = (Individual)phaseBestSolution.clone(); 
		}
		
		perfQ.add(0, new Double(phaseBestSolution.getFitness()));

		if( Config.MIN )
		{  
			if( bestSolution.getFitness() <= environ.getMaxFitness() )
			{
				phaseBestSolution.setFitness(1000000);
				perfQ.clear();
				environ.nextTask();
			}
		} 
		else if(bestSolution.getFitness() >= environ.getMaxFitness() )
		{
			phaseBestSolution.setFitness(0.0);
			perfQ.clear();
			environ.nextTask();
		}

		// if performance stagnates, do something
		if( perfQ.size() >= Config.NUM_STAGNATION) {
			if(Config.MIN) {
				if(bestSolution.getFitness() >= ((Double)perfQ.get(Config.NUM_STAGNATION-1)).doubleValue()) {
					handleStagnation();
				}
			} else if(bestSolution.getFitness() <= ((Double)perfQ.get(Config.NUM_STAGNATION-1)).doubleValue()) {
				handleStagnation();
			}
		}
		
		testRecord.put("bestfitness", new Double(bestSolution.getFitness()));
		testRecord.put("phasefitness", new Double(phaseBestSolution.getFitness()));
		testRecord.put("globalfitness", new Double(globalBestSolution.getFitness()));
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
		population.deltify(globalBestSolution);
	}
}