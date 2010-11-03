package solvers;

import java.util.Comparator;
import java.util.TreeMap;
import java.util.Map.Entry;

import main.Edge;
import main.Graph;
import main.GraphVisualizer;
import main.Tour;

/**
 * // TODO: ClarkeWrightApproximation is a ...
 * 
 * @author Martin Nycander
 * @since
 */
public class ClarkeWrightApproximation implements StartApproxer
{
	private static final boolean debug = true;
	private Graph graph;
	private Tour tour;

	private int hubNode;

	private GraphVisualizer gv;

	/* (non-Javadoc)
	 * @see solvers.StartApproxer#getTour(main.Graph)
	 */
	public Tour getTour(Graph graph)
	{
		this.graph = graph;

		// Take an arbitrarily good hub node
		hubNode = 0;

		// Create tour
		tour = new Tour((graph.countNodes() - 1) * 2);
		if (debug)
		{
			gv = new GraphVisualizer(graph, 5);
			gv.setTour(tour);
		}

		for (int node = 0; node < graph.countNodes(); node++)
		{
			if (node == hubNode)
				continue;

			tour.addEdge(graph.getEdge(hubNode, node));
			tour.addEdge(graph.getEdge(node, hubNode));
		}

		// Calculate savings for each non-hub node

		// savings: Save -> nodeA -> nodeB, order descending
		TreeMap<Integer, int[]> savings = new TreeMap<Integer, int[]>(new Comparator<Integer>()
		{
			public int compare(Integer a, Integer b)
			{
				return b - a;
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

		// We go through the non-hub city pairs in non-increasing order of
		// savings,

		int[] nonHubdegree = new int[graph.countNodes()];
		// Performing the bypass ...
		while (tour.getLength() != graph.countNodes() && !savings.isEmpty())
		{
			Entry<Integer, int[]> saving = savings.pollFirstEntry();
			int[] nodes = saving.getValue();
			int a = nodes[0];
			int b = nodes[1];

			if (trySwitch(a, b, nonHubdegree))
			{
				nonHubdegree[a]++;
				nonHubdegree[b]++;
				continue;
			}
			if (trySwitch(b, a, nonHubdegree))
			{
				nonHubdegree[a]++;
				nonHubdegree[b]++;
				continue;
			}
		}

		System.out.println(savings.firstKey());
		return tour;
	}

	private boolean trySwitch(int a, int b, int[] nonHubdegree)
	{
		if (!tour.contains(graph.getEdge(hubNode, b)))
			return false;
		if (!tour.contains(graph.getEdge(a, hubNode)))
			return false;

		// ...so it does not cause a non-hub city to become adjacent to more
		// than two other non-hub cities.
		if (nonHubdegree[a] >= 2 || nonHubdegree[b] >= 2)
			return false;

		// ... or create a cycle of nonhub cities
		if (createsCycleOfNonHubNodes(a, b, tour))
			return false;

		if (debug)
		{
			System.out.println(a + "->" + hubNode + "->" + b + " => " + a + "->" + b);
			System.out.println("Tour before: " + tour.toEdgeString());
		}

		int aToHubIndex = tour.indexOf(graph.getEdge(a, hubNode));
		int hubToBIndex = tour.indexOf(graph.getEdge(hubNode, b));

		Edge[] extractedPath = new Edge[hubToBIndex - aToHubIndex - 1];
		for (int i = aToHubIndex + 1, j = 0; i < hubToBIndex; i++, j++)
		{
			extractedPath[j] = tour.getEdge(i);
		}

		tour.setEdge(aToHubIndex, graph.getEdge(a, b));
		tour.setEdge(aToHubIndex + 1, tour.getEdge(hubToBIndex + 1));

		for (int i = 0; i < extractedPath.length; i++)
		{
			tour.setEdge(aToHubIndex + 1 + i, extractedPath[i]);
		}

		tour.removeEdge(graph.getEdge(a, hubNode));
		int replaceIndex = tour.indexOf(graph.getEdge(hubNode, b));
		tour.setEdge(replaceIndex, graph.getEdge(a, b));

		if (debug)
		{
			System.out.println("Tour after:  " + tour.toEdgeString());
			gv.repaint();
			try
			{
				Thread.sleep(5000);
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
	 * @param nodes
	 * @param tour
	 * @return
	 */
	private boolean createsCycleOfNonHubNodes(int a, int b, Tour tour)
	{
		for (int i = tour.indexOf(graph.getEdge(hubNode, b)); i < tour.countEdges(); i++)
		{
			Edge e = tour.getEdge(i);
			if (e.nodeB == hubNode)
				return false;
			if (e.nodeB == a)
				return true;
		}

		return true;
	}
}
