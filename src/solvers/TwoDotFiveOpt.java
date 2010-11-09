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
		int length, opt;

		// Search through the tour for a 2.5-optimization
		for (short t1 = 0; t1 < t.countNodes(); t1++)
		{
			short t2 = (short) ((t1+1)% t.countNodes());
			short t3 = (short) ((t2+1)% t.countNodes());
			short[] neighbours = g.getNeighbours(t.indexOf(t2));
			// Find a good edge of t1 to relocate t2 to
			for (short n = 0; n < neighbours.length; n++)
			{
				short e1 = t.indexOf(neighbours[n]);
				// It shouldn't be any of our selected nodes
				if (e1 == t1 || e1 == t2 || e1 == t3)
					continue;
				
				short e2 = (short) ((e1+1) % t.countNodes());

				// Again, not the selected nodes.
				if (e2 == t1 || e2 == t2 || e2 == t3)
					continue;

				length = g.distance(t.getNode(t1),t.getNode(t2)) + g.distance(t.getNode(t2), t.getNode(t3)) + g.distance(t.getNode(e1), t.getNode(e2));
				opt = g.distance(t.getNode(e2), t.getNode(t2)) + g.distance(t.getNode(t2), t.getNode(e1)) + g.distance(t.getNode(t1), t.getNode(t3));

				// Is the relocation good?
				if (opt < length)
				{
					if (Main.verbose)
					{
						System.out.println("2.5-opt: Wins " + (length - opt));
						System.out.println("\t[" + t1 + "->" + t2 + "->" + t3 + "]+[" + e1 + "->" + e2 + "], len=" + length + "=> [" + t1 + "->" + t3 + "]+[" + e1 + "->" + t2 + "->" + e2 + "], len=" + opt);
					}
					t.moveNode(t2, e2);
					improvement = true;
					break;
				}
			}
		}
		return improvement;
	}
}
