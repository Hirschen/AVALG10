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

	public int addEdge(short a, short b)
	{
		int last = getLast();
		int first = getFirst();
		if (a == last)
		{
			appendNode(b);
			return b;
		}
		if (b == last)
		{
			appendNode(a);
			return a;
		}
		if (a == first)
		{
			prependNode(b);
			return b;
		}
		if (b == first)
		{
			prependNode(a);
			return a;
		}

		return -1;
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
			short r = tour[end];
			tour[end++] = 0;
			return r;
		}

		short r = tour[0];
		System.arraycopy(tour, 1, tour, 0, start);
		tour[start--] = 0;
		return r;
	}

	public short removeFirst()
	{
		if (start < 0)
		{
			short r = tour[tour.length - 1];
			System.arraycopy(tour, end, tour, end + 1, tour.length - 1 - end);
			tour[end++] = 0;
			return r;
		}
		short r = tour[start];
		tour[start--] = 0;
		return r;
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