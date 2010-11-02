package main;

import java.util.ArrayList;


public class Tour
{
	// private short[] tour;
	private ArrayList<Edge> edges;

	public Tour()
	{
		edges = new ArrayList<Edge>();
	}

	public Tour(int nodes)
	{
		// tour = new short[nodes];
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
		// this.tour = new short[size];
		edges = new ArrayList<Edge>();

		for (int i = 1; i < size; i++)
		{
			// assert (tour[i] < Short.MAX_VALUE);
			// this.tour[i] = (short) tour[i];
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
		if (i == countEdges())
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
		return getNode(tourPosition - 1);
	}

	/*
	 * Get suceeding node
	 */
	public int getSuccessorNode(int tourPosition)
	{
		return getNode(tourPosition + 1);
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
		if (edges.size() > 1 && edges.get(edges.size() - 1).nodeB != e.nodeA)
		{
			throw new RuntimeException("You can not connect " + e + " with this tour. The last edge is " + edges.get(edges.size() - 1) + ". This can not be conneced with " + e + ".");
		}
		edges.add(e);
	}

	/**
	 * @param insertAt
	 * @param edge
	 */
	public void addEdge(int insertAt, Edge edge)
	{
		edges.add(insertAt, edge);
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
			sb.append(' ');
		}
		sb.append(edges.get(edges.size() - 1).nodeB);

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
}
