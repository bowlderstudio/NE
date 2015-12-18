package domain;

import java.util.Properties;
import java.util.Vector;
import evolution.*;

public class OptFunction extends Environment {
	
	public static int ACKLEY=1;
	public static int ROSENBROCK=2;
	public static int RASTRIGIN=3;
	public static int SCHWEFEL=4;
	public static int TRID=5;
	public static int MICHALEWICS=6;
	public static int POWELL=7;
	public static int SCHWEFEL2=8;
	public static int BOOTH=9;
	
	private int dimension;
	private int geneType=Individual.BITS_GENE;
	private int popNumber;
	private int[] popLength;
	private double lowBound;
	private double upBound;
	public static int testFunction=SCHWEFEL;
	public static boolean rotation=false;
	
	public OptFunction(String propertyFile)
	{
		super(propertyFile);
		initializeFunction();
	}

	public void initializeFunction()
	{
		if (testFunction==ACKLEY)
		{
			dimension=20;
			lowBound=-30;//30
			upBound=30;
		}
		else if (testFunction==ROSENBROCK)
		{
			dimension=20;
			lowBound=-2.048;//30
			upBound=2.048;
		}
		else if (testFunction==RASTRIGIN)
		{
			dimension=20;
			lowBound=-5.12;
			upBound=5.12;
		}
		else if (testFunction==SCHWEFEL)
		{
			dimension=2;
			lowBound=-500;
			upBound=500;
		}
		else if (testFunction==TRID)
		{
			dimension=10;
			lowBound=-dimension*dimension;
			upBound=dimension*dimension;
		}
		else if (testFunction==BOOTH)
		{
			dimension=10;
			lowBound=-100;
			upBound=100;
		}
		else if (testFunction==MICHALEWICS)
		{
			dimension=10;
			lowBound=0;
			upBound=Math.PI;
		}
		else if (testFunction==POWELL)
		{
			dimension=12;
			lowBound=-4;
			upBound=4;
		}
		else if (testFunction==SCHWEFEL2)
		{}
	}
	
	private double[] convertVariables(Individual[] individual)
	{
		double[] variables=new double[dimension];
		String chromString="";
		String bitString;

		for (int i=0;i<individual.length;i++)
		{
			chromString=chromString.concat(individual[i].toString());
		}
		
		for (int i=0;i<dimension;i++)
		{
			bitString=chromString.substring(bitsLen*i,bitsLen*(i+1));
			//chromosome to string
			variables[i]=bitsToReal(bitString);
		}
		
		return variables;
	}
	
	private double[] convertVariables(Individual individual)
	{
		double[] variables=new double[dimension];
		String bitString;
		
		//chromosome to string
		bitString=individual.toString();
		
		for (int i=0;i<dimension;i++)
		{
			variables[i]=bitsToReal(bitString.substring(i*bitsLen, (i+1)*bitsLen));
		}
		
		return variables;
	}
	
	public double bitsToReal(String bits)
	{
		int integer=Integer.parseInt(bits, 2);
		return (double)integer/Math.pow(2, bitsLen)*(upBound-lowBound)+lowBound;
	}
	
	public double evalSolution(Individual[] individual)
	{
		double[] x=convertVariables(individual);
		if (rotation)
			x=rotation(x);
		
		if (testFunction==ACKLEY)
			return ackley(x);
		else if (testFunction==ROSENBROCK)
			return rosenbrock(x);
		else if (testFunction==RASTRIGIN)
			return rastrigin(x);
		else if (testFunction==SCHWEFEL)
			return schwefel(x);
		else if (testFunction==TRID)
			return trid(x);
		else if (testFunction==MICHALEWICS)
			return michalewics(x);
		else if (testFunction==POWELL)
			return powell(x);
		else if (testFunction==SCHWEFEL2)
			return schwefel2(x);
		else if (testFunction==BOOTH)
			return booth(x);
		else
			return 0;
	}
	
	public double evalSolution(Individual individual)
	{
		double[] x=convertVariables(individual);
		if (rotation)
			x=rotation(x);
		
		if (testFunction==ACKLEY)
			return ackley(x);
		else if (testFunction==ROSENBROCK)
			return rosenbrock(x);
		else if (testFunction==RASTRIGIN)
			return rastrigin(x);
		else if (testFunction==SCHWEFEL)
			return schwefel(x);
		else if (testFunction==TRID)
			return trid(x);
		else if (testFunction==MICHALEWICS)
			return michalewics(x);
		else if (testFunction==POWELL)
			return powell(x);
		else if (testFunction==SCHWEFEL2)
			return schwefel2(x);
		else if (testFunction==BOOTH)
			return booth(x);
		else
			return 0;
	}
	
