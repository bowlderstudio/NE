package evolution;

import java.util.*;

public abstract class Evolution{
	
	public static final int STANDARDEVOLUTION = 0;
	public static final int STANDARDCOEVOLUTION = 1;
	public static final int ARCHIVECOEVOLUTION = 2;
	
	public static final int FULLEVAL = 0;
	public static final int HALFEVAL = 1;
	public static final int RANDOMEVAL = 2;
	public static final int PARETOEVAL = 3;
	
	int generationNumber;
	int populationSize;
	Environment environ;
		
	public static ArrayList<EvolutionRecord> evolutionProcess;
	
	public Evolution(Environment e, String propertyFile)
	{
		Properties p = Utils.loadProperties(propertyFile);
		if (p == null)
			System.exit(1);
		generationNumber=Integer.parseInt(p.getProperty("max_generation", "100"));
		populationSize=Integer.parseInt(p.getProperty("pop_size", "100"));
		environ = e;
		evolutionProcess=new ArrayList<EvolutionRecord>();
	}
	
	public abstract void evolve();
	
	public void deltify(Individual[] individuals,Individual bestIndividual)
	{
		for (int i=0;i<individuals.length;i++)
		{
			individuals[i].perturb(bestIndividual);
		}
	}

	void populationSorting(Population Pop)
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
}