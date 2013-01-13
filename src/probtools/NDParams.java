package probtools;

public class NDParams {
	boolean hasLimits;
	double mean, stdDev;
	double upper, lower;
	
	NDParams(double mean, double stdDev)
	{
		this.hasLimits = false;
		this.mean = mean;
		this.stdDev = stdDev;
	}
	
	public NDParams(double mean, double stdDev, double lower, double upper)
	{
		//System.out.println("We be constructin");
		this.hasLimits = true;
		this.mean = mean;
		this.stdDev = stdDev;
		this.lower = lower;
		this.upper = upper;
	}

	public boolean hasLimits() {
		return hasLimits;
	}

	public double getMean() {
		return mean;
	}

	public double getStdDev() {
		return stdDev;
	}

	public double getUpper() {
		return upper;
	}

	public double getLower() {
		return lower;
	}
}
