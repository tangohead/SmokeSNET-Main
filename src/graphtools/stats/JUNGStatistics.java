package graphtools.stats;

import java.util.ArrayList;
import simconsts.SimConstants;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import agents.BaseHuman;
import edu.uci.ics.jung.algorithms.metrics.Metrics;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import graphtools.io.converters.GraphFormat;

public class JUNGStatistics {

	public static GraphStats GenerateStatistics(DirectedSparseMultigraph<String, Double> graph, LinkedList<BaseHuman> list)
	{
		GraphStats gs;
		//graph.
		
		HashMap<String, Double> clusterCoeff = (HashMap<String, Double>) Metrics.clusteringCoefficients(graph);
		
		double avg = 0;
		int nodeCount = 0;
		ArrayList<BaseHuman> highCCs = new ArrayList<BaseHuman>();
		ArrayList<BaseHuman> localHighCCs = new ArrayList<BaseHuman>();
		for(BaseHuman bh : list)
		{
			avg += clusterCoeff.get(bh.getID());
			if(clusterCoeff.get(bh.getID()) > 0.5)
				highCCs.add(bh);
			//System.out.println("Node " + bh.getID() + ": " + clusterCoeff.get(bh.getID()));
			nodeCount++;
		}
		avg = avg / nodeCount;
		System.out.println(avg);
		
		Collection c = graph.getVertices();
		Iterator iter = c.iterator();
		for(BaseHuman bh: list)
		{
			if(Math.random() < (c.size() * 0.1))
			{
				String node = bh.getID();
				double runningTotal = clusterCoeff.get(node);
				
				Collection<String> nbrs = graph.getNeighbors(node);
				Iterator innerIter = nbrs.iterator();
				while(innerIter.hasNext())
					runningTotal += clusterCoeff.get(innerIter.next());
				runningTotal = runningTotal / (graph.getNeighborCount(node) + 1);
				//System.out.println("[NODE "+ node + "]: Local Neighbour CC is " + runningTotal);
				if(runningTotal > SimConstants.LocalClusterCoefficientThreshold)
					localHighCCs.add(bh);
			}
		}
		
		gs = new GraphStats(avg, highCCs, localHighCCs);
		return gs;
	}
	
}
