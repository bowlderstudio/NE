package domain;

import java.util.Properties;

import evolution.*;

public class EvolutionaryApplication
{
	int evolutionType;
	String propertyFile;
	
	public EvolutionaryApplication(String propertyFile) {
		this.propertyFile=propertyFile;
		Properties p = Utils.loadProperties(propertyFile);
		if (p == null)
			System.exit(1);
		
		evolutionType=Integer.parseInt(p.getProperty("evolution_type","0"));
		
	}
	
	public void start() {
		//TODO should be fetched from property file
		Environment environment=new OptFunction();
		Evolution evolution=null;
		switch (evolutionType) {
			case Evolution.STANDARDEVOLUTION:
				evolution= new StandardEvolution(environment,propertyFile);
				break;
			case Evolution.STANDARDCOEVOLUTION:
				evolution=new Coevolution(environment,propertyFile);
				break;
		}
		evolution.evolve();
	}
	
	public static void main(String[] args) {
		EvolutionaryApplication evolutionaryApplication=new EvolutionaryApplication(args[0]);
		evolutionaryApplication.start();
		//Environment environment=evolutionaryApplication.getEnvironment("domain.OptFunction");
	}

	@SuppressWarnings("unchecked")
	public Environment getEnvironment(String environmentClassName) {
		Object obj=null;
		try {
			Class classTemp = Class.forName(environmentClassName);

	        obj =classTemp.newInstance();
			return (Environment)obj;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return (Environment)obj;
	}
}

