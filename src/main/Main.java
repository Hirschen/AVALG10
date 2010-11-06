package main;

import java.io.IOException;
import java.io.InputStream;

import solvers.BruteForceOptimalTourFor8nodes;
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
		
		if (graph.countNodes() <= 8)
		{
			StartApproxer solver = new BruteForceOptimalTourFor8nodes();
			Tourable tour = solver.getTour(graph);
			tour.printTo(io);
			io.flush();
			return tour;
		}

		Tourable tour = approximateTour(graph);
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

		graphTime = timeDiff(time(), time);
		time = time();

		if (graph.countNodes() <= 8)
		{
			StartApproxer solver = new BruteForceOptimalTourFor8nodes();
			Tourable tour = solver.getTour(graph);
			approxTime = timeDiff(time(), time);
			time = time();
			tour.printTo(io);
			io.flush();
			outputTime = timeDiff(time(), time);
			return tour;
		}

		Tourable tour = approximateTour(graph);

		approxTime = timeDiff(time(), time);
		time = time();

		tour = improveTour(graph, tour);

		improveTime = timeDiff(time(), time);
		time = time();

		// Output tour
		tour.printTo(io);
		io.flush();
		outputTime = timeDiff(time(), time);

		// Check the tour for errors
		String[] tourString = tour.toString().split("\\s+");
		if (tourString.length != graph.countNodes())
		{
			new GraphVisualizer(graph).setTour(tour);
			throw new RuntimeException("The number of nodes in the tour is not correct. " + tourString.length + " != " + graph.countNodes());
		}
		
		boolean[] visited = new boolean[graph.countNodes()];
		for (int i = 0; i < tourString.length; i++)
		{
			int node = Integer.parseInt(tourString[i].trim());
			if (visited[node])
			{
				new GraphVisualizer(graph).setTour(tour);
				throw new RuntimeException("The node " + node + " has already been visited by the tour. Tour=" + tour);
			}
			visited[node] = true;
		}
		
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
	 * @param graph
	 * @return
	 */
	private Tourable approximateTour(Graph graph)
	{
		/* */
		StartApproxer solver = new ClarkeWrightApproximation();
		Tourable t = solver.getTour(graph);
		/* * /
		StartApproxer solver = new NaiveSolver();
		Tourable t = solver.getTour(graph);
		/* */
		return t;
	}

	private Tourable improveTour(Graph g, Tourable t)
	{
		/*Improver imp3 = new ThreeOpt();
		for (int i = 0; i < 100; i++)
		{
			imp3.improve(g, t);
		}
		/**/
		//# of laps
		int random = 2500000 / t.countNodes();
		int traverseNeighbours = random*2;
		
		Improver imp2 = new TwoOpt(t.countNodes());
		((TwoOpt) imp2).setRandom(true);
		for (int i = 0; i < random; i++)
		{
			imp2.improve(g, t);
		}
		((TwoOpt) imp2).setRandom(false);
		for (int i = 0; i < traverseNeighbours; i++)
		{
			imp2.improve(g, t);
		}
		/* */
		return t;
	}
}
