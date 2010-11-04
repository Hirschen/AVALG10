package solvers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import main.Edge;
import main.Graph;
import main.GraphVisualizer;
import main.Main;
import main.Tour;

/**
 * ClarkeWrightApproximation is a variation of the Clarke-Wright savings
 * algorithm. It creates a start approximation for the tsp problem. It find a
 * random node, which is calls the "hub node". It then draws paths to and from
 * every other node to the "hub node". After that it can create a sub-optimal
 * tour by using the triangle inequality.
 * 
 * 
 * TODO: Extend Graph to get the best performance as possible? (only calculate
 * the edges we need).
 * 
 * TODO: Store "neighbors" of nodes then we dont have to have such a huge
 * savings-list.
 * 
 * TODO: Implement TourConstruction-object for all the merging and such.
 * 
 * TODO: Document the methods.
 * 
 * @author Martin Nycander
 * @since 2010-11-03
 */
public class ClarkeWrightApproximation implements StartApproxer
{
	private Graph graph;
	private Tour tour;
	private LinkedList<LinkedList<Edge>> edges;
	private int addedNodes = 0;

	private int hubNode;

	private GraphVisualizer gv;

	private final boolean measureTime = true;

	/**
	 * 
	 */
	public ClarkeWrightApproximation()
	{
	}

	/* (non-Javadoc)
	 * @see solvers.StartApproxer#getTour(main.Graph)
	 */
	public Tour getTour(Graph graph)
	{
		// Special case for one node.
		if (graph.countNodes() == 1)
		{
			Tour tour = new Tour(1);
			tour.addEdge(graph.getEdge(0, 0));
			return tour;
		}

		this.graph = graph;
		this.tour = new Tour();
		edges = new LinkedList<LinkedList<Edge>>();

		if (Main.verbose)
		{
			gv = new GraphVisualizer(graph, 6);
			gv.setTour(tour);
			gv.setHilightedEdges(edges);
		}

		// Take an arbitrarily good hub node
		hubNode = (int) Math.round(Math.random() * ((double) graph.countNodes() - 1));
		if (Main.verbose)
		{
			System.out.println("Hub node: " + hubNode);
		}

		double time = 0.0;
		if (Main.verbose || measureTime)
		{
			time = Main.time();
		}

		// Calculate savings for each non-hub node
		// savings: Save -> nodeA -> nodeB, order descending
		Saving[] savings = calculateSavings(graph);

		if (Main.verbose || measureTime)
		{
			System.out.println("Calculated savings in: " + Main.timeDiff(Main.time(), time) + " ms.");
			time = Main.time();
		}

		// Go through the non-hub city pairs in descending order of savings.
		int[] nonHubdegree = new int[graph.countNodes()];
		int sp = 0;
		while (sp < savings.length && !(addedNodes == graph.countNodes() - 1 && edges.size() == 1))
		{
			Saving saving = savings[sp++];

			int a = saving.edge.nodeA;
			int b = saving.edge.nodeB;

			if (tryToAddEdge(a, b, nonHubdegree))
			{
				nonHubdegree[a]++;
				nonHubdegree[b]++;
			}
		}

		if (Main.verbose || measureTime)
		{
			System.out.println("Found path in: " + Main.timeDiff(Main.time(), time) + " ms.");
			time = Main.time();
		}

		// Now merge all seperate tours
		Tour tour = new Tour(graph.countNodes());

		LinkedList<Edge> t = edges.getFirst();
		// add path to the hubNode
		tour.addEdge(graph.getEdge(hubNode, t.getFirst().nodeA));
		// add constructed tour
		for (Edge e : t)
		{
			tour.addEdge(e);
		}
		// add path to the hubNode
		tour.addEdge(graph.getEdge(t.getLast().nodeB, hubNode));

		if (Main.verbose || measureTime)
			System.out.println("Constructed tour in: " + Main.timeDiff(Main.time(), time) + " ms.");

		if (Main.verbose)
		{
			gv.setHilightedEdges(null);
			gv.setTour(tour);
			gv.repaint();
		}

		return tour;
	}

	/**
	 * Calculates how much distance is saved for every pair of non-hub nodes.
	 * 
	 * @param graph
	 *            the graph containing the nodes.
	 * @return a sorted list of all savings
	 */
	private Saving[] calculateSavings(Graph graph)
	{
		int nodes = graph.countNodes() - 1;
		Saving[] savings = new Saving[(nodes * (nodes - 1)) / 2];
		int p = 0;

		int nodeCount = graph.countNodes();
		for (int a = 0; a < nodeCount; a++)
		{
			if (a == hubNode)
				continue;

			int hubNodeToA = graph.getEdge(hubNode, a).length;

			for (int b = a + 1; b < nodeCount; b++)
			{
				if (b == hubNode)
					continue;
				Edge e = graph.getEdge(a, b);
				int save = hubNodeToA + graph.getEdge(hubNode, b).length - e.length;
				savings[p++] = new Saving(save, e);
			}
		}
		Arrays.sort(savings);
		return savings;
	}

