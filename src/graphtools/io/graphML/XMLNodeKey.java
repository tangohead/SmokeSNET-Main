package graphtools.io.graphML;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="key")
public
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

	public String getAttrName() {
		// TODO Auto-generated method stub
		return this.attrName;
	}
	
	public String getID() {
		return this.id;
	}
}