package evolution;

public class EvolutionRecord {
	private int generation;
	private double fitness;
	
	public EvolutionRecord(int generation, double fitness) {
		this.generation=generation;
		this.fitness=fitness;
	}
	public int getGeneration() {
		return generation;
	}
	public void setGeneration(int generation) {
		this.generation = generation;
	}
	public double getFitness() {
		return fitness;
	}
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
}
