package solvers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import main.Edge;
import main.Graph;
import main.GraphVisualizer;
import main.Main;
import main.Tour;

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
	private static final boolean verbose = false;

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
		// Special case for one node.
		if (g.countNodes() == 1)
		{
			Tour tour = new Tour(1);
			tour.addEdge(g.getEdge(0, 0));
			return tour;
		}

		HashSet<Edge> mst = findMinimumSpanningTree(g);

		double time = Main.time();

		Tour t = buildResult(new ArrayList<Edge>(mst), g);

		if (verbose)
		{
			System.out.println("Built result in: " + Main.timeDiff(Main.time(), time) + " ms.");
		}
		return t;
	}

	/**
	 * @param g
	 */
	@SuppressWarnings("unchecked")
	private HashSet<Edge> findMinimumSpanningTree(Graph g)
	{
		Edge[] edges = g.getSortedEdgeList();

		double time;
		if (verbose)
		{
			System.out.println("Graph: " + g);
			// System.out.println("Edges: " + Arrays.toString(edges));
			time = Main.time();
		}


		HashSet<Edge>[] forest = new HashSet[g.countNodes()];
		HashSet<Edge> treeA;
		HashSet<Edge> treeB;
		for (int ep = 0; ep < edges.length; ep++)
		{
			// remove an edge with minimum weight from S
			Edge e = edges[ep];

			// make sure that edge connects two different trees.
			treeA = forest[e.nodeA];
			treeB = forest[e.nodeB];
			if (treeA != null && treeA == treeB)
			{
				continue;
			}

			// combine the two trees into a single tree
			HashSet<Edge> tree = null;
			if (treeA != null && treeB == null)
			{
				tree = treeA;
			}
			else if (treeA == null && treeB != null)
			{
				tree = treeB;
			}
			else if (treeA != null && treeB != null)
			{
				treeA.addAll(treeB);
				for (Edge te : treeB)
				{
					forest[te.nodeA] = treeA;
					forest[te.nodeB] = treeA;
				}
				tree = treeA;
			}
			else
			// treeA == null && treeB == null
			{
				tree = new HashSet<Edge>();
			}
			
			// then add it to the tree and update the forest
			tree.add(e);
			forest[e.nodeA] = tree;
			forest[e.nodeB] = tree;

			if (tree.size() == g.countNodes() - 1)
			{
				if (verbose)
				{
					System.out.println("MST: " + tree);
					System.out.println("Found MST in: " + Main.timeDiff(Main.time(), time) + " ms.");
				}
				return tree;
			}
		}
		throw new RuntimeException("Could not find a minimum spanning tree. This should be impossible in a complete graph.");
	}

	private Tour buildTour(HashSet<Edge> mst, Graph graph)
	{
		// Find the node with the most edges
		int centerNode = 0;
		int highestDegree = -1;

		for (int node = 0; node < graph.countNodes(); node++)
		{
			int degree = 0;
			for (Edge edge : mst)
			{
				if (edge.nodeA == node)
					degree++;
				if (edge.nodeB == node)
					degree++;
			}

			if (degree > highestDegree)
			{
				highestDegree = degree;
				centerNode = node;
			}
		}

		// System.out.println("Center node: " + centerNode + " (degree " +
		// highestDegree + ")");

		// Draw a tour
		Tour tour = new Tour((graph.countNodes() - 1) * 2);
		for (int node = 0; node < graph.countNodes(); node++)
		{
			if (node == centerNode)
				continue;

			tour.addEdge(graph.getEdge(centerNode, node));
			tour.addEdge(graph.getEdge(node, centerNode));
		}

		return tour;
	}
	
	/**
	 * @param mst
	 * @return
	 */
	@SuppressWarnings("unused")
	private Tour buildResult(List<Edge> mst, Graph graph)
	{
		Tour tour = new Tour();
		tour.addEdge(mst.get(0));

		List<Edge> todo = new ArrayList<Edge>(mst);

		// Go over all the edges in the minimum spanning tree
		for (int i = 1; i < mst.size(); i++)
		{
			Edge edge = mst.get(i);

			if (verbose)
				System.out.println(" inc. Tour: " + tour);

			Edge insertedNode = null;
			int insertAt = -1;
			// Find any connecting edge
			Iterator<Edge> it = todo.iterator();
			while (it.hasNext())
			{
				Edge e = it.next();
				if (edge.nodeA == e.nodeB)
				{
					insertAt = tour.indexOf(e) + 1;
					insertedNode = edge;
					it.remove();
					break;
				}
				if (edge.nodeB == e.nodeA)
				{
					insertAt = tour.indexOf(e);
					insertedNode = edge;
					it.remove();
					break;
				}
				if (edge.nodeA == e.nodeA)
				{
					insertAt = tour.indexOf(e);
					insertedNode = new Edge(edge.nodeB, edge.nodeA, edge.length);
					it.remove();
					break;
				}
				if (edge.nodeB == e.nodeB)
				{
					insertAt = tour.indexOf(e) + 1;
					insertedNode = new Edge(edge.nodeB, edge.nodeA, edge.length);
					it.remove();
					break;
				}
			}
			if (insertAt == -1)
			{
				insertAt = 0;
			}

			if (insertAt < tour.countEdges())
			{
				Edge neighbour = tour.getEdge(insertAt);
				if (insertedNode.nodeB != neighbour.nodeA)
				{
					tour.addEdge(insertAt, new Edge(insertedNode.nodeB, neighbour.nodeA, edge.length));
				}
			}
			tour.addEdge(insertAt, insertedNode);
		}
		tour.addEdge(graph.getEdge(tour.getLast().nodeB, tour.getFirst().nodeA));

		if (verbose)
			System.out.println("Tour: " + tour);

		return tour;
	}

	public static void main(String[] args) throws Exception
	{
		/* Kattis * /
		Problem p = new Problem(new File("testdata/pr2392.tsp"), new File("testdata/pr2392.opt.tour"));
		Graph g = new Graph(p.coordinates);
		StartApproxer sa = new KruskalApproximation();
		Tour t = sa.getTour(g);
		System.out.println(t);
		/**/
		/* Simple graph */
		double[][] coords = new double[][] { { 0, 3 }, { 3, 0 }, { 3, 3 }, { 3, 6 }, { 6, 3 } };
		Graph g = new Graph(coords);

		StartApproxer sa = new KruskalApproximation();
		Tour t = sa.getTour(g);
		System.out.println(t);
		/**/
		/* Graph with branching * /
		double[][] coords = new double[][] { { 2, 2 }, { 2, 4 }, { 3, 3 }, { 6, 3 } };
		Graph g = new Graph(coords);
		StartApproxer sa = new KruskalApproximation();
		System.out.println(sa.getTour(g));
		/**/
		new GraphVisualizer(g, 10).setTour(t);
	}
}
