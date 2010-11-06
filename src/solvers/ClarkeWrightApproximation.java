package solvers;

import java.util.Random;

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
	private TourConstruction tour;

	private short hubNode;

	private GraphVisualizer gv;

	private final boolean measureTime = false;

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


		// Take an arbitrarily good hub node
		hubNode = (short) new Random().nextInt(graph.countNodes());

		// Calculate savings for each non-hub node
		// savings: Save -> nodeA -> nodeB, order descending
		Saving[] savings = graph.calculateSavings(this, hubNode);
		final int savingsLength = savings.length;
		int sp = 0;

		final int edgeGoal = graph.countNodes() - 2;
		int addedEdges = 0;

		int[] nonHubdegree = new int[graph.countNodes()];
		// Go through the non-hub city pairs in descending order of savings.
		while (sp < savingsLength)
		{
			Saving saving = savings[sp++];

			short a = saving.a;
			short b = saving.b;

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

	/* (non-Javadoc)
	 * @see solvers.StartApproxer#getTour(main.Graph)
	 */
	public Tourable getTourSlow(Graph graph)
	{
		tour = new TourConstruction(graph);
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
			gv.setTourConstruction(tour);
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
		Saving[] savings = graph.calculateSavings(this, hubNode);

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

			short a = saving.a;
			short b = saving.b;

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

			tour.addEdge(a, b, nonHubdegree);
			nonHubdegree[a]++;
			nonHubdegree[b]++;

			if (Main.verbose)
			{
				gv.repaint();
				/* */
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				/**/
			}
		}

		if (Main.verbose || measureTime)
		{
			System.out.println("Found path in: " + Main.timeDiff(Main.time(), time) + " ms.");
			time = Main.time();
		}

		// Let's create a tour!
		Tourable t = tour.getTour();
		// add path to the hubNode
		t.addNode(hubNode);

		if (Main.verbose || measureTime)
			System.out.println("Constructed tour in: " + Main.timeDiff(Main.time(), time) + " ms.");

		if (Main.verbose)
		{
			gv.setTourConstruction(null);
			gv.setTour(t);
			gv.repaint();
		}

		return t;
	}

	public final class Saving implements Comparable<Saving>
	{
		public final int saving;
		public final short a;
		public final short b;

		public Saving(int save, short a, short b)
		{
			saving = save;
			this.a = a;
			this.b = b;
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
}
