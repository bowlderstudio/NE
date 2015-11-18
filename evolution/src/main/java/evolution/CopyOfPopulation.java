//////////////////////////////////////////////////////////////////////
//  Created by Min Shi
//	This is a population class of Genetic Algorithms, which contains the basic methods for a population
//	such as initialization, crossover and mutation 
//
//////////////////////////////////////////////////////////////////////

package evolution;

import java.util.*;

public class CopyOfPopulation{
	
	//define the types of crossover operator: 
	//CROSSOVER_ONEPOINT is one-point crossover and CROSSOVER_TWOPOINT is two-point crossover
	public final static int CROSSOVER_ONEPOINT=0;
	public final static int CROSSOVER_TWOPOINT=1;

	//define the types of mutation operator:
	//MUTATION_ONEBIT flips one bit in the chromosome
	//MUTATION_TWOSWAP swaps two bits in the chromosome
	public final static int MUTATION_ONEBIT=11;
	public final static int MUTATION_TWOSWAP=12;
	
	//The parameters of genetic algorithms
	//The individuals in the population
	Individual[] pop; 
	//The number of individuals to do the crossover 
	private int numBreed;
	//The mutation rate
	private double mutationRate;
	//The mutation type
	private int mutationType;
	//The crossover type
	private int crossoverType;
	
	//The constructor of population initializes some parameters of the genetic algorithms
	public CopyOfPopulation(int size)
	{
		pop=new Individual[size];
		numBreed=Config.NUM_BREED;
		mutationRate=Config.MUT_RATE;
		mutationType=Config.MUT_TYPE;
		crossoverType=Config.MATE_TYPE;
	}
	
	//Initialize the population
	//geneType: the type of gene of an individual chromosome---0:bit; 1:integer; 2:real number
	//len: the length of an individual
	public void initializePopulation(int geneType, int len)
	{
		for (int i=0;i<pop.length;i++)
		{
			pop[i]=new Individual(geneType,len);
		}
	}
	
	//reset the fitness and the times for evaluation of all indivduals in the population
	public void evaluationReset()
	{
		for (int i=0;i<pop.length;i++)
		{
			pop[i].resetFitness();
			pop[i].resetTestsTime();
		}
	}
	
	//get the size of the population
	public int getPopulationSize() {
		return pop.length;
	}
	
	//get the index-th individual of the population 
	public Individual getIndividual( int index )
	{
		return pop[index];
	}
	
	//set the index-th individual of the population
	//index: the index of the modified individual in the population 
	//individual: the new individual
	public void setIndividual(int index, Individual individual)
	{
		pop[index]=individual;
	}
	
	//get the entire individuals of the population
	public Individual[] getPopulation()
	{
		return pop;
	}
	
	//recombine two individuals to do crossover.
	public void recombination() {
		for (int i = 0; i < numBreed; ++i) {
			//find a mate to do the crossover
			int mate = findMate(i);
			//the first two are parents and the last two are children
    		crossover(pop[i], pop[mate],pop[pop.length-(1+i*2)],pop[pop.length-(2+i*2)]);
	    }
	}
	
	//randomly find a mate for the num-th individual from the breeding individuals 
	//as long as its mate is not itself.
	private int findMate(int num) {
		int mate;
		do
		{
			mate=Math.abs(RandomSingleton.getInstance().nextInt())%numBreed;
		}while (mate==num);// if the mate is itself then select another
		return mate;
	}
	
	//do crossover 
	//parent 1 and parent 2 are individuals to do the crossover
	//child1 and child2 are two children of parent 1 and parent 2 after crossover
	private void crossover(	final Individual parent1, final Individual parent2, Individual child1, Individual child2) {
		//perform one-point crossover
		if (crossoverType==CROSSOVER_ONEPOINT)
		{
			crossoverOnePoint(parent1, parent2, child1, child2);
		}
		//perform two-points crossover
		else if (crossoverType==CROSSOVER_TWOPOINT)
		{
			crossoverTwoPoint(parent1, parent2, child1, child2);
		}
	}

	//one point crossover
	private void crossoverOnePoint(Individual parent1, Individual parent2, Individual child1, Individual child2)
	{
		int point1;
		int len=parent1.getLength();
		
		//randomly select one point in the chromosome
		point1=RandomSingleton.getInstance().nextInt(len);

		//copy parent1 and parent2 to child1 and child2
		child1.copyChromosome(parent1.getChromosome());
		child2.copyChromosome(parent2.getChromosome());
		
		//crossover the genes before the point1
		for (int i=0;i<=point1;i++)
		{
			child2.setGene(i,parent1.getGene(i));
			child1.setGene(i,parent2.getGene(i));
		}
	}

	//two points crossover
	private void crossoverTwoPoint(Individual parent1, Individual parent2, Individual child1, Individual child2)
	{
		int point1,point2,temp;
		int len=parent1.getLength();
		
		//randomly select two points in the chromosome and point1 != point2
		point1=RandomSingleton.getInstance().nextInt(len);
		do 
		{
			point2=RandomSingleton.getInstance().nextInt(len);
		}while (point1==point2);
		
		//point1 should be smaller than point2
		if(point1>point2)
		{
			temp=point1;
			point1=point2;
			point2=temp;
		}
		
		//copy parent1 and parent2 to child1 and child2
		child1.copyChromosome(parent1.getChromosome());
		child2.copyChromosome(parent2.getChromosome());

		//crossover the genes between two points
		for (int i=point1;i<=point2;i++)
		{
			child1.setGene(i,parent2.getGene(i));
			child2.setGene(i,parent1.getGene(i));
		}
	}

	//do mutation on the population, only mutationRate% individuals will perform mutation
	public void mutation()
	{
		for (int i=0;i<pop.length;i++)  {
			if( RandomSingleton.getInstance().nextDouble() < mutationRate) {
				doMutation(pop[i]);
    		}
    	}
	}
	
	//perform mutation on the individual
	private void doMutation(Individual individual)
	{
		//perform one-bit mutation
		if (mutationType==MUTATION_ONEBIT)
		{
			mutationOneBit(individual);
		}
		//perform swap mutation between two genes
		else if (mutationType==MUTATION_TWOSWAP)
		{
			mutationTwoSwap(individual);
		}
	}

	//perform one bit mutation on the individual
	//for example---before mutation 1(0); after mutation 0(1)
	private void mutationOneBit(Individual individual)
	{
		int point1,temp;
		int len=individual.getLength();
		
		//randomly select one points in the chromosome
		point1=RandomSingleton.getInstance().nextInt(len);

		//change 1 to 0 or change 0 to 1
		temp=1-Integer.parseInt((String)individual.getGene(point1)) ;
		individual.setGene(point1,Integer.toString(temp));
	}
	
	//perform two swap mutation on the individual
	//for example---before mutation :abcd; after mutation:dbca
	private void mutationTwoSwap(Individual individual)
	{
		int point1,point2;
		Object temp;
		int len=individual.getLength();
		
		//randomly select one points in the chromosome as long as point1 != point2
		point1=RandomSingleton.getInstance().nextInt(len);
		do 
		{
			point2=RandomSingleton.getInstance().nextInt(len);
		}while (point1==point2);
		
		//swap point1 and point2
		temp=individual.getGene(point1);
		individual.setGene(point1,individual.getGene(point2));
		individual.setGene(point2,temp);
	}

	//randomly select an individual from the population
	public Individual selectRandomIndividual()
	{
		int random = Math.abs(RandomSingleton.getInstance().nextInt());
		return pop[random % pop.length];
	}
}