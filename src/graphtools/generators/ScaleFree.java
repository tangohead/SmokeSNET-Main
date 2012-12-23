package graphtools.generators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.space.graph.Network;
import repast.simphony.util.collections.IndexedIterable;
import agents.BaseHuman;


public class ScaleFree {
	
	/**
	 * Adds a BaseHuman class to a Repast Network
	 * @param humanInstance - the instance to be added
	 * @param context - Repast context
	 * @param network - Repast network
	 * @param reciprocate - whether to automatically reciprocate with the node that this instance connects to.
	 * @param reciprocateChance - the chance of an edge being reciprocated
	 */
	public static void addToRSF(BaseHuman humanInstance, Context<?> context, Network<BaseHuman> network, boolean reciprocate, double reciprocateChance)
	{
		//Add to scale-free network
		Iterable<BaseHuman> nodes = network.getNodes();

		ArrayList<ArrayList<BaseHuman>> newEdges = new ArrayList<ArrayList<BaseHuman>>();
		for(BaseHuman current : nodes)
		{
			double iDegree = network.getDegree(current);
			double jSumDegree = 0;
			for(BaseHuman n : network.getNodes())
			{
				jSumDegree += network.getDegree(n);
			}
			
			double prob = iDegree / jSumDegree;
			/*if(this.isSmoker() != current.isSmoker())
				prob = prob * 0.5;*/
			
			if(Math.random() <= prob)
			{
				ArrayList<BaseHuman> pair = new ArrayList<BaseHuman>();
				pair.add(humanInstance);
				pair.add(current);
				newEdges.add(pair);
				
				if(reciprocate && Math.random() <= reciprocateChance)
				{
					ArrayList<BaseHuman> rPair = new ArrayList<BaseHuman>();
					rPair.add(current);
					rPair.add(humanInstance);
					newEdges.add(rPair);
				}
			}
		}
		
		for(ArrayList<BaseHuman> pair : newEdges)
		{
			network.addEdge(pair.get(0), pair.get(1));
		}
	}
	
	/**
	 * 
	 * @param context
	 * @param networkName name of the network to be created
	 * @param startWith number of nodes to start the network with
	 * @param numNodes number of nodes to add to the network
	 * @param reciprocate whether to reciprocate network links (wrt reciprocate chance)
	 * @param reciprocateChance the chance of reciprocating an edge
	 * @return
	 */
	public static Network<BaseHuman> createRSF(Context<Object> context, String networkName, int startWith, int numNodes, boolean reciprocate, double reciprocateChance)
	{
		NetworkBuilder<BaseHuman> builder = new NetworkBuilder(networkName, context, true);
		Network<BaseHuman> network = builder.buildNetwork();
		
		//First up, we generate a random starting network
		//Add the initial nodes (must be more than 2)
		if(startWith < 2)
			startWith = 2;
		for(int i = 0; i < startWith; i++)
		{
			//note params don't matter since this is the base
			context.add(new BaseHuman("n" + i, context, network, false, false, 0));
		}

		IndexedIterable<Object> startNodes = context.getObjects(BaseHuman.class);
		
		//add random edges until everything has >=1 degree
		boolean acceptableDegree = false;
		while(!acceptableDegree)
		{
			//Generate src and dest until they're different
			int source = (int) Math.round(Math.random() * (startWith));
			int dest = (int) Math.round(Math.random() * (startWith));
			
			//Keep regenerating until they're different and within array bounds
			while(source == dest || (source <= 0 || source > startWith) || (dest <= 0 || dest > startWith))
			{
				source = (int) Math.round(Math.random() * (startWith));
				dest = (int) Math.round(Math.random() * startWith);
			}
			network.addEdge((BaseHuman)startNodes.get(source-1), (BaseHuman)startNodes.get(dest-1));
			
			//check for all degrees being >= 1
			Iterable<BaseHuman> nodes = network.getNodes();
			Iterator<BaseHuman> iter = nodes.iterator();
			int minDegree = 999;
			//get the min degree
			while(iter.hasNext())
			{
				int currDegree = network.getDegree(iter.next());
				if(currDegree < minDegree)
					minDegree = currDegree;
			}
			if(minDegree > 1)
				acceptableDegree = true;
		}
		
		//Now add the rest onto the base network
		for( int i = startWith; i < numNodes ; i ++) {
			context.add(new BaseHuman("n" + i, context, network, true, reciprocate, reciprocateChance));
		}
		
		return network;
	}
	
	
}