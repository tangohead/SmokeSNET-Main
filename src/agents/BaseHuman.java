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

import probtools.Distributions;
import probtools.NDParams;

import graphtools.generators.*;
import graphtools.io.graphML.XMLAttribute;
import graphtools.io.graphML.XMLNodeKey;
import graphtools.samplers.GeneralTools;

public class BaseHuman implements Comparable{
	String id;
	private Context<?> context;
	private Network<BaseHuman> network;
	
	private boolean isSmoker;
	private double willpower;
	private double health;
	private int smokedPerDay;
	private boolean isChanging;
	
	private double influenceability;
	private double sociable;
	
	
	/**
	 * Constructor for scale free networks
	 * @param id
	 * @param context
	 * @param network
	 * @param generateOnAddition
	 * @param reciprocate
	 * @param reciprocateChance
	 */
	public BaseHuman(String id, Context<?> context, Network<BaseHuman> network, boolean generateOnAddition, boolean reciprocate, double reciprocateChance, NDParams nd)
	{
		this.id = id;
		this.context = context;
		this.network = network;
		if(Math.random() < 0.5)
			this.isSmoker = false;
		else
			this.isSmoker = true;
		if(Math.random() < 0.5)
			this.isChanging = false;
		else
			this.isChanging = true;
		this.willpower = Distributions.getNDWithLimits(0.5, 0.8, 0, 1);
		this.health = Distributions.getNDWithLimits(0.7, 0.2, 0, 1);
		this.sociable = Distributions.getNDWithLimits(0.6, 0.4, 0, 1);
		if(isSmoker)
			this.smokedPerDay = Distributions.getIntNDWithLimits(15, 0.8, 0, 40);
		else
			this.smokedPerDay = 0;
		//Insert code for adding to network
		if(generateOnAddition)
			ScaleFree.addToRSF(this, context, network, reciprocate, reciprocateChance, nd);
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
		HashSet<NeighborStore> localNeighborhood = GeneralTools.getUniqueWithinHops(context, network, this, 3, true);
		//System.out.println(id + " has " + localNeighborhood.size() + " nodes within 3 hops.");
		boolean idealIsSmoker = false;
		double calcIdealIsSmoker = 0;
		double idealWillpower = 0, idealHealth = 0;
		int idealSmokedPerDay = 0;
		
		double influenceSum = 0;
		
		for(NeighborStore ns : localNeighborhood)
		{
			//System.out.println("Influence for neighbor " + ns.getNeighbor().getID() + " is " + ns.getRelativeInfluence());
			if(ns.getNeighbor().isSmoker())
				calcIdealIsSmoker += ns.getRelativeInfluence() * 1;
			else
				calcIdealIsSmoker += ns.getRelativeInfluence() * -1;
			
			idealWillpower +=  ns.getRelativeInfluence() * ns.getNeighbor().getWillpower();
			idealHealth += ns.getRelativeInfluence() * ns.getNeighbor().getHealth();
			idealSmokedPerDay += ns.getRelativeInfluence() * ns.getNeighbor().getSmokedPerDay();
			
			influenceSum += ns.getRelativeInfluence();
			//System.out.println("\t" + ns.getNeighbor().getID());
		}
		
		if(localNeighborhood.size() > 0)
		{
			//calcIdealIsSmoker /= localNeighborhood.size();
			calcIdealIsSmoker /= influenceSum;
			if(calcIdealIsSmoker < 0)
				idealIsSmoker = false;
			else
				idealIsSmoker = true;
			//System.out.println("Node " + id + " has an ideal smoker value of " + calcIdealIsSmoker + " has a willpower of " + idealWillpower + ", health of " + idealHealth + " and smokes " + idealSmokedPerDay  );
			//idealWillpower /= localNeighborhood.size();
			//idealHealth /= localNeighborhood.size();
			//idealSmokedPerDay /= localNeighborhood.size();
			
			idealWillpower /= influenceSum;
			idealHealth /= influenceSum;
			idealSmokedPerDay /= influenceSum;
		}
		//System.out.println("Node " + id + " has " + isSmoker + " for smoker, willpower of " + willpower + " and health of " + health + " smoking " + smokedPerDay);
		//System.out.println("Node " + id + " has an ideal smoker value of " + calcIdealIsSmoker + " has a willpower of " + idealWillpower + ", health of " + idealHealth + " and smokes " + idealSmokedPerDay  );
		
		
		//if(idealIsSmoker != isSmoker && (health * 1.5 < idealHealth) && (Math.random() < willpower))
			//System.out.println("Node " + id + " says 'I'm changing!'");
		
		//basis for decision tree stuff is going here
		//System.out.println("I'm node " + id);
		double smoker = 0;
		for(NeighborStore ns: localNeighborhood)
		{
			//We'll look for people with attributes similar to the ideal
			BaseHuman candidate = ns.getNeighbor();
			
			//need to come up with some scoring model for the other person!
			//should incorporate self and ideal person.
			
			if(candidate.isSmoker() == idealIsSmoker && ( health > candidate.getHealth() * 0.995 && health < candidate.getHealth() * 1.005))
			{
				//System.out.println("Node " + candidate.getID() + " is reasonably close to " + id + " with healths being " + health + " " + candidate.getHealth());
				//check if they're being influenced already, if not, add edge
				RepastEdge<BaseHuman> ed1 = network.getEdge(candidate, this);
				
				if(ed1 == null && Math.random() < sociable)
				{
					network.addEdge(candidate, this, Distributions.getND(new NDParams(0.7, 0.5, 0, 1)));
				}
			}
			//also look for removals
			else if(( health < candidate.getHealth() * 0.9 || health > candidate.getHealth() * 1.1))
			{
				RepastEdge<BaseHuman> ed1 = network.getEdge(candidate, this);
				if(ed1 != null)
					network.removeEdge(ed1);
			}
			
			
		}
		
		for(BaseHuman bh : network.getNodes())
		{
			if(bh.isSmoker())
				smoker++;
		}
		if(network.size() > 0)
			System.out.println("Current smoker rate: " + (smoker / network.size()) * 100 + "%");
		
		//We now need to permute our own attributes relative to everyone else
		double permuteHealth = health - idealHealth;
		health = health + (0.1 * idealHealth);
		
		if(Math.random() < willpower)
		{
			isSmoker = idealIsSmoker;
			if(isSmoker)
				System.out.println("NODE " + id + ": I just became a smoker!");
			else
				System.out.println("NODE " + id + ": I just gave up smoking!");
		}
		
	}
	
	private void decisionTree()
	{
		
	}
	
	//Used to modify the scale free algorithm's connection probability
	public double compatProb(BaseHuman oth)
	{
		double prob = 1;
		
		if(isSmoker != oth.isSmoker())
			prob *= 0.5;
		
		 prob *= sociable;
		
		return prob;
	}

	public double getSociable() {
		return sociable;
	}

	public boolean isSmoker() {
		return isSmoker;
	}

	public void setSmoker(boolean isSmoker) {
		this.isSmoker = isSmoker;
	}

	public double getWillpower() {
		return willpower;
	}

	public void setWillpower(double willpower) {
		this.willpower = willpower;
	}

	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
	}

	public int getSmokedPerDay() {
		return smokedPerDay;
	}

	public void setSmokedPerDay(int smokedPerDay) {
		this.smokedPerDay = smokedPerDay;
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
