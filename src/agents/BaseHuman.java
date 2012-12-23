package agents;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

import graphtools.generators.*;

public class BaseHuman{
	String id;
	private Context<?> context;
	private Network<BaseHuman> network;
	boolean smoker;
	
	/**
	 * Constructor for scale free networks
	 * @param id
	 * @param context
	 * @param network
	 * @param generateOnAddition
	 * @param reciprocate
	 * @param reciprocateChance
	 */
	public BaseHuman(String id, Context<?> context, Network<BaseHuman> network, boolean generateOnAddition, boolean reciprocate, double reciprocateChance)
	{
		this.id = id;
		this.context = context;
		this.network = network;
		this.smoker = false;
		//Insert code for adding to network
		ScaleFree.addToRSF(this, context, network, reciprocate, reciprocateChance);
	}
	
	/**
	 * Constructor for small world networks
	 */
	public BaseHuman(String id, Context<?> context)
	{
		this.id = id;
		this.context = context;
		this.smoker = false;
	}
	
	/**
	 * Sets the network of the node. Should only be used after a small world generation
	 * @param network
	 */
	public void setNetwork(Network<BaseHuman> network)
	{
		
	}
	
	
	@ScheduledMethod(start = 1, interval = 100)
	public void step() {
		//insert decisions, etc
		//decide actions
		//reconfigure networks
		
	}

	public String getID() {
		return id;
	}
	
	public HashMap<String, Object> generateAttrList(HashMap<String, String> keyMap)
	{
		HashMap<String, Object> rtnList = new HashMap<String, Object>();
		rtnList.put(keyMap.get("smoker"), this.smoker);
		
		return rtnList;
	}
	


	
}
