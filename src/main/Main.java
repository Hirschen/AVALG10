package main;

import java.io.BufferedInputStream;
import java.util.Scanner;

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

	/**
	 * 
	 */
	public Main()
	{

	}


	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// Read input
		Scanner sc = new Scanner(new BufferedInputStream(System.in));
		int size = sc.nextInt();

		double[][] g = new double[size][2];
		for (int i = 0; i < size; i++)
		{
			g[i][0] = Double.parseDouble(sc.next());
			g[i][1] = Double.parseDouble(sc.next());
		}

		Graph graph = new Graph(g);
		StartApproxer solver = new NaiveSolver();

		// Run algorithm
		int[] tour = solver.getSolution(graph);

		// Output tour
		for (int i = 0; i < size; i++)
		{
			System.out.println(tour[i]);
		}
	}
}
