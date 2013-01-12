package graphtools.samplers;

import graphtools.io.converters.RepastSummary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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
	
	public static HashSet<BaseHuman> getWithinHops(Context context, Network<BaseHuman> network, BaseHuman start, int numHops, boolean outHops)
	{
//		Iterable<RepastEdge<BaseHuman>> edges;
//		if(outHops)
//			edges = network.getOutEdges(start);
//		else
//			edges = network.getInEdges(start);
//		
		HashSet<BaseHuman> localNeighborhood = hopper(context, network, start, 0, numHops, outHops);
		
		if(localNeighborhood.contains(start))
			localNeighborhood.remove(start); 
		
		return localNeighborhood;
	}
	
	private static HashSet<BaseHuman> hopper(Context context, Network<BaseHuman> network, BaseHuman start, int currentHops, int maxHops, boolean outHops)
	{
		if(currentHops < maxHops)
		{
			Iterable<RepastEdge<BaseHuman>> edges;
			HashSet<BaseHuman> localNeighborhood = new HashSet<BaseHuman>();
			if(outHops)
				edges = network.getOutEdges(start);
			else
				edges = network.getInEdges(start);
			
			Iterator<RepastEdge<BaseHuman>> iter = edges.iterator();
			while(iter.hasNext())
			{
				RepastEdge<BaseHuman> currEdge = iter.next();
				if(currEdge.getSource() == start)
					localNeighborhood.add(currEdge.getTarget());
				else
					localNeighborhood.add(currEdge.getSource());
				
			}
			HashSet<BaseHuman> currNeighborhood = new HashSet<BaseHuman>();
			currNeighborhood.addAll(localNeighborhood);
			for(BaseHuman b : currNeighborhood)
			{
				localNeighborhood.addAll(hopper(context, network, b, currentHops + 1, maxHops, outHops));
			}
			
//			HashSet<BaseHuman> rtnList = new ArrayList<BaseHuman>();
//			rtnList.addAll(localNeighborhood);
//			String tabs = "";
//			for(int k = 0; k < currentHops; k++)
//			{
//				tabs += "\t";
//			}
//			if(localNeighborhood.size() > 100);
//			{
//				String printString = "";
//				for(BaseHuman b : localNeighborhood)
//				{
//					printString += b.getID() + ", ";
//				}
//				
//				System.out.println(tabs + "PRE-TRIM: AT HOP " + currentHops + " FOR " + start.getID() + ": " + printString);
//			}
//			for(int i = 0; i < localNeighborhood.size(); i++)
//			{
//				ArrayList<Integer> removalList = new ArrayList<Integer>();
//				for(int j = 0; j < rtnList.size(); j++)
//				{
//					if(rtnList.get(j).getID().compareTo(localNeighborhood.get(i).getID()) == 0)
//						removalList.add(j);
//				}
//				String printString = "";
//				for(Integer p : removalList)
//				{
//					printString += p + ", ";
//				}
//				System.out.println(tabs + "Removing " + removalList.size() + " items: " + printString);
//				for(Integer j : removalList)
//					System.out.println(rtnList.remove(j.intValue()));
//			}
//			if(localNeighborhood.size() > 100);
//			{
//				String printString = "";
//				for(BaseHuman b : rtnList)
//				{
//					printString += b.getID() + ", ";
//				}
//				System.out.println(tabs + "POST-TRIM: AT HOP " + currentHops + " FOR " + start.getID() + ": " + printString);
//			}
			return localNeighborhood;
		}
		else
		{
			HashSet<BaseHuman> rtn = new HashSet<BaseHuman>();
			rtn.add(start);
			return rtn;
		}	
	}
}
