package probtools;

import java.util.Random;

public class Distributions {
	public static double getNormalDistNum(double mean, double stdDev)
	{
		Random r = new Random(System.currentTimeMillis());
		return (r.nextGaussian() + mean) * stdDev;
	}
}
