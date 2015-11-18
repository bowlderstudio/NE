package evolution;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class ParetoCoevolution extends Evolution{

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
	
	public ParetoCoevolution(Environment e)
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
			
			System.out.println("generation"+(generation++)+" start."+"	global best fitnes="+currentBestSolution[0].getFitness());
			testRecord=new Hashtable();
			testRecord.put("generation", new Integer(generation));
			
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
			testRecord.put("frontSize", new Integer(population[i % popNumber].getNonDominationNum()));
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
				solution=paretoCombination(i,population[i].getIndividual(j),archiveSolutions[k]);
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
		testRecord.put("bestfitness", new Double(currentBestSolution[0].getFitness()));
		testRecord.put("phasefitness", new Double(currentBestSolution[0].getFitness()));
		testRecord.put("globalfitness", new Double(currentBestSolution[0].getFitness()));
	}

	/*
	private void populationSorting(Population Pop)
	{
		if (Config.SORTING_TYPE == Config.PARETOSORING)
			dominationSorting(Pop);
		else if (Config.SORTING_TYPE == Config.GREEDYSORTING)
			greedySorting(Pop);
		else if (Config.SORTING_TYPE == Config.DISTRIBUTIONSORTING)
			distributionSorting(Pop);
	}
	
	private void greedySorting(Population Pop)
	{
		for (int i=0;i<Pop.getPopulationSize();i++)
		{
			//sort the MOFitness of each individual;
			Pop.getIndividual(i).sortMOFitness();
		}
		//****************delete this line after debug********************
		Pop.paretoSort(0, Pop.getPopulationSize());
	}
	
	private void distributionSorting(Population Pop)
	{
		Vector[] sortingPops= new Vector[Config.ARCHIVE_SIZE];

		//sort the MOFitness of each individual;
		for (int i=0;i<Pop.getPopulationSize();i++)
		{
			Pop.getIndividual(i).copyMOFitness();
		}
		
		for (int i=0;i<Config.ARCHIVE_SIZE;i++)
		{
			sortingPops[i]=new Vector(Pop.getPopulationSize());
			
			Pop.fitnessSort(i);
				
			//copy all the individuals into sortingPops
			for (int j=0;j<Pop.getPopulationSize();j++)
			{
				sortingPops[i].add(j,Pop.getIndividual(j));
			}
		}
		
		Individual ind;
		for (int i=0;i<Pop.getPopulationSize();i++)
		{
			ind=(Individual)(Individual)sortingPops[i % Config.ARCHIVE_SIZE].elementAt(0);
			Pop.setIndividual(i, (Individual)ind.clone());
			
			//remove all the ind away from the sortingPops
			for (int j=0;j<Config.ARCHIVE_SIZE;j++)
			{
				sortingPops[j].remove(ind);
			}
		}
	}
	
	private void dominationSorting(Population Pop)
	{
		Vector candidatePop=new Vector();
		Vector nonDominationPop=new Vector();
		Vector sortingPop=new Vector();
		int frontLayer=0;
		boolean nonDominateCan;
		
		//copy all the individuals into candidatePop
		for (int i=0;i<Pop.getPopulationSize();i++)
		{
			//sort the MOFitness of each individual;
			Pop.getIndividual(i).sortMOFitness();
			//copy
			candidatePop.add(i,Pop.getIndividual(i));
		}
		
		while (candidatePop.size()>0)
		{
			//To start with, the first solution from the population is kept in the set nondominationPop. 
			nonDominationPop.add(0,candidatePop.elementAt(0));
			//remove the first individual from candidatePop
			candidatePop.removeElementAt(0);

			//each solution p (the second solution onwards) in candidatePop is compared 
			//with all members of the set nondominationPop one by one.
			int i=0;
			while (i<candidatePop.size())
			{
				nonDominateCan=false;
				for (int j=0;j<nonDominationPop.size();)
				{
					//If the solution p dominates any member q of P', 
					//then solution q is removed from P'.
					if (dominate((Individual)candidatePop.elementAt(i),(Individual)nonDominationPop.elementAt(j)))
					{
						candidatePop.add(0,nonDominationPop.elementAt(j));
						nonDominationPop.removeElementAt(j);
						i++;
					}
					else 
					{
						//if solution p is dominated by any member of P', 
						//move p to sortingPop
						if (dominate((Individual)nonDominationPop.elementAt(j),(Individual)candidatePop.elementAt(i)))
						{
							nonDominateCan=true;
							break;
						}
						j++;
					}
				}
				
				//if solution p is dominated by any member of P', 
				//the solution p is ignored
				//else If solution p is not dominated by any member of P', it is entered in P'.
				if (!nonDominateCan)
				{
					nonDominationPop.add(candidatePop.elementAt(i));
					candidatePop.removeElementAt(i);
					i--;
				}
				i++;
			}
			
			//save the nonDominationPop into sortringPop
			for (int k=0;k<nonDominationPop.size();k++)
			{
				((Individual)nonDominationPop.elementAt(k)).setFrontLayer(frontLayer);
				sortingPop.add(nonDominationPop.elementAt(k));
			}
			
			nonDominationPop.clear();
			
			frontLayer++;
		}
		
		//copy sortingPop into pop and sort the individual within the same front layer
		int frontStart=0,index=1;
		Pop.setIndividual(0, (Individual)sortingPop.elementAt(0));
		frontLayer=Pop.getIndividual(0).getFrontLayer();
		while (index<sortingPop.size())
		{
			Pop.setIndividual(index, (Individual)sortingPop.elementAt(index));
			if (Pop.getIndividual(index).getFrontLayer()!=frontLayer)
			{
				Pop.paretoSort(frontStart, index);

				frontLayer=Pop.getIndividual(index).getFrontLayer();
				frontStart=index;
			}
			index++;
		}

		Pop.paretoSort(frontStart, index);
	}
		
	//if individual indi1 dominate individual indi2 then return true, else return false
	private boolean dominate(Individual indi1, Individual indi2)
	{
		boolean dominate=true;
		boolean dominater=false;
		double[] fitness1,fitness2;

		fitness1=indi1.getMOFitness();
		fitness2=indi2.getMOFitness();
		
		if (Config.MIN)
		{
			for (int i=0;i<indi1.getMOFitness().length;i++)
			{
				if (fitness1[i]>fitness2[i])
				{
					dominate=false;
					break;
				}
				else if (fitness1[i]<fitness2[i])
				{
					dominater=true;
				}
			}
		}
		else
		{
			for (int i=0;i<indi1.getSortedMOFitness().length;i++)
			{
				if (fitness1[i]<fitness2[i])
				{
					dominate=false;
					break;
				}
				else if (fitness1[i]>fitness2[i])
				{
					dominater=true;
				}
			}
		}
		return dominate && dominater;
	}
	*/
	
	public void copySolution(Individual[] source, Individual[] destination)
	{
		for (int i=0;i<destination.length;i++)
		{
			destination[i]=(Individual)source[i].clone();
		}
	}
	
	public Individual[] paretoCombination(int index, Individual individual, Individual[] archiveSolution)
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