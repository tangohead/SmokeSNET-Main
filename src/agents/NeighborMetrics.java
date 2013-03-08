package agents;

import java.util.HashSet;

import probtools.Distributions;
import probtools.NDParams;
import repast.simphony.space.graph.RepastEdge;

public class NeighborMetrics {
	//influence metrics
	private double infWillpower;
	private double infHealth;
	private double infCigPerDay;
	private double infIsSmokerVal;
	private boolean infIsSmoker;
	
	private double infPcSmokes;
	private double infPcGivingUp;
	
	public double getInfPcSmokes() {
		return infPcSmokes;
	}

	public double getInfPcGivingUp() {
		return infPcGivingUp;
	}

	//count metrics
	private double pcSmokes;
	private double pcGivingUp;
	
	private double avgCigPerDay;

	
	NeighborMetrics(HashSet<NeighborStore> neighborhood)
	{
		double influenceSum = 0;
		double smokerInfluenceSum = 0, nonSmokerInfluenceSum = 0;
		int numSmokers = 0;
		double numInfSmokers = 0, numInfGiveUp = 0;
		for(NeighborStore ns : neighborhood)
		{
			if(ns.getNeighbor().isSmoker())
				infIsSmokerVal += ns.getRelativeInfluence() * 1;
			else
				infIsSmokerVal += ns.getRelativeInfluence() * -1;
			infWillpower +=  ns.getRelativeInfluence() * ns.getNeighbor().getWillpower();
			infHealth += ns.getRelativeInfluence() * ns.getNeighbor().getHealth();
			infCigPerDay += ns.getRelativeInfluence() * ns.getNeighbor().getSmokedPerDay();
			
			//do +1*inf
			
			influenceSum += ns.getRelativeInfluence();
			
			if(ns.getNeighbor().isSmoker())
			{
				numSmokers++;
				pcSmokes++;
				avgCigPerDay += ns.getNeighbor().getSmokedPerDay();
				smokerInfluenceSum += ns.getRelativeInfluence();
			}
			else
				nonSmokerInfluenceSum += ns.getRelativeInfluence();
			if(ns.getNeighbor().isGivingUp())
			{
				pcGivingUp++;
				numInfGiveUp += ns.getRelativeInfluence();
			}
		}
		
		if(neighborhood.size() > 0)
		{
			//calcIdealIsSmoker /= localNeighborhood.size();
			infIsSmokerVal /= influenceSum;
			if(infIsSmokerVal < 0)
				infIsSmoker = false;
			else
				infIsSmoker = true;
			
			infWillpower /= influenceSum;
			infHealth /= influenceSum;
			
			infCigPerDay /= smokerInfluenceSum;
			
			avgCigPerDay /= numSmokers;
			
			pcSmokes /= neighborhood.size();
			pcGivingUp /= neighborhood.size();
			
			infPcGivingUp = numInfGiveUp / influenceSum;
			infPcSmokes = smokerInfluenceSum / influenceSum; 
//			System.out.println("-------------------");
//			System.out.println("0 " + smokerInfluenceSum + " " + influenceSum + " " + infPcSmokes);
//			System.out.println("1 " + numInfGiveUp + " " + influenceSum + " " + infPcGivingUp);
//			System.out.println("-------------------");

		
		}
		
	}

	public double getAvgCigPerDay() {
		return avgCigPerDay;
	}

	public double getPcSmokes() {
		return pcSmokes;
	}

	public double getPcGivingUp() {
		return pcGivingUp;
	}

	public double getInfWillpower() {
		return infWillpower;
	}

	public double getInfHealth() {
		return infHealth;
	}

	public double getInfCigPerDay() {
		return infCigPerDay;
	}

	public double getInfIsSmokerVal() {
		return infIsSmokerVal;
	}

	public boolean isInfIsSmoker() {
		return infIsSmoker;
	}
}
