package solvers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import main.Edge;
import main.Graph;
import main.GraphVisualizer;
import main.Main;
import main.ShortTour;
import main.Tourable;

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
	private LinkedList<LinkedList<Edge>> edges;

	private short hubNode;

	private GraphVisualizer gv;

	private final boolean measureTime = false;

	/**
	 * 
	 */
	public ClarkeWrightApproximation()
	{
		edges = new LinkedList<LinkedList<Edge>>();
	}

	/* (non-Javadoc)
	 * @see solvers.StartApproxer#getTour(main.Graph)
	 */
	public Tourable getTour(Graph graph)
	{
		// Special case for one node.
		if (graph.countNodes() == 1)
		{
			Tourable tour = new ShortTour(1);
			tour.addNode((short) 0);
			return tour;
		}

		this.graph = graph;

		if (Main.verbose)
		{
			gv = new GraphVisualizer(graph, 6);
			gv.setHilightedEdges(edges);
		}

		// Take an arbitrarily good hub node
		hubNode = (short) Math.round(Math.random() * ((double) graph.countNodes() - 1));
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
		int edgeGoal = graph.countNodes() - 2;
		int addedEdges = 0;
		while (sp < savings.length && addedEdges < edgeGoal)
		{
			Saving saving = savings[sp++];

			int a = saving.edge.nodeA;
			int b = saving.edge.nodeB;

			// ...so it does not cause a non-hub city to become adjacent to more
			// than two other non-hub cities.
			if (nonHubdegree[a] >= 2 || nonHubdegree[b] >= 2)
			{
				if (Main.verbose)
					System.out.println("  " + a + "->" + b + ": Already has degree 2.");
				continue;
			}

			// ... or create a cycle of nonhub cities
			if (createsCycleOfNonHubNodes(a, b))
			{
				if (Main.verbose)
					System.out.println("  " + a + "->" + b + ": Would create a cycle.");
				continue;
			}

			addEdge(a, b, nonHubdegree);
			nonHubdegree[a]++;
			nonHubdegree[b]++;
		}

		if (Main.verbose || measureTime)
		{
			System.out.println("Found path in: " + Main.timeDiff(Main.time(), time) + " ms.");
			time = Main.time();
		}

		// Let's create a tour!
		LinkedList<Edge> t = edges.getFirst();
		Tourable tour = new ShortTour(graph.countNodes());
		// add path to the hubNode
		tour.addNode(hubNode);
		// add constructed tour
		for (Edge e : t)
		{
			tour.addNode(e.nodeA);
		}
		// add path to the hubNode
		tour.addNode(t.getLast().nodeB);

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
		int nodeCount = graph.countNodes();
		Saving[] savings = new Saving[nodes * (nodes - 1) / 2];
		int ep = 0;
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

				savings[ep++] = new Saving(save, e);
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
			if (s == null)
				return -1;

			return (saving < s.saving ? 1 : -1);
		}
	}

	private void addEdge(int a, int b, int[] nonHubdegree)
	{
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
		}
		else if (matches.size() == 1)
		{
			// Append the edge to the matched tour!
			addToTour(a, b, matches.get(0));
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
			gv.repaint();
			/* */
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/**/
		}
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
