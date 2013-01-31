package graphtools.io.converters;

import agents.BaseHuman;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

public class GraphFormat {
	
	/**
	 * Takes a BaseHuman network and spits out a JUNG DirectedSparseMultigraph of Node IDs against Influence
	 * @param network
	 */
	public static DirectedSparseMultigraph<BaseHuman, Double> RepastToJUNG(Network network) {
		
		DirectedSparseMultigraph<BaseHuman, Double> jnet = new DirectedSparseMultigraph<BaseHuman, Double>();
		
		Iterable<BaseHuman> nIter = network.getNodes();
		for(BaseHuman bh : nIter)
		{
			jnet.addVertex(bh);
		}
		
		Iterable<RepastEdge<BaseHuman>> eIter = network.getEdges();
		for(RepastEdge<BaseHuman> re : eIter)
		{
			Pair<BaseHuman> couple = new Pair<BaseHuman>(re.getSource(), re.getTarget());
			
			jnet.addEdge(re.getWeight(), couple);
		}
		System.out.println("Repast Network has " + network.size() + " nodes and degree " + network.getDegree());
		System.out.println("JUNG Graph has " + jnet.getVertexCount() + " nodes");
		
		return jnet;
		
	}

}
