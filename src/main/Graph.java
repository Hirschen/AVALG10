package main;

/**
 * // TODO: Graph is a ... LOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOL
 * 
 * @author Martin Nycander
 * @since 
 */
public class Graph
{
	private int distanceCounts = 0;
	private double[][] distance;
	private int nodes;

	/**
	 * Constructs a graph object given a set of coordinates.
	 * 
	 * @param coordinates
	 *            is an array of coordinates in the format matrix[size] = {x, y}
	 */
	public Graph(double[][] coordinates)
	{
		distance = coordinates;
		nodes = coordinates.length;
	}

	/**
	 * @return the number of nodes in the graph.
	 */
	public int countNodes()
	{
		return nodes;
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
		double xDiff = distance[nodeA][0] - distance[nodeB][0];
		double yDiff = distance[nodeA][1] - distance[nodeB][1];

		++distanceCounts; // For stats

		return Math.round((Math.sqrt(xDiff * xDiff + yDiff * yDiff)) );
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
