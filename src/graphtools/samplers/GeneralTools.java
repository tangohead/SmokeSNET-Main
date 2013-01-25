package graphtools.samplers;

import graphtools.io.converters.RepastSummary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import agents.BaseHuman;
import agents.NeighborStore;
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
	
	public static HashSet<NeighborStore> getUniqueWithinHops(Context context, Network<BaseHuman> network, BaseHuman start, int numHops, boolean outHops)
	{
		HashSet<NeighborStore> localNeighborhood = getWithinHops(context, network, start, numHops, outHops);
		
		//We want to return the links of highest influence, possibly, so
		//go through, remove the others
		HashSet<BaseHuman> neighborList = new HashSet<BaseHuman>();
		Iterator<NeighborStore> iter = localNeighborhood.iterator();
		HashMap<String, NeighborStore> rtnMap = new HashMap<String, NeighborStore>();
		
		//get a list of unique BHs
		while(iter.hasNext())
		{
			NeighborStore ns = iter.next();
			if(rtnMap.containsKey(ns.getNeighbor().getID()))
			{
				//check val
				//System.out.println("Key found!");
				NeighborStore tmp = rtnMap.get(ns.getNeighbor().getID());
				if(ns.getRelativeInfluence() > tmp.getRelativeInfluence())
					rtnMap.put(ns.getNeighbor().getID(), ns);
			}
			else 
			{
				rtnMap.put(ns.getNeighbor().getID(), ns);
			}
		}
		
		//put in hashmap by id, compare on inf
		HashSet<NeighborStore> rtnStore = new HashSet<NeighborStore>();
		Collection vals = rtnMap.values();
		
		Iterator<NeighborStore> rtnIter = vals.iterator();
		while(rtnIter.hasNext())
			rtnStore.add(rtnIter.next());
		return rtnStore; 
	}
	
	/**
	 * Note, may return duplicate BaseHumans
	 * @param context
	 * @param network
	 * @param start
	 * @param numHops
	 * @param outHops
	 * @return
	 */
	public static HashSet<NeighborStore> getWithinHops(Context context, Network<BaseHuman> network, BaseHuman start, int numHops, boolean outHops)
	{
//		Iterable<RepastEdge<BaseHuman>> edges;
//		if(outHops)
//			edges = network.getOutEdges(start);
//		else
//			edges = network.getInEdges(start);
//		
		NeighborStore ns = new NeighborStore(start, 1);
		HashSet<NeighborStore> localNeighborhood = hopper(context, network, ns, 0, numHops, outHops);
		
		if(localNeighborhood.contains(start))
			localNeighborhood.remove(start); 

		
		return localNeighborhood;
	}
	
	private static HashSet<NeighborStore> hopper(Context context, Network<BaseHuman> network, NeighborStore start, int currentHops, int maxHops, boolean outHops)
	{
		if(currentHops < maxHops)
		{
			Iterable<RepastEdge<BaseHuman>> edges;
			HashSet<NeighborStore> localNeighborhood = new HashSet<NeighborStore>();
			if(outHops)
				edges = network.getOutEdges(start.getNeighbor());
			else
				edges = network.getInEdges(start.getNeighbor());
			
			Iterator<RepastEdge<BaseHuman>> iter = edges.iterator();
			while(iter.hasNext())
			{
				RepastEdge<BaseHuman> currEdge = iter.next();
				if(currEdge.getSource() == start.getNeighbor())
					localNeighborhood.add(new NeighborStore(currEdge.getTarget(), start.getRelativeInfluence() * currEdge.getWeight()));
				else
					localNeighborhood.add(new NeighborStore(currEdge.getSource(), start.getRelativeInfluence() * currEdge.getWeight()));
				
			}
			HashSet<NeighborStore> currNeighborhood = new HashSet<NeighborStore>();
			currNeighborhood.addAll(localNeighborhood);
			for(NeighborStore b : currNeighborhood)
			{
				localNeighborhood.addAll(hopper(context, network, b, currentHops + 1, maxHops, outHops));
			}

			return localNeighborhood;
		}
		else
		{
			HashSet<NeighborStore> rtn = new HashSet<NeighborStore>();
			rtn.add(start);
			return rtn;
		}	
	}

}
