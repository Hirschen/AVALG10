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
	private short[] reverse;
	private int addPointer = 0;

	/**
	 * 
	 */
	public ShortTour(int size)
	{
		nodes = new short[size];
		for (int i = 0; i < nodes.length; i++)
			nodes[i] = -1;
		
		reverse = new short[size];
		for (int i = 0; i < reverse.length; i++)
			reverse[i] = -1;
	}

	public ShortTour(int[] tour)
	{
		nodes = new short[tour.length];
		reverse = new short[tour.length+1];
		for (short i = 0; i < tour.length; i++){
			nodes[i] = (short) tour[i];
			reverse[tour[i]] = i;
		}
		addPointer = tour.length;
	}

	/**
	 * @param t //TODO - not garantued working
	 */
	@Deprecated
	public ShortTour(int size, Tourable t)
	{
		nodes = new short[size];
		int i = 0;
		for (; i < t.countNodes(); i++)
			nodes[i] = (short) t.getNode(i);
		addPointer = i;
		for (; i < size; i++)
			nodes[i] = -1;
	}

	/* (non-Javadoc)
	 * @see main.Tourable#getNode(int)
	 */
	public short getNode(int position)
	{
		return nodes[position];
	}
	
	public short[] getNodes(){
		
		return nodes;
	}

	/* (non-Javadoc)
	 * @see main.Tourable#addNode(short)
	 */
	public void addNode(short node)
	{
		reverse[node] = (short) addPointer;
		nodes[addPointer++] = node;
	}

	/* (non-Javadoc)
	 * @see main.Tourable#setNode(int, short)
	 */
	public void setNode(int position, short node)
	{
		nodes[position] = node;
		reverse[node] = (short) position;

		if (position >= addPointer)
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
			for (short i = (short) (pointer + 1); i < nodes.length; i++){
				reverse[i-1] = reverse[i];
				nodes[i - 1] = nodes[i];
			}
			reverse[nodes.length-1] = -1;
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
	@Deprecated
	public void addEdge(Edge e)
	{
		if (nodes[addPointer - 1] != e.nodeA){
			nodes[addPointer++] = e.nodeA;
		}
		if (nodes[addPointer - 1] != e.nodeB){
			nodes[addPointer++] = e.nodeB;
		}
		else
			throw new RuntimeException("Tried to add the edge " + e + " which could not fit in tour " + this + ".");
	}

	/* (non-Javadoc)
	 * @see main.Tourable#contains(int)
	 */
	public boolean contains(int a)
	{
		return reverse[a] != -1;
	}
	/*
	 * b1 and a2 changes
	 * (non-Javadoc)
	 * @see main.Tourable#switchEdgesNonOpted(short, short, short, short)
	 */
	public void switchEdgesNonOpted(short a1, short b1, short a2, short b2)
	{
		if (b1 < a2)
		{
			short tmp = reverse[nodes[a2]];
			reverse[nodes[a2]] = reverse[nodes[b1]];
			reverse[nodes[b1]] = tmp;
			
			tmp = nodes[a2];
			nodes[a2] = nodes[b1];
			nodes[b1] = tmp;

			reverseBetweenEdges(b1, a2);
		}
		else
		{
			short tmp = reverse[nodes[a1]];
			reverse[nodes[a1]] = reverse[nodes[b2]];
			reverse[nodes[b2]] = tmp;
			
			tmp = nodes[a1];
			nodes[a1] = nodes[b2];
			nodes[b2] = tmp;
			reverseBetweenEdges(b2, a1);
		}
	}
	/*
	 * b1 and a2 changes!
	 * (non-Javadoc)
	 * @see main.Tourable#switchEdgesNonOptedWithLeap(short, short, short, short)
	 */
	public void switchEdgesNonOptedWithLeap(short a1, short b1, short a2, short b2){
		if (b1 > a2)
		{
			short tmp = reverse[nodes[a2]];
			reverse[nodes[a2]] = reverse[nodes[b1]];
			reverse[nodes[b1]] = tmp;
			
			tmp = nodes[a2];
			nodes[a2] = nodes[b1];
			nodes[b1] = tmp;

			reverseBetweenEdgesWithLeap(b1, a2);
		}
		else
		{
			short tmp = reverse[nodes[a1]];
			reverse[nodes[a1]] = reverse[nodes[b2]];
			reverse[nodes[b2]] = tmp;
			
			tmp = nodes[a1];
			nodes[a1] = nodes[b2];
			nodes[b2] = tmp;
			reverseBetweenEdgesWithLeap(b2, a1);
		}
	}

	/* (non-Javadoc)
	 * @see main.Tourable#switchEdges(main.Graph, int, int, main.Edge, main.Edge)
	 */
	public void switch2EdgesOpted(short a1, short b1, short a2, short b2)
	{
		if (Math.abs(a2 - a1) < addPointer / 2)
		{
			if (b1 < a2)
			{
				short tmp = reverse[nodes[a2]];
				reverse[nodes[a2]] = reverse[nodes[b1]];
				reverse[nodes[b1]] = tmp;
				
				tmp = nodes[a2];
				nodes[a2] = nodes[b1];
				nodes[b1] = tmp;

				reverseBetweenEdges(b1, a2);
			}
			else
			{
				short tmp = reverse[nodes[a1]];
				reverse[nodes[a1]] = reverse[nodes[b2]];
				reverse[nodes[b2]] = tmp;
				
				tmp = nodes[a1];
				nodes[a1] = nodes[b2];
				nodes[b2] = tmp;
				reverseBetweenEdges(b2, a1);
			}
		}
		else
		{
			if (b1 > a2)
			{
				short tmp = reverse[nodes[a2]];
				reverse[nodes[a2]] = reverse[nodes[b1]];
				reverse[nodes[b1]] = tmp;
				
				tmp = nodes[a2];
				nodes[a2] = nodes[b1];
				nodes[b1] = tmp;

				reverseBetweenEdgesWithLeap(b1, a2);
			}
			else
			{
				short tmp = reverse[nodes[a1]];
				reverse[nodes[a1]] = reverse[nodes[b2]];
				reverse[nodes[b2]] = tmp;
				
				tmp = nodes[a1];
				nodes[a1] = nodes[b2];
				nodes[b2] = tmp;
				reverseBetweenEdgesWithLeap(b2, a1);
			}
		}
	}

	private void reverseBetweenEdges(short p1, short p2)
	{
		if(p1 == p2){
			return;
		}
		short distance = (short) (p2 - p1 - 1), i, j;
		short[] tmp = new short[distance];
		short[] tmp2 = new short[distance];
		for (i = (short) (p2 - 1), j = 0; j < distance; i--, j++)
		{
			tmp[j] = nodes[i];
			tmp2[j] = reverse[nodes[i]];
		}
		for (j = 0; j < distance; i++, j++)
		{
			reverse[nodes[i+1]] = tmp2[j];
			nodes[i + 1] = tmp[j];
			
		}
	}

	private void reverseBetweenEdgesWithLeap(short p1, short p2)
	{
		short distance, i, j;
		distance = (short) (addPointer - p1 + p2 - 1);
		short[] tmp = new short[distance];
		short[] tmp2 = new short[distance];
		// p2 -> 0
		for (i = (short) (p2 - 1), j = 0; i >= 0; i--, j++)
		{
			tmp2[j] = reverse[nodes[i]];
			tmp[j] = nodes[i];
		}
		// size -> p1
		for (i = (short) (addPointer - 1); j < distance; i--, j++)
		{
			tmp2[j] = reverse[nodes[i]];
			tmp[j] = nodes[i];
		}

		for (i = (short) (p1 + 1), j = 0; i <= addPointer - 1; i++, j++)
		{
			reverse[nodes[i]] = tmp2[j];
			nodes[i] = tmp[j];
		}
		for (i = 0; j < distance; i++, j++)
		{
			reverse[nodes[i]] = tmp2[j];
			nodes[i] = tmp[j];
		}
	}

	/* (non-Javadoc)
	 * @see main.Tourable#getNextNode(short)
	 */
	public short getNextNode(short a)
	{
		int index = a + 1;
		if (index == addPointer)
			index = 0;
		return nodes[index];
	}

	/* (non-Javadoc)
	 * @see main.Tourable#getNextNode(short, int)
	 */
	public short getNextNode(short a, int i)
	{
		int index = (a + i) % addPointer;
		return nodes[index];
	}

	/* (non-Javadoc)
	 * @see main.Tourable#indexOf(short)
	 */
	public short indexOf(short node)
	{
		return reverse[node];
	}

	/* (non-Javadoc)
	 * @see main.Tourable#moveNode(int, short)
	 */
	public void moveNode(short currentIndex, short targetIndex)
	{
		short node = getNode(currentIndex);

		if (currentIndex == targetIndex)
		{
			return;
		}
		else if (currentIndex > targetIndex)
		{
			for (short i = currentIndex; i > targetIndex; i--)
			{
				reverse[nodes[i]] = reverse[nodes[i-1]];
				nodes[i] = nodes[i - 1];
				
			}
			reverse[node] = targetIndex;
			nodes[targetIndex] = node;
		}
		else
		{
			for (short i = currentIndex; i < targetIndex; i++)
			{
				reverse[nodes[i]] = reverse[i+1];
				nodes[i] = nodes[i + 1];
				
			}
			reverse[node] = targetIndex;
			nodes[targetIndex] = node;
		}
	}
}
