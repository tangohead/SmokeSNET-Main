package graphtools.generators;

import java.util.Iterator;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.graph.NetworkGenerator;
import repast.simphony.context.space.graph.WattsBetaSmallWorldGenerator;
import repast.simphony.space.graph.Network;
import agents.BaseHuman;

public class SmallWorld {
	/**
	 * Constructs a directed small-world graph. Yes, that's not what the Watts & Strogatz Model is supposed to do, but oh well.
	 * @param context
	 * @param networkName
	 * @param numNodes
	 * @param SWBeta
	 * @param SWDegree
	 * @return
	 */
	public static Network generateRSW(Context context, String networkName, int numNodes, double SWBeta, int SWDegree)
	{
		//Add the nodes to the network
		for( int i = 0; i < numNodes ; i++) {
			context.add(new BaseHuman("n" + i, context));
		}
		NetworkBuilder<BaseHuman> builder = new NetworkBuilder(networkName, context, true);
		NetworkGenerator gen = new WattsBetaSmallWorldGenerator(0.2, 2, false);
		
		
		builder.setGenerator(gen);
		Network<BaseHuman> network = builder.buildNetwork();
		
		Iterable<BaseHuman> nodes = network.getNodes();
		Iterator<BaseHuman> iter = nodes.iterator();
		while(iter.hasNext())
		{
			iter.next().setNetwork(network);
		}
		
		return network;
	}
}
