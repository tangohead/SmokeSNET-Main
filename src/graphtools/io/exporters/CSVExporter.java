package graphtools.io.exporters;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import agents.BaseHuman;

public class CSVExporter {

	public static void exportToCSV(Iterable<BaseHuman> nodes, String filename)
	{
		//System.out.println("Writing to CSV - " + filename);
		Field[] nodeFields = BaseHuman.class.getDeclaredFields();
		FileWriter fw;

		ArrayList<BaseHuman> humanNodes = new ArrayList<BaseHuman>();
		for(Object o : nodes)
		{
			if(o instanceof BaseHuman)
				humanNodes.add((BaseHuman) o);
			
		}
		try {
			 fw = new FileWriter(filename + ".csv");
			 String writeLine = "";
				
			for(Field f : nodeFields)
			{
				if(f.getName() != "context" && f.getName() != "network")
				{
					writeLine += f.getName() + ",";
					//bit of a hack, allows the next bit to be as general as possible
					f.setAccessible(true);
				}
				
			}
			//Remove final comma
			writeLine = writeLine.substring(0, writeLine.length()-1);
			writeLine += "\n";
			fw.append(writeLine);
			for(BaseHuman b : humanNodes)
			{
				writeLine ="";
				for(Field f : nodeFields)
				{
					if(f.getName() != "context" && f.getName() != "network")
					{
						Object o = f.get(b);
						Object tempVal = null;

						if(o instanceof Integer)
							tempVal = (Integer) o;
						else if (o instanceof Long)
							tempVal = (Long) o;
						else if (o instanceof Double)
							tempVal = (Double) o;
						else if(o instanceof Boolean)
							tempVal = (Boolean) o;
						else if(o instanceof String)
							tempVal = (String) o;
						writeLine += tempVal + ",";
					}
				}
				writeLine = writeLine.substring(0, writeLine.length()-1);
				writeLine += "\n";
				fw.append(writeLine);
			}
			
			fw.close();
			//System.out.println("CSV Created.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("*********** ERROR: File writer could not be created ***********");
			return;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
