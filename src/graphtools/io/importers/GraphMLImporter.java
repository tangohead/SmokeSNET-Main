package graphtools.io.importers;

import graphtools.io.converters.RepastSummary;
import graphtools.io.graphML.GraphMLObject;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class GraphMLImporter {
	public static RepastSummary GraphMLToRepast(String filename)
	{
		try {
			File file = new File(filename);
			JAXBContext jaxbContext = JAXBContext.newInstance(GraphMLObject.class);
	 
			//Read in GraphML
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			GraphMLObject GML = (GraphMLObject) jaxbUnmarshaller.unmarshal(file);
			
			RepastSummary rs;
			
			for(XMLNode node : GML.getXMLNodes())
			{
				
			}
			
			
			return rs;
		  } catch (JAXBException e) {
			e.printStackTrace();
		  }
		return null;
	}
}
