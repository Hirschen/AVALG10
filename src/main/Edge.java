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

	/**
	 * @param a
	 * @param b
	 * @param calculateDistance
	 */
	public Edge(int a, int b, int d)
	{
		nodeA = (short) a;
		nodeB = (short) b;
		length = d;
	}

	/**
	 * @return the reverse of this edge
	 */
	public Edge getReverse()
	{
		return new Edge(nodeB, nodeA, length);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Edge e)
	{
		if (equals(e))
			return 0;

		return (length < e.length ? -1 : 1);
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		if (nodeA < nodeB)
			return nodeA * 1000 + nodeB;
		else
			return nodeB * 1000 + nodeA;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (nodeA != other.nodeA && nodeA != other.nodeB)
			return false;
		if (nodeB != other.nodeB && nodeB != other.nodeA)
			return false;

		return true;
	}
}