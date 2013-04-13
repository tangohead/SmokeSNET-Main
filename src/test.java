import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import agents.BaseHuman;


import graphtools.io.converters.*;
import graphtools.io.exporters.GraphMLExporter;
import graphtools.io.importers.*;
import graphtools.samplers.*;
import edu.uci.ics.jung.graph.Graph;


public class test {
	public static void main(String[] args) 
	{
	/*	System.out.println("Whattup");
		Field[] fields = BaseHuman.class.getDeclaredFields();
		for(Field field : fields)
		{
			System.out.println(field.getName());
		}*/
		//AnalysisTools.repastNetworkToGraphML(context, net, "test.graphml");
		StanfordParser sp = new StanfordParser("/Users/matt/git/SmokeSNET-Main/import/soc-Slashdot0811.txt");
		sp.Parse();
		
		Graph<String, Long> testGraph;
		testGraph = sp.toGraph();
		/*
		Collection<String> v = testGraph.getVertices();
		Iterator iter = v.iterator();
		double deg = 0;
		while(iter.hasNext())
		{
			String s = (String) iter.next();
			deg = deg + testGraph.degree(s);
		}
		GraphConverter.JUNGGraphToGraphML(testGraph, "slashdot.graphml");
		*/
		//System.out.println("total deg = " + deg + " avg deg = " + deg / testGraph.getVertexCount());
	/*	for(int i = 0; i < 3; i++)
		{*/
			
			Graph<String, Long> sampledGraph = DepthFirst.SnowballSampler(testGraph, 7500);
			
			System.out.println("Nodes: " + sampledGraph.getVertexCount());
			
			
			Collection<String> v;
			ArrayList<String> removalIds = new ArrayList<String>();
			for(int i = 0; i < 3; i++)
			{
				v = sampledGraph.getVertices();
				Iterator<String> iter = v.iterator();
				while(iter.hasNext())
				{
					String id = iter.next();
					if(sampledGraph.degree(id) == 1)
						removalIds.add(id);
				}
				for(String s : removalIds)
					sampledGraph.removeVertex(s);
				removalIds.clear();
			}
			
			GraphMLExporter.JUNGGraphToGraphML(sampledGraph, "sd.graphml");
			System.out.println("After degree removal: " + sampledGraph.getVertexCount());
//		/}
	}
}
