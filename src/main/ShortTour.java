package main;

import java.util.Iterator;

/**
 * // TODO: ShortTour is a ...
 * 
 * @author Martin Nycander
 * @since 2010-11-05
 */
public class ShortTour implements Tourable
{
	/**
	 * Contains the nodes of the tour. The order is important!
	 */
	private short[] nodes;
	private int addPointer = 0;

	/**
	 * 
	 */
	public ShortTour(int size)
	{
		nodes = new short[size];
		for (int i = 0; i < nodes.length; i++)
			nodes[i] = -1;
	}

	public ShortTour(int[] tour)
	{
		nodes = new short[tour.length];
		for (int i = 0; i < tour.length; i++)
			nodes[i] = (short) tour[i];
	}

	/* (non-Javadoc)
	 * @see main.Tourable#getNode(int)
	 */
	public short getNode(int position)
	{
		return nodes[position];
	}

	/* (non-Javadoc)
	 * @see main.Tourable#addNode(short)
	 */
	public void addNode(short node)
	{
		nodes[addPointer++] = node;
	}

	/* (non-Javadoc)
	 * @see main.Tourable#setNode(int, short)
	 */
	public void setNode(int position, short node)
	{
		nodes[position] = node;

		if (position > addPointer)
			addPointer = position + 1;
	}

	/* (non-Javadoc)
	 * @see main.Tourable#countNodes()
	 */
	public int countNodes()
	{
		return addPointer;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Short> iterator()
	{
		return new TourIterator();
	}

	/**
	 * TourIterator is a simple iterator class for iterating through the tour
	 * rather fast.
	 * 
	 * @author Martin Nycander
	 * @since 2010-11-05
	 */
	private final class TourIterator implements Iterator<Short>
	{
		private int pointer = 0;

		public boolean hasNext()
		{
			return pointer < nodes.length;
		}

		public Short next()
		{
			return nodes[pointer++];
		}

		public void remove()
		{
			for (int i = pointer + 1; i < nodes.length; i++)
				nodes[i - 1] = nodes[i];

			nodes[nodes.length - 1] = -1;
		}
	}

	/* (non-Javadoc)
	 * @see main.Tourable#printTo(java.io.OutputStream)
	 */
	public void printTo(Kattio out)
	{
		for (int i = 0; i < nodes.length; i++)
		{
			out.print(nodes[i]);
			out.print(' ');
		}
		out.print('\n');
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < nodes.length; i++)
		{
			sb.append(nodes[i]);
			sb.append(' ');
		}
		sb.append('\n');

		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see main.Tourable#addEdge(main.Edge)
	 */
	public void addEdge(Edge e)
	{
		if (nodes[addPointer - 1] != e.nodeA)
			nodes[addPointer++] = e.nodeA;

		if (nodes[addPointer - 1] != e.nodeB)
			nodes[addPointer++] = e.nodeB;
		else
			throw new RuntimeException("Tried to add the edge " + e + " which could not fit in tour " + this + ".");
	}

	/* (non-Javadoc)
	 * @see main.Tourable#switchEdges(main.Graph, int, int, main.Edge, main.Edge)
	 */
	public void switchEdges(Graph g, int e1, int e2, Edge f1, Edge f2)
	{
		// TODO Auto-generated method stub

	}
}
