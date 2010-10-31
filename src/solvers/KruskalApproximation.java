package solvers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import main.Graph;
import main.Tour;
import main.Graph.Edge;

/**
 * <a href="http://en.wikipedia.org/wiki/Kruskal's_algorithm">Kruskal's
 * algorithm - Wikipedia</a>
 * 
 * 
 * @author Martin Nycander
 * @since
 */
public class KruskalApproximation implements StartApproxer
{
	private static final boolean verbose = true;

	/**
	 * 
	 */
	public KruskalApproximation()
	{

	}

	/* (non-Javadoc)
	 * @see solvers.StartApproxer#getTour(main.Graph)
	 */
	public Tour getTour(Graph g)
	{
		Edge[] edges = g.createEdgeList(g);
		Arrays.sort(edges);

		if (verbose)
		{
			System.out.println("Graph: " + g);
			System.out.println("Edges: " + Arrays.toString(edges));
		}

		HashMap<Short, Set<Edge>> forest = new HashMap<Short, Set<Edge>>();
		for (int ep = 0; ep < edges.length; ep++)
		{
			// remove an edge with minimum weight from S
			Edge e = edges[ep];

			// make sure that edge connects two different trees.
			Set<Edge> treeA = forest.get(e.nodeA);
			Set<Edge> treeB = forest.get(e.nodeB);
			if (treeA != null && treeA == treeB)
			{
				continue;
			}

			// then add it to the forest, combining two trees into a single tree
			Set<Edge> tree = new HashSet<Edge>();
			if (treeA != null)
			{
				tree.addAll(treeA);
			}
			if (treeB != null)
			{
				tree.addAll(treeB);
			}
			tree.add(e);

			for (Edge te : tree)
			{
				forest.put(te.nodeA, tree);
				forest.put(te.nodeB, tree);
			}

			if (tree.size() == g.countNodes() - 1)
			{
				if (verbose)
					System.out.println("MST: " + tree);
				return buildResult(new ArrayList<Edge>(tree), g);
			}
		}

		return null;
	}

	/**
	 * @param mst
	 * @return
	 */
	private Tour buildResult(List<Edge> mst, Graph graph)
	{
		LinkedList<Edge> tour = new LinkedList<Edge>();

		tour.add(mst.get(0));
		for (int i = 1; i < mst.size(); i++)
		{
			Edge edge = mst.get(i);

			if (verbose)
				System.out.println(" inc. Tour: " + tour);

			Edge insertedNode = null;
			int insertAt = -1;
			// Find any of the nodes
			for (Edge e : tour)
			{
				if (edge.nodeA == e.nodeB)
				{
					insertAt = tour.indexOf(e) + 1;
					insertedNode = edge;
					break;
				}
				if (edge.nodeB == e.nodeA)
				{
					insertAt = tour.indexOf(e);
					insertedNode = edge;
					break;
				}
				if (edge.nodeA == e.nodeA)
				{
					insertAt = tour.indexOf(e);
					insertedNode = graph.new Edge(edge.nodeB, edge.nodeA, edge.length);
					break;
				}
				if (edge.nodeB == e.nodeB)
				{
					insertAt = tour.indexOf(e) + 1;
					insertedNode = graph.new Edge(edge.nodeB, edge.nodeA, edge.length);
					break;
				}
			}
			if (insertAt == -1)
			{
				insertAt = 0;
			}

			if (insertAt < tour.size())
			{
				Edge neighbour = tour.get(insertAt);
				if (insertedNode.nodeB != neighbour.nodeA)
					tour.add(insertAt, graph.new Edge(insertedNode.nodeB, neighbour.nodeA, edge.length));
			}
			tour.add(insertAt, insertedNode);
		}
		// tour.add(new Edge(tour.getFirst().nodeB, tour.getLast().nodeA,
		// (short) graph.distance(tour.getFirst().nodeB,
		// tour.getLast().nodeA)));
		if (verbose)
			System.out.println("Tour: " + tour);

		Tour t = new Tour(tour.size() + 2);
		for (int i = 0; i < tour.size(); i++)
		{
			t.setNode(i, tour.get(i).nodeA);
		}
		t.setNode(tour.size(), tour.getLast().nodeB);
		t.setNode(tour.size() + 1, tour.getFirst().nodeA);

		return t;
	}


	public static void main(String[] args)
	{
		/* Simple graph */
		double[][] coords = new double[][] { { 0, 3 }, { 3, 0 }, { 3, 3 }, { 3, 6 }, { 6, 3 } };
		Graph g = new Graph(coords);

		StartApproxer sa = new KruskalApproximation();
		Tour t = sa.getTour(g);
		System.out.println(t);
		/**/
		/* Graph with branching * /
		double[][] coords = new double[][] { { 0, 0 }, { 0, 2 }, { 1, 1 }, { 4, 1 } };
		Graph g = new Graph(coords);
		StartApproxer sa = new KruskalApproximation();
		System.out.println(sa.getTour(g));
		/**/
	}
}
