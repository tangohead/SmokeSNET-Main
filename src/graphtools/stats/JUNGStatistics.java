package graphtools.stats;

import java.util.ArrayList;
import java.util.HashMap;
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
		for(BaseHuman bh : list)
		{
			avg += clusterCoeff.get(bh.getID());
			if(clusterCoeff.get(bh.getID()) > 0.5)
				highCCs.add(bh);
			System.out.println("Node " + bh.getID() + ": " + clusterCoeff.get(bh.getID()));
			nodeCount++;
		}
		avg = avg / nodeCount;
		System.out.println(avg);
		
		gs = new GraphStats(avg, highCCs);
		return gs;
	}
}
