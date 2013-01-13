package graphtools.samplers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
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
			
			return SnowballSampler(inGraph, rtnGraph, vertexSet, sampleSize, rtnGraph.getVertexCount());
		
		}
		else
			return null;
		
	}
	
	private static Graph<String, Long> SnowballSampler(Graph<String, Long> inGraph, Graph<String, Long> currGraph, List<String> vertexSet, int sampleSize, int prevSize)
	{
		Collection<Long> workingEdgeSet = new LinkedList<Long>(); 
		Iterator<Long> edgeIter;
		Pair<String> workingPair;
		List<String> nextVertexSet = new ArrayList<String>();
		
		String newNode;
		System.out.println("==============================================");
		System.out.println("Graph size is " + currGraph.getVertexCount());
		
		
		//Snowball sampler needs to pick randomly
		//For each vertex...
		
		for(String o : vertexSet)
		{
			//...get the out-edges and iterate through them
			Collection<Long> temp = inGraph.getOutEdges(o);
			Iterator iter = temp.iterator();
			while(iter.hasNext())
			{
				workingEdgeSet.add((Long) iter.next());
			}
				
		}
		
		edgeIter = workingEdgeSet.iterator();
		System.out.println("Current fringe is " + workingEdgeSet.size());
		int maxSizeAtEnd = workingEdgeSet.size() + currGraph.getVertexCount();
		boolean selective = false;
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		System.out.println("Max size at end of iteration " + maxSizeAtEnd);
		if( maxSizeAtEnd > sampleSize)
		{
			selective = true;
			//Now we need to select just enough edges to meet the sample
			int edgesNeeded = sampleSize - currGraph.getVertexCount();
			int edgeCount = 0;
			while(edgeCount < edgesNeeded)
			{
				int index = (int) Math.round(Math.random() * workingEdgeSet.size());
				if(!indexList.contains(index))
				{
					indexList.add(index);
					edgeCount++;
				}
			}
			System.out.println("Needed " + edgesNeeded + " has " + edgeCount );
				
		}
		
		int iterCount = 0;
		while(edgeIter.hasNext())
		{
			//Find out which edge is not the one we are expanding from (i.e. the new node to be added)
			Long currentEdge = edgeIter.next();
			workingPair = inGraph.getEndpoints(currentEdge);
			//if(workingPair.getFirst().equals(o))
			//we got out edges, so it should be a second one	
			newNode = workingPair.getSecond();
			//else
			//	newNode = workingPair.getFirst();
			
			//If it's not in the graph, add it
			//note that we also check if we're being selective (on the final iteration), or not
			//if we are, we look up the id to see if it's already in the graph
			if(!currGraph.containsVertex(newNode) && ((selective && indexList.contains(iterCount)) || (!selective)))
			{
				System.out.println("Are we selecting? " + selective + " At index? " + iterCount);
				currGraph.addVertex(newNode);
				nextVertexSet.add(newNode);
			}
			//If the edge to the new node doesn't exist, add it.
			//	Note, this is separate because if we expand to a node that IS already in the graph but 
			//	said node links to some other in-graph node, then we still need to add the edge.
			if(!currGraph.containsEdge(currentEdge) && ((selective && indexList.contains(iterCount)) || (!selective)))
				currGraph.addEdge(currentEdge, workingPair, EdgeType.DIRECTED);
			iterCount++;
		}
		
		System.out.println("Graph size at end of iteration " + currGraph.getVertexCount());
		//If the current graph is smaller than the sample size, and the previous graph size was less than current graph size (i.e. we
		// are actually adding stuff), then recurse.
		if(currGraph.getVertexCount() < sampleSize  && (prevSize < currGraph.getVertexCount()))
			currGraph = SnowballSampler(inGraph, currGraph, nextVertexSet, sampleSize, currGraph.getVertexCount());
		
		return currGraph;
	}
}

