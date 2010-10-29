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
	private Short[][] edges;
	private int nodeCount;

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
		edges = new Short[nodeCount][nodeCount];
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
		return (nodeCount * (nodeCount - 1)) / 2;
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
	public long distance(int nodeA, int nodeB)
	{
		// If this distance has been calculated before, return the cached value.
		if (edges[nodeA][nodeB] != null)
			return edges[nodeA][nodeB]; // TODO: Calculate this list in the
										// beginning.

		double xDiff = nodes[nodeA][0] - nodes[nodeB][0];
		double yDiff = nodes[nodeA][1] - nodes[nodeB][1];

		++distanceCounts; // For stats

		long distance = Math.round(Math.sqrt(xDiff * xDiff + yDiff * yDiff));

		// Sanity check for mem. optimization
		assert (distance < Short.MAX_VALUE);
		short d = (short) distance;

		// Store calculated values
		edges[nodeA][nodeB] = d;
		edges[nodeB][nodeA] = d;

		return distance;
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
}
