package solvers;

import main.Graph;
import main.ShortTour;
import main.Tourable;

/**
 * BruteForceOptimal solves the TSP for n <= 8. This constraint exists since it
 * stores the tour as a long, and each node is stored as 4 bits (64 / 4 = 16).
 * 
 * 
 * 
 * @author Martin Nycander
 * @since
 */
public class BruteForceOptimal implements StartApproxer
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
			long mask = 0xf;
			long bits = (long) (i * 4);
			long masked = (bestTour & (long) (mask << bits));
			t.setNode(i, (short) (masked >> bits));
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
				long mask = 0xf;
				long bits = (long) (i * 4);
				long masked = (tour & (long) (mask << bits));
				t[(int) i] = (short) (masked >> bits);
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

			long mask = 0xf;
			long bits = (long) (length * 4);
			long masked = ((a & mask) << bits);
			bruteForce(tour | masked, (visited | (1 << a)), length + 1);
		}
	}
}
