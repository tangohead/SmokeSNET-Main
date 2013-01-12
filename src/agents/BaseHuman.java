package agents;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

import org.apache.commons.math.distribution.NormalDistributionImpl;

import graphtools.generators.*;
import graphtools.io.graphML.XMLAttribute;
import graphtools.io.graphML.XMLNodeKey;
import graphtools.samplers.GeneralTools;

public class BaseHuman implements Comparable{
	String id;
	private Context<?> context;
	private Network<BaseHuman> network;
	
	boolean isSmoker;
	double willpower;
	double health;
	int smokedPerDay;
	
	
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
		this.isSmoker = false;
		//this.willpower = new NormalDistributionImpl(0.7, 0.2).cumulativeProbability(arg0);
		this.willpower = Math.random();
		this.health = Math.random();
		if(isSmoker)
			this.smokedPerDay = (int)Math.random() * 20;
		else
			this.smokedPerDay = 0;
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
		this.isSmoker = false;
	}
	
	/**
	 * Constructor for graphML importer
	 */
	public BaseHuman(Context context, String id, HashMap<String, String> attrList, HashMap<String, String> keyMap)
	{
		this.id = id;
		this.context = context;
		String keyID = "";
		
		keyID = keyMap.get("smoker");
		isSmoker = Boolean.parseBoolean(attrList.get(keyID));
	}
	
	/**
	 * Sets the network of the node. Should only be used after a small world generation/GraphML
	 * importing
	 * @param network
	 */
	public void setNetwork(Network<BaseHuman> network)
	{
		this.network = network;
	}
	
	
	@ScheduledMethod(start = 1, interval = 100)
	public void step() {
		//insert decisions, etc
		//decide actions
		
		
		//reconfigure networks
		//we probabably want to get nodes within a local network (2 or 3 hops)
		//to see if anyone random is worth picking.
		HashSet<BaseHuman> localNeighborhood = GeneralTools.getWithinHops(context, network, this, 3, true);
		System.out.println(id + " has " + localNeighborhood.size() + " nodes within 3 hops.");
		boolean idealIsSmoker;
		double calcIdealIsSmoker;
		double idealWillpower, idealHealth;
		int idealSmokedPerDay;
		
		for(BaseHuman bh : localNeighborhood)
		{
			
		}
		
	}

	public String getID() {
		return id;
	}
	
	public HashMap<String, Object> generateAttrList(HashMap<String, String> keyMap)
	{
		HashMap<String, Object> rtnList = new HashMap<String, Object>();
		rtnList.put(keyMap.get("smoker"), this.isSmoker);
		
		return rtnList;
	}


	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		BaseHuman b0 = (BaseHuman) arg0;
		
		return this.getID().compareTo(b0.getID());
	}
	



	
}
