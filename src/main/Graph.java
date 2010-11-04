package main;

import java.util.Arrays;

/**
 * // TODO: Graph is a ...
 * 
 * @author Martin Nycander
 * @since
 */
public class Graph
{
	private double[][] nodes;
	private Edge[][] edges;

	private int nodeCount;
	private int edgeCount;

	private Edge[] sortedEdges;// TODO: Remove
	protected Edge[][] neighbours;

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

		// Precalculate edges
		for (short a = 0; a < nodeCount; a++)
		{
			edges[a][a] = new Edge(a, a, 0);
			for (short b = (short) (a + 1); b < nodeCount; b++)
			{
				// assert (distance < Integer.MAX_VALUE);
				int dist = calculateDistance(a, b);
				
				// Store calculated values
				edges[a][b] = new Edge(a, b, dist);
				edges[b][a] = new Edge(b, a, dist);
			}
		}
	}

	public Graph(Kattio io)
	{
		nodeCount = io.getInt();
		nodes = new double[nodeCount][2];

		edgeCount = (nodeCount * (nodeCount - 1)) / 2;
		edges = new Edge[nodeCount][nodeCount];
		
		
		double width = 0;
		double height = 0;
		
		// Read and store nodes
		for (short a = 0; a < nodeCount; a++)
		{
			// TODO: Do we really use nodes[][]?
			nodes[a][0] = io.getDouble();
			nodes[a][1] = io.getDouble();
			
			if (nodes[a][0] > width)
				width = nodes[a][0];
			if (nodes[a][1] > height)
				height = nodes[a][1];

			// Precalculate edges
			edges[a][a] = new Edge(a, a, 0);
			for (short b = (short) (a - 1); b >= 0; b--)
			{
				int dist = calculateDistance(a, b);
				edges[a][b] = new Edge(a, b, dist);
				edges[b][a] = new Edge(b, a, dist);
			}
		}
		int size = (int) ((width + height) / 2);
		int sizePerNode = size / nodeCount;
		int neighBourThreshold = sizePerNode * 2;
		neighbours = new Edge[nodeCount][4];
		System.out.println("Searching for " + neighbours[0].length + " neighbours with distance threshold " + neighBourThreshold);

		for (short a = 0; a < nodeCount; a++)
		{
			short b = 0;
			for (int neighbor = 0; neighbor < neighbours[a].length; neighbor++)
			{
				while (edges[a][b].length > neighBourThreshold)
				{
					if (++b == nodeCount)
					{
						neighBourThreshold *= 2;
						if (Main.verbose)
							System.out.println("Increasing search threshold to " + neighBourThreshold);
						b = 0;
						neighbor = 0;
					}
				}
				neighbours[a][neighbor] = edges[a][b];
				if (++b == nodeCount)
				{
					neighBourThreshold *= 2;
					if (Main.verbose)
						System.out.println("Increasing search threshold to " + neighBourThreshold);
					b = 0;
					neighbor = 0;
				}
			}
		}
		int a = 1;
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
	private int calculateDistance(int nodeA, int nodeB)
	{
		double xDiff = nodes[nodeA][0] - nodes[nodeB][0];
		double yDiff = nodes[nodeA][1] - nodes[nodeB][1];

		long distance = Math.round(Math.sqrt(xDiff * xDiff + yDiff * yDiff));
		return (int) distance;
	}

	public int distance(int nodeA, int nodeB)
	{
		return edges[nodeA][nodeB].length;
	}

	/**
	 * @param tour
	 * @return
	 */
	public int calculateLength(Tour tour)
	{
		int length = 0;
		for (Edge e : tour)
		{
			length += e.length;
		}
		return length;
	}
	
	/**
	 * @param g
	 * @return
	 */
	public Edge getEdge(int a, int b)
	{
		return edges[a][b];
	}

	public Edge[][] getEdges()
	{
		return edges;
	}

	/**
	 * @param g
	 * @return
	 */
	public Edge[] getSortedEdgeList()
	{
		if (sortedEdges != null)
			return sortedEdges;

		sortedEdges = new Edge[edgeCount];
		int ep = 0;
		for (int a = 0; a < nodeCount; a++)
		{
			for (int b = a + 1; b < nodeCount; b++)
			{
				sortedEdges[ep++] = edges[a][b];
			}
		}
		Arrays.sort(sortedEdges);
		return sortedEdges;
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

}
