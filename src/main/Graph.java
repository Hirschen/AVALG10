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
	private short[][] edges;
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
		edges = new short[nodeCount][nodeCount];
		edgeCount = (nodeCount * (nodeCount - 1)) / 2;

		// Precalculate edges
		for (int a = 0; a < nodeCount; a++)
		{
			for (int b = a + 1; b < nodeCount; b++)
			{
				short distance = (short) calculateDistance(a, b);

				// Store calculated values
				edges[a][b] = distance;
				edges[b][a] = distance;
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
		return edges[nodeA][nodeB];
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