	private double ackley(double[] x)
	{
		int a = 20; 
		double b = 0.2; 
		double c = 2*Math.PI;
		
		double s1 = 0, s2 = 0;
		for (int i=0;i<x.length;i++)
		{
		   s1 = s1+Math.pow(x[i], 2);
		   s2 = s2+Math.cos(c*x[i]);
		}
		return -a*Math.exp(-b*Math.sqrt(1.0/x.length*s1))-Math.exp(1.0/x.length*s2)+a+Math.exp(1);
	}

	private double rosenbrock(double[] x)
	{
		double sigma=0;
		for (int i=0;i<(x.length-1);i++)
		{
			sigma+=100*Math.pow(x[i+1]-Math.pow(x[i], 2), 2)+Math.pow(x[i]-1, 2);
		}
		return sigma;
	}
	
	private double booth(double[] x)
	{
		double sigma=0;
		for (int i=0;i<(x.length-1);i++)
		{
			sigma+=Math.pow(x[i]+2*x[i+1]-7,2)+Math.pow(2*x[i]+x[i+1]-5,2);
		}
		return sigma;
	}
	
	private double rastrigin(double[] x)
	{
		double sigma1=0;
		int A=3;
		
		for (int i=0;i<x.length;i++)
		{
			sigma1+=Math.pow(x[i], 2)-A*Math.cos(2*Math.PI*x[i]);
		}
		
		return x.length*A+sigma1;
	}
	
	private double schwefel(double[] x)
	{
		double sigma1=0;
		for (int i=0;i<x.length;i++)
		{
			sigma1+=x[i]*Math.sin(Math.sqrt(Math.abs(x[i])));
		}
		return sigma1+418.9829*x.length;
	}
	
	private double schwefel2(double[] x)
	{
		double maximum=Math.abs(x[1]);
		double bias=-450;
		for (int i=1;i<x.length;i++)
		{
			if (maximum<Math.abs(x[i]))
				maximum=Math.abs(x[i]);
		}
		return maximum+bias;
	}
	
	private double trid(double[] x)
	{
		double sigma1=0, sigma2=0;

		for (int i=0;i<x.length;i++)
		{
			sigma1+=Math.pow(x[i]-1, 2);
		}
		
		for (int i=1;i<x.length;i++)
		{
			sigma2+=x[i]*x[i-1];
		}
		return sigma1-sigma2;
	}
	
	private double michalewics(double[] x)
	{
		double sum=0;
		int m=10;
		for (int i=0;i<x.length;i++)
		{
			sum+=Math.sin(x[i])*Math.pow(Math.sin(i*x[i]*x[i]/Math.PI),2*m);
		}
		return -sum;
	}
	
	private double powell(double[] x)
	{
		double sum=0;
		double part1=0,part2=0,part3=0,part4=0;
		for (int i=0;i<x.length/4;i++)
		{
			part1=Math.pow(x[i*4]+10*x[i*4+1],2);
			part2=Math.pow(x[i*4+2]-x[i*4+3],2)*5;
			part3=Math.pow(x[i*4+1]-x[i*4+2],4);
			part4=Math.pow(x[i*4]-x[i*4+3],4)*10;
			sum+=part1+part2+part3+part4;
		}
		return sum;
	}
	
	public double[] rotation(double[] x)
	{
		double[][] matrix=Matrix.multipleTransformation(x.length);
		int n=matrix.length;
		double[] rotatedX=new double[n];
		double sum=0;
		for (int i=0;i<n;i++)
		{
			sum=0;
			for (int j=0;j<n;j++)
			{
				sum+=matrix[i][j]*x[j];
			}
			rotatedX[i]=sum;
		}
		return rotatedX;
	}
	
	public Vector createFullSolution()
	{
		return new Vector(bitsLen*dimension);
	}
	
	public Vector createSubSolution(int i)
	{
		return new Vector(getSubChromoLength(i));
	}
	
	public double getMaxFitness()
	{
		return 0;
	}
	
	public int getPopulationNumber()
	{
		return this.popNumber;
	}
	
	public void randomCutPopulation(int n)
	{
		popLength=new int[n];
		int currentLen=0;
		int i;
		for (i=0;i<n-1;i++)
		{
			popLength[i]=bitsLen*(i+1)+randomInt(3)-currentLen;
			currentLen+=popLength[i];
		}
		popLength[i]=bitsLen*(i+1)-currentLen;
	}
	
	public int randomInt(int n)
	{
		return RandomSingleton.getInstance().nextInt(n*2+1)-n;
	}
	
	public void nextTask()
	{
		
	}

	@Override
	public int getDimension() {
		return dimension;
	}
}