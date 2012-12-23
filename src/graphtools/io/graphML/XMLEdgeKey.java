package graphtools.io.graphML;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


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
	public String getAttrName() {
		// TODO Auto-generated method stub
		return this.attrName;
	}
	
	public String getID() {
		return this.id;
	}
}