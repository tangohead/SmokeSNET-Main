package graphtools.samplers;

import java.util.ArrayList;
import java.util.Iterator;

import agents.BaseHuman;
import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;

public class GeneralTools {
	/**
	 * Gets rid of all nodes with less than or equal to the degree threshold
	 * @param context
	 * @param network
	 * @param degreeThreshold the threshold below which to trim (inclusive)
	 * @return a trimmed network
	 */
	public static Network trimNodesByDegree(Context context, Network network, int degreeThreshold)
	{
		Iterable<BaseHuman> nodes = network.getNodes();
		Iterator<BaseHuman> iterator = nodes.iterator();
		ArrayList<BaseHuman> BHsToRemove = new ArrayList<BaseHuman>();
		
		while(iterator.hasNext())
		{
			BaseHuman currObj = iterator.next();
			if(network.getDegree(currObj) <= degreeThreshold)
			{
				BHsToRemove.add(currObj);
			}
		}
		context.removeAll(BHsToRemove);
		return network;
	}
}
