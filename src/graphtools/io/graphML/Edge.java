package graphtools.io.graphML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="edge")
public class Edge 
{
	public String getSource() {
		return source;
	}
	public String getTarget() {
		return target;
	}

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
	
	public HashMap<String, String> getAttrList() 
	{
		HashMap<String, String> rtnMap = new HashMap<String, String>();
		for(XMLAttribute attr : attrList)
		{
			rtnMap.put(attr.getKey(), attr.getValue());
		}
		return rtnMap;
	}
}