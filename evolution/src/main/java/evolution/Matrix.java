package evolution;

import java.util.*;

public class Matrix
{
	public Matrix()
	{
		
	}
	
	public static double[][] multiply(double[][] matrix1, double[][] matrix2)
	{
		double[][] matrix3=null;
		double sigma;
		if (matrix1[0].length!=matrix2.length)
		{
			System.out.println("The dimension of the two matrixes are not matched");
		}
		else
		{
			matrix3=new double[matrix1.length][matrix2[0].length];
			for (int i=0;i<matrix1.length;i++)
			{
				for (int j=0;j<matrix2[0].length;j++)
				{
					sigma=0;
					for (int k=0;k<matrix1[0].length;k++)
					{
						sigma+=matrix1[i][k]*matrix2[k][j];
					}
					matrix3[i][j]=sigma;
				}
			}
		}
		return matrix3;
	}
	
	public static double[][] identity(int n)
	{
		double[][] matrix=new double[n][n];
		for (int i=0;i<n;i++)
		{
			for (int j=0;j<n;j++)
			{
				if (i==j)
					matrix[i][j]=1;
				else
					matrix[i][j]=0;
			}
		}
		return matrix;
	}
	
	public static double[][] transformation(int i, int j, int n)
	{
		double[][] matrix=identity(n);
		double angle=(RandomSingleton.getInstance().nextDouble()-0.5)*(Math.PI/2);
		matrix[i][i]=Math.cos(angle);
		matrix[j][j]=Math.cos(angle);
		matrix[i][j]=Math.sin(angle);
		matrix[j][i]=-Math.sin(angle);
		return matrix;
	}
	
	public static double[][] multipleTransformation(int n)
	{
		double[][] matrix=identity(n);
		for (int i=0;i<n-1;i++)
		{
			matrix=multiply(matrix,transformation(0,i+1,n));
		}
		for (int i=0;i<n-2;i++)
		{
			matrix=multiply(matrix,transformation(i+1,n-1,n));
		}
		return matrix;
	}
	
	public static void main(String args[])
	{
		double[][] matrixA={{1,1},{2,0}};
		double[][] matrixB={{0,2,3},{1,1,2}};
		double[][] matrixC=multiply(matrixA,matrixB);
		double[][] matrixD=multipleTransformation(5);
		System.out.print(matrixD.toString());
	}
}