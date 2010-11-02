package main;


public class Tour
{
	private short[] tour;


	public Tour(int nodes)
	{
		tour = new short[nodes];
	}

	public Tour(int[] tour)
	{
		int size = tour.length;
		this.tour = new short[size];

		for (int i = 0; i < size; i++)
		{
			// assert (tour[i] < Short.MAX_VALUE);
			this.tour[i] = (short) tour[i];
		}
	}

	public int getLength()
	{
		return tour.length;
	}

	public int getNode(int i)
	{
		return tour[i];
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

	public void setNode(int i, int v)
	{
		// assert (v < Short.MAX_VALUE);
		tour[i] = (short) v;
	}

	/*
	 TODO: Lagra edges här istället och sedan generera en short[] ?
	
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
		removeEdge(e.nodeA, e.nodeB);
	}

	public void removeEdge(int nodeA, int nodeB)
	{
		edges[nodeA][nodeB] = null;
		edges[nodeB][nodeA] = null;
	}

	public void addEdge(Edge e)
	{
		edges[e.nodeA][e.nodeB] = e;
		edges[e.nodeB][e.nodeA] = e;
	}
	*/
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tour.length; i++)
		{
			sb.append(tour[i]);
			sb.append(' ');
		}
		return sb.toString();
	}
}
