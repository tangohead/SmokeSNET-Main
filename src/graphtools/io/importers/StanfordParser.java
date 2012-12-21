package graphtools.io.importers;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;


public class StanfordParser {

	private String filePath;
	private String fileComments = "" ;
	private ArrayList<String[]> edges = new ArrayList<String[]>();
	private Graph<String, Long> graphRep = new SparseMultigraph<String, Long>();
	
	
	StanfordParser(String filePath)
	{
		this.filePath = filePath;
	}
	
	public boolean Parse() 
	{
		boolean error = false;
		try
		{
			FileInputStream fstream = new FileInputStream(filePath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			String tempArr[];
			
			
			//Code from http://www.roseindia.net/java/beginners/java-write-to-file.shtml
			while ((strLine = br.readLine()) != null)   {
				//System.out.println(strLine);
				if(strLine.startsWith("#") == true )
				{
					fileComments += strLine + "\n";
				}
				else
				{
					tempArr = strLine.split("\t");
					String insertArr[] = new String[2];
					for(int i = 0; i < insertArr.length; i++)
					{
						insertArr[i] = tempArr[i];
					}
					//System.out.println("Adding " + insertArr[0] + " to " + insertArr[1]);
					edges.add(insertArr);
				}
			}
			in.close();
		}
		catch (FileNotFoundException e){
			System.err.println("File not found!");
			error = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("I/O Error!");
			error = true;
		}
		return error;
	}
	
	public ArrayList<String[]> getDataSet()
	{
		return edges;
	}
	
	public String getComments() 
	{
		return fileComments;
	}
	
	public Graph<String, Long> toGraph()
	{
		//Add vertices to graph first
		//STORE ADDED IDS IN COLLECTION
		//CHECK FOR ID BEFORE ADDING NEW NODE
		HashSet<String> nodes = new HashSet<String>();
		Long edgeName = (long) 0;
		EdgeType edgeType = EdgeType.DIRECTED;
		Pair<String> vPair;
		for(int i = 0; i < edges.size(); i++)
		{
			//System.out.println("Adding " + edges.get(i)[0] + " to " + edges.get(i)[1]);
			
			if(!nodes.contains(edges.get(i)[0]))
			{
				graphRep.addVertex(edges.get(i)[0]);
				nodes.add(edges.get(i)[0]);
				//System.out.println(graphRep.getVertexCount());
			}
			if(!nodes.contains(edges.get(i)[1]))
			{
				graphRep.addVertex(edges.get(i)[1]);
				nodes.add(edges.get(i)[1]);
				//System.out.println(graphRep.getVertexCount());
			}
			vPair = new Pair<String>(edges.get(i)[0], edges.get(i)[1]);
			edgeName++;
			graphRep.addEdge(edgeName, vPair, edgeType);
			//System.out.println(graphRep.getVertexCount());
		}
		
		return graphRep;
	}
}
