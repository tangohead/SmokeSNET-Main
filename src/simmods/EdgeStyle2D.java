package simmods;

import java.awt.Color;

import agents.BaseHuman;

import repast.simphony.space.graph.RepastEdge;
import repast.simphony.visualizationOGL2D.DefaultEdgeStyleOGL2D;

public class EdgeStyle2D extends DefaultEdgeStyleOGL2D{
	
	//set line colours depending on range
	@Override
	public Color getColor(RepastEdge<?> edge)
	{
		RepastEdge<BaseHuman> e = (RepastEdge<BaseHuman>) edge;
		if(e.getWeight() < 0.25)
			return Color.BLACK;
		else if(e.getWeight() < 0.5)
			return Color.GREEN;
		else if(e.getWeight() < 0.75)
			return Color.YELLOW;
		else 
			return Color.RED;
	}

}
