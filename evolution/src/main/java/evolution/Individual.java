package evolution;

import java.util.*;

public class Individual implements Cloneable 
{
	public static final int BITS_GENE=0;
	public static final int INTEGER_GENE=1;
	public static final double REALNUMBER_GENE=2;
	
	private double coeff=0.3;
	private int length;
	private double fitness;
	double[] MOFitness;
	double[] SortedMOFitness;
	int frontLayer;
	private int tests;
	private Vector chromosome;
	private int geneType;

	public Individual(int type, int len)
	{
		geneType=type;
		length=len;
		initChromosome();
	}
	
	//construct a given population
	public Individual(Vector pop)
	{
		chromosome=pop;
	}
	
	public String createBits()
	{
		return String.valueOf(RandomSingleton.getInstance().nextInt(2));
	}
	
	public String createIntegers()
	{
		int difference=(int)(Config.geneUpBound-Config.geneLowBound);
		return String.valueOf(RandomSingleton.getInstance().nextInt(difference)+(int)Config.geneLowBound);
	}
	
	public String createDoubles()
	{
		double difference=Config.geneUpBound-Config.geneLowBound;
		return String.valueOf(RandomSingleton.getInstance().nextDouble()*difference+Config.geneLowBound);
	}
	
	public void initChromosome()
	{
		chromosome=new Vector(length);
		if (geneType==this.BITS_GENE)
		{
			for (int i=0;i<length;i++)
			{
				chromosome.addElement(createBits());
			}
		}
		else if (geneType==this.INTEGER_GENE)
		{
			for (int i=0;i<length;i++)
			{
				chromosome.addElement(createIntegers());
			}
		}
		else if (geneType==this.REALNUMBER_GENE)
		{
			for (int i=0;i<length;i++)
			{
				chromosome.addElement(createDoubles());
			}
		}
	}
	
	//return the chromosome of population
	public Vector getChromosome()
	{
		return chromosome;
	}
	
	//return the chromosome of population
	public void setChromosome(Vector ch)
	{
		chromosome=ch;
	}
	
	//set the ith gene of a chromosome
	public void setGene(int i,Object value)
	{
		chromosome.setElementAt(value,i);
	}
	
	//get the index of gene in a chromosome
	public int indexOf(Object value)
	{
		return chromosome.indexOf(value);
	}
	
	//add the ith gene of a chromosome
	public void addGene(int index,Object value)
	{
		chromosome.add(index,value);
	}
	
	//get the ith gene of a chromosome
	public Object getGene(int i)
	{
		return chromosome.elementAt(i);
	}
	
	//Removes from this List all of the elements whose index is between fromIndex to toIndex
	public void removeGene(int fromIndex, int toIndex)
	{
		for (int i=toIndex;i>=fromIndex;i--)
		{
			chromosome.removeElementAt(i);
		}
	}
	
	//Tests if the specified object is a component in this vector
	public boolean containsGene(Object obj)
	{
		return chromosome.contains(obj);
	}
		
	//return the length of a chrmosome
	public int getLength()
	{
		return chromosome.size();
	}
	
	//set the fitness of population
	public void setFitness(double f)
	{
		fitness = f;
	}
	
	//get the fitness of population
	public double getFitness()
	{
		return fitness;
	}
	
	public void resetFitness()
	{
		fitness=0;
	}
	
	public void setMOFitnessSize(int fitnessSize)
	{
		MOFitness=new double[fitnessSize];
		SortedMOFitness=new double[fitnessSize];
	}
	
	public void setMOFitness(int index, double value)
	{
		MOFitness[index]=value;
	}
	
	public double[] getMOFitness()
	{
		return MOFitness;
	}
	
	public double[] getSortedMOFitness()
	{
		return SortedMOFitness;
	}
	
	public void setFrontLayer(int layer)
	{
		frontLayer=layer;
	}
	
	public int getFrontLayer()
	{
		return frontLayer;
	}
	
	public void sortMOFitness()
	{
		SortedMOFitness=(double[])MOFitness.clone();
		Arrays.sort(SortedMOFitness);			
	}
	
	public void copyMOFitness()
	{
		SortedMOFitness=(double[])MOFitness.clone();
	}
	
	public void setTestsTime(int t)
	{
		tests=t;
	}
	
	public int getTestsTime()
	{
		return tests;
	}
	
	public void resetTestsTime()
	{
		tests=0;
	}
	
	public int getGeneType()
	{
		return geneType;
	}
	
	public String toString()
	{
		String strChromosome="";
		for (int i=0;i<chromosome.size();i++)
		{
			strChromosome=strChromosome.concat((String)chromosome.elementAt(i)+"");
		}

		return strChromosome;
	}
	
	//clone the population object
	public Object clone() 
	{ 
		try 
		{
			Individual p=(Individual)super.clone();
			p.chromosome=(Vector)chromosome.clone();
			if (MOFitness!=null)
				p.MOFitness=(double[])MOFitness.clone();
			if (SortedMOFitness!=null)
			p.SortedMOFitness=(double[])SortedMOFitness.clone();
			return p; 
		}
		catch (CloneNotSupportedException e) 
		{
			throw new InternalError("Clone error!!!");
		}
	}

	public void copyChromosome(Vector chrom)
	{
		for (int i=0;i<chromosome.size();i++)
		{
			chromosome.setElementAt(chrom.elementAt(i), i);
		}
	}
	
	public void perturb( Individual n ) {
		if (geneType==BITS_GENE)
			perturbBITS( n, coeff );
		else if (geneType==INTEGER_GENE)
			perturbINTEGER( n, coeff );
		else if (geneType==REALNUMBER_GENE)
			perturbREALNUMBER( n, coeff );
	}
	
	public void perturbBITS(Individual n, double coeff) {
		for(int i=0;i<chromosome.size();i++) 
		{
			if (RandomSingleton.getInstance().nextDouble()<coeff)
				chromosome.setElementAt(String.valueOf((1-Integer.parseInt((String)n.chromosome.elementAt(i)))), i);
			else
				chromosome.setElementAt(n.chromosome.elementAt(i), i);
		}
	}
	
	public void perturbINTEGER(Individual n, double coeff){
		
	}
	
	public void perturbREALNUMBER(Individual n, double coeff){
		
	}

}
