package solvers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import main.Graph;
import main.Main;
import main.Tourable;

/**
 * // TODO: TourConstruction is a ...
 * 
 * @author Martin Nycander
 * @since
 */
public class TourConstruction implements Iterable<UnfinishedTour>
{
	private LinkedList<UnfinishedTour> edges;
	private Graph graph;

	/**
	 * 
	 */
	public TourConstruction(Graph graph)
	{
		edges = new LinkedList<UnfinishedTour>();
		this.graph = graph;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<UnfinishedTour> iterator()
	{
		return edges.iterator();
	}

	public void addEdge(short a, short b, int[] nonHubdegree)
	{
		if (Main.verbose)
		{
			System.out.println("+ " + a + "->" + b);
		}

		// Find all tours which might fit our edge
		ArrayList<UnfinishedTour> matches = new ArrayList<UnfinishedTour>();
		for (UnfinishedTour unFinishedTour : edges)
		{
			if (unFinishedTour.startsOrEndsWith(a) || unFinishedTour.startsOrEndsWith(b))
			{
				matches.add(unFinishedTour);
			}
		}

		if (matches.isEmpty())
		{
			// Create a new tour!
			UnfinishedTour ut = new UnfinishedTour(graph.countNodes());
			ut.appendNode(a);
			ut.appendNode(b);
			edges.add(ut);
		}
		else if (matches.size() == 1)
		{
			// Append the edge to the matched tour!
			matches.get(0).addEdge(a, b);
		}
		else if (matches.size() == 2)
		{
			// Stop. Mergetime!
			merge(a, b, matches);
		}
		else
		{
			throw new RuntimeException("Oh dear..");
		}
	}

	/**
	 * @param a
	 * @param b
	 * @param matches
	 */
	private void merge(short a, short b, ArrayList<UnfinishedTour> matches)
	{
		if (Main.verbose)
		{
			System.out.println("\tMerging! Glue is: " + a + "->" + b);
			System.out.println("\tTours before: " + edges);
		}
		UnfinishedTour smallTour = matches.get(0);
		UnfinishedTour largeTour = matches.get(1);
		if (largeTour.size() < smallTour.size())
		{
			smallTour = matches.get(1);
			largeTour = matches.get(0);
		}
		largeTour.addEdge(a, b);

		boolean reverse = (smallTour.getLast() == largeTour.getLast() || smallTour.getFirst() == largeTour.getFirst());
		boolean append = (smallTour.getLast() == largeTour.getLast() || largeTour.getLast() == smallTour.getFirst());

		if (append == reverse)
			smallTour.removeLast();
		else
			smallTour.removeFirst();

		// Reverse the smallest and add it to the bigger tour
		while (!smallTour.isEmpty())
		{
			if (append)
			{
				if (reverse)
					largeTour.appendNode(smallTour.removeLast());
				else
					largeTour.appendNode(smallTour.removeFirst());
			}
			else
			{
				if (reverse)
					largeTour.prependNode(smallTour.removeFirst());
				else
					largeTour.prependNode(smallTour.removeLast());
			}
		}

		edges.remove(smallTour);
		if (Main.verbose)
		{
			System.out.println("\tTours after:  " + edges);
		}
	}

	/**
	 * @return
	 */
	public Tourable getTour()
	{
		return edges.getFirst().getTour();
	}
}
