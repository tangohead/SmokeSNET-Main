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
	
	//count metrics
	private double pcSmokes;
	private double pcGivingUp;

	
	NeighborMetrics(HashSet<NeighborStore> neighborhood)
	{
		double influenceSum = 0;
		for(NeighborStore ns : neighborhood)
		{
			if(ns.getNeighbor().isSmoker())
				infIsSmokerVal += ns.getRelativeInfluence() * 1;
			else
				infIsSmokerVal += ns.getRelativeInfluence() * -1;
			infWillpower +=  ns.getRelativeInfluence() * ns.getNeighbor().getWillpower();
			infHealth += ns.getRelativeInfluence() * ns.getNeighbor().getHealth();
			infCigPerDay += ns.getRelativeInfluence() * ns.getNeighbor().getSmokedPerDay();
			
			influenceSum += ns.getRelativeInfluence();
			
			if(ns.getNeighbor().isSmoker())
				pcSmokes++;
			if(ns.getNeighbor().isGivingUp())
				pcGivingUp++;
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
			infCigPerDay /= influenceSum;
			
			pcSmokes /= neighborhood.size();
			pcGivingUp /= neighborhood.size();
		}
		
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
