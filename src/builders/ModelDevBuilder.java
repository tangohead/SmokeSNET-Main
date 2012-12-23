package builders;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.record.formula.functions.T;
import agents.BaseHuman;

import edu.uci.ics.jung.graph.Graph;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.graph.NetworkGenerator;
import repast.simphony.context.space.graph.WattsBetaSmallWorldGenerator;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.collections.IndexedIterable;

import graphtools.generators.ScaleFree;
import graphtools.generators.SmallWorld;
import graphtools.io.converters.*;
import graphtools.io.importers.GraphMLImporter;
import graphtools.samplers.GeneralTools;
public class ModelDevBuilder implements ContextBuilder<Object> {

	@Override
	public Context build(Context<Object> context) {
		// TODO Auto-generated method stub
		context.setId("SmokeSNET-Main");
		Network<BaseHuman> network;
		
		boolean generateOnAddition = false;
		if(generateOnAddition)
		{
			network = ScaleFree.createRSF(context, "TestNet", 4, 100, true, 0.8);
			network = GeneralTools.trimNodesByDegree(context, network, 0);
		}
		else
		{
			//network = SmallWorld.generateRSW(context, "TestNet", 100, 0.5, 5);
			RepastSummary rs = GraphMLImporter.GraphMLToRepast("sample-1356266483049-SW.graphml", "TestNet", context);
			context = rs.getContext();
			network = rs.getNetwork();
			
			Iterable<RepastEdge<BaseHuman>> sdf = network.getEdges();
			Iterator<RepastEdge<BaseHuman>> asda = sdf.iterator();
			while(asda.hasNext())
				System.out.println(asda.next().getWeight());
		}
		
		
		//GraphConverter.repastNetworkToGraphML(context, net, "sample-"+ System.currentTimeMillis() +"-SW.graphml");
		
		return context;
	}
	
	

}
