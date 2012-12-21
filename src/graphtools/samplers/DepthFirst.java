package graphtools.samplers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class DepthFirst {

	public static Graph<String, Long> SnowballSampler(Graph<String, Long> inGraph, int sampleSize)
	{
		Graph<String, Long> rtnGraph = new SparseMultigraph<String, Long>();
		
		int sampleCount = 0;
		Collection<String> vertices = inGraph.getVertices();
		Iterator<String> vertIter = vertices.iterator();
		boolean startVFound = false;
		int startVCount = 0;
		int startVertexIndex = Math.round((float)(Math.random() * inGraph.getVertexCount()));
		System.out.println("Start at " + startVertexIndex);
		String startVertex = null;
		while(vertIter.hasNext() && !startVFound)
		{
			//System.out.println("Node is " + startVCount);
			if(startVCount == startVertexIndex)
			{
				System.out.println("Here!");
				startVertex = vertIter.next();
			}
			else
				vertIter.next();
			startVCount++;
		}
		
		if(startVertex != null)
		{
			rtnGraph.addVertex(startVertex);
			
			sampleCount++;
		
			//Collection<Object> currentVertexBoundary;
			//Collection<Object> workingEdgeSet = inGraph.getOutEdges(startVertex);
			//Iterator<Object> edgeSet = workingEdgeSet.iterator();
			ArrayList<String> vertexSet = new ArrayList<String>();
			vertexSet.add(startVertex);
			//Now grab the out edges
//			Object currVertex = startVertex;
//			Pair<Object> workingPair;
			
			return SnowballSampler(inGraph, rtnGraph, vertexSet, sampleSize, rtnGraph.getVertexCount());
			
			//while < sampleSize
			//for each current Vertex Boundary
			//	Iterate over workingEdgeSet
				//	if node not in graph, add it
				//		add to vertex boundary
				//	if edge not in graph, add it
			//	get rid of edge set
			//	expand next currVertexBoundary edge into edgeSet
			
		
			
//			while(sampleCount < sampleSize)
//			{
//				while(edgeSet.hasNext())
//				{
//					workingPair = inGraph.getEndpoints(edgeSet.next());
//					if(workingPair.getFirst().equals(startVertex))
//					{
//						rtnGraph.addVertex(workingPair.getSecond());
//						currentVertexBoundary.add(workingPair.getSecond());
//						
//					}
//					else
//					{
//						rtnGraph.addVertex(workingPair.getFirst());			
//						currentVertexBoundary.add(workingPair.getFirst());
//					}
//					rtnGraph.addEdge(null, workingPair, EdgeType.DIRECTED);
//					
//				}
//				
//			}
			
			//add nodes to vertex boundary
			
		}
		else
			return null;
		
	}
	//for each in nodeSet
	//	Expand to get all edges
	//	if other v not in rtnGraph
	//		add it to rtnGraph, with edge
	//		add it to nextNodeSet
	//	elif edge not in rtnGraph
	//		add it
	//if(rtnGraph size < sampleSize)
	//	call self
	//else
	//	return rtnGraph
	
	private static Graph<String, Long> SnowballSampler(Graph<String, Long> inGraph, Graph<String, Long> currGraph, List<String> vertexSet, int sampleSize, int prevSize)
	{
		Collection<Long> workingEdgeSet; 
		Iterator<Long> edgeIter;
		Pair<String> workingPair;
		List<String> nextVertexSet = new ArrayList<String>();
		
		String newNode;
		
		System.out.println("Graph size is " + currGraph.getVertexCount());
		
		//Snowball sampler needs to pick randomly
		for(String o : vertexSet)
		{
			workingEdgeSet = inGraph.getOutEdges(o);
			edgeIter = workingEdgeSet.iterator();
			
			while(edgeIter.hasNext())
			{
				Long currentEdge = edgeIter.next();
				workingPair = inGraph.getEndpoints(currentEdge);
				if(workingPair.getFirst().equals(o))
					newNode = workingPair.getSecond();
				else
					newNode = workingPair.getFirst();
				
				if(!currGraph.containsVertex(newNode))
				{
					currGraph.addVertex(newNode);
					nextVertexSet.add(newNode);
				}
				if(!currGraph.containsEdge(currentEdge))
					currGraph.addEdge(currentEdge, workingPair, EdgeType.DIRECTED);
			}
		}
		
		if(currGraph.getVertexCount() < sampleSize  && (prevSize < currGraph.getVertexCount()))
			currGraph = SnowballSampler(inGraph, currGraph, nextVertexSet, sampleSize, currGraph.getVertexCount());
		
		return currGraph;
	}
}
