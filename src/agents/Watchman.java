package agents;
import edu.uci.ics.jung.algorithms.metrics.*;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import graphtools.io.converters.GraphFormat;
import graphtools.io.exporters.CSVExporter;
import graphtools.io.exporters.GraphMLExporter;
import graphtools.samplers.GeneralTools;
import graphtools.stats.GraphStats;
import graphtools.stats.JUNGStatistics;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.jung.statistics.RepastJungGraphStatistics;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

public class Watchman {

	long turnCount;
	long interval;
	String name;
	boolean generateGML;
	boolean dumpAttrs;
	boolean networkGuard;
	Context context;
	Network network;
	String ext = ".graphml";
	
	public Watchman(int interval, boolean genGML, boolean dumpAttrs, boolean networkGuard, String name, Context context, Network network)
	{
		this.turnCount = 0;
		this.interval = interval;
		this.name = name;
		this.generateGML = genGML;
		this.dumpAttrs = dumpAttrs;
		this.networkGuard = networkGuard;
		this.context = context;
		this.network = network;
		
		if(genGML)
			GraphMLExporter.repastNetworkToGraphML(context, network, name + "-step-0" + ext);
		if(dumpAttrs)
			CSVExporter.exportToCSV(network.getNodes(), name + "-step-0");
		
		//System.out.println("There are " + network.size() + " nodes in the graph.");
		LinkedList<BaseHuman> nodes = GeneralTools.getBaseHumans(network);
		//System.out.println("There are " + nodes.size() + " humans in the graph.");
		
		boolean success = (new File(name)).mkdirs();
		if(!success)
			System.out.println("[NOTICE]: Output directory cannot be created. Assuming it exists.");
		
		if(networkGuard)
		{
			DirectedSparseMultigraph graph = GraphFormat.RepastToJUNG(network);
			GraphStats gs = JUNGStatistics.GenerateStatistics(graph, GeneralTools.getBaseHumans(network));
			System.out.println("Avg Clustering Coefficient: " + gs.getAvgClusterCoeff());
			
			LinkedList<BaseHuman> l = GeneralTools.getBaseHumans(network);
			double smokeCount = 0;
			double giveUpCount = 0;
			double humanCount = 0;
			double avgWillpower = 0;
			double avgSPD = 0;
			for(BaseHuman h : l)
			{
				if(h.isSmoker())
					smokeCount++;
				if(h.isGivingUp())
					giveUpCount++;
				avgWillpower += h.getWillpower();
				avgSPD += h.getSmokedPerDay();
				humanCount++;
			}
			
			System.out.println("[STAT]: Number of humans is " + humanCount);
			System.out.println("[STAT]: Percentage of network that smokes is " + (smokeCount / humanCount) * 100 + "%");
			System.out.println("[STAT]: Percentage of network that are giving up is " + (giveUpCount / humanCount) * 100 + "%");
			System.out.println("[STAT]: Percentage of network that are NS/NGU is " + ((humanCount-giveUpCount-smokeCount) / humanCount) * 100 + "%");
			System.out.println("[STAT]: Average willpower " + (avgWillpower / humanCount));
			System.out.println("[STAT]: Average smoker per day " + (avgSPD / smokeCount));
		}
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
		System.out.println("********** WATCHMAN ***********");
		if(generateGML)
		{
			GraphMLExporter.repastNetworkToGraphML(context, network, name + "/" + name + "-step-" + turnCount + ext);
		}
		if(dumpAttrs)
		{
			CSVExporter.exportToCSV(network.getNodes(), name + "/" + name + "-step-" + turnCount);
		}
		if(networkGuard)
		{
			DirectedSparseMultigraph graph = GraphFormat.RepastToJUNG(network);
			GraphStats gs = JUNGStatistics.GenerateStatistics(graph, GeneralTools.getBaseHumans(network));
			System.out.println("Avg Clustering Coefficient: " + gs.getAvgClusterCoeff());
			if(gs.getAvgClusterCoeff() > 0.12)
			{
				for(int i = 0; i < (int)(network.size()/10); i++)
				{
					BaseHuman tmp = gs.getHighClusterNode();
					if(tmp != null)
					{
						Iterable<RepastEdge> iter = network.getEdges(tmp);
						for(RepastEdge e : iter)
						{
							if(Math.random() < 0.5)
							{
								System.out.println("Removing edge between " + ((BaseHuman)e.getSource()).getID() + " to " + ((BaseHuman)e.getTarget()).getID());
								network.removeEdge(e);
							}
						}
					}
					
					
				}
			}
			for(int i = 0; i < (int)(network.size()/10); i++)
			{
				BaseHuman lclTmp = gs.getLocalClusterNode();
				if(lclTmp != null)
				{
					Iterable<RepastEdge> iter = network.getEdges(lclTmp);
					for(RepastEdge e : iter)
					{
						if(Math.random() < 0.5)
						{
							System.out.println("Removing edge between " + ((BaseHuman)e.getSource()).getID() + " to " + ((BaseHuman)e.getTarget()).getID());
							network.removeEdge(e);
						}
					}
				}
			}
			
			LinkedList<BaseHuman> l = GeneralTools.getBaseHumans(network);
			double smokeCount = 0;
			double giveUpCount = 0;
			double humanCount = 0;
			for(BaseHuman h : l)
			{
				if(h.isSmoker())
					smokeCount++;
				if(h.isGivingUp())
					giveUpCount++;
				humanCount++;
			}
			
			System.out.println("[STAT]: Number of humans is " + humanCount);
			System.out.println("[STAT]: Percentage of network that smokes is " + (smokeCount / humanCount) * 100 + "%");
			System.out.println("[STAT]: Percentage of network that are giving up is " + (giveUpCount / humanCount) * 100 + "%");
			System.out.println("[STAT]: Percentage of network that are NS/NGU is " + ((humanCount-giveUpCount-smokeCount) / humanCount) * 100 + "%");
		}
		
		System.out.println("******** END WATCHMAN *********");

	}
	
	@ScheduledMethod(start = 1, interval = 100)
	public void step() {
		checkWatch();
	}
	
}
