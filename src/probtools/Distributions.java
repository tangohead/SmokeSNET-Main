package probtools;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Distributions {
	//TEST THIS
	static Random r = new Random();
	
	public static void test(String filename, NDParams nd)
	{
		try {
			FileWriter fw = new FileWriter(filename);
			for(int i = 0; i < 1000;  i++)
				fw.write(getND(0, 1) + "\n");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static double getND(double mean, double stdDev)
	{
		return (r.nextGaussian() * stdDev) + mean;
	}
	
	public static double getNDWithLimits(double mean, double stdDev, double lower, double upper)
	{
		double ND = getND(mean, stdDev);
		while(ND < lower || ND > upper)
		{
			//System.out.println("ND was " + ND + " no good!" );
			ND = getND(mean, stdDev);
		}
		/*FileWriter fw;
		try {
			fw = new FileWriter("tst.txt", true);
			fw.write(ND + "\n");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		return ND;
	}
	
	public static int getIntNDWithLimits(double mean, double stdDev, double lower, double upper)
	{
		int ND = (int) Math.round(getND(mean, stdDev));
		while(ND < lower || ND > upper)
		{
			ND = (int) Math.round(getND(mean, stdDev));
		}
		return ND;
	}
	
	public static double getND(NDParams nd)
	{
		if(nd.hasLimits())
		{
			//System.out.println("We got limits!");
			return getNDWithLimits(nd.getMean(), nd.getStdDev(), nd.getLower(), nd.getUpper());
		}
		else
		{
			//System.out.println("No limits!");
			return getND(nd.getMean(), nd.getStdDev());
		}
	}
}
