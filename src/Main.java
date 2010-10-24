import java.util.Scanner;

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
		Scanner sc = new Scanner(System.in);
		int size = sc.nextInt();

		double[][] graph = new double[size][2];
		for (int i = 0; i < size; i++)
		{
			graph[i][0] = Double.parseDouble(sc.next());
			graph[i][1] = Double.parseDouble(sc.next());
		}

		// Run algorithm
		int[] tour = new int[size];
		boolean[] used = new boolean[size];
		used[0] = true;
		for (int i = 1; i < size; i++)
		{
			int best = -1;
			for (int j = 0; j < size; j++)
			{
				if (used[j])
					continue;

				if (best == -1 || dist(graph, tour[i - 1], j) < dist(graph, tour[i - 1], best))
				{
					best = j;
				}
			}
			tour[i] = best;
			used[best] = true;
		}

		// Output tour
		for (int i = 0; i < size; i++)
		{
			System.out.println(tour[i]);
		}
	}

	/**
	 * @param graph
	 * @param i
	 * @param best
	 * @return
	 */
	private static double dist(double[][] graph, int a, int b)
	{
		double xDiff = Math.abs(graph[a][0] - graph[b][0]);
		double yDiff = Math.abs(graph[a][1] - graph[b][1]);

		return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
	}

}
