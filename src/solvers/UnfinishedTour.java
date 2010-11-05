package solvers;

import main.ShortTour;
import main.Tourable;

/**
 * // TODO: UnfinishedTour is a ...
 * 
 * @author Martin Nycander
 * @since 
 */
public class UnfinishedTour
{
	private short[] tour;

	private int start; // Increases
	private int end; // Decreases

	public UnfinishedTour(int nodeCount)
	{
		tour = new short[nodeCount];
		start = -1;
		end = nodeCount;
	}

	public void addEdge(short a, short b)
	{
		int last = getLast();
		int first = getFirst();
		if (a == last)
		{
			appendNode(b);
			return;
		}
		if (b == last)
		{
			appendNode(a);
			return;
		}
		if (a == first)
		{
			prependNode(b);
			return;
		}
		if (b == first)
		{
			prependNode(a);
			return;
		}
	}

	public void appendNode(short a)
	{
		tour[--end] = a;
	}

	public void prependNode(short a)
	{
		tour[++start] = a;
	}

	public short getLast()
	{
		return get(size() - 1);
	}

	public short getFirst()
	{
		return get(0);
	}

	public short get(int u)
	{
		if (isEmpty())
			return -1;

		if (u > start)
			return tour[tour.length - (u - start)];

		return tour[start - u];
	}

	public boolean startsOrEndsWith(int a)
	{
		return a == getFirst() || a == getLast();
	}

	public int size()
	{
		return (start + 1) + (tour.length - end);
	}

	public boolean isEmpty()
	{
		return (start == -1 && end == tour.length);
	}

	public short removeLast()
	{
		if (end < tour.length)
		{
			return tour[end++];
		}

		short r = tour[0];
		for (int i = start - 1; i >= 0; i--)
			tour[i] = tour[i + 1];
		tour[start] = -1;
		start--;
		return r;
	}

	public short removeFirst()
	{
		if (start < 0)
		{
			short r = tour[tour.length - 1];
			for (int i = tour.length - 1; i >= end; i--)
				tour[i] = tour[i - 1];
			end++;
			return r;
		}

		return tour[start--];
	}

	public Tourable getTour()
	{
		Tourable t = new ShortTour(tour.length);
		for (int i = start; i >= 0; i--)
			t.addNode(tour[i]);
		for (int i = tour.length - 1; i >= end; i--)
			t.addNode(tour[i]);
		return t;
	}

	@Override
	public String toString()
	{
		String s = getTour().toString();
		return "[" + s.substring(0, s.indexOf('-') - 1) + "]";
	}
}