package solvers;

import main.Graph;
import main.GraphVisualizer;
import main.Tourable;

public class TwoOpt implements Improver
{

	private short searchSize = -1;
	private int minGain = 0;
	private boolean random = false;

	public TwoOpt()
	{
	}

	public TwoOpt(int tourSize)
	{
		searchSize = setSearchSize(tourSize);
	}

	public TwoOpt(int tourSize, int minimumGain)
	{
		searchSize = (short) setSearchSize(tourSize);
		minGain = minimumGain;
	}

	public void setRandom(boolean b)
	{
		random = b;
	}

	private short setSearchSize(int s)
	{
		short res = 4;
		if (s > 10)
		{
			res = 6;
		}
		if (s > 30)
		{
			res = 12;
		}
		if (s > 100)
		{
			res = 20;
		}
		if (s > 500)
		{
			res = 50;
		}
		if (s > 800)
		{
			res = 70;
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * @see solvers.Improver#improve(main.Graph, main.Tour)
	 */
	public boolean improve(Graph g, Tourable t)
	{
		if (t.countNodes() <= 1)
		{
			return false;
		}
		short a1 = fetchFirstEdge(t), b1 = (short) (a1 + 1 % (t.countNodes() - 1));
		short a2, b2;
		if (random)
		{
			if (searchSize == -1)
			{
				searchSize = setSearchSize(t.countNodes());
			}
			a2 = (short) (fetchFirstEdge(t) % (t.countNodes() - 1));
			for (int i = 0; i < searchSize; i++, a2 = (short) (fetchFirstEdge(t) % (t.countNodes() - 1)))
			{
				b2 = (short) (a2 + 1);
				if(b2 == t.countNodes()){
					b2 = 0;
				}
				if (t.getNode(a1) != t.getNode(a2) && gotGain(g, t, a1, b1, a2, b2) && checkFeasbility(a1, b1, a2, b2, t))
				{
					t.switch2EdgesOpted(a1, b1, a2, b2);
					return true;
				}
			}
		}

		if (searchSize == -1)
		{
			for (a2 = 0; a2 < t.countNodes() - 1; a2++)
			{
				b2 = (short) (a2 + 1);
				if (b2 == t.countNodes())
				{
					b2 = 0;
				}
				if (t.getNode(a1) != t.getNode(a2) && gotGain(g, t, a1, b1, a2, b2) && checkFeasbility(a1, b1, a2, b2, t))
				{
					t.switch2EdgesOpted(a1, b1, a2, b2);
					return true;
				}
			}
		}
		else
		{
			a2 = (short) (a1 - searchSize);
			for (int i = 0; i < 2 * searchSize; i++, a2++)
			{
				if (a2 < 0)
				{
					a2 = (short) (t.countNodes() - 1 + a2);
				}
				if (a2 > t.countNodes() - 2)
				{
					a2 = 0;
				}
				b2 = (short) (a2 + 1);
				if (t.getNode(a1) != t.getNode(a2) && gotGain(g, t, a1, b1, a2, b2) && checkFeasbility(a1, b1, a2, b2, t))
				{
					t.switch2EdgesOpted(a1, b1, a2, b2);
					return true;
				}
			}
		}
		return false;
	}

	private boolean gotGain(Graph g, Tourable t, short a1, short b1, short a2, short b2)
	{
		return (g.distance(t.getNode(a1), t.getNode(b1)) + g.distance(t.getNode(a2), t.getNode(b2)) - (g.distance(t.getNode(a1), t.getNode(a2)) + g.distance(t.getNode(b1), t.getNode(b2)))) > minGain;
	}

	private boolean checkFeasbility(short a1, short b1, short a2, short b2, Tourable t)
	{
		int tmp = Math.abs(b1 - a2);
		if (tmp == 0)
		{
			return false;
		}
		return true;
	}

	private short fetchFirstEdge(Tourable t)
	{
		return (short) (Math.random() * (t.countNodes() - 1));
	}

	public static void main(String[] args) throws InterruptedException
	{
		/* Simple graph */
		double[][] coords = new double[][] { { 0, 3 }, { 3, 0 }, { 4, 3 }, { 3, 10 }, { 10, 3 }, { 25, 4 }, { 10, 10 }, { 25, 11 }, {12,23},{8,6},{1,1},{10,5}, {25,1},{16,11},{15,15}, {25,25}, {20,20}, {23,23} };
		Graph g = new Graph(coords);
		GraphVisualizer vis = new GraphVisualizer(g);

		StartApproxer sa = new NaiveSolver();
		Tourable t = sa.getTour(g);
		vis.setTour(t);
		Improver imp = new TwoOpt();
		//((TwoOpt) imp).setRandom(true);
		System.out.println(g.calculateLength(t));
		for (int i = 0; i < 1000; i++)
		{
			Thread.sleep(1);
			imp.improve(g, t);
			System.out.println(g.calculateLength(t));
			vis.updateUI();
		}
		System.out.println("Length of tour: " + g.calculateLength(t));
		System.out.println(t);
	}
}
