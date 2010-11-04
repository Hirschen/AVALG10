package main;

import java.io.IOException;
import java.io.InputStream;

import solvers.ClarkeWrightApproximation;
import solvers.Improver;
import solvers.StartApproxer;
import solvers.TwoOpt;

/**
 * // TODO: Main is a ...
 * 
 * @author Martin Nycander
 * @since
 */
public class Main
{
	public static final boolean verbose = true;
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
	public Tour runFast()
	{
		// Read input
		// double[][] g = readInput();

		Graph graph = new Graph(io);
		Tour tour = approximateTour(graph);
		tour = improveTour(graph, tour);

		// Output tour
		io.println(tour);
		io.flush();

		return tour;
	}

	/**
	 * @throws IOException
	 * 
	 */
	public Tour runVerbose(boolean delayedInput) throws IOException
	{
		if (delayedInput)
			waitForInput();

		double time = time();

		Graph graph = new Graph(io);
		// new GraphVisualizer(graph);

		graphTime = timeDiff(time(), time);
		System.out.println("Created graph for " + graphTime + " ms.");
		time = time();

		Tour tour = approximateTour(graph);

		approxTime = timeDiff(time(), time);
		System.out.println("Created approximation for " + approxTime + " ms.");
		time = time();

		tour = improveTour(graph, tour);

		improveTime = timeDiff(time(), time);
		System.out.println("Created improved for " + improveTime + " ms.");
		time = time();

		// Output tour
		io.println(tour);
		io.flush();
		outputTime = timeDiff(time(), time);
		System.out.println("Wrote output for " + outputTime + " ms.");

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
	private Tour approximateTour(Graph graph)
	{
		StartApproxer solver = new ClarkeWrightApproximation();
		return solver.getTour(graph);
	}

	private Tour improveTour(Graph g, Tour t)
	{
		Improver imp = new TwoOpt();
		for (int i = 0; i < 3000; i++)
		{
			imp.improve(g, t);
		}
		return t;
	}
}
