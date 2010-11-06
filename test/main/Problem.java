package main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

/**
 * // TODO: Problem is a ...
 * 
 * @author Martin Nycander
 * @since 
 */
public class Problem
{
	private String name;
	@SuppressWarnings("unused")
	private String comment;

	public final int[] optimalRoute;
	public final double[][] coordinates;

	/**
	 * @throws IOException
	 * 
	 */
	public Problem(File problem, File optimal) throws IOException
	{
		coordinates = parseProblem(problem);
		optimalRoute = parseOptSolution(optimal);
	}

	private double[][] parseProblem(File probFile) throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(probFile)));

		Properties properties = new Properties();
		String prop = in.readLine();
		while (!prop.equals("NODE_COORD_SECTION"))
		{
			String[] s = prop.split(":");
			String key = s[0].trim().toLowerCase();
			properties.put(key, s[1].trim());

			if (key.equals("edge_weight_type") && !properties.get(key).equals("EUC_2D"))
				throw new IllegalArgumentException("The problem have the property edge_weight_type set to EUC_2D, but was " + properties.get(key) + "!");

			prop = in.readLine();
		}

		name = properties.getProperty("name");
		comment = properties.getProperty("comment");
		int size = Integer.parseInt(properties.getProperty("dimension"));

		double[][] coords = new double[size][2];
		for (int i = 0; i < size; i++)
		{
			String line = in.readLine().trim();
			String[] s = line.split("\\s+");
			coords[i][0] = Double.parseDouble(s[1].trim());
			coords[i][1] = Double.parseDouble(s[2].trim());
		}
		return coords;
	}

	private int[] parseOptSolution(File optFile) throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(optFile)));

		ArrayList<Integer> tour = new ArrayList<Integer>();

		String line;
		while ((line = in.readLine()) != null)
		{
			tour.add(Integer.parseInt(line.trim()));
		}

		int[] t = new int[tour.size()];
		for (int i = 0; i < t.length; i++)
			t[i] = tour.get(i);

		return t;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		/*StringBuffer res = new StringBuffer();
		res.append(name + " (" + comment + ")\n");
		res.append("Optimal route: " + Arrays.toString(optimalRoute) + "\n");
		for (int i = 0; i < coordinates.length; i++)
		{
			res.append("[" + coordinates[i][0] + ", " + coordinates[i][1] + "]\n");
		}
		return res.toString();*/
		
		StringBuilder sb = new StringBuilder();

		sb.append(coordinates.length);
		sb.append('\n');
		for (int i = 0; i < coordinates.length; i++)
		{
			sb.append(coordinates[i][0]);
			sb.append(' ');
			sb.append(coordinates[i][1]);
			sb.append('\n');
		}
		sb.append('\n');

		return sb.toString();
	}

	public static void main(String[] args) throws IOException
	{
		String name = (args.length == 1 ? args[0] : "pcb442");
		Problem p = new Problem(new File("testdata/" + name + ".tsp"), new File("testdata/" + name + ".opt.tour"));
		Graph g = new Graph(p.coordinates);
		System.out.println("Tour length: " + g.calculateLength(p.optimalRoute));
		System.out.println(p);
	}

	/**
	 * @return
	 */
	public String getName()
	{
		return name;
	}
}
