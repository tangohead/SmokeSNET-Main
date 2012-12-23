package graphtools.io.graphML;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


/*@XmlRootElement(name="data")*/
public class XMLAttribute
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
	
	public String getKey()
	{
		return this.key;
	}
	
	public String getValue()
	{
		return this.value;
	}
}
