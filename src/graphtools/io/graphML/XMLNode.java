package graphtools.io.graphML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="node")
public class XMLNode
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
	
	public String getID()
	{
		return this.id;
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