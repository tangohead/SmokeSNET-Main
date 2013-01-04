package graphtools.samplers;

import graphtools.io.converters.RepastSummary;

import java.util.ArrayList;
import java.util.Iterator;

import agents.BaseHuman;
import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

public class GeneralTools {
	/**
	 * Gets rid of all nodes with less than or equal to the degree threshold
	 * @param context
	 * @param network
	 * @param degreeThreshold the threshold below which to trim (inclusive)
	 * @return a trimmed network
	 */
	public static RepastSummary trimNodesByDegree(Context context, Network<BaseHuman> network, int degreeThreshold)
	{
		Iterable<BaseHuman> nodes = network.getNodes();
		Iterator<BaseHuman> iterator = nodes.iterator();
		ArrayList<BaseHuman> BHsToRemove = new ArrayList<BaseHuman>();
		RepastSummary rs = new RepastSummary();
		
		while(iterator.hasNext())
		{
			BaseHuman currObj = iterator.next();
			if(network.getDegree(currObj) <= degreeThreshold)
			{
				BHsToRemove.add(currObj);
			}
		}
		context.removeAll(BHsToRemove);
		rs.setContext(context);
		rs.setNetwork(network);
		return rs;
	}
	
	public static ArrayList<BaseHuman> getWithinHops(Context context, Network<BaseHuman> network, BaseHuman start, int numHops, boolean outHops)
	{
		Iterable<RepastEdge<BaseHuman>> edges;
		if(outHops)
			edges = network.getOutEdges(start);
		else
			edges = network.getInEdges(start);
		
		
		
		return new ArrayList<BaseHuman>();
	}
	
	private static ArrayList<BaseHuman> hopper(Context context, Network<BaseHuman> network, BaseHuman start, int currentHops, boolean outHops)
	{
		return null;
		
	}
}
