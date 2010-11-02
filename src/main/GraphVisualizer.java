package main;

import java.awt.Color;
import java.awt.Dimension;
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
	private Tour tour;

	/**
	 * 
	 */
	public GraphVisualizer(Graph g)
	{
		graph = g;

		createFrame();
	}

	public void setTour(Tour t)
	{
		tour = t;
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
		frame.setSize(new Dimension(600, 300));
		frame.repaint();

		this.setOpaque(true);
	}

	@Override
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;
		g.setColor(Color.black);
		g.setBackground(new Color(255, 255, 255));

		int scale = 20;

		// Draw edges
		for(int a = 0; a < graph.countNodes(); a++)
		{
			for(int b = a+1; b < graph.countNodes(); b++)
			{
				g.drawLine((int) graph.getX(a) * scale, (int) graph.getY(a) * scale, (int) graph.getX(b) * scale, (int) graph.getY(b) * scale);
			}
		}
		// Draw nodes
		for (int a = 0; a < graph.countNodes(); a++)
		{
			int size = 4;
			g.drawOval((int) graph.getX(a) * scale - size / 2, (int) graph.getY(a) * scale - size / 2, size, size);

			g.setColor(Color.blue);
			g.drawString("" + a, (int) graph.getX(a) * scale - size / 2, (int) graph.getY(a) * scale - size / 2);
		}

		// Draw tour?
		if (tour == null)
			return;
	}
}

