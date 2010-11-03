package solvers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Map.Entry;

import main.Edge;
import main.Graph;
import main.GraphVisualizer;
import main.Main;
import main.Tour;

/**
 * // TODO: ClarkeWrightApproximation is a ...
 * 
 * @author Martin Nycander
 * @since
 */
public class ClarkeWrightApproximation implements StartApproxer
{
	private static boolean debug;
	private Graph graph;
	private Tour tour;
	private LinkedList<LinkedList<Edge>> edges;
	private int addedNodes = 0;

	private int hubNode;

	private GraphVisualizer gv;

	/**
	 * 
	 */
	public ClarkeWrightApproximation()
	{
		debug = Main.verbose;
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
		if (debug)
		{
			gv = new GraphVisualizer(graph, 5);
			gv.setTour(tour);
			gv.setHilightedEdges(edges);
		}

		// Take an arbitrarily good hub node
		hubNode = (int) Math.round(Math.random() * ((double) graph.countNodes() - 1));
		if (debug)
		{
			System.out.println("Hub node: " + hubNode);
		}

		// Calculate savings for each non-hub node
		// savings: Save -> nodeA -> nodeB, order descending
		TreeMap<Integer, int[]> savings = calculateSavings(graph);

		// Go through the non-hub city pairs in descending order of savings.
		int[] nonHubdegree = new int[graph.countNodes()];
		// Performing the bypass ...
		Iterator<Entry<Integer, int[]>> iterator = savings.entrySet().iterator();
		while (iterator.hasNext() && !(addedNodes == graph.countNodes() - 1 && edges.size() == 1))
		{
			Entry<Integer, int[]> saving = iterator.next();

			int[] nodes = saving.getValue();
			int a = nodes[0];
			int b = nodes[1];

			if (tryToAddEdge(a, b, nonHubdegree))
			{
				nonHubdegree[a]++;
				nonHubdegree[b]++;
			}
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

		if (debug)
		{
			gv.setHilightedEdges(null);
			gv.setTour(tour);
			gv.repaint();
		}

		return tour;
	}

	/**
	 * @param graph
	 * @return
	 */
	private TreeMap<Integer, int[]> calculateSavings(Graph graph)
	{
		TreeMap<Integer, int[]> savings = new TreeMap<Integer, int[]>(new Comparator<Integer>()
		{
			public int compare(Integer a, Integer b)
			{
				return (a < b ? 1 : -1);
			}
		});
		for (int a = 0; a < graph.countNodes(); a++)
		{
			if (a == hubNode)
				continue;

			for (int b = a + 1; b < graph.countNodes(); b++)
			{
				if (b == hubNode)
					continue;

				int save = (int) (graph.distance(hubNode, a) + graph.distance(hubNode, b) - graph.distance(a, b));
				savings.put(save, new int[] { a, b });
			}
		}
		return savings;
	}

	private boolean tryToAddEdge(int a, int b, int[] nonHubdegree)
	{
		// ...so it does not cause a non-hub city to become adjacent to more
		// than two other non-hub cities.
		if (nonHubdegree[a] >= 2 || nonHubdegree[b] >= 2)
		{
			if (debug)
				System.out.println("  " + a + "->" + b + ": Already has degree 2.");
			return false;
		}

		// ... or create a cycle of nonhub cities
		if (createsCycleOfNonHubNodes(a, b))
		{
			if (debug)
				System.out.println("  " + a + "->" + b + ": Would create a cycle.");
			return false;
		}

		if (debug)
		{
			System.out.println("+ " + a + "->" + b);
		}

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
			LinkedList<Edge> l = new LinkedList<Edge>();
			l.add(graph.getEdge(a, b));
			edges.add(l);

			addedNodes += 2;
		}
		else if (matches.size() == 1)
		{
			addToTour(a, b, matches.get(0));
			addedNodes++;
		}
		else if (matches.size() == 2)
		{
			// Stop. Mergetime!
			if (debug)
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
						largeTour.addLast(smallTour.removeLast().getReverse());
					else
						largeTour.addLast(smallTour.removeFirst());
				}
				else
				{
					if (reverse)
						largeTour.addFirst(smallTour.removeFirst().getReverse());
					else
						largeTour.addFirst(smallTour.removeLast());
				}
			}

			edges.remove(smallTour);
			if (debug)
			{
				System.out.println("\tTours after:  " + edges);
			}
		}
		else
		{
			throw new RuntimeException("Oh dear..");
		}

		if (debug)
		{
			// System.out.println("Tour after:  " + edges);
			System.out.println("Node count: " + addedNodes);
			gv.repaint();
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return true;
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
