package main;

public class Tour
{
	private short[] tour;

	public Tour()
	{
		this(16);
	}

	public Tour(int nodes)
	{
		tour = new short[nodes];
	}

	public Tour(int[] tour)
	{
		this.tour = new short[tour.length];
		for (int i = 0; i < tour.length; i++)
		{
			assert (tour[i] < Short.MAX_VALUE);
			this.tour[i] = (short) tour[i];
		}
	}

	public int getLength()
	{
		return tour.length;
	}

	public int getNode(int i)
	{
		return tour[i];
	}

	public void setNode(int i, int v)
	{
		if (i >= tour.length)
		{
			resizeToFit(i);
		}
		assert (v < Short.MAX_VALUE);
		tour[i] = (short) v;
	}

	/**
	 * @param i
	 */
	private void resizeToFit(int i)
	{
		int newSize = tour.length * 2;
		while (newSize <= i)
		{
			newSize *= 2;
		}

		resizeTo(newSize);
	}

	/**
	 * @param newSize
	 */
	private void resizeTo(int newSize)
	{
		short[] newTour = new short[newSize];
		for (int i = 0; i < tour.length; i++)
		{
			newTour[i] = tour[i];
		}
		tour = newTour;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tour.length; i++)
		{
			sb.append(tour[i]);
			sb.append(' ');
		}
		return sb.toString();
	}
}
