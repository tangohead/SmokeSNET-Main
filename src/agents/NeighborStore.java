package agents;

import java.util.HashMap;

import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;

public class NeighborStore{
	BaseHuman neighbor;
	public BaseHuman getNeighbor() {
		return neighbor;
	}

	public double getRelativeInfluence() {
		return relativeInfluence;
	}

	double relativeInfluence;
	
	public NeighborStore(BaseHuman neighbor, double relativeInfluence) {
		this.neighbor = neighbor;
		this.relativeInfluence = relativeInfluence;
	}
	
	
}
