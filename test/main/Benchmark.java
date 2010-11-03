package main;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;

import solvers.NaiveSolver;
import solvers.StartApproxer;

/**
 * // TODO: Benchmark is a ...
 * 
 * @author Martin Nycander
 * @since 
 */
public class Benchmark
{
	private static final boolean DEBUG = true;

	public Benchmark(File testDir, File resultFile)
	{
		File[] problemFiles = testDir.listFiles(new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				return name.endsWith(".tsp");
			}
		});

		double sum = 0.0;
		int problemSet = 0;

		for (File probFile : problemFiles)
		{
			try
			{
				final Problem p = new Problem(probFile, new File(probFile.getAbsolutePath().replace(".tsp", ".opt.tour")));

				final PipedInputStream pis = new PipedInputStream();
				final PipedOutputStream pos = new PipedOutputStream(pis);
				Thread thread = new Thread(new Runnable()
				{

					@Override
					public void run()
					{
						PrintWriter pw = new PrintWriter(pos, true);
						pw.println(p);
						pw.flush();
						pw.close();
					}

				});
				thread.start();
				Main m = new Main(pis);
				Tour t = null;
				if (DEBUG)
					t = m.runVerbose(false);
				else
					t = m.runFast();

				Graph graph = new Graph(p.coordinates);
				StartApproxer naiveSolver = new NaiveSolver();

				Tour naiveTour = naiveSolver.getTour(graph);
				Tour optimalTour = new Tour(p.optimalRoute, graph);
				Tour valTour = t;

				if (t.countNodes() != graph.countNodes() + 1)
				{
					System.err.println("File " + probFile + ": Tried to answer with " + t.countNodes() + " nodes, but graph requires " + (graph.countNodes() + 1) + "!");
				}

				double naiveCost = graph.calculateLength(naiveTour);
				double optimalCost = graph.calculateLength(optimalTour);
				double valCost = graph.calculateLength(valTour);
				
				double x;
				if (naiveCost == optimalCost)
				{
					if (valCost == optimalCost)
						x = 0.0;
					else if (valCost > optimalCost)
						x = Double.MAX_VALUE;
					else
						throw new RuntimeException();
				}
				else
				{
					// x = (Val - Opt) / (Naive - Opt).
					x = (valCost - optimalCost) / (naiveCost - optimalCost);	
				}

				double score = Math.pow(0.02, x);
				if (DEBUG)
				{
					System.out.println();
					System.out.println(p.getName() + ": " + ((double) Math.round(1000000.0 * score)) / 1000000.0 + " ( = 0.02^" + x + ")");
					System.out.println();
				}
				sum += score;
				problemSet++;
			}
			catch (IllegalArgumentException e)
			{
				System.err.println("File " + probFile + ": " + e.getMessage());
			}
			catch (IOException e)
			{
				System.err.println("File " + probFile + ": Failed I/O (" + e.getMessage() + ")");
			}
		}
		System.out.println("Result: " + sum + " / " + problemSet);
		System.out.println("Estimated kattis score: " + (sum / (double) problemSet) * 50.0);
	}

	public static void main(String[] args)
	{
		new Benchmark(new File("testdata/"), new File(System.currentTimeMillis() + ".csv"));
	}
}
