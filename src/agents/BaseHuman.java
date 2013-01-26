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
	
	private boolean givingUp;
	private int stepsSinceGiveUp;
	
	private double influenceability;
	private double sociable;
	
	private int maxDegree = 10;
	private int cigLimit = 70;
	
	
	/*
	 * 
	 * ADD ATTRIBUTES INTO KEY MAP!!
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
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
		{
			this.isSmoker = false;
			//Some people are giving up
			if(Math.random() < 0.5)
				this.givingUp = false;
			else
			{
				this.givingUp = true;
				this.stepsSinceGiveUp = (int) Math.random() * 100;
			}
		}
		else
		{
			this.isSmoker = true;
			this.givingUp = false;
			this.stepsSinceGiveUp = 0;
		}
		
		
		
		
		
		this.willpower = Distributions.getNDWithLimits(0.5, 0.8, 0, 1);
		this.health = Distributions.getNDWithLimits(0.7, 0.2, 0, 1);
		this.sociable = Distributions.getNDWithLimits(0.6, 0.4, 0, 1);
		this.influenceability = Distributions.getNDWithLimits(0.5, 0.4, 0, 1);
		if(isSmoker)
			this.smokedPerDay = Distributions.getIntNDWithLimits(12, 0.8, 0, 40);
		else
			this.smokedPerDay = 0;
		//Insert code for adding to network
		if(generateOnAddition)
			ScaleFree.addToRSF(this, context, network, reciprocate, reciprocateChance, nd);
	}
	
	public boolean isGivingUp() {
		return givingUp;
	}

	public int getStepsSinceGiveUp() {
		return stepsSinceGiveUp;
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

		NeighborMetrics nm = new NeighborMetrics(localNeighborhood);
		
		double smoker = 0;
//		for(NeighborStore ns: localNeighborhood)
//		{
//			//We'll look for people with attributes similar to the ideal
//			BaseHuman candidate = ns.getNeighbor();
//			
//			//need to come up with some scoring model for the other person!
//			//should incorporate self and ideal person.
//			
//			if(candidate.isSmoker() == idealIsSmoker && ( health > candidate.getHealth() * 0.995 && health < candidate.getHealth() * 1.005))
//			{
//				//System.out.println("Node " + candidate.getID() + " is reasonably close to " + id + " with healths being " + health + " " + candidate.getHealth());
//				//check if they're being influenced already, if not, add edge
//				RepastEdge<BaseHuman> ed1 = network.getEdge(candidate, this);
//				
//				if(ed1 == null && Math.random() < sociable)
//				{
//					network.addEdge(candidate, this, Distributions.getND(new NDParams(0.7, 0.5, 0, 1)));
//				}
//			}
//			//also look for removals
//			else if(( health < candidate.getHealth() * 0.9 || health > candidate.getHealth() * 1.1))
//			{
//				RepastEdge<BaseHuman> ed1 = network.getEdge(candidate, this);
//				if(ed1 != null)
//					network.removeEdge(ed1);
//			}
//			
//			
//		}
		
		Iterable init = network.getNodes();
		ArrayList<BaseHuman> nodes = new ArrayList<BaseHuman>();
		for(Object o : init)
		{
			if(o instanceof BaseHuman)
				nodes.add((BaseHuman) o);
			
		}
		
		for(BaseHuman bh : nodes)
		{
			if(bh.isSmoker())
				smoker++;
		}
		if(network.size() > 0){}
			//System.out.println("Current smoker rate: " + (smoker / network.size()) * 100 + "%");
		
		decisionTree(nm);
		connectionAdjust(localNeighborhood, nm, network);
		if(this.isGivingUp())
			this.stepsSinceGiveUp++;
			
		
	}
	
	private void decisionTree(NeighborMetrics nm)
	{
		double yesProb;
		//am I a smoker?
		if(isSmoker)
		{
			//calculate the
			//should I stop?
			//low health == higher prob
			
			//I'm concerned about the smoker val - it's being mixed with probabilities
			yesProb = (1 - health) * /*nm.getInfIsSmokerVal() +*/ nm.getPcGivingUp();
			//yesProb = Math.abs(yesProb) / 2;
			//System.out.println(id + ": My stop probability is " + yesProb);
			if(Math.random() < yesProb)
			{
				isSmoker = false;
				//System.out.println(id + ": I gave up smoking!");
				
				this.givingUp = true;
				this.stepsSinceGiveUp = 0;
				
				//what effect does this have on my health?
				
			}
			else
			{
				//should I smoke more?
				if(!(this.smokedPerDay * 0.9 > nm.getInfCigPerDay() && this.smokedPerDay * 1.1 < nm.getInfCigPerDay()))
				{
					
					double zeroedChange = nm.getInfCigPerDay() - this.smokedPerDay;
					if(zeroedChange + this.smokedPerDay < cigLimit)
						this.smokedPerDay += zeroedChange * this.influenceability;
					//System.out.println(id + ": Changed to " + this.smokedPerDay + " cigs per day, with change " + zeroedChange);
				}
			}
			
		}
		else
		{
			//should I start?
			yesProb = health * /*nm.getInfIsSmokerVal() +*/ nm.getPcSmokes() * nm.getPcGivingUp();
			
			//yesProb = Math.abs(yesProb) / 3;
			//System.out.println(id + ": My start probability is " + yesProb);
			if(Math.random() < yesProb)
			{
				isSmoker = true;
				//System.out.println(id + ": I began up smoking!");
				//how many?
				if(nm.getInfCigPerDay() < 0)
					smokedPerDay = 5;
				else
				{
					smokedPerDay = (int) Math.round(nm.getInfCigPerDay());
					if(smokedPerDay < 0 || smokedPerDay > cigLimit)
						smokedPerDay = Distributions.getIntNDWithLimits(12, 0.5, 0, cigLimit);
				}
				//System.out.println("My influence val is " + nm.getInfCigPerDay());
			}
			else
			{
				//how should I give up?
			}
		}		
	}
	
	private void connectionAdjust(HashSet<NeighborStore> neighbors, NeighborMetrics nm, Network network)
	{
		//First see if we want to add anyone
		Iterator<NeighborStore> iter = neighbors.iterator();
		
		//We want to look for similar people or role models
		while(iter.hasNext())
		{
			NeighborStore other = iter.next();
			double score = scoreAgainst(other.getNeighbor());
			//System.out.println(id + ": My score against " + other.getNeighbor().getID() + " is " + score);
			if(score > 0.3)
			{
				if(network.getInDegree(this) >= maxDegree)
				{
					RepastEdge<BaseHuman> removalCandidate = getMinInfluence(network.getInEdges(this));
					//lower influence nodes more likely to be binned
					if(Math.random() < (1 - removalCandidate.getWeight()) && removalCandidate != null)
					{
						RepastEdge<BaseHuman> ed1 = network.getEdge(other.getNeighbor(), this);
						if(ed1 == null /*&& Math.random() < sociable*/)
						{
							double nd = Distributions.getND(new NDParams((score - 0.5)*2, 0.3, 0, 1));
							network.addEdge(other.getNeighbor(), this, nd);
							//System.out.println(id + ": I attached to " + other.getNeighbor().getID() + " with influence " + nd /*+ " and prob " + prob + " and points " + points*/);
							network.removeEdge(removalCandidate);
						}
					}
				}
				else
				{
					RepastEdge<BaseHuman> ed1 = network.getEdge(other.getNeighbor(), this);
					if(ed1 == null && Math.random() < sociable)
					{
						double nd = Distributions.getND(new NDParams((score - 0.5)*2, 0.3, 0, 1));
						network.addEdge(other.getNeighbor(), this, nd);
						//System.out.println(id + ": I attached to " + other.getNeighbor().getID() + " with influence " + nd /*+ " and prob " + prob + " and points " + points*/);
					}
				}
				
			}
			else
			{
				RepastEdge<BaseHuman> ed1 = network.getEdge(other.getNeighbor(), this);
				if(ed1 != null /*&& Math.random() < sociable*/)
				{
					network.removeEdge(ed1);
					//System.out.println(id + ": I detached from " + other.getNeighbor().getID() /*+ " with prob " + prob*/);
				}
			}
			//System.out.println(id + ": My score against " + other.getNeighbor().getID() + " is " + scoreAgainst(other.getNeighbor()));
		}
			
	}
	
	private RepastEdge<BaseHuman> getMinInfluence(Iterable<RepastEdge<BaseHuman>> edges)
	{
		Iterator<RepastEdge<BaseHuman>> edgesIterator = edges.iterator();
		RepastEdge<BaseHuman> min = null;
		
		while(edgesIterator.hasNext())
		{
			if(min == null)
				min = edgesIterator.next();
			else
			{
				RepastEdge<BaseHuman> current = edgesIterator.next();
				if(current.getWeight() < min.getWeight())
					min = current;
			}
		}
		return min;
	}
	
	public double scoreAgainst(BaseHuman other)
	{
		double score = 0;
		
		if(this.isSmoker() == other.isSmoker())
		{
			score++;
			if(this.isGivingUp() == true && this.isGivingUp() == other.isGivingUp())
				score =+ 2;
			
			if(this.isSmoker())
			{
				
				//double pctSPD = Math.abs((other.getSmokedPerDay() - this.getSmokedPerDay()))/this.getSmokedPerDay();
				double pctSPD = pctDiff(other.getSmokedPerDay(), this.getSmokedPerDay());
				if(pctSPD > 1)
					score = 0;
				else
					score = 5 - pctSPD * 5;
				
				//want to get % diff
				/*
				zeroedSPD = Math.abs(zeroedSPD / this.getSmokedPerDay());
				
				if(zeroedSPD == 0)
					score += 5;
				else
					score = (1/zeroedSPD) * 5;*/
				/*double l = 5 - pctDiff(other.getSmokedPerDay(), this.getSmokedPerDay()) * 5;
				if(l < 0)
					System.out.println("Zeroed SPD " + l);*/
			}
			
		}
		
		//double pctHealth = Math(other.);
		
		/*if(zeroedHealth == 0)
			score += 5;
		else*/
		double pctHealth = pctDiff(other.getHealth(), this.getHealth());
		if(pctHealth > 1)
			score = 0;
		else
			score = 5 - pctHealth * 5;
		//score = 5 - pctDiff(other.getHealth(), this.getHealth()) * 5;
		//System.out.println("Zeroed Health " + zeroedHealth + " " + (1/zeroedHealth) * 5);
		
		
		
		return score/13;
	}
	
	private double pctDiff(double num1, double num2)
	{
		return Math.abs(num1 - num2)/num2;
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
		rtnList.put(keyMap.get("isSmoker"), this.isSmoker);
		rtnList.put(keyMap.get("willpower"), this.willpower);
		rtnList.put(keyMap.get("health"), this.health);
		rtnList.put(keyMap.get("smokedPerDay"), this.smokedPerDay);
		rtnList.put(keyMap.get("givingUp"), this.givingUp);
		rtnList.put(keyMap.get("stepsSinceGiveUp"), this.stepsSinceGiveUp);
		rtnList.put(keyMap.get("influenceability"), this.influenceability);
		rtnList.put(keyMap.get("sociable"), this.sociable);

		
		return rtnList;
	}


	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		BaseHuman b0 = (BaseHuman) arg0;
		
		return this.getID().compareTo(b0.getID());
	}
	
