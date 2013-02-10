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
	private int giveUpAttempts;
	
	private double influenceability;
	private double persuasiveness;
	private double sociable;
	
	
	
	//new attrs - leadership?
	
	private int maxInDegree = 10;
	private int maxOutDegree = 10;
	private int cigLimit = 70;
	
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
		
		this.giveUpAttempts = 0;
		
		this.willpower = Distributions.getNDWithLimits(0.5, 0.8, 0, 1);
		this.health = Distributions.getNDWithLimits(0.7, 0.2, 0, 1);
		this.sociable = Distributions.getNDWithLimits(0.6, 0.4, 0, 1);
		this.influenceability = Distributions.getNDWithLimits(0.5, 0.4, 0, 1);
		
		this.persuasiveness = Distributions.getNDWithLimits(0.5, 0.4, 0, 1);
		
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
		
		keyID = keyMap.get("isSmoker");
		this.isSmoker = Boolean.parseBoolean(attrList.get(keyID));
		
		keyID = keyMap.get("willpower");
		this.willpower = Double.parseDouble(attrList.get(keyID));
		
		keyID = keyMap.get("health");
		this.health = Double.parseDouble(attrList.get(keyID));
		
		keyID = keyMap.get("smokedPerDay");
		this.smokedPerDay = Integer.parseInt(attrList.get(keyID));
		
		keyID = keyMap.get("givingUp");
		this.givingUp = Boolean.parseBoolean(attrList.get(keyID));
		
		keyID = keyMap.get("stepsSinceGiveUp");
		this.stepsSinceGiveUp = Integer.parseInt(attrList.get(keyID));
		
		keyID = keyMap.get("influenceability");
		this.influenceability = Double.parseDouble(attrList.get(keyID));
		
		keyID = keyMap.get("persuasiveness");
		this.persuasiveness = Double.parseDouble(attrList.get(keyID));
		
		keyID = keyMap.get("sociable");
		this.sociable = Double.parseDouble(attrList.get(keyID));
		
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
		
		HashSet<NeighborStore> localNeighborhood = GeneralTools.getUniqueWithinHops(context, network, this, 2, true);
		
		NeighborMetrics nm = new NeighborMetrics(localNeighborhood);	
		if(decisionTree(nm))
			connectionAdjust(localNeighborhood, nm, network);
		if(this.isGivingUp())
			if(this.getStepsSinceGiveUp() < 50)
				this.stepsSinceGiveUp++;
			else
			{
				this.stepsSinceGiveUp = 0;
				this.givingUp = false;
			}
		
	}
	
	
	private boolean decisionTree(NeighborMetrics nm)
	{
		//System.out.println("\t -------- DECTREE --------");
		double yesProb;
		boolean changeMade = false;
		
		
		//basic attribute changes
		//persuasiveness depends on whether the person has a lot of collections and
		
		
		//am I a smoker?
		if(isSmoker)
		{
			//calculate the
			//should I stop?
			//low health == higher prob
			if(this.health - this.health * 0.01 > 0)
				this.health -= this.health * 0.01;
			
			yesProb = (1 - health) * this.willpower * nm.getPcGivingUp() *  nm.getPcSmokes();
			if(this.giveUpAttempts > 0)
				yesProb *= (1-this.giveUpAttempts)/25;
			
			
			print("Probability of stopping smoking is " + yesProb + ".");
			if(Math.random() < yesProb)
			{
				isSmoker = false;
				//System.out.println(id + ": I gave up smoking!");
				
				this.givingUp = true;
				this.stepsSinceGiveUp = 0;
				if(this.giveUpAttempts < 25)
					this.giveUpAttempts++;
				
				changeMade = true;
				
				double potentialWillpower = (1 - this.influenceability) * nm.getPcSmokes();
				
				if(potentialWillpower < 0 || potentialWillpower > 1)
				{
					double tmpWillpower;
					if(potentialWillpower > this.willpower)
					{
						tmpWillpower = this.willpower + potentialWillpower * 0.5;
						if(tmpWillpower < 1)
							this.willpower = tmpWillpower;
					}
					else
					{
						tmpWillpower = this.willpower - potentialWillpower * 0.5;
						if(tmpWillpower > 0)
							this.willpower = tmpWillpower;
					}
				}
				else
					this.willpower = potentialWillpower;
				//what effect does this have on my health?
				
			}
			else
			{
				//should I smoke more?
				if(!(this.smokedPerDay * 0.9 > nm.getInfCigPerDay() && this.smokedPerDay * 1.1 < nm.getInfCigPerDay()) &&
						this.health * 0.9 > nm.getInfHealth() && this.health * 1.1 < nm.getInfHealth())
				{
					changeMade = true;
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
			yesProb = health * nm.getPcSmokes() * nm.getPcGivingUp() * this.influenceability;
			
			if(this.giveUpAttempts > 0)
				yesProb *= (this.giveUpAttempts)/25;
			
			print("Probability of starting smoking is " + yesProb + ".");
			if(this.health + this.health * 0.001 < 1)
				this.health += this.health * 0.001;
			//yesProb = Math.abs(yesProb) / 3;
			//System.out.println(id + ": My start probability is " + yesProb);
			if(Math.random() < yesProb)
			{
				isSmoker = true;
				
				this.givingUp = false;
				this.stepsSinceGiveUp = 0;
				
				if(this.influenceability + (this.influenceability * 0.001) < 1)
					this.influenceability -= this.influenceability * 0.001;
				//System.out.println(id + ": I began up smoking!");
				changeMade = true;
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
				this.influenceability += this.influenceability * 0.001;
				
			}
		}
		return changeMade;
	}
	
	private void connectionAdjust(HashSet<NeighborStore> neighbors, NeighborMetrics nm, Network network)
	{
		//First see if we want to add anyone
		Iterator<NeighborStore> iter = neighbors.iterator();
		boolean canAdd = true;
		//We want to look for similar people or role models
		while(iter.hasNext())
		{
			NeighborStore other = iter.next();
			double score = scoreAgainst(other.getNeighbor());
			//System.out.println(id + ": My score against " + other.getNeighbor().getID() + " is " + score);
			if(score > 0.7)
			{
				if(network.getInDegree(this) >= maxInDegree )
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
							if(!canAdd)
								canAdd = true;
						}
					}
				}
				else if(canAdd)
				{
					//also check if the influencer has reached their maxOutDegree
					RepastEdge<BaseHuman> ed1 = network.getEdge(other.getNeighbor(), this);
					if(ed1 == null && !other.getNeighbor().equals(this) && Math.random() < sociable && network.getOutDegree(other.getNeighbor()) < maxOutDegree)
					{
						double nd = Distributions.getND(new NDParams((score - 0.5)*2, 0.3, 0, 1));
						network.addEdge(other.getNeighbor(), this, nd);
						canAdd = false;
						//System.out.println(id + ": I attached to " + other.getNeighbor().getID() + " with influence " + nd /*+ " and prob " + prob + " and points " + points*/);
					}
				}
				
			}
			else if(score < 0.3)
			{
				RepastEdge<BaseHuman> ed1 = network.getEdge(other.getNeighbor(), this);
				if(ed1 != null && Math.random() < sociable)
				{
					network.removeEdge(ed1);
				}
			}
		}
	}
	
	public double scoreAgainst(BaseHuman other)
	{
		double score = 0;
		
		if(this.isSmoker() == other.isSmoker())
		{
			score++;
			if(this.isGivingUp() == true && this.isGivingUp() == other.isGivingUp())
			{
				score =+ 2;
				if(this.getStepsSinceGiveUp() * 1.5 < other.getStepsSinceGiveUp())
					score += 5;
				else
					score += 2;
			}
			
						
			if(this.isSmoker())
			{
				double pctSPD = pctDiff(other.getSmokedPerDay(), this.getSmokedPerDay());
				if(pctSPD > 1)
					score = 0;
				else
					score += 5 - pctSPD * 5;
			}
		}

		double pctHealth = pctDiff(other.getHealth(), this.getHealth());
		if(pctHealth > 1)
			score += 0;
		else
			score += 5 - pctHealth * 5;

		//maybe change to make influenceable people stick with those who aren't influenced?
		score += this.influenceability * 3;
		score += this.persuasiveness * 5;
		
		double pctWill = pctDiff(other.willpower, this.willpower);
		if(pctWill > 1)
			score += 0;
		else
			score += 5 - pctWill * 5;
		
		double pctSoc = pctDiff(other.sociable, this.sociable);
		if(pctSoc > 1)
			score += 0;
		else
			score += 2 - pctSoc * 2;
		return score/33;
	}
	
	private void print(String msg)
	{
		System.out.println("[NODE " + this.id + "]: "+msg);
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
		rtnList.put(keyMap.get("persuasiveness"), this.persuasiveness);
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
		
}
