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
	private short[][] neighbours;

	private final int nodeCount;
	private final int edgeCount;

	public Graph(Kattio io)
	{
		nodeCount = io.getInt();
		nodes = new double[nodeCount][2];

		edgeCount = (nodeCount * (nodeCount - 1)) / 2;
		edges = new int[nodeCount][nodeCount];

		// Read and store nodes
		for (short a = 0; a < nodeCount; a++)
		{
			nodes[a][0] = io.getDouble();
			nodes[a][1] = io.getDouble();

			// Precalculate edges
			edges[a][a] = 0;
			for (short b = 0; b < a; b++)
			{
				final int dist = calculateDistance(a, b);
				edges[a][b] = dist;
				edges[b][a] = dist;
			}
		}

		if (!Main.calculateSavingsAndNeighboursTogether)
			neighbours = calculateNeighbours();
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
		if (!Main.calculateSavingsAndNeighboursTogether)
			neighbours = calculateNeighbours();
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

	public long[] calculateSavingsAndNeighbours(short hubNode)
	{
		final int self = 1;

		int neighbourCount = nodeCount - self;
		if (nodeCount > 900)
			neighbourCount = 16;
		else if (nodeCount > 750)
			neighbourCount = 22;
		else if (nodeCount > 650)
			neighbourCount = 23;
		else if (nodeCount > 500)
			neighbourCount = 35;
		else if (nodeCount > 200)
			neighbourCount = 65;


		neighbours = new short[nodeCount][neighbourCount];
		final int nonHubNodes = nodeCount - 1;
		final long[] savings = new long[nonHubNodes * (nonHubNodes - 1) / 2];
		int sp = 0;
		for (short a = 0; a < nodeCount; a++)
		{
			final int hubNodeToA = edges[hubNode][a];
			final boolean[] visited = new boolean[nodeCount];
			neighbours[a] = new short[neighbourCount];
			for (short n = 0; n < neighbourCount; n++)
			{
				// Find the closest neighbour linearly
				short minNode = -1;
				int minDist = Integer.MAX_VALUE;
				for (short b = 0; b < nodeCount; b++)
				{
					if (n == 0 && b > a && b != hubNode && a != hubNode)
					{
						// Calculate savings
						int save = hubNodeToA + edges[hubNode][b] - edges[a][b];
						long v = ((long) save << 20) + ((int) a << 10) + b;
						savings[sp++] = -v;
					}

					if (visited[b])
						continue;
					if (a == b)
						continue;

					int d = edges[a][b];
					if (d < minDist)
					{
						minDist = d;
						minNode = b;
					}
				}

				if (minNode == -1)
					throw new RuntimeException("Elände!");

				visited[minNode] = true;
				neighbours[a][n] = minNode;
			}

		}
		Arrays.sort(savings);
		return savings;
	}

	protected short[][] calculateNeighbours()
	{
		final int self = 1;
		int neighbourCount = nodeCount - self;
		if (nodeCount > 900)
			neighbourCount = 16;
		else if (nodeCount > 750)
			neighbourCount = 22;
		else if (nodeCount > 650)
			neighbourCount = 25;
		else if (nodeCount > 500)
			neighbourCount = 35;
		else if (nodeCount > 200)
			neighbourCount = 65;
		short[][] neighbours = new short[nodeCount][neighbourCount];

		for (short a = 0; a < nodeCount; a++)
		{
			boolean[] visited = new boolean[nodeCount];
			neighbours[a] = new short[neighbourCount];
			for (short i = 0; i < neighbourCount; i++)
			{
				// Find the closest neighbour linearly
				short minNode = -1;
				int minDist = Integer.MAX_VALUE;
				for (short b = 0; b < nodeCount; b++)
				{
					if (visited[b])
						continue;
					if (a == b)
						continue;

					int d = edges[a][b];
					if (d < minDist)
					{
						minDist = d;
						minNode = b;
					}
				}

				if (minNode == -1)
					throw new RuntimeException("Elände!");

				visited[minNode] = true;
				neighbours[a][i] = minNode;
			}

			// TODO: Sort!
		}

		return neighbours;
	}

	/**
	 * @return the neighbours
	 */
	public short[][] getNeighbours()
	{
		if (neighbours == null)
		{
			neighbours = calculateNeighbours();
		}
		return neighbours;
	}

	/**
	 * @return the neighbours
	 */
	public short[] getNeighbours(int node)
	{
		if (neighbours == null)
		{
			neighbours = calculateNeighbours();
		}
		return neighbours[node];
	}
}
