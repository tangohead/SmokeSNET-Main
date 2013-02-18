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
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.collections.IndexedIterable;
import simconsts.Operations;

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
	
	private double stress;
	
	private boolean givingUp;
	private int stepsSinceGiveUp;
	private int giveUpAttempts;
	
	private double influenceability;
	
	private double sociable;
	
	//new attrs - leadership?
	
	private int maxInDegree = 10;
	private int maxOutDegree = 10;
	private int cigLimit = 70;
	private int giveUpStepLimit = 50;
	
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
		
		this.stress = Distributions.getNDWithLimits(0.5, 0.4, 0, 1);
		
		//We need some heavy and light smokers, so add some in
		double rand = Math.random();
		if(rand < 0.2)
		{
			if(isSmoker)
				this.smokedPerDay = Distributions.getIntNDWithLimits(5, 0.8, 0, 40);
			else
				this.smokedPerDay = 0;
		}
		else if (rand > 0.8)
		{
			if(isSmoker)
				this.smokedPerDay = Distributions.getIntNDWithLimits(12, 0.8, 0, 40);
			else
				this.smokedPerDay = 0;
		}
		else
		{
			if(isSmoker)
				this.smokedPerDay = Distributions.getIntNDWithLimits(20, 0.8, 0, 40);
			else
				this.smokedPerDay = 0;
		}
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
	
	
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		//insert decisions, etc
		//decide actions
		
		
		//reconfigure networks
		//we probabably want to get nodes within a local network (2 or 3 hops)
		//to see if anyone random is worth picking.
		
		HashSet<NeighborStore> localNeighborhood = GeneralTools.getUniqueWithinHops(context, network, this, 2, true);
		
		NeighborMetrics nm = new NeighborMetrics(localNeighborhood);
		
		statusCall();
		decisionTree(nm);
		connectionAdjust(localNeighborhood, nm, network);
		if(this.isGivingUp())
			this.stepsSinceGiveUp++;

		
	}
	
	
	private boolean decisionTree(NeighborMetrics nm)
	{
		//Add bypass probability for decisions
		boolean changeMade = false;		
		
		print("Influence: H:" + nm.getInfHealth() + " W:" + nm.getInfWillpower() + " S:" + nm.getInfIsSmokerVal());
		
		if(this.isSmoker)
		{
			this.health = changeWithinBounds(this.health, 0, 1, (this.smokedPerDay/this.cigLimit)/10, Operations.SUBTRACT);
			if(this.smokedPerDay <= 10)
			{
				if(nm.getPcSmokes() > 0.3 || irrationalChoice())
				{
					giveUpSmoking();
					changeMade = true;
				}
				else
				{
					if(nm.getAvgCigPerDay() > this.smokedPerDay * 1.2 || irrationalChoice())
					{
						this.smokedPerDay = (int)Math.round(changeWithinBounds(this.smokedPerDay, 0, this.cigLimit, (nm.getInfCigPerDay()* 0.5), Operations.ADD));
					}
					else if(nm.getAvgCigPerDay() < this.smokedPerDay * 0.8 || irrationalChoice())
					{
						this.smokedPerDay = (int)Math.round(changeWithinBounds(this.smokedPerDay, 0, this.cigLimit, (nm.getInfCigPerDay()* 0.5), Operations.SUBTRACT));
					}
				}
			}
			else if(this.smokedPerDay > 10 && this.smokedPerDay < 20)
			{
				if(nm.getPcGivingUp() > 0.5 || irrationalChoice())
				{
					giveUpSmoking();
					changeMade = true;
				}
				else
				{
					if(nm.getAvgCigPerDay() > this.smokedPerDay * 1.2 )
					{
						this.smokedPerDay = (int)Math.round(changeWithinBounds(this.smokedPerDay, 0, this.cigLimit, (nm.getInfCigPerDay()* 0.5), Operations.ADD));
					}
					else if(nm.getAvgCigPerDay() < this.smokedPerDay * 0.8)
					{
						this.smokedPerDay = (int)Math.round(changeWithinBounds(this.smokedPerDay, 0, this.cigLimit, (nm.getInfCigPerDay()* 0.5), Operations.SUBTRACT));
					}
				}
			}
			else 
			{
				if(this.health < 0.5)
				{
					if(nm.getPcGivingUp() > 0.4 || irrationalChoice())
					{
						giveUpSmoking();
						changeMade = true;
					}
					else
					{
						if(nm.getAvgCigPerDay() > this.smokedPerDay * 1.2 )
						{
							this.smokedPerDay = (int)Math.round(changeWithinBounds(this.smokedPerDay, 0, this.cigLimit, (nm.getInfCigPerDay()* 0.5), Operations.ADD));
						}
						else if(nm.getAvgCigPerDay() < this.smokedPerDay * 0.8)
						{
							this.smokedPerDay = (int)Math.round(changeWithinBounds(this.smokedPerDay, 0, this.cigLimit, (nm.getInfCigPerDay()* 0.5), Operations.SUBTRACT));
						}
					}
				}
				else
				{
					if(nm.getPcGivingUp() > 0.8 || irrationalChoice())
					{
						giveUpSmoking();
						changeMade = true;
					}
					else
					{
						if(nm.getAvgCigPerDay() > this.smokedPerDay * 1.2 )
						{
							this.smokedPerDay = (int)Math.round(changeWithinBounds(this.smokedPerDay, 0, this.cigLimit, (nm.getInfCigPerDay()* 0.5), Operations.ADD));
						}
						else if(nm.getAvgCigPerDay() < this.smokedPerDay * 0.8)
						{
							this.smokedPerDay = (int)Math.round(changeWithinBounds(this.smokedPerDay, 0, this.cigLimit, (nm.getInfCigPerDay()* 0.5), Operations.SUBTRACT));
						}
					}
				}
			}
		}
		else
		{
			if(this.givingUp)
			{
				this.health = changeWithinBounds(this.health, 0, 1, (this.stepsSinceGiveUp/this.giveUpStepLimit)/10, Operations.ADD);
				if(this.giveUpAttempts == 0)
				{
					//Stronger people so far, hinge on own behaviour
					if(this.health > nm.getInfHealth())
					{ 
						//maybe add willpower or some neighbourhood statistic
						if(nm.getPcSmokes() > 0.5 || irrationalChoice())
						{
							relapseSmoking((int)nm.getInfCigPerDay());
							changeMade = true;
						}
						else
						{
							this.willpower = changeWithinBounds(this.willpower, 0, 1, (this.willpower + 0.01), Operations.ADD);
							changeMade = true;
						}
					}
					else
					{
						//weaker, hinge on others
						if(nm.getPcSmokes() > 0.8 || irrationalChoice())
						{
							relapseSmoking((int)nm.getInfCigPerDay());
							changeMade = true;
						}
						else
						{
							this.willpower = changeWithinBounds(this.willpower, 0, 1, (this.willpower + 0.01), Operations.ADD);
							changeMade = true;
						}
					}
				}
				else if(this.giveUpAttempts >= 1 && this.giveUpAttempts < 5)
				{
					if(this.willpower < 0.5)
					{ 
						if(nm.getPcSmokes() > 0.5 || irrationalChoice())
						{
							relapseSmoking((int)nm.getInfCigPerDay());
							changeMade = true;
						}
						else
						{
							this.willpower = changeWithinBounds(this.willpower, 0, 1, (this.willpower + 0.01), Operations.ADD);
							changeMade = true;
						}
					}
					else 
					{
						if(nm.getPcSmokes() > 0.8 || irrationalChoice())
						{
							relapseSmoking((int)nm.getInfCigPerDay());
							changeMade = true;
						}
						else
						{
							this.willpower = changeWithinBounds(this.willpower, 0, 1, (this.willpower + 0.01), Operations.ADD);
							changeMade = true;
						}
					}
				}
				else
				{
					//weaker, hinge on others
					if(nm.getPcSmokes() > 0.4 || irrationalChoice())
					{
						relapseSmoking((int)nm.getInfCigPerDay());
						changeMade = true;
					}
					else
					{
						this.willpower = changeWithinBounds(this.willpower, 0, 1, (this.willpower + 0.01), Operations.ADD);
						changeMade = true;
					}
				}
				//completely arbitrary
				if(this.stepsSinceGiveUp >= this.giveUpStepLimit)
				{
					this.givingUp = false;
				}
			}
			else
			{
				if(nm.getPcSmokes() > 0.7 || irrationalChoice())
				{
					relapseSmoking((int)nm.getInfCigPerDay());
					changeMade = true;
				}
				else
				{
					this.willpower = changeWithinBounds(this.willpower, 0, 1, (this.willpower + 0.01), Operations.ADD);
					changeMade = true;
				}
			}
		}
		
		return changeMade;
	}
	
	
	private void statusCall()
	{
		//print("Smoker: " + this.isSmoker + " Cigs: " + this.smokedPerDay + " Giving Up?: " + this.givingUp + " Steps: " + this.stepsSinceGiveUp); 
	}
	
	private boolean irrationalChoice()
	{
		if(Math.random() < 0.0001)
		{
			print("I made an irrational choice.");
			return true;
		}
		return false;
	}
	private void relapseSmoking(int numCigarettes)
	{
		print("I started smoking!");
		this.isSmoker = true;
		this.givingUp = false;
		this.giveUpAttempts++;
		this.smokedPerDay = numCigarettes;
	}
	
	private void giveUpSmoking()
	{
		print("I gave up smoking!");
		this.isSmoker = false;
		this.givingUp = true;
		this.giveUpAttempts++;
		this.smokedPerDay = 0;
	}
	
	private double changeWithinBounds(double base, double lower, double upper, double change, Operations op)
	{
		double result = 0;
		double temp = 0;
		switch(op)
		{
		case ADD:
			temp = base + change;
			break;
		case SUBTRACT:
			temp = base - change;
			break;
		case MULTIPLY:
			temp = base * change;
			break;
		case DIVIDE:
			temp = base / change;
			break;
		default:
			temp = -1;
		}
		
		boolean invalid = true;
		if(temp != -1)
		{
			int bound = 0;
			
			do
			{
				if(temp < lower)
				{
					switch(op)
					{
					case ADD:
						temp = base + (change * 0.5);
						break;
					case SUBTRACT:
						temp = base - (change * 0.5);
						break;
					case MULTIPLY:
						temp = base * (change * 0.5);
						break;
					case DIVIDE:
						temp = base / (change * 0.5);
						break;
					default:
						temp = -1;
					}
				}
				else if(temp > upper)
				{
					switch(op)
					{
					case ADD:
						temp = base + (change * 0.5);
						break;
					case SUBTRACT:
						temp = base - (change * 0.5);
						break;
					case MULTIPLY:
						temp = base * (change * 0.5);
						break;
					case DIVIDE:
						temp = base / (change * 0.5);
						break;
					default:
						temp = -1;
					}
				}
				else
				{
					invalid = false;
				}
				bound++;
			} while(bound < 3 && invalid == true);
		}
		if(!invalid)
			return temp;
		else 
			return base;
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
			if(score > 0.65)
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
			else if(score < 0.35)
			{
				RepastEdge<BaseHuman> ed1 = network.getEdge(other.getNeighbor(), this);
				if(ed1 != null && Math.random() < sociable)
				{
					network.removeEdge(ed1);
				}
			}
		}
		
		//Also have the chance of adding a random edge with a random influence
		if(Math.random() < (sociable * 0.0001)  && network.getInDegree(this) < maxInDegree )
		{
			System.out.println("Adding a random edge");
			IndexedIterable<BaseHuman> allNodes = (IndexedIterable<BaseHuman>) context.getObjects(BaseHuman.class);
			BaseHuman random = allNodes.get((int)Math.round(Math.random() * (allNodes.size()-1)));
			RepastEdge<BaseHuman> ed1 = network.getEdge(random, this);
			if(ed1 == null && random != this && random.checkCanEdgeOut())
			{
				double nd = Distributions.getND(new NDParams(Math.random(), 0.3, 0, 1));
				network.addEdge(random, this, nd);
				print("I randomly attached to " + random.getID());
				//System.out.println(id + ": I attached to " + other.getNeighbor().getID() + " with influence " + nd /*+ " and prob " + prob + " and points " + points*/);
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
		return score/28;
	}
	
	private void print(String msg)
	{
		System.out.println("[NODE " + this.id + "]: "+msg);
	}
	
	public boolean checkCanEdgeOut()
	{
		if(network.getOutDegree(this) > this.maxOutDegree)
			return false;
		else 
			return true;
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

		double prob = scoreAgainst(oth);
		//print(prob + " against " + oth.getID());
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

}
