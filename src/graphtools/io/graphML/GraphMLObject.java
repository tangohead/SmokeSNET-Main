
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


@XmlRootElement(name="edge")
public class Edge 
{
	@XmlAttribute
	String id;
	@XmlAttribute
	String source;
	@XmlAttribute
	String target;
	@XmlAttribute
	boolean directed;

	@XmlElement(name="data")
	ArrayList<XMLAttribute> attrList = new ArrayList<XMLAttribute>();
	
	Edge()
	{
		this.id = null;
		this.source = null;
		this.target = null;
		this.directed = true;
		
	}
	Edge(String id, String source, String target)
	{
		this.id = id;
		this.source = source;
		this.target = target;
		this.directed = true;
	}
	Edge(String id, String source, String target, boolean directed)
	{
		this.id = id;
		this.source = source;
		this.target = target;
		this.directed = directed;
	}
	Edge(String id, String source, String target, HashMap<String, Object> attrs)
	{
		this.id = id;
		this.source = source;
		this.target = target;
		this.directed = true;
		
		Iterator iter = attrs.entrySet().iterator();
		while(iter.hasNext())
		{
			Map.Entry pair = (Map.Entry)iter.next();
			//System.out.println("Adding KEY:" + pair.getKey() + " VAL:" + pair.getValue());
			attrList.add(new XMLAttribute((String)pair.getKey(), pair.getValue()));
		}
	}
	
	Edge(String id, String source, String target, boolean directed, HashMap<String, Object> attrs)
	{
		this.id = id;
		this.source = source;
		this.target = target;
		this.directed = directed;
		Iterator iter = attrs.entrySet().iterator();
		while(iter.hasNext())
		{
			Map.Entry pair = (Map.Entry)iter.next();
			attrList.add(new XMLAttribute((String)pair.getKey(), pair.getValue()));
		}
	}
}

@XmlRootElement(name="node")
class XMLNode
{
	@XmlAttribute
	String id;
	@XmlElement(name="data")
	ArrayList<XMLAttribute> attrList = new ArrayList<XMLAttribute>();
	
	XMLNode()
	{
		this.id = null;
		
	}
	XMLNode(String id)
	{
		this.id = id;
	}
	XMLNode(String id, HashMap<String, Object> attrs)
	{
		this.id = id;
		Iterator iter = attrs.entrySet().iterator();
		while(iter.hasNext())
		{
			Map.Entry pair = (Map.Entry)iter.next();
			attrList.add(new XMLAttribute((String)pair.getKey(), pair.getValue()));
		}
	}
}


/*@XmlRootElement(name="data")*/
class XMLAttribute
{
	@XmlAttribute
	String key;
	@XmlElement
	String value;
	
	XMLAttribute()
	{
		this.key = null;
		this.value = null;
	}
	
	XMLAttribute(String key, Object value)
	{
		this.key = key;
		if(value instanceof Double)
			this.value = Double.toString((Double)value);
		else if(value instanceof Integer)
			this.value = Integer.toString((Integer)value);
		else if(value instanceof Long)
			this.value = Long.toString((Long)value);
		else
			this.value = value.toString();
	}
}


class XMLKey
{
	@XmlAttribute
	String id;
	@XmlElement(name="default")
	String defaultString;
	@XmlAttribute(name="attr.name")
	String attrName;
	@XmlAttribute(name="attr.type")
	String attrType;
	
	XMLKey()
	{
		this.id = null;
		this.attrName = null;
		this.attrType = null;
	}
	
	XMLKey(String key, String attrName, String attrType)
	{
		this.id = key;
		this.attrName = attrName;
		this.attrType = attrType;
	}
	
	XMLKey(String key, String attrName, String attrType, String defaultString)
	{
		this.id = key;
		this.attrName = attrName;
		this.attrType = attrType;
		this.defaultString = defaultString;
	}
		
	public String getKey()
	{
		return this.id;
	}
}

@XmlRootElement(name="key")
class XMLNodeKey extends XMLKey
{
	@XmlAttribute(name="for")
	String forAttr = "node";
	
	XMLNodeKey()
	{
		super();
	}
	
	XMLNodeKey(String key, String attrName, String attrType)
	{
		super(key, attrName, attrType);
	}
	
	XMLNodeKey(String key, String attrName, String attrType, String defaultString)
	{
		super(key, attrName, attrType, defaultString);
	}
}
@XmlRootElement(name="key")
class XMLEdgeKey extends XMLKey
{
	@XmlAttribute(name="for")
	String forAttr = "edge";
	
	XMLEdgeKey()
	{
		super();
	}
	XMLEdgeKey(String key, String attrName, String attrType)
	{
		super(key, attrName, attrType);
	}
	
	XMLEdgeKey(String key, String attrName, String attrType, String defaultString)
	{
		super(key, attrName, attrType, defaultString);
	}
}