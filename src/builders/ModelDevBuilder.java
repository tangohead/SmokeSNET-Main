package builders;

import java.util.ArrayList;

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

public class ModelDevBuilder implements ContextBuilder<Object> {

	@Override
	public Context build(Context<Object> context) {
		// TODO Auto-generated method stub
		context.setId("NetworkDevelopment");
		NetworkBuilder<BaseHuman> builder = new NetworkBuilder("TestNet", context, true);
		Network<BaseHuman> net = builder.buildNetwork();

		
		/*NetworkGenerator gen = new WattsBetaSmallWorldGenerator(0.2, 2, false);
		
		builder.setGenerator(gen);
		Network<Node> net = builder.buildNetwork();
		System.out.println(net.size());
		AnalysisTools.repastNetworkToGraphML(context, net, "sample-"+ System.currentTimeMillis() +"-SW.graphml");*/
		
		return context;
	}
	
	

}
