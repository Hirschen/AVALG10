package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * // TODO: Visualizer is a ...
 * 
 * @author Martin Nycander
 * @since 
 */
public class GraphVisualizer extends JPanel
{
	private static final long serialVersionUID = 1L;

	private JFrame frame;

	private Graph graph;

	/**
	 * 
	 */
	public GraphVisualizer(Graph g)
	{
		graph = g;

		createFrame();
	}

	/**
	 * @throws HeadlessException
	 */
	private void createFrame() throws HeadlessException
	{
		frame = new JFrame("Graph Visualizer");
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(1, 1));
		frame.add(this);
		frame.pack();
		frame.repaint();
	}

	@Override
	public void paintComponent(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics;
		g.setColor(Color.black);
		g.setBackground(Color.white);
		
		// Draw nodes
		for(int a = 0; a < graph.countNodes(); a++)
		{
			// for(int )
		}
		
	}
}
