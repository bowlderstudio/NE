package evolution;

public class EnvironmentConfig {
	public enum SelectionType {BESTSELECTION, RANDOMSELECTION, MIXSELECTION}
	public enum CrossoverType {ONEPOINT, TWOPINTS}
	public enum MutationType {ONEBIT}
	
	public int maxGeneration=100;
	public int popSize=100;
	public int numberBreed=30;
	public int numNewBoold=60;//POP_SIZE/2;
	public int mutationType=Population.MUTATION_ONEBIT;
	public int crossoverType=Population.CROSSOVER_TWOPOINT;
	public double MUT_RATE=(double)0.05;

	public int POP_ELITE=10;
	public double POP_ELITE_RATE=0.2;
	
	private SelectionType selectionType;
	public static EnvironmentConfig loadDefaultConfig() {
		EnvironmentConfig config= new EnvironmentConfig();
		config.selectionType=SelectionType.BESTSELECTION;
		config.maxGeneration=100;
		config.popSize=100;
		public int NUM_BREED=30;
		public int NUM_NEWBLOOD=60;//POP_SIZE/2;
		public int MUT_TYPE=Population.MUTATION_ONEBIT;
		public int MATE_TYPE=Population.CROSSOVER_TWOPOINT;
		public double MUT_RATE=(double)0.05;
		return config;
	}
	
}
