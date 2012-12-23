package graphtools.io.graphML;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public class XMLKey
{
	public String getId() {
		return id;
	}

	public String getAttrName() {
		return attrName;
	}

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

