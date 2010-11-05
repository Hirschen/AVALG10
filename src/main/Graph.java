package main;

import java.util.Arrays;

import solvers.ClarkeWrightApproximation;
import solvers.ClarkeWrightApproximation.Saving;

/**
 * The Graph class is a data structure for storing complete symmetric weighted
 * graphs.
 * 
 * 
 * TODO: Vaska riktade kanter? (Vi behöver inte hålla koll på riktningen
 * egentligen, det ger bara en massa extra arbete)
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

		edges = new Edge[nodeCount][nodeCount];
		edgeCount = (nodeCount * (nodeCount - 1)) / 2;

		// Precalculate edges
		for (short a = 0; a < nodeCount; a++)
		{
			edges[a][a] = new Edge(a, a, 0);
			for (short b = (short) (a + 1); b < nodeCount; b++)
			{
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

		// Read and store nodes
		for (short a = 0; a < nodeCount; a++)
		{
			nodes[a][0] = io.getDouble();
			nodes[a][1] = io.getDouble();

			// Precalculate edges
			edges[a][a] = new Edge(a, a, 0);
			for (short b = 0; b < a; b++)
			{
				int dist = calculateDistance(a, b);
				edges[a][b] = new Edge(a, b, dist);
				edges[b][a] = new Edge(b, a, dist);
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
	private int calculateDistance(int nodeA, int nodeB)
	{
		double xDiff = nodes[nodeA][0] - nodes[nodeB][0];
		double yDiff = nodes[nodeA][1] - nodes[nodeB][1];

		return (int) Math.round(Math.sqrt(xDiff * xDiff + yDiff * yDiff));
	}

	public int distance(int nodeA, int nodeB)
	{
		return edges[nodeA][nodeB].length;
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
			length += edges[previous][current].length;
			previous = current;
		}
		if (previous != first)
			length += edges[previous][first].length;
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
		Edge[] sortedEdges = new Edge[edgeCount];
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

	/**
	 * Calculates how much distance is saved for every pair of non-hub nodes.
	 * 
	 * @param graph
	 *            the graph containing the nodes.
	 * @return a sorted list of all savings
	 */
	public Saving[] calculateSavings(ClarkeWrightApproximation cw, short hubNode)
	{
		int nodes = nodeCount - 1;
		Saving[] savings = new Saving[nodes * (nodes - 1) / 2];
		int ep = 0;
		for (short a = 0; a < nodeCount; a++)
		{
			if (a == hubNode)
				continue;

			int hubNodeToA = edges[hubNode][a].length;

			for (short b = (short) (a + 1); b < nodeCount; b++)
			{
				if (b == hubNode)
					continue;

				int save = hubNodeToA + edges[hubNode][b].length - edges[a][b].length;
				savings[ep++] = cw.new Saving(save, a, b);
			}
		}
		Arrays.sort(savings);
		return savings;
	}

	/**
	 * @param width
	 * @param height
	 * @return
	 */
	public Edge[][] calculateApproximateNeighbours(double width, double height)
	{
		if (nodeCount == 1)
		{
			return new Edge[][] { new Edge[] { edges[0][0] } };
		}
		int size = (int) Math.max(width, height) / 2;
		int neighBourThreshold = size + 1;

		int neighbourCount = nodeCount - 1;
		if (nodeCount > 100)
			neighbourCount = 25;
		else if (nodeCount > 500)
			neighbourCount = 50;
		else if (nodeCount > 750)
			neighbourCount = 100;

		Edge[][] neighbours = new Edge[nodeCount][neighbourCount];
		System.out.println("Searching for " + neighbours[0].length + " neighbours with distance threshold " + neighBourThreshold);

		for (short a = 0; a < nodeCount; a++)
		{
			int b = (a + 1) % nodeCount;
			int startB = b;
			for (int neighbour = 0; neighbour < neighbours[a].length; neighbour++)
			{
				// Find a worthy neighbour
				while (edges[a][b].length > neighBourThreshold || a == b)
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
				neighbours[a][neighbour] = edges[a][b];
				b = (b == nodeCount - 1 ? 0 : b + 1);

				// If we have looked at all nodes, increase the threshold
				if (b == a)
				{
					neighBourThreshold *= 2;
					if (Main.verbose)
						System.out.println("Increasing threshold to " + neighBourThreshold + "...");
					b = startB;
				}
			}
		}
		return neighbours;
	}
}
