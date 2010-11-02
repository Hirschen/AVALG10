package main;

import java.util.ArrayList;
import java.util.Iterator;


public class Tour implements Iterable<Edge>
{
	private ArrayList<Edge> edges;

	public Tour()
	{
		edges = new ArrayList<Edge>();
	}

	public Tour(int nodes)
	{
		edges = new ArrayList<Edge>(nodes);
	}

	/**
	 * Constructs a tour object from a given tour and a graph.
	 * 
	 * @param tour
	 *            the already constructed tour.
	 * @param graph
	 *            the graph which contains edge weights.
	 */
	public Tour(int[] tour, Graph graph)
	{
		int size = tour.length;
		edges = new ArrayList<Edge>();

		for (int i = 1; i < size; i++)
		{
			edges.add(graph.getEdge(tour[i - 1], tour[i]));
		}
	}


	/**
	 * @return the number of nodes in the tour.
	 */
	public int getLength()
	{
		return edges.size() + 1;
	}

	/**
	 * @return
	 */
	public int countEdges()
	{
		return edges.size();
	}

	/**
	 * Gets a node id from a certain spot in the tour.
	 * 
	 * @param i
	 *            the place on the tour to fetch the node from.
	 * @return a node id.
	 */
	public int getNode(int i)
	{
		if (i > 0 && i == edges.size())
			return edges.get(i - 1).nodeB;

		return edges.get(i).nodeA;
	}

	public Edge getEdge(int position)
	{
		return edges.get(position);
	}
	

	/*
	 * Get preceding node
	 */
	public int getPredecessorNode(int tourPosition)
	{
		if(tourPosition <= 0){
			return getNode(edges.size()-1);
		}else{
			return getNode(tourPosition - 1);
		}
	}

	/*
	 * Get suceeding node
	 */
	public int getSuccessorNode(int tourPosition)
	{
		if(tourPosition >= edges.size()-1){
			return getNode(0);
		}else{
			return getNode(tourPosition + 1);
		}
	}
	
	public Edge getLongestEdge()
	{
		// TODO
		return null;
	}

	public Edge getShortestEdge()
	{
		// TODO
		return null;
	}

	public void removeEdge(Edge e)
	{
		edges.remove(e);
	}

	public void addEdge(Edge e)
	{
		if (edges.size() > 0 && edges.get(edges.size() - 1).nodeB != e.nodeA)
		{
			throw new RuntimeException("You can not connect " + e + " with this tour. The last edge is " + edges.get(edges.size() - 1) + ". This can not be conneced with " + e + ".");
		}
		edges.add(e);
	}
	
	

	/**
	 * @param setAt
	 * @param edge
	 */
	public void setEdge(int setAt, Edge edge, Graph g)
	{
		int a = setAt, b = setAt;
		if(setAt == 0){
			a = edges.size();
		}
		if(setAt == edges.size()-1){
			b=-1;
		}
		
		edges.set(setAt, edge);
		edges.set(a-1, g.getEdge(edges.get(a-1).nodeA, edge.nodeA));
		edges.set(b+1, g.getEdge(edge.nodeB, edges.get(b+1).nodeB ));
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (Edge e : edges)
		{
			sb.append(e.nodeA);
			sb.append('\n');
		}

		if (edges.size() > 1)
		{
			sb.append(edges.get(edges.size() - 1).nodeB);
			sb.append('\n');
		}

		return sb.toString();
	}
	

	/**
	 * @param e
	 * @return
	 */
	public int indexOf(Edge e)
	{
		return edges.indexOf(e);
	}

	@Override
	public Iterator<Edge> iterator() 
	{
		// TODO Auto-generated method stub
		return edges.iterator();
	}

	public void addEdge(int insertAt, Edge edge) {
		edges.add(insertAt,edge);
		
	}
}
