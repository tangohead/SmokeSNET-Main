package graphtools.io.importers;

import graphtools.io.converters.RepastSummary;
import graphtools.io.graphML.GraphMLObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import agents.BaseHuman;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.space.graph.Network;
import repast.simphony.util.collections.IndexedIterable;

import graphtools.io.graphML.*;

public class GraphMLImporter {
	
	/**
	 * Reads in a GraphML file and converts it to a Repast network
	 * @param filename
	 * @param graphName
	 * @param context
	 * @return
	 */
	public static RepastSummary GraphMLToRepast(String filename, String graphName, Context context)
	{
		try {
			File file = new File(filename);
			JAXBContext jaxbContext = JAXBContext.newInstance(GraphMLObject.class);
	 
			//Read in GraphML
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			GraphMLObject GML = (GraphMLObject) jaxbUnmarshaller.unmarshal(file);
			
			//This is a way to return both a context and a network
			RepastSummary rs = new RepastSummary();

			//Build the temporary network
			NetworkBuilder<BaseHuman> builder = new NetworkBuilder(graphName, context, true);
			Network<BaseHuman> tempNetwork = builder.buildNetwork();
			
			//We want to make it easier to look up values, so we get this 
			// map of class var names to ids
			HashMap<String, String> nodeKeyMap = GML.getNodeKeyRegister();
			HashMap<String, String> edgeKeyMap = GML.getEdgeKeyRegister();
			
			//We know we only have weights for edges on import, so
			String edgeWeightID = edgeKeyMap.get("edgeWeight");
			for(XMLNode node : GML.getXMLNodes())
			{
				context.add(new BaseHuman(context, node.getID(), node.getAttrList(), nodeKeyMap));
			}
			
			//now generate a hashmap of objects to look up when adding edges
			IndexedIterable<BaseHuman> index = context.getObjects(BaseHuman.class);
			Iterator<BaseHuman> iter = index.iterator();
			HashMap<String, BaseHuman> objMap = new HashMap<String, BaseHuman>();
			
			while(iter.hasNext())
			{
				BaseHuman tmp = iter.next();
				objMap.put(tmp.getID(), tmp);
				
			}
			//Add the edges
			for(Edge edge : GML.getEdges())
			{
				HashMap<String, String> attrList = edge.getAttrList();
				if(attrList.get(edgeWeightID) == null)
					tempNetwork.addEdge(objMap.get(edge.getSource()), objMap.get(edge.getTarget()));
				else
					tempNetwork.addEdge(objMap.get(edge.getSource()), objMap.get(edge.getTarget()), Double.parseDouble(attrList.get(edgeWeightID)));
			}
			
			rs.setContext(context);
			rs.setNetwork(tempNetwork);
			//go through context and assign network
			index = context.getObjects(BaseHuman.class);
			iter = index.iterator();

			while(iter.hasNext())
			{
				BaseHuman tmp = iter.next();
				tmp.setNetwork(tempNetwork);
			}
			
			
			return rs;
		  } catch (JAXBException e) {
			e.printStackTrace();
		  }
		return null;
	}
}
