package solvers;

import main.Graph;
import main.ShortTour;
import main.Tourable;

/**
 * NaiveSolver is the greedy algorithm kattis runs to get a base case solution.
 * Anything better than this will yield points.
 * 
 * @author Martin Nycander
 * @since 2010-10-29
 */
public class NaiveSolver implements StartApproxer
{

	/**
	 * 
	 */
	public NaiveSolver()
	{
	}

	/* (non-Javadoc)
	 * @see Solver#getSolution(Graph)
	 */
	public Tourable getTour(Graph graph)
	{
		Tourable tour = new ShortTour(graph.countNodes());

		boolean[] used = new boolean[graph.countNodes()];
		used[0] = true;
		tour.setNode(0, (short) 0);
		for (int i = 1; i < graph.countNodes(); i++)
		{
			short best = -1;

			// Find the closest unused neighbor

			for (short j = 0; j < graph.countNodes(); j++)
			{
				if (used[j])
					continue;

				if (best == -1 || graph.distance(i - 1, j) < graph.distance(i - 1, best))
				{
					best = j;
				}
			}

			tour.setNode(i, best);
			used[best] = true;
		}
		return tour;
	}

}
