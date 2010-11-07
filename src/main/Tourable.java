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
	 * @param a
	 * @return
	 */
	boolean contains(int a);

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

	void addEdge(Edge e);

	void switch2EdgesOpted(short a1, short a2, short b1, short b2);

	/**
	 * @param a
	 * @return
	 */
	short getNextNode(short a);

	/**
	 * @param a
	 * @param i
	 * @return
	 */
	short getNextNode(short a, int i);

	/**
	 * @param e1
	 * @return
	 */
	short indexOf(short e1);

	/**
	 * @param node
	 * @param targetIndex
	 */
	void moveNode(short node, short targetIndex);

}