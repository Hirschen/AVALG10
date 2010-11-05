package main;

import java.io.IOException;
import java.io.InputStream;

import solvers.NaiveSolver;
import solvers.StartApproxer;

/**
 * // TODO: Main is a ...
 * 
 * @author Martin Nycander
 * @since
 */
public class Main
{
	public static final boolean verbose = false;
	private Kattio io;

	protected double graphTime;
	protected double approxTime;
	protected double improveTime;
	protected double outputTime;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		Main m = new Main();
		if (verbose && args.length > 0)
		{
			m.runVerbose(true);
			System.out.println("Created graph for " + m.graphTime + " ms.");
			System.out.println("Created approximation for " + m.approxTime + " ms.");
			System.out.println("Created improved for " + m.improveTime + " ms.");
			System.out.println("Wrote output for " + m.outputTime + " ms.");
		}
		else
		{
			m.runFast();
		}
	}

	/**
	 * 
	 */
	public Main()
	{
		this(System.in);
	}

	public Main(InputStream in)
	{
		io = new Kattio(in, System.out);
	}

	/**
	 * 
	 */
	public Tourable runFast()
	{
		Graph graph = new Graph(io);
		Tourable tour = approximateTour(graph);
		tour.addNode(tour.getNode(0));
		tour = improveTour(graph, tour);
		// Output tour
		tour.printTo(io);
		io.flush();

		return tour;
	}

	/**
	 * @throws IOException
	 * 
	 */
	public Tourable runVerbose(boolean delayedInput) throws IOException
	{
		if (delayedInput)
			waitForInput();

		double time = time();

		Graph graph = new Graph(io);
		// new GraphVisualizer(graph);

		graphTime = timeDiff(time(), time);
		time = time();

		Tourable tour = approximateTour(graph);
		tour.addNode(tour.getNode(0));

		approxTime = timeDiff(time(), time);
		time = time();

		tour = improveTour(graph, tour);

		improveTime = timeDiff(time(), time);
		time = time();

		// Output tour
		tour.printTo(io);
		io.flush();
		outputTime = timeDiff(time(), time);

		return tour;
	}

	/**
	 * @return in ms
	 */
	public static double time()
	{
		return ((int) Math.round(System.nanoTime() / 1000.0)) / 1000.0;
	}

	public static double timeDiff(double t1, double t2)
	{
		return Math.round(t1 * 1000.0 - t2 * 1000.0) / 1000.0;
	}

	/**
	 * @throws IOException
	 */
	private void waitForInput() throws IOException
	{
		System.out.print("Paste problem here: (start with a newline) ");
		System.in.read();
	}

	/**
	 * @return
	 */
	protected double[][] readInput()
	{
		int size = io.getInt();
		double[][] g = new double[size][2];
		for (int i = 0; i < size; i++)
		{
			g[i][0] = io.getDouble();
			g[i][1] = io.getDouble();
		}
		return g;
	}

	/**
	 * @param graph
	 * @return
	 */
	private Tourable approximateTour(Graph graph)
	{
		/* * /
		StartApproxer solver = new ClarkeWrightApproximation();
		Tourable t = solver.getTour(graph);
		/* */
		StartApproxer solver = new NaiveSolver();
		Tourable t = solver.getTour(graph);
		/* */
		return t;
	}

	private Tourable improveTour(Graph g, Tourable t)
	{
		/*Improver imp = new TwoOpt();
		for (int i = 0; i < 0; i++)
		{
			imp.improve(g, t);
		}*/
		return t;
	}
}
