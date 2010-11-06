package solvers;

import main.Graph;
import main.ShortTour;
import main.Tourable;

/**
 * // TODO: BruteForceOptimal is a ...
 * 
 * @author Martin Nycander
 * @since
 */
public class BruteForceOptimalTourFor8nodes implements StartApproxer
{
	private Graph graph;
	private long bestTour;
	private long dist = Long.MAX_VALUE;

	/* (non-Javadoc)
	 * @see solvers.StartApproxer#getTour(main.Graph)
	 */
	public Tourable getTour(Graph g)
	{
		graph = g;

		for (byte n = 0; n < g.countNodes(); n++)
		{
			long tour = n & 0xf;
			bruteForce(tour, (1 << n), 1);
		}

		Tourable t = new ShortTour(g.countNodes());
		for (int i = 0; i < g.countNodes(); i++)
		{
			t.setNode(i, (short) ((bestTour & (0xf << (i * 4))) >> (i * 4)));
		}

		return t;
	}

	private void bruteForce(long tour, int visited, long length)
	{
		if (length == graph.countNodes())
		{
			int[] t = new int[(int) length];
			for (long i = 0; i < t.length; i++)
			{
				t[(int) i] = (short) ((tour & (0xf << (i * 4))) >> (i * 4));
			}
			int d = graph.calculateLength(t);
			if (d < dist)
			{
				dist = d;
				bestTour = tour;
			}
			return;
		}

		for (long a = 0; a < graph.countNodes(); a++)
		{
			if (((visited & (1 << a)) >> a) == 1)
				continue;

			bruteForce(tour | (long) ((a & 0xf) << (length * 4)), (visited | (1 << a)), length + 1);
		}
	}
}
