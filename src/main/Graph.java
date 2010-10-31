package main;


/**
 * // TODO: Graph is a ...
 * 
 * @author Martin Nycander
 * @since
 */
public class Graph
{
	private int distanceCounts = 0;
	private double[][] nodes;
	private Edge[][] edges;
	private int nodeCount;
	private int edgeCount;

	/**
	 * Constructs a graph object given a set of coordinates.
	 * 
	 * @param coordinates
	 *            is an array of coordinates in the format matrix[size] = {x, y}
	 */
	public Graph(double[][] coordinates)
	{
		nodes = coordinates;
		nodeCount = coordinates.length;
		edges = new Edge[nodeCount][nodeCount];
		edgeCount = (nodeCount * (nodeCount - 1)) / 2;
		Edge tmp;

		// Precalculate edges
		for (short a = 0; a < nodeCount; a++)
		{
			for (short b =  (short) (a + 1) ; b < nodeCount; b++)
			{
				short distance = (short) calculateDistance(a, b);

				// Store calculated values
				tmp = new Edge(a,b,distance);
				edges[a][b] = tmp;
				edges[b][a] = tmp;
			}
		}
	}

	/**
	 * @return the number of nodes in the graph.
	 */
	public int countNodes()
	{
		return nodeCount;
	}

	public int countEdges()
	{
		return edgeCount;
	}

	/**
	 * The distance between two points is computed as the Euclidean distance
	 * between the two points, rounded to the nearest integer.
	 * 
	 * The order of the nodes does not matter to the outcome.
	 * 
	 * @param nodeA
	 *            the first node.
	 * @param nodeB
	 *            the second node.
	 * @return the euclidean distance between two nodes.
	 */
	private long calculateDistance(int nodeA, int nodeB)
	{
		double xDiff = nodes[nodeA][0] - nodes[nodeB][0];
		double yDiff = nodes[nodeA][1] - nodes[nodeB][1];

		++distanceCounts; // For stats

		long distance = Math.round(Math.sqrt(xDiff * xDiff + yDiff * yDiff));

		return distance;
	}

	public long distance(int nodeA, int nodeB)
	{
		return edges[nodeA][nodeB].length;
	}

	/**
	 * @param naiveTour
	 * @return
	 */
	public int calculateLength(Tour naiveTour)
	{
		int length = 0;
		for (int i = 1; i < naiveTour.getLength(); i++)
		{
			length += distance(naiveTour.getNode(i - 1), naiveTour.getNode(i));
		}
		return length;
	}
	
	/**
	 * @param g
	 * @return
	 */
	public Edge[] createEdgeList(Graph g)
	{
		Edge[] edges = new Edge[g.countEdges()];
		int ep = 0;
		for (short a = 0; a < g.countNodes(); a++)
		{
			for (short b = (short) (a + 1); b < g.countNodes(); b++)
			{
				edges[ep++] = new Edge(a, b, (short) g.distance(a, b));
			}
		}
		return edges;
	}
	
	/*
	 * 
	 * 
	 */
	public class Edge implements Comparable<Edge>
	{
		public final short nodeA;
		public final short nodeB;
		public final short length;

		public Edge(short a, short b, short l)
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

		public String toString()
		{
			return nodeA + "-[" + length + "]-" + nodeB;
		}
	}
}