/*
 * int points = 0;
			NeighborStore current = addIter.next();
			if(current.getNeighbor().isSmoker() == this.isSmoker)
			{
				if(this.isSmoker == false)
				{
					//If they're both giving up, call in giving up together effect
					if(this.isGivingUp() == current.getNeighbor().isGivingUp())
					{
						points += 5;
					}
					else 
					{
						points++;
					}
				}
				else
				{
					if(this.smokedPerDay * 0.9 > current.getNeighbor().getSmokedPerDay() && this.smokedPerDay * 1.1 < current.getNeighbor().getSmokedPerDay())
					{
						points += 5;
					}
					else
					{
						points++;
					}
				}
				
				if(this.health * 0.95 < current.getNeighbor().getHealth() && this.health * 1.05 > current.getNeighbor().getHealth())
				{
					points++;
				}
				else if(this.health * 0.9 > current.getNeighbor().getHealth() )
				{
					points--;
				}
				else 
				{
					points += 5;
				}
				
				
				
			}
			double prob = ((double)points) / 15;
			prob *= sociable;
			//System.out.println(id + ": My attatch prob to " + current.getNeighbor().getID() + " is " + prob + " and I have " + points + ".");
			if(Math.random() < prob)
			{
				RepastEdge<BaseHuman> ed1 = network.getEdge(current.getNeighbor(), this);
				if(ed1 == null && Math.random() < sociable)
				{
					double nd = Distributions.getND(new NDParams(prob, 0.3, 0, 1));
					network.addEdge(current.getNeighbor(), this, nd );
					System.out.println(id + ": I attached to " + current.getNeighbor().getID() + " with influence " + nd + " and prob " + prob + " and points " + points);
				}
			}
			
		}
		
		
		//Then see if we want to remove anyone
	
		Iterator<NeighborStore> remIter = neighbors.iterator();
		while(remIter.hasNext())
		{
			int points = 0;
			NeighborStore current = remIter.next();
			if(current.getNeighbor().isSmoker() != this.isSmoker)
			{
				if(this.isSmoker == false)
				{
					//If I am giving up and the other person isn't, then super-avoid
					if(this.isGivingUp() != current.getNeighbor().isGivingUp())
					{
						points += 2;
					}
					else 
					{
						points++;
					}
				}
			}
			else
			{
				if(this.smokedPerDay * 0.8 > current.getNeighbor().getSmokedPerDay() && this.smokedPerDay * 1.2 < current.getNeighbor().getSmokedPerDay())
				{
					points--;
				}
				else
				{
					points++;
				}
			}
				
			if(this.health * 0.9 < current.getNeighbor().getHealth() && this.health * 1.1 > current.getNeighbor().getHealth())
			{
				points--;
			}
			//not sure about this
			else if(this.health * 0.9 < current.getNeighbor().getHealth() )
			{
				points++;
			}
			else 
			{
				points -= 2;
			}
			
			double prob = ((double)points) / 5;
			prob *= sociable;
			System.out.println(id + ": My dettatch prob to " + current.getNeighbor().getID() + " is " + prob + " and I have " + points + ".");
			if(Math.random() < Math.abs(prob))
			{
				RepastEdge<BaseHuman> ed1 = network.getEdge(current.getNeighbor(), this);
				if(ed1 != null /*&& Math.random() < sociable)
				{
					network.removeEdge(ed1);
					System.out.println(id + ": I detached from " + current.getNeighbor().getID() + " with prob " + prob);
				}
			}
		}*/
 


	
}