	private final class Saving implements Comparable<Saving>
	{
		public final int saving;
		public final Edge edge;

		public Saving(int save, Edge e)
		{
			saving = save;
			edge = e;
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Saving s)
		{
			return (saving < s.saving ? 1 : -1);
		}
	}

	private boolean tryToAddEdge(int a, int b, int[] nonHubdegree)
	{
		// ...so it does not cause a non-hub city to become adjacent to more
		// than two other non-hub cities.
		if (nonHubdegree[a] >= 2 || nonHubdegree[b] >= 2)
		{
			if (Main.verbose)
				System.out.println("  " + a + "->" + b + ": Already has degree 2.");
			return false;
		}

		// ... or create a cycle of nonhub cities
		if (createsCycleOfNonHubNodes(a, b))
		{
			if (Main.verbose)
				System.out.println("  " + a + "->" + b + ": Would create a cycle.");
			return false;
		}

		if (Main.verbose)
		{
			System.out.println("+ " + a + "->" + b);
		}

		// Find all tours which might fit our edge
		ArrayList<LinkedList<Edge>> matches = new ArrayList<LinkedList<Edge>>();
		for (LinkedList<Edge> unFinishedTour : edges)
		{
			int start = unFinishedTour.getFirst().nodeA;
			int end = unFinishedTour.getLast().nodeB;

			if (a == start || a == end || b == start || b == end)
			{
				matches.add(unFinishedTour);
			}
		}

		if (matches.isEmpty())
		{
			// Create a new tour!
			LinkedList<Edge> l = new LinkedList<Edge>();
			l.add(graph.getEdge(a, b));
			edges.add(l);

			addedNodes += 2;
		}
		else if (matches.size() == 1)
		{
			// Append the edge to the matched tour!
			addToTour(a, b, matches.get(0));
			addedNodes++;
		}
		else if (matches.size() == 2)
		{
			// Stop. Mergetime!
			merge(a, b, matches);
		}
		else
		{
			throw new RuntimeException("Oh dear..");
		}

		if (Main.verbose)
		{
			// System.out.println("Tour after:  " + edges);
			System.out.println("Node count: " + addedNodes);
			gv.repaint();
			/* */
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/**/
		}

		return true;
	}

	/**
	 * @param a
	 * @param b
	 * @param matches
	 */
	private void merge(int a, int b, ArrayList<LinkedList<Edge>> matches)
	{
		if (Main.verbose)
		{
			System.out.println("\tMerging! Glue is: " + a + "->" + b);
			System.out.println("\tTours before: " + edges);
		}
		LinkedList<Edge> smallTour = matches.get(0);
		LinkedList<Edge> largeTour = matches.get(1);
		if (largeTour.size() < smallTour.size())
		{
			smallTour = matches.get(1);
			largeTour = matches.get(0);
		}

		addToTour(a, b, largeTour);

		boolean reverse = (smallTour.getLast().nodeB == largeTour.getLast().nodeB || smallTour.getFirst().nodeA == largeTour.getFirst().nodeA);
		boolean append = (smallTour.getLast().nodeB == largeTour.getLast().nodeB || largeTour.getLast().nodeB == smallTour.getFirst().nodeA);

		// Reverse the smallest and add it to the bigger tour
		while (!smallTour.isEmpty())
		{
			if (append)
			{
				if (reverse)
					largeTour.addLast(smallTour.removeLast().invert());
				else
					largeTour.addLast(smallTour.removeFirst());
			}
			else
			{
				if (reverse)
					largeTour.addFirst(smallTour.removeFirst().invert());
				else
					largeTour.addFirst(smallTour.removeLast());
			}
		}

		edges.remove(smallTour);
		if (Main.verbose)
		{
			System.out.println("\tTours after:  " + edges);
		}
	}

	/**
	 * @param a
	 * @param b
	 * @param unFinishedTour
	 */
	private boolean addToTour(int a, int b, LinkedList<Edge> unFinishedTour)
	{
		int start = unFinishedTour.getFirst().nodeA;
		int end = unFinishedTour.getLast().nodeB;

		if (a == end)
		{
			unFinishedTour.addLast(graph.getEdge(a, b));
			return true;
		}
		if (a == start)
		{
			unFinishedTour.addFirst(graph.getEdge(b, a));
			return true;
		}
		if (b == start)
		{
			unFinishedTour.addFirst(graph.getEdge(a, b));
			return true;
		}
		if (b == end)
		{
			unFinishedTour.addLast(graph.getEdge(b, a));
			return true;
		}
		return false;
	}

	/**
	 * @param nodes
	 * @param tour
	 * @return
	 */
	private boolean createsCycleOfNonHubNodes(int a, int b)
	{
		for (LinkedList<Edge> unFinishedTour : edges)
		{
			Edge start = unFinishedTour.getFirst();
			Edge end = unFinishedTour.getLast();

			if (start.nodeA == a && end.nodeB == b)
			{
				return true;
			}
			if (start.nodeA == b && end.nodeB == a)
			{
				return true;
			}
		}

		return false;
	}
}
