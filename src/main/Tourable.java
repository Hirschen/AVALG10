package main;


/**
 * // TODO: Tourable is a ...
 * 
 * @author Martin Nycander
 * @since 
 */
public interface Tourable extends Iterable<Short>
{
	/**
	 * @return the number of nodes in the tour.
	 */
	int countNodes();

	/**
	 * 
	 * @param position
	 * @param node
	 */
	void setNode(int position, short node);

	/**
	 * 
	 * @param node
	 */
	void addNode(short node);

	/**
	 * @param position
	 * @return
	 */
	short getNode(int position);

	/**
	 * Outputs the tour to a given output.
	 * 
	 * @param out
	 *            the Kattio object to write with.
	 */
	void printTo(Kattio out);

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	String toString();

	@Deprecated
	void addEdge(Edge e);

	@Deprecated
	void switchEdges(Graph g, int e1, int e2, Edge f1, Edge f2);
}