package main;

import java.util.Arrays;

/**
 * The Graph class is a data structure for storing complete symmetric weighted
 * graphs.
 * 
 * @author Martin Nycander
 * @since
 */
public class Graph
{
	private final double[][] nodes;
	private final int[][] edges;
	private int[][] neighbours;

	private final int nodeCount;
	private final int edgeCount;

	private int maxCoordinate = 0;

	public Graph(Kattio io)
	{
		nodeCount = io.getInt();
		nodes = new double[nodeCount][2];

		edgeCount = (nodeCount * (nodeCount - 1)) / 2;
		edges = new int[nodeCount][nodeCount];

		int max = 0;

		// Read and store nodes
		for (short a = 0; a < nodeCount; a++)
		{
			nodes[a][0] = io.getDouble();
			nodes[a][1] = io.getDouble();

			if (nodes[a][0] > maxCoordinate)
				max = (int) nodes[a][0];
			if (nodes[a][1] > maxCoordinate)
				max = (int) nodes[a][1];

			// Precalculate edges
			edges[a][a] = 0;
			for (short b = 0; b < a; b++)
			{
				final int dist = calculateDistance(a, b);
				edges[a][b] = dist;
				edges[b][a] = dist;
			}
		}
		maxCoordinate = max;
	}

	/**
	 * Constructs a graph object given a set of coordinates.
	 * 
	 * @param coordinates
	 *            is an array of coordinates in the format matrix[size] = {x, y}
	 */
	public Graph(double[][] coordinates)
	{
		nodeCount = coordinates.length;
		nodes = coordinates;

		edges = new int[nodeCount][nodeCount];
		edgeCount = (nodeCount * (nodeCount - 1)) / 2;

		// Precalculate edges
		for (short a = 0; a < nodeCount; a++)
		{
			edges[a][a] = 0;
			for (short b = (short) (a + 1); b < nodeCount; b++)
			{
				int dist = calculateDistance(a, b);

				// Store calculated values
				edges[a][b] = dist;
				edges[b][a] = dist;
			}
		}
	}

	/**
	 * @return the number of nodes in the graph.
	 */
	public final int countNodes()
	{
		return nodeCount;
	}

	public final int countEdges()
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
	private final int calculateDistance(int nodeA, int nodeB)
	{
		double xDiff = nodes[nodeA][0] - nodes[nodeB][0];
		double yDiff = nodes[nodeA][1] - nodes[nodeB][1];

		return (int) Math.round(Math.sqrt((xDiff * xDiff + yDiff * yDiff)));
	}

	public final int distance(int nodeA, int nodeB)
	{
		return edges[nodeA][nodeB];
	}

	/**
	 * @param tour
	 * @return
	 */
	public int calculateLength(Tourable tour)
	{
		int length = 0;
		short first = tour.getNode(0);
		short previous = first;
		for (short i = 1; i < tour.countNodes(); i++)
		{
			short current = tour.getNode(i);
			length += edges[previous][current];
			previous = current;
		}
		if (previous != first)
			length += edges[previous][first];
		return length;
	}

	public int calculateLength(int[] tour)
	{
		int length = 0;
		int first = tour[0];
		int previous = first;
		for (short i = 1; i < tour.length; i++)
		{
			int current = tour[i];
			length += edges[previous][current];
			previous = current;
		}
		if (previous != first)
			length += edges[previous][first];
		return length;

	}

	/**
	 * @param a
	 * @return
	 */
	public double getX(int a)
	{
		return nodes[a][0];
	}

	/**
	 * @param a
	 * @return
	 */
	public double getY(int a)
	{
		return nodes[a][1];
	}

	/**
	 * Calculates how much distance is saved for every pair of non-hub nodes.
	 * Used by the clarke-wright heuristics.
	 * 
	 * @param graph
	 *            the graph containing the nodes.
	 * @return a sorted list of all savings, first ten bits is node a, second 10
	 *         bits are node b, and the rest is the amount of savings.
	 */
	public long[] calculateSavings(short hubNode)
	{
		final int nodes = nodeCount - 1;
		final long[] savings = new long[nodes * (nodes - 1) / 2];
		int ep = 0;
		for (short a = 0; a < nodeCount; a++)
		{
			if (a == hubNode)
				continue;

			final int hubNodeToA = edges[hubNode][a];

			for (short b = (short) (a + 1); b < nodeCount; b++)
			{
				if (b == hubNode)
					continue;

				int save = hubNodeToA + edges[hubNode][b] - edges[a][b];

				long v = ((long) save << 20) + ((int) a << 10) + b;
				savings[ep++] = -v;
			}
		}
		Arrays.sort(savings);
		return savings;
	}

	/**
	 * @param hub
	 * @return
	 */
	public long sumEdges(int hub)
	{
		long sum = 0;
		for (int a = 0; a < nodeCount; a++)
			sum += edges[hub][a];
		return sum;
	}

	/**
	 * @param max
	 * @return
	 */
	public int[][] calculateApproximateNeighbours(int max)
	{
		if (nodeCount == 1)
		{
			return new int[][] { new int[] { edges[0][0] } };
		}

		int neighBourThreshold = max / 4;
		int neighbourCount = Math.min(nodeCount, 14);

		int[][] neighbours = new int[nodeCount][neighbourCount];
		if (Main.verbose)
			System.out.println("Searching for " + neighbours[0].length + " neighbours with distance threshold " + neighBourThreshold);

		for (short a = 0; a < nodeCount; a++)
		{
			int b = (a + 1) % nodeCount;
			int startB = b;
			for (int neighbour = 0; neighbour < neighbours[a].length; neighbour++)
			{
				// Find a worthy neighbour
				while (edges[a][b] > neighBourThreshold || a == b)
				{
					b = (b + 1 == nodeCount ? 0 : b + 1);

					// If we have looked at all nodes, increase the threshold
					if (b == a)
					{
						neighBourThreshold *= 2;
						if (Main.verbose)
							System.out.println("Increasing threshold to " + neighBourThreshold + "...");
						b = startB;
					}
				}

				// Add this as a neighbour
				neighbours[a][neighbour] = b;
				b = (b == nodeCount - 1 ? 0 : b + 1);

				// If we have looked at all nodes, increase the threshold
				if (b == a)
				{
					neighBourThreshold *= 2;
					if (Main.verbose)
						System.out.println("Increasing threshold to " + neighBourThreshold + "... .. ");
					b = startB;
				}
			}
		}
		return neighbours;
	}

	/**
	 * @return the neighbours
	 */
	public int[][] getNeighbours()
	{
		if (neighbours == null)
		{
			neighbours = calculateApproximateNeighbours(maxCoordinate);
		}
		return neighbours;
	}

	/**
	 * @return the neighbours
	 */
	public int[] getNeighbours(int node)
	{
		if (neighbours == null)
		{
			neighbours = calculateApproximateNeighbours(maxCoordinate);
		}
		return neighbours[node];
	}
}
