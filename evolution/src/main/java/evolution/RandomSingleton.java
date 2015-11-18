package evolution;

import java.util.Random;

public class RandomSingleton {
	private static Random m_random = new Random();
	
	public RandomSingleton()
	{
		m_random.setSeed(System.currentTimeMillis());
	}
	
	public static Random getInstance() {
		return m_random;
	}
}

