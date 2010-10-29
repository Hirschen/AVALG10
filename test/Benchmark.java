import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import main.Graph;
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
	private boolean DEBUG;

	public Benchmark(StartApproxer solver, File testDir, File resultFile)
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
				Problem p = new Problem(probFile, new File(probFile.getAbsolutePath().replace(".tsp", ".opt.tour")));

				Graph graph = new Graph(p.coordinates);
				StartApproxer naiveSolver = new NaiveSolver();

				int[] naiveTour = naiveSolver.getSolution(graph);
				int[] optimalTour = p.optimalRoute;
				int[] valTour = solver.getSolution(graph);

				double naiveCost = graph.calculateLength(naiveTour);
				double optimalCost = graph.calculateLength(optimalTour);
				double valCost = graph.calculateLength(valTour);
				
				double x;
				if (naiveCost == optimalCost)
				{
					if (valCost == optimalCost)
						x = 0;
					else
						// valCost > optimalCost
						x = Double.MAX_VALUE;
				}
				else
				{
					x = (valCost - optimalCost) / (naiveCost - optimalCost);	
				}

				double score = Math.pow(0.02, x);
				assert (score >= 0.0 && score <= 1.0);
				if (DEBUG)
					System.out.println(p.getName() + ": " + score);
				sum += score;
				problemSet++;
			}
			catch (IllegalArgumentException e)
			{
				System.err.println("File " + probFile + ": " + e.getMessage());
			}
			catch (IOException e)
			{
				System.err.println("File " + probFile + ": Failed I/O.");
			}
		}
		System.out.println("Result: " + sum + " / " + problemSet);
		System.out.println("Estimated kattis score: " + (sum / (double) problemSet) * 50.0);
	}

	public static void main(String[] args)
	{
		if (args.length != 1)
		{
			System.err.println("Usage: java Benchmark [solver]\nSolvers: naive");
			return;
		}

		StartApproxer s = null;
		if (args[0].equals("naive"))
			s = new NaiveSolver();

		new Benchmark(s, new File("testdata/"), new File(System.currentTimeMillis() + ".csv"));
	}
}
