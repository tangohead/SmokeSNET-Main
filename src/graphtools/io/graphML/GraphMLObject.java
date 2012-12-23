
package graphtools.io.graphML;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Matt Smith
 * 
 *	This class is used to marshal/unmarshal GraphML
 */
@XmlRootElement(name = "graphml")
public class GraphMLObject {

	/*@XmlAttribute(name="xmlns")
	String xmlns = "http://graphml.graphdrawing.org/xmlns";*/
	
	//Stores full detail on keys 
	@XmlElement(name="key", type = XMLNodeKey.class)
	ArrayList<XMLNodeKey> nodeKeyRegister = new ArrayList<XMLNodeKey>();
	
	@XmlElement(name="key", type = XMLEdgeKey.class)
	ArrayList<XMLEdgeKey> edgeKeyRegister = new ArrayList<XMLEdgeKey>();
	
	@XmlElement
	Graph graph;
	
	public GraphMLObject()
	{
		this.graph = new Graph(null, "directed");
	}
	
	GraphMLObject(String id, String edgedefault)
	{
		this.graph = new Graph(id, edgedefault);
	}
	
	public void addEdge(String id, String source, String target)
	{
		graph.addEdge(id, source, target);
	}
	public void addEdge(String id, String source, String target, boolean directed)
	{
		graph.addEdge(id, source, target, directed);
	}
	public void addEdge(String id, String source, String target, HashMap<String, Object> attrs)
	{
		graph.addEdge(id, source, target, attrs);
	}
	public void addEdge(String id, String source, String target, boolean directed, HashMap<String, Object> attrs)
	{
		graph.addEdge(id, source, target, directed, attrs);
	}
	public ArrayList<Edge> getEdges()
	{
		return graph.getEdges();
	}
	
	
	
	public void addXMLNode(String id)
	{
		graph.addXMLNode(id);
	}
	
	public void addXMLNode(String id, HashMap<String, Object> attrs)
	{
		graph.addXMLNode(id, attrs);
	}
	
	public ArrayList<XMLNode> getXMLNodes() 
	{
		return graph.getXMLNodes();
	}
	
	/**
	 * @param attrName Attribute name
	 * @param attrType Attribute data type
	 * @param forNode Whether it is node attribute (if false, then edge is assumed)
	 * @param attrDefault Default value, can be null
	 * @return key to add future attributes by if successful, else null
	 */
	public String addAttributeKey(String attrName, String attrType, boolean forNode, String attrDefault)
	{
		String key = "";
		if(forNode) 
			key += "dn" + nodeKeyRegister.size(); 
		else 
			key += "de" + edgeKeyRegister.size();
		
		//check attribute doesn't exist already
		if(forNode)
		{
			for(XMLNodeKey x : nodeKeyRegister)
				if(x.attrName == attrName)
					return null;
			nodeKeyRegister.add(new XMLNodeKey(key, attrName, attrType, attrDefault));
		}
		else
		{
			for(XMLEdgeKey x : edgeKeyRegister)
				if(x.attrName == attrName)
					return null;
			edgeKeyRegister.add(new XMLEdgeKey(key, attrName, attrType, attrDefault));
		}

		return key;
	}
	
	
	/**
	 * Generates a map of class vars -> ids for easy  settings
	 * @return
	 */
	public HashMap<String, String> getNodeKeyRegister() {
		HashMap<String, String> keyMap = new HashMap<String, String>();
		for(XMLNodeKey key : nodeKeyRegister)
		{
			keyMap.put(key.getAttrName(), key.getID());
		}
		return keyMap;
		
	}
	
	
	public HashMap<String, String> getEdgeKeyRegister() {
		HashMap<String, String> keyMap = new HashMap<String, String>();
		for(XMLEdgeKey key : edgeKeyRegister)
		{
			keyMap.put(key.getAttrName(), key.getID());
		}
		return keyMap;
	}
}

@XmlRootElement(name="graph")
class Graph 
{
	@XmlAttribute
	String id;
	
	@XmlAttribute
	String edgedefault;
	
	@XmlElement(name = "node", type = XMLNode.class)
	ArrayList<XMLNode> nodes = new ArrayList<XMLNode>();
	
	@XmlElement(name = "edge", type = Edge.class)
	ArrayList<Edge> edges = new ArrayList<Edge>();
	

	Graph()
	{
		this.id = null;
		this.edgedefault = "directed";
	}
	
	

	Graph(String id, String edgedefault)
	{
		this.id = id;
		this.edgedefault = edgedefault;
	}
	
	public void addEdge(String id, String source, String target)
	{
		edges.add(new Edge(id, source, target));
	}
	public void addEdge(String id, String source, String target, boolean directed)
	{
		edges.add(new Edge(id, source, target, directed));
	}
	public void addEdge(String id, String source, String target, HashMap<String, Object> attrs)
	{
		edges.add(new Edge(id, source, target, attrs));
	}
	public void addEdge(String id, String source, String target, boolean directed, HashMap<String, Object> attrs)
	{
		edges.add(new Edge(id, source, target, directed, attrs));
	}
	public ArrayList<Edge> getEdges() {
		// TODO Auto-generated method stub
		return edges;
	}
	
	
	
	public void addXMLNode(String id)
	{
		nodes.add(new XMLNode(id));
	}
	
	public void addXMLNode(String id, HashMap<String, Object> attrs)
	{
		nodes.add(new XMLNode(id, attrs));
	}
	
	public ArrayList<XMLNode> getXMLNodes()
	{
		return nodes;
	}
}




