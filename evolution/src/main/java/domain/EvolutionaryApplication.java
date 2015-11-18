package domain;

import java.util.ArrayList;
import java.util.Hashtable;
import evolution.*;

public class EvolutionaryApplication
{
	private static long seed = System.currentTimeMillis();

	public EvolutionaryApplication()
	{
	}
	
	public static void main(String[] args)	{
		//the start time of running
		long startTime;
		//the end time of running
		long endTime;
		startTime=System.currentTimeMillis();
		EvolutionaryApplication gea=new EvolutionaryApplication();
		
		Evolution evolution=null;
		RandomSingleton.getInstance().setSeed(EvolutionaryApplication.seed);

		OptFunction domain = new OptFunction();
		
		if (Config.EVOLUTION_TYPE==Evolution.STANDARDEVOLUTION)
		{
			evolution= new StandardEvolution(domain);
		}
		else if (Config.EVOLUTION_TYPE==Evolution.STANDARDCOEVOLUTION)
		{
			evolution=new Coevolution(domain);
		}
		else if (Config.EVOLUTION_TYPE==Evolution.ARCHIVECOEVOLUTION)
		{
			evolution=new ArchiveCoevolution(domain);
		}
		
		evolution.evolve(Config.MAX_GENERATION);
		
		gea.displayResults();
		
		endTime=System.currentTimeMillis();
		
		System.out.println("Total run time is " + (endTime-startTime)/1000 + " seconds. Population number is "+ domain.getPopulationNumber());
		System.out.println();
		System.out.println();
		System.out.println();
	}

	public void displayResults()
	{
		/*
		System.out.println("\nTTT2 Game Running Parameters: \n");
		System.out.println("Initialized population number: "+Config.POP_SIZE);
		System.out.println("Max generations: "+Config.MAX_GENERATION);
		System.out.println("Mutation type: "+Config.MUT_TYPE);
		System.out.println("Mutation rate: "+Config.MUT_RATE);
		
		System.out.println("Crossover type: "+Config.MATE_TYPE);
		System.out.println("Breed number: "+Config.NUM_BREED);
		
		System.out.println("Stagnation: "+Config.NUM_STAGNATION);

		System.out.println("Optimization function: "+OptFunction.testFunction);
		
		System.out.println("gen	bestfitness	phasefitness	globalfitness");*/
		System.out.println("generations	globalfitness	evaluations");
		ArrayList testData=Evolution.testData;
		for (int i=0;i<testData.size();i++)
		{
			System.out.print(((Hashtable)testData.get(i)).get("generations").toString()+"	"
					//+((Hashtable)testData.get(i)).get("bestfitness").toString()+"		"
					//+((Hashtable)testData.get(i)).get("phasefitness").toString()+"		"
					+((Hashtable)testData.get(i)).get("bestFitness").toString()+"	"
					+((Hashtable)testData.get(i)).get("evaluationNumbers").toString()+"	"
					);
			
			if (Config.EVOLUTION_TYPE==Evolution.ARCHIVECOEVOLUTION)
			{
				//System.out.print(((Hashtable)testData.get(i)).get("frontSize").toString());
			}

			System.out.println();
		}
	}
}

