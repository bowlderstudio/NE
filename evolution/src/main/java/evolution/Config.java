//package evolution;
//
//public class Config {
//	
//	public static final int RANDOMSELECTION = 0;
//	public static final int BESTSELECTION = 1;
//	public static final int MIXEDSELECTION = 2;
//	
//	public static final int PARETOSORING = 0;
//	public static final int GREEDYSORTING = 1;
//	public static final int DISTRIBUTIONSORTING = 2;
//	
//	public static final double geneLowBound=-5;
//	public static final double geneUpBound=5;
//	// parameters for all evaluation models
//	//=====================================================================
//	public static int MAX_GENERATION=100;
//	public static int MAX_EVALUATIONS=500000;
//	public static int POP_SIZE=100;
//	public static int NUM_BREED=30;
//	public static int NUM_NEWBLOOD=60;//POP_SIZE/2;
//	public static int MUT_TYPE=Population.MUTATION_ONEBIT;
//	public static int MATE_TYPE=Population.CROSSOVER_TWOPOINT;
//	public static double MUT_RATE=(double)0.05;
//
//	public static int POP_ELITE=10;
//	public static double POP_ELITE_RATE=0.2;
//
//	public static boolean MIN = true;
//	public static boolean GROUP_REPRODUCTION = false;//if false then do reproduction after every generation
////	public static double MAX_FITNESS=100000;
//	public static int NUM_STAGNATION = 50;			// the length of the performance queue.
//
//	// parameters for evolution model
//	public static int EVOLUTION_TYPE = Evolution.STANDARDEVOLUTION;
//
///**********parameters for coevolutionary model**************/
//	// parameters for evaluation model
////	public static int EVALUATION_TYPE = Evolution.STANDARDCOEVOLUTION;
//	public static int SELECTION_TYPE = BESTSELECTION;
//	
//	public static int SORTING_TYPE = GREEDYSORTING;
//	// parameters for n-trial model
//	public static int NUM_TRIALS=2000;
//	
//	// parameters for archive model
//	public static int ARCHIVE_SIZE=5;
//
//	//random cut the whole chromosome with fix length each
//	public static boolean RANDOM_POP=false;
//	
//	//random cut the whole chromosome with fix number of population
//	public static boolean RANDOM_CUT=false;
//}
