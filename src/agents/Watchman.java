package agents;

import graphtools.io.exporters.GraphMLExporter;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;

public class Watchman {

	long turnCount;
	long interval;
	String name;
	boolean generateGML;
	Context context;
	Network network;
	String ext = ".graphml";
	
	public Watchman(int interval, boolean genGML, String name, Context context, Network network)
	{
		this.turnCount = 0;
		this.interval = interval;
		this.name = name;
		this.generateGML = genGML;
		this.context = context;
		this.network = network;
		
		if(genGML)
			GraphMLExporter.repastNetworkToGraphML(context, network, name + "-step-0" + ext);
	}
	
	public void checkWatch()
	{
		turnCount++;
		
		System.out.println("Tick");
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
	}
	
	@ScheduledMethod(start = 1, interval = 100)
	public void step() {
		checkWatch();
	}
	
}
