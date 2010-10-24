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
	private double[][] distance;
	private int nodes;

	/**
	 * 
	 */
	public Graph(double[][] distances)
	{
		distance = distances;
		nodes = distances.length;
	}

	public int countNodes()
	{
		return nodes;
	}

	public double distance(int nodeA, int nodeB)
	{
		double xDiff = Math.abs(distance[nodeA][0] - distance[nodeB][0]);
		double yDiff = Math.abs(distance[nodeA][1] - distance[nodeB][1]);

		++distanceCounts; // For stats

		return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
	}

}
