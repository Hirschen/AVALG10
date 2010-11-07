package solvers;

import java.util.Random;

import main.Graph;
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
 * TODO: Document all methods in Clarke-Wright.
 * 
 * @author Martin Nycander
 * @since 2010-11-03
 */
public class ClarkeWrightApproximation implements StartApproxer
{
	private Graph graph;
	private TourConstruction tour;
	private short hubNode;

	/**
	 * 
	 */
	public ClarkeWrightApproximation()
	{
	}

	/* (non-Javadoc)
	 * @see solvers.StartApproxer#getTour(main.Graph)
	 */
	public final Tourable getTour(Graph g)
	{
		// Special case for one node.
		if (g.countNodes() == 1)
		{
			Tourable tour = new ShortTour(1);
			tour.addNode((short) 0);
			return tour;
		}

		graph = g;
		tour = new TourConstruction(g);

		// Take <s>an arbitrarily</a> a really good hub node
		hubNode = getHubNode();

		if (Main.verbose)
		{
			// new GraphVisualizer(graph).setTourConstruction(tour);
			System.out.println("Hub node: " + hubNode);
		}

		// Calculate savings for each non-hub node
		// savings: Save -> nodeA -> nodeB, order descending
		long[] savings = graph.calculateSavings(hubNode);
		final int savingsLength = savings.length;
		int sp = 0;

		final int edgeGoal = graph.countNodes() - 2;
		int addedEdges = 0;

		int[] nonHubdegree = new int[graph.countNodes()];
		// Go through the non-hub city pairs in descending order of savings.
		while (sp < savingsLength)
		{
			// Here be dragons. Stay away.
			long saving = -savings[sp++];
			short a = (short) ((saving & 0xffc00) >> 10);
			short b = (short) (saving & 0x3ff);

			// ...so it does not cause a non-hub city to become adjacent to more
			// than two other non-hub cities or create a cycle of nonhub cities
			if (nonHubdegree[a] > 1 || nonHubdegree[b] > 1 || createsCycleOfNonHubNodes(a, b))
				continue;

			tour.addEdge(a, b, nonHubdegree);
			++nonHubdegree[a];
			++nonHubdegree[b];
			if (++addedEdges == edgeGoal)
				break;
		}

		// System.err.println("Clarke-wright searched through " + sp + " of " +
		// savings.length + " edges.");

		// Let's create a tour!
		Tourable t = tour.getTour();
		// add path to the hubNode
		t.addNode(hubNode);

		return t;
	}

	/**
	 * @return
	 */
	private short getHubNode()
	{
		if (graph.countNodes() > 1000)
			return (short) new Random().nextInt(graph.countNodes());

		// Find the node with the longest distance to everyone
		short hub = 0;
		long dist = graph.sumEdges(hub);
		for (short i = 1; i < graph.countNodes(); i++)
		{
			long d = graph.sumEdges(i);
			if (d < dist)
			{
				dist = d;
				hub = i;
			}
		}
		return hub;
	}

	/**
	 * @param nodes
	 * @param tour
	 * @return
	 */
	private boolean createsCycleOfNonHubNodes(int a, int b)
	{
		for (UnfinishedTour unFinishedTour : tour)
		{
			short start = unFinishedTour.getFirst();
			short end = unFinishedTour.getLast();

			if (start == a && end == b)
			{
				return true;
			}
			if (start == b && end == a)
			{
				return true;
			}
		}

		return false;
	}
}
