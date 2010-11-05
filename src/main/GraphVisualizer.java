package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Point;
import java.util.Collection;

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
	private Tourable tour;
	private Collection<? extends Collection<Edge>> hilightedEdges;

	private double scale;

	private Point offset = new Point(0, 0);

	public GraphVisualizer(Graph g)
	{
		this(g, 10);
	}

	/**
	 * 
	 */
	public GraphVisualizer(Graph g, double scale)
	{
		graph = g;
		this.scale = scale;
		createFrame();

		int maxX = 0;
		int maxY = 0;
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		for (int a = 0; a < graph.countNodes(); a++)
		{
			int x = (int) graph.getX(a);
			int y = (int) graph.getY(a);

			maxX = Math.max(x, maxX);
			maxY = Math.max(y, maxY);
			minX = Math.min(x, minX);
			minY = Math.min(y, minY);
		}

		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);

		int graphHeight = maxY - minY;
		int graphWidth = maxX - minX;

		this.scale = Math.min((double) (frame.getHeight() - 100) / (double) graphHeight, (double) frame.getWidth() / (double) graphWidth);
		offset = new Point((int) (minX * this.scale), (int) (minY * this.scale));
		System.out.print("Scale: " + this.scale + " ");
		System.out.println("Offset: " + offset + " ");

		/*
				int fw = frame.getWidth();
				int fh = frame.getHeight() + 100;
				if (maxX + 40 > fw || maxY + 40 > fh)
				{
					this.setMinimumSize(new Dimension(Math.max(fw, maxX + 40), Math.max(fh, maxY + 40)));
					this.setPreferredSize(new Dimension(Math.max(fw, maxX + 40), Math.max(fh, maxY + 40)));
					frame.pack();
				}
		*/
	}

	public void setTour(Tourable t)
	{
		tour = t;
	}

	public void setHilightedEdges(Collection<? extends Collection<Edge>> edges)
	{
		hilightedEdges = edges;
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
		// Fill background with white
		g.setColor(new Color(255, 255, 255, 255));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		// Set some default settings
		g.setColor(Color.black);
		g.translate(20 - offset.x, 20 - offset.y);
		// Draw edges
		/*for (int a = 0; a < graph.countNodes(); a++)
		{
			for (int b = a + 1; b < graph.countNodes(); b++)
			{
				g.setColor(new Color(190, 190, 190));
				g.drawLine((int) (graph.getX(a) * scale), (int) (graph.getY(a) * scale), (int) (graph.getX(b) * scale), (int) (graph.getY(b) * scale));
			}
		}*/
		// Draw nodes
		for (int a = 0; a < graph.countNodes(); a++)
		{
			int size = 4;
			g.setColor(Color.black);
			int x = (int) (graph.getX(a) * scale - size / 2);
			int y = (int) (graph.getY(a) * scale - size / 2);
			g.drawOval(x, y, size, size);

			g.setColor(Color.blue);
			g.drawString("  " + a, x, y);
		}

		drawTour(g);
		drawHilights(g);
	}

	/**
	 * @param g
	 */
	private void drawHilights(Graphics2D g)
	{
		// Draw tour?
		if (hilightedEdges == null)
			return;

		Color[] colors = new Color[] { Color.green, Color.blue, Color.yellow, Color.magenta, Color.cyan, Color.orange, Color.pink };
		int i = 0;
		for (Collection<Edge> c : hilightedEdges)
		{
			g.setColor(colors[i % colors.length]);
			for (Edge e : c)
			{
				g.drawLine((int) (graph.getX(e.nodeA) * scale), (int) (graph.getY(e.nodeA) * scale), (int) (graph.getX(e.nodeB) * scale), (int) (graph.getY(e.nodeB) * scale));
			}
			i++;
		}
	}

	/**
	 * @param g
	 */
	private void drawTour(Graphics2D g)
	{
		// Draw tour?
		if (tour == null)
			return;

		int a = tour.getNode(0);
		for (int i = 0; i <= tour.countNodes(); i++)
		{
			int b = tour.getNode(i % tour.countNodes());
			g.setColor(new Color(255, 0, 0, 100));
			g.drawLine((int) (graph.getX(a) * scale), (int) (graph.getY(a) * scale), (int) (graph.getX(b) * scale), (int) (graph.getY(b) * scale));
			a = b;
		}
	}
}
