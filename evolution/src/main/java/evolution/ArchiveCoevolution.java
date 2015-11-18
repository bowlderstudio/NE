package evolution;


import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class ArchiveCoevolution extends Evolution{

	private Population[] population;
	private Individual[][] archiveSolutions;
	private Individual[][] bestArchiveSolutions;
	private int popNumber;
	private int archiveSize;

	private Individual[] currentBestSolution=null;
	private Individual[] phaseBestSolution=null;
	private Individual[] globalBestSolution=null;
	
	private List perfQ = new Vector();	// double
	private static boolean SKIP = false;	// skip recombination;
	
	public ArchiveCoevolution(Environment e)
	{
		super(e);
		popNumber=e.getPopulationNumber();
		archiveSize=Config.ARCHIVE_SIZE;
		population=new Population[popNumber];
		archiveSolutions=new Individual[archiveSize][popNumber];
		bestArchiveSolutions=new Individual[archiveSize][popNumber];
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
		
		for (int i=0;i<archiveSize;i++)
		{
			for (int j=0;j<popNumber;j++)
			{
				archiveSolutions[i][j]=new Individual(e.getGeneType(),e.getSubChromoLength(i));
				bestArchiveSolutions[i][j]=new Individual(e.getGeneType(),e.getSubChromoLength(i));
			}
		}
	}
	
	public void evolve(int maxGeneration)
	{
		int random;
		double fitness;
		
		for (int i=0;i<popNumber;i++)
		{
			population[i].initializePopulation(environ.getGeneType(),environ.getSubChromoLength(i));
		}
		
		//initialize archive population with random individuals
		for (int i=0;i<Config.ARCHIVE_SIZE;i++)
		{
			for (int j=0;j<popNumber;j++)
			{
				random = Math.abs(RandomSingleton.getInstance().nextInt())% population[j].getPopulationSize();
				archiveSolutions[i][j]=(Individual)population[j].getIndividual(random).clone();
			}
			fitness=environ.evalSolution(archiveSolutions[i]);
			for (int j=0;j<popNumber;j++)
			{
				archiveSolutions[i][j].setFitness(fitness);
				bestArchiveSolutions[i][j]=(Individual)archiveSolutions[i][j].clone();
			}
		}
		
		//initialize multifitness of individuals in the populations
		for (int i=0;i<popNumber;i++)
			for (int j=0;j<population[i].getPopulationSize();j++)
			{
				population[i].getIndividual(j).setMOFitnessSize(archiveSize);
				
				if (Config.MIN)
				{
					for (int k=0;k<archiveSize;k++)
						population[i].getIndividual(j).setMOFitness(k,100000);
				}
				else
				{
					for (int k=0;k<archiveSize;k++)
						population[i].getIndividual(j).setMOFitness(k,0);
				}
			}
		
		for (int i=0;i<maxGeneration;i++)
		{
			if (evaluationNumbers>=Config.MAX_EVALUATIONS)
			{
				break;
			}
			
			testRecord=new Hashtable();
			testRecord.put("generations", new Integer(i+1));
			
			//if not group reproduction then update archive solutions every generation
			if (!Config.GROUP_REPRODUCTION)
			{
				for (int k=0;k<archiveSize;k++)
					for (int j=0;j<popNumber;j++)
					{
						archiveSolutions[k][j]=(Individual)bestArchiveSolutions[k][j].clone();
					}
			}
			else if (Config.GROUP_REPRODUCTION && (i % popNumber==0))
			{
				for (int k=0;k<archiveSize;k++)
					for (int j=0;j<popNumber;j++)
					{
						archiveSolutions[k][j]=(Individual)bestArchiveSolutions[k][j].clone();
					}
			}
			
			//System.out.println("generation"+(generation++)+" start."+"	global best fitnes="+currentBestSolution[0].getFitness());
			
			evalPopulation(i % popNumber);
			
			if( SKIP ) {
				SKIP = false;// skip recombination if we have just perturb the population
			}
			else if (Config.GROUP_REPRODUCTION && (i % popNumber==popNumber-1))
			{
				for (int j=0;j<popNumber;j++)
				{
	//				printSortedMOfitness(population[j]);
					populationSorting(population[j]);
	//				printSortedMOfitness(population[j]);
					population[j].paretoRecombine();
					population[j].mutation();
				}
			}
			else if (!Config.GROUP_REPRODUCTION)
			{
//				printSortedMOfitness(population[i % popNumber]);
				populationSorting(population[i % popNumber]); 
				//population[i % popNumber].paretoSort(0, population[i % popNumber].getPopulationSize());
//				printSortedMOfitness(population[i % popNumber]);
				population[i % popNumber].paretoRecombine();
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
		double fitness;
		
		for (int j=0;j<population[i].getPopulationSize();j++)
		{
			for (int k=0;k<archiveSize;k++)
			{
				if (evaluationNumbers++>=Config.MAX_EVALUATIONS)
				{
					return;
				}
				solution=archiveCombination(i,population[i].getIndividual(j),archiveSolutions[k]);
				fitness=environ.evalSolution(solution);
				population[i].getIndividual(j).setMOFitness(k, fitness);
				
				if (Config.MIN)
				{
					if (fitness<bestArchiveSolutions[k][i].getFitness())
					{
						bestArchiveSolutions[k][i]=(Individual)population[i].getIndividual(j).clone();
						//update fitness for archive k
						for (int m=0;m<popNumber;m++)
						{
							bestArchiveSolutions[k][m].setFitness(fitness);
						}
					}
					if (fitness<currentBestSolution[i].getFitness())
					{
						for (int m=0;m<popNumber;m++)
						{
							currentBestSolution[m]=(Individual)bestArchiveSolutions[k][m].clone();
						}
					}
				}
				else if (fitness>bestArchiveSolutions[k][i].getFitness())
				{
					bestArchiveSolutions[k][i]=(Individual)population[i].getIndividual(j).clone();
					//update fitness for archive k
					for (int m=0;m<popNumber;m++)
					{
						bestArchiveSolutions[k][m].setFitness(fitness);
					}
					
					if (fitness>currentBestSolution[i].getFitness())
					{
						for (int m=0;m<popNumber;m++)
						{
							currentBestSolution[m]=(Individual)bestArchiveSolutions[k][m].clone();
						}
					}
				}
			}
		}
		//testRecord.put("bestfitness", new Double(currentBestSolution[0].getFitness()));
		//testRecord.put("phasefitness", new Double(currentBestSolution[0].getFitness()));
		//testRecord.put("globalfitness", new Double(currentBestSolution[0].getFitness()));
	}

	public void copySolution(Individual[] source, Individual[] destination)
	{
		for (int i=0;i<destination.length;i++)
		{
			destination[i]=(Individual)source[i].clone();
		}
	}
	
	public Individual[] archiveCombination(int index, Individual individual, Individual[] archiveSolution)
	{
		Individual[] solution=new Individual[popNumber];
		for (int i=0;i<popNumber;i++)
		{
			if (i==index)
				solution[i]=individual;
			else
				solution[i]=archiveSolution[i];
		}
		return solution;
		//return bestCombination(index,individual);
	}
	
	public Individual[] bestCombination(int index,int arcIndex, Individual individual)
	{
		Individual[] solution=new Individual[popNumber];
		for (int i=0;i<popNumber;i++)
		{
			if (i==index)
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

	private void printMOfitness(Population pop)
	{
		double[] moFitness;
		for(int i=0;i<pop.getPopulationSize();i++)
		{
			moFitness=pop.getIndividual(i).getMOFitness();
			System.out.print(pop.getIndividual(i).getFrontLayer()+"	");
			for (int j=0;j<moFitness.length;j++)
			{
			System.out.print(moFitness[j]+"		");
			}
			System.out.println();
		}
		System.out.println("__________________end_Sorted Pop__________________");
	}
	
	private void printSortedMOfitness(Population pop)
	{
		double[] moFitness;
		for(int i=0;i<pop.getPopulationSize();i++)
		{
			moFitness=pop.getIndividual(i).getSortedMOFitness();
			System.out.print(pop.getIndividual(i).getFrontLayer()+"	");
			for (int j=0;j<moFitness.length;j++)
			{
			System.out.print(moFitness[j]+"		");
			}
			System.out.println();
		}
		System.out.println("__________________end_Sorted Pop__________________");
	}
}