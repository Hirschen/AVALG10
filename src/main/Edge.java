package main;

/**
 * @author Martin Nycander
 */
public final class Edge implements Comparable<Edge>
{
	public final short nodeA;
	public final short nodeB;
	public final int length;

	public Edge(short a, short b, int l)
	{
		nodeA = a;
		nodeB = b;
		length = l;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Edge e)
	{
		return this.length - e.length;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return nodeA + "-[" + length + "]-" + nodeB;
	}
}