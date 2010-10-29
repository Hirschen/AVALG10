package solvers;
import main.Graph;


/**
 * // TODO: NaiveSolver is a ...
 * 
 * @author Martin Nycander
 * @since 
 */
public class NaiveSolver implements StartApproxer
{

	/**
	 * 
	 */
	public NaiveSolver()
	{
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see Solver#getSolution(Graph)
	 */
	public int[] getSolution(Graph graph)
	{
		int[] tour = new int[graph.countNodes()];
		boolean[] used = new boolean[graph.countNodes()];
		used[0] = true;
		for (int i = 1; i < graph.countNodes(); i++)
		{
			int best = -1;
			for (int j = 0; j < graph.countNodes(); j++)
			{
				if (used[j])
					continue;

				if (best == -1 || graph.distance(tour[i - 1], j) < graph.distance(tour[i - 1], best))
				{
					best = j;
				}
			}
			tour[i] = best;
			used[best] = true;
		}
		return tour;
	}

}
