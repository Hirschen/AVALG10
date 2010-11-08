package solvers;

import main.Graph;
import main.Main;
import main.Tourable;

/**
 * // TODO: TwoDotFiveOpt is a ...
 * 
 * "In a 2.5-Opt move one relocates a single city from its current location to a
 * position between two current tour neighbors elsewhere in the tour."
 * 
 * - http://www.research.att.com/~dsj/papers/TSPchapter.pdf @ page 32
 * 
 * @author Martin Nycander
 * @since 2010-11-07
 */
public class TwoDotFiveOpt implements Improver
{

	/**
	 * 
	 */
	public TwoDotFiveOpt()
	{
	}

	/* (non-Javadoc)
	 * @see solvers.Improver#improve(main.Graph, main.Tourable)
	 */
	public boolean improve(Graph g, Tourable t)
	{
		boolean improvement = false;

		// Search through the tour for a 2.5-optimization
		for (short a = 0; a < t.countNodes(); a++)
		{
			short t1 = t.getNode(a);
			short t2 = t.getNextNode(a);
			short t3 = t.getNextNode(a, 2);
			
			// Find a good edge of t1 to relocate t2 to
			for (short e1 = 0; e1 < t.countNodes(); e1++)
			{
				// It shouldn't be any of our selected nodes
				if (e1 == t1 || e1 == t2 || e1 == t3)
					continue;
				
				short e2 = t.getNextNode(t.indexOf(e1));

				// Again, not the selected nodes.
				if (e2 == t1 || e2 == t2 || e2 == t3)
					continue;

				int length = g.distance(t1, t2) + g.distance(t2, t3) + g.distance(e1, e2);
				int opt = g.distance(e2, t2) + g.distance(t2, e1) + g.distance(t1, t3);

				// Is the relocation good?
				if (opt < length)
				{
					if (Main.verbose)
					{
						System.out.println("2.5-opt: Wins " + (length - opt));
						System.out.println("\t[" + t1 + "->" + t2 + "->" + t3 + "]+[" + e1 + "->" + e2 + "], len=" + length + "=> [" + t1 + "->" + t3 + "]+[" + e1 + "->" + t2 + "->" + e2 + "], len=" + opt);
					}
					t.moveNode(t2, t.indexOf(e2));
					improvement = true;
				}
			}
		}
		return improvement;
	}
}
