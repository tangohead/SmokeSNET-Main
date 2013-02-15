package builders;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.record.formula.functions.T;

import probtools.Distributions;
import probtools.NDParams;
import agents.BaseHuman;
import agents.Watchman;

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
import graphtools.io.exporters.CSVExporter;
import graphtools.io.importers.GraphMLImporter;
import graphtools.samplers.GeneralTools;
public class ModelDevBuilder implements ContextBuilder<Object> {

	@Override
	public Context build(Context<Object> context) {
		// TODO Auto-generated method stub
		context.setId("SmokeSNET-Main");
		Network<BaseHuman> network = null;
		
		boolean generateOnAddition = true;
		int mode = 2;
		if(mode == 1)
		{
			NDParams nd = new NDParams(0.8, 0.4, 0, 1);
			network = ScaleFree.createRSF(context, "TestNet", 4, 500, true, 0.8, true, nd);
			RepastSummary rs = GeneralTools.trimNodesByDegree(context, network, 0);
			context = rs.getContext();
			network = rs.getNetwork();
		}
		else if (mode == 2)
		{
			//network = SmallWorld.generateRSW(context, "TestNet", 100, 0.5, 5);
			RepastSummary rs = GraphMLImporter.GraphMLToRepast("v0.8-testbase.graphml", "TestNet", context);
			context = rs.getContext();
			network = rs.getNetwork();
			
//			Iterable<RepastEdge<BaseHuman>> sdf = network.getEdges();
//			Iterator<RepastEdge<BaseHuman>> asda = sdf.iterator();
//			while(asda.hasNext())
//				System.out.println(asda.next().getWeight());
		}
		else if(mode == 3)
		{
			RepastSummary rs = GraphMLImporter.GraphMLToRepast("basetest.graphml", "TestNet", context);
			context = rs.getContext();
			network = rs.getNetwork();
			NDParams nd = new NDParams(0.8, 0.4, 0, 1);
			network = ScaleFree.SFOnBase(context, network, 500, true, 0.5, true, nd);
			rs = GeneralTools.trimNodesByDegree(context, network, 0);
			context = rs.getContext();
			network = rs.getNetwork();
		}
		Watchman watch = new Watchman(10, true, true, true, "v0.9", context, network);
		context.add(watch);
		
		//CSVExporter.exportToCSV(network.getNodes(), "test");
		//GraphConverter.repastNetworkToGraphML(context, net, "sample-"+ System.currentTimeMillis() +"-SW.graphml");
		
		return context;
	}
	
	

}
