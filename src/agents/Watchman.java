package agents;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator;

import edu.uci.ics.jung.graph.Graph;
import graphtools.io.converters.GraphFormat;
import graphtools.io.exporters.CSVExporter;
import graphtools.io.exporters.GraphMLExporter;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.jung.statistics.RepastJungGraphStatistics;
import repast.simphony.space.graph.Network;

public class Watchman {

	long turnCount;
	long interval;
	String name;
	boolean generateGML;
	boolean dumpAttrs;
	Context context;
	Network network;
	String ext = ".graphml";
	
	public Watchman(int interval, boolean genGML, boolean dumpAttrs, String name, Context context, Network network)
	{
		this.turnCount = 0;
		this.interval = interval;
		this.name = name;
		this.generateGML = genGML;
		this.dumpAttrs = dumpAttrs;
		this.context = context;
		this.network = network;
		
		if(genGML)
			GraphMLExporter.repastNetworkToGraphML(context, network, name + "-step-0" + ext);
		if(dumpAttrs)
			CSVExporter.exportToCSV(network.getNodes(), name + "-step-0");
		GraphFormat.RepastToJUNG(network);
	}
	
	public void checkWatch()
	{
		turnCount++;
		
		//System.out.println("Tick");
		if(interval != 0)
		{
			if(turnCount % interval == 0)
			{
				runWatch(context, network);
			}
		}
	}
	
	private void runWatch(Context context, Network network)
	{
		if(generateGML)
		{
			GraphMLExporter.repastNetworkToGraphML(context, network, name + "-step-" + turnCount + ext);
		}
		if(dumpAttrs)
		{
			CSVExporter.exportToCSV(network.getNodes(), name + "-step-" + turnCount);
		}
		

	}
	
	@ScheduledMethod(start = 1, interval = 100)
	public void step() {
		checkWatch();
	}
	
}
