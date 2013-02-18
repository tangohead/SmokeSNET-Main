package graphtools.io.exporters;

import graphtools.io.graphML.GraphMLObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.thoughtworks.xstream.core.util.Fields;

import agents.BaseHuman;

import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class GraphMLExporter {
	public static void repastNetworkToGraphML(Context context, Network network, String filename)
	{
		//System.out.println("Creating "  + filename + " ...");
		GraphMLObject template = new GraphMLObject();
		
		Iterable init = network.getNodes();
		ArrayList<BaseHuman> nodes = new ArrayList<BaseHuman>();
		for(Object o : init)
		{
			if(o instanceof BaseHuman)
				nodes.add((BaseHuman) o);
			
		}
		
		
		HashMap<String, String> keyMap = new HashMap<String, String>();
		
		//Go through the fields of the BaseHuman, adding them as keys
		Field[] baseHumanFields = BaseHuman.class.getDeclaredFields();
		for(Field baseHumanField : baseHumanFields)
		{
			//We don't want to add contexts or networks as fields
			if(!baseHumanField.getType().getName().contains("Context") && !baseHumanField.getType().getName().contains("Network"))
			{
				//This gives us a raw type, incl package names. We need to strip those first.
				String fullType = baseHumanField.getType().getName();
				String[] parts = fullType.split("\\.");
				String type = parts[parts.length-1];
				
				keyMap.put(baseHumanField.getName(), template.addAttributeKey(baseHumanField.getName(), type, true, null));
			}
		}
		
		HashMap<String, Object> nodeAttr = new HashMap<String, Object>();
		for(BaseHuman n: nodes)
		{
			template.addXMLNode(n.getID(), n.generateAttrList(keyMap));
		}
		
		Iterable<RepastEdge> edges = network.getEdges();
		Integer i = 0;
		String edgeID = template.addAttributeKey("edgeWeight", "double", false, "1");
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		for(RepastEdge<BaseHuman> e: edges)
		{
			tempMap.put(edgeID, e.getWeight());
			template.addEdge(i.toString(), ((BaseHuman) e.getSource()).getID(), ((BaseHuman) e.getTarget()).getID(), tempMap);
			i++;
			tempMap.clear();
		}
		
		String headerString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"\n" +
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
				"xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns \n" +
				"http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">";
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(GraphMLObject.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			      
			FileWriter fstream = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fstream);
			/*out.write(headerString);*/
			jaxbMarshaller.marshal(template, out);
			
			out.close();
			fstream.close();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//System.out.println("GraphML Creation Finished");
	}
	
	public static void JUNGGraphToGraphML(Graph<String, Long> g, String filename)
	{
		GraphMLObject template = new GraphMLObject();
		String headerString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"\n" +
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
				"xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns \n" +
				"http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">";
		
		boolean directed = false;
		ArrayList<String> nodesToRemove = new ArrayList<String>();
		for(String l : g.getVertices())
		{
			template.addXMLNode("n" + l.toString());
		}
		
		for(Long l : g.getEdges())
		{
			//For each edge, we need the end points as well
			if(g.getEdgeType(l) == EdgeType.DIRECTED)
				directed = true;
			Pair<String> vPair = g.getEndpoints(l);
			if(g.containsVertex(vPair.getFirst()) && g.containsVertex(vPair.getSecond()))
				template.addEdge("e" + l.toString(), "n" + vPair.getFirst().toString(), "n" + vPair.getSecond().toString(), directed);
		}
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(GraphMLObject.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			      
			FileWriter fstream = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fstream);
			/*out.write(headerString);*/
			jaxbMarshaller.marshal(template, out);
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("GraphML Created.");
	}
}
