package evolution;

import java.util.Arrays;
import java.util.Comparator;

//////////////////////////////////////////////////////////////////////
//   Min Shi
//
//
//////////////////////////////////////////////////////////////////////

public class Population{
	
	public final static int CROSSOVER_ONEPOINT=0;
	public final static int CROSSOVER_TWOPOINT=1;

	public final static int MUTATION_ONEBIT=11;
	public final static int MUTATION_TWOSWAP=12;
	public final static int MUTATION_MULTIBIT=13;
	public final static int MUTATION_INVERSE=14;
	
	Individual[] pop; 
	int nonDonimationNum;
	private int numBreed=Config.NUM_BREED;  //number of neurons to be mated in SubPopulation
	private double mutationRate=Config.MUT_RATE;
	private int mutationType=Config.MUT_TYPE;
	private int crossoverType=Config.MATE_TYPE;
	private int elite=Config.POP_ELITE;
	private double eliteRate=Config.POP_ELITE_RATE;
	
	public Population(int size)
	{
		pop=new Individual[size];
	}
	
	public void initializePopulation(int geneType, int len)
	{
		for (int i=0;i<pop.length;i++)
		{
			pop[i]=new Individual(geneType,len);
		}
	}
	
	public void evaluationReset()
	{
		for (int i=0;i<pop.length;i++)
		{
			pop[i].resetFitness();
			pop[i].resetTestsTime();
		}
	}
	
	public int getPopulationSize() {
		return pop.length;
	}
	
	public Individual getIndividual( int index )
	{
		return pop[index];
	}
	
	public void setIndividual(int index, Individual individual)
	{
		pop[index]=individual;
	}
	
	public Individual[] getPopulation()
	{
		return pop;
	}
	
	public void setNonDominationNum(int num)
	{
		nonDonimationNum=num;
	}
	
	public int getNonDominationNum()
	{
		return nonDonimationNum;
	}
	
	public void deltify(Individual individual)
	{
		for(int i= 0; i < pop.length; ++i) {
			pop[i].perturb(individual);
		}
	}

//----------------------------------------------------------------------
// recombine neurons with members of their subpop using crossover.
	public void recombination() {
		for (int i = 0; i < numBreed; ++i) {
			int mate = findMate(i);
    		crossover(pop[i], pop[mate],pop[pop.length-(1+i*2)],pop[pop.length-(2+i*2)]);
	    }
	}
	
	public void paretoRecombine()
	{
		int index=0;
		int secondFront, parent1, parent2;
		
		//set non-domination number
		while (index<(pop.length-1) && pop[index++].getFrontLayer()==0);
		setNonDominationNum(index-1);
		
		index=0;
		//all the individuals in the front set are copied into the new generation
		while (index<(Config.POP_SIZE-Config.NUM_NEWBLOOD)
				&& pop[index++].getFrontLayer()==0);
		secondFront=index;
		index=Config.POP_SIZE-Config.NUM_NEWBLOOD;
		//while (pop[index++].getFrontLayer()==0);
		//secondFront=index;
		secondFront=secondFront<numBreed?numBreed:secondFront;
		
		if ((pop.length-index)%2!=0)
			index++;
		//System.out.println("index="+index+",	secondFront="+secondFront);
		while (index<pop.length)
		{
			parent1=RandomSingleton.getInstance().nextInt(secondFront);
			do
			{
				parent2=RandomSingleton.getInstance().nextInt(secondFront);
			}while (parent1==parent2);

			crossover(pop[parent1], pop[parent2],pop[index], pop[index+1]);
			index+=2;
		}
	}
	
//----------------------------------------------------------------------
// randomly find a mate in the same subpop.
	private int findMate(int num) {
		/*
		if( num == 0 ) {
    		return (Math.abs(RandomSingleton.getInstance().nextInt())%numBreed);
		} 
		else {
    		return (Math.abs(RandomSingleton.getInstance().nextInt())%num);
    	}*/
		int mate;
		do
		{
			mate=Math.abs(RandomSingleton.getInstance().nextInt())%numBreed;
		}while (mate==num);
		return mate;
	}
	
//----------------------------------------------------------------------
// do crossover
	private void crossover(	final Individual parent1, final Individual parent2, Individual child1, Individual child2) {
		if (crossoverType==CROSSOVER_ONEPOINT)
		{
			crossoverOnePoint(parent1, parent2, child1, child2);
		}
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
		
		//randomly select two points in the chromosome
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
		
		//copy father and mother to child1 and child2
		child1.copyChromosome(parent1.getChromosome());
		child2.copyChromosome(parent2.getChromosome());

		//crossover the genes between two points
		for (int i=point1;i<=point2;i++)
		{
			child1.setGene(i,parent2.getGene(i));
			child2.setGene(i,parent1.getGene(i));
		}
	}

//----------------------------------------------------------------------
// do mutation
	public void mutation()
	{
		for (int i=0;i<pop.length;i++)  {
			if( RandomSingleton.getInstance().nextDouble() < mutationRate) {
				doMutation(pop[i]);
    		}
    	}
	}
	
