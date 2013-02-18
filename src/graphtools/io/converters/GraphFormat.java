package graphtools.io.converters;

import java.util.LinkedList;

import agents.BaseHuman;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;
import graphtools.samplers.EdgeData;
import graphtools.samplers.GeneralTools;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

public class GraphFormat {
	
	/**
	 * Takes a BaseHuman network and spits out a JUNG DirectedSparseMultigraph of Node IDs against Influence
	 * @param network
	 */
	public static DirectedSparseMultigraph<String, EdgeData> RepastToJUNG(Network network) {
		
		DirectedSparseMultigraph<String, EdgeData> jnet = new DirectedSparseMultigraph<String, EdgeData>();
		
		LinkedList<BaseHuman> list = GeneralTools.getBaseHumans(network);
		for(BaseHuman bh : list)
		{
			jnet.addVertex(bh.getID());
		}
		
		Iterable<RepastEdge<BaseHuman>> eIter = network.getEdges();
		for(RepastEdge<BaseHuman> re : eIter)
		{
			Pair<String> couple = new Pair<String>(re.getSource().getID(), re.getTarget().getID());
			
			jnet.addEdge(new EdgeData(re.getSource().getID() + "-" + re.getTarget().getID(), re.getWeight()), couple);
		}
		//System.out.println("Repast Network has " + network.size() + " nodes and degree " + network.getDegree());
		//System.out.println("JUNG Graph has " + jnet.getVertexCount() + " nodes");
		
		return jnet;
		
	}

}