	private void doMutation(Individual individual)
	{
		if (mutationType==MUTATION_ONEBIT)
		{
			mutationOneBit(individual);
		}
		else if (mutationType==MUTATION_TWOSWAP)
		{
			mutationTwoSwap(individual);
		}
		else if (mutationType==MUTATION_MULTIBIT)
		{
			mutationMultiBit(individual);
		}
		else if (mutationType==MUTATION_INVERSE)
		{
			mutationInverse(individual);
		}
	}

	//one bit mutation before 1(0) after 0(1)
	private void mutationOneBit(Individual individual)
	{
		int point1,temp;
		int len=individual.getLength();
		
		//randomly select one points in the chromosome
		point1=RandomSingleton.getInstance().nextInt(len);

		temp=1-Integer.parseInt((String)individual.getGene(point1)) ;
		individual.setGene(point1,Integer.toString(temp));
	}
	
	//two swap mutation before:abcd after:dbca
	private void mutationTwoSwap(Individual individual)
	{
		int point1,point2;
		Object temp;
		int len=individual.getLength();
		
		//randomly select one points in the chromosome
		point1=RandomSingleton.getInstance().nextInt(len);
		do 
		{
			point2=RandomSingleton.getInstance().nextInt(len);
		}while (point1==point2);
		
		temp=individual.getGene(point1);
		individual.setGene(point1,individual.getGene(point2));
		individual.setGene(point2,temp);
	}
	
	//do multi bits mutation before:10110 after:01001
	private void mutationMultiBit(Individual individual)
	{
		int point1,point2,temp;
		int len=individual.getLength();
		
		//randomly select one points in the chromosome
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

		for (int i=point1;i<=point2;i++)
		{
			temp=Integer.parseInt((String)individual.getGene(i));
			temp=1-temp;
			individual.setGene(i,Integer.toString(temp));
		}
	}
	
	//inverse mutation before:abcd after:dcba
	private void mutationInverse(Individual individual)
	{
		int point1,point2,tempPoint;
		Object temp;
		int len=individual.getLength();
		
		//randomly select one points in the chromosome
		point1=RandomSingleton.getInstance().nextInt(len);
		do 
		{
			point2=RandomSingleton.getInstance().nextInt(len);
		}while (point1==point2);
		//point1 should be smaller than point2
		if(point1>point2)
		{
			tempPoint=point1;
			point1=point2;
			point2=tempPoint;
		}

		//inverse the points
		for (int i=0;i<(point2-point1)/2+1;i++)
		{
			temp=individual.getGene(point1+i);
			individual.setGene(point1+i,individual.getGene(point2-i));
			individual.setGene(point2-i,temp);
		}
	}
//----------------------------------------------------------------------
// sort the neurons in each subpop using quicksort.
	private static final Comparator minimize_fit = new MinimizeFit();
	private static final Comparator maximize_fit = new MaximizeFit();
	private static final Comparator paretominimize_fit = new ParetoMinimizeFit();
	private static final Comparator paretomaximize_fit = new ParetoMaximizeFit();
	
	public void qsort() {
		if( Config.MIN ) {
			Arrays.sort( pop, minimize_fit );
		} else {
			Arrays.sort( pop, maximize_fit );
    	}
	}
	
	public void paretoSort(int start,int end)
	{
		if( Config.MIN ) {
			Arrays.sort( pop, start, end, paretominimize_fit );
		} else {
			Arrays.sort( pop, start, end, paretomaximize_fit );
    	}
	}
	
	//sort fitness according to the indexTH fitness
	public void fitnessSort(int index)
	{
		double temp=0;
		if (index !=0)
		{
			//move the ith fitness value to the first one, because sorting algorithm sorts pop according to the first fitness value
			for (int i=0;i<pop.length;i++)
			{
				temp=pop[i].SortedMOFitness[0];
				pop[i].SortedMOFitness[0]=pop[i].SortedMOFitness[index];
				pop[i].SortedMOFitness[index]=temp;
			}
		}
		if( Config.MIN ) {
			Arrays.sort( pop, paretominimize_fit );
		} 
		else {
			Arrays.sort( pop, paretomaximize_fit );
    	}
	}
	
	public Individual selectBestIndividual(int bestIndex)
	{
		return pop[bestIndex];
	}
	
	public Individual selectRandomIndividual()
	{
		int random = Math.abs(RandomSingleton.getInstance().nextInt());
		return pop[random % pop.length];
	}
}