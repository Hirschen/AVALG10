package solvers.unused;


/**
 * <a href="http://en.wikipedia.org/wiki/Kruskal's_algorithm">Kruskal's
 * algorithm - Wikipedia</a>
 * 
 * 
 * @author Martin Nycander
 * @since
 * /
 public class KruskalApproximation implements StartApproxer
 {
 private static final boolean verbose = false;

 /**
 * 
 * /
 public KruskalApproximation()
 {

 }

 /* (non-Javadoc)
 * @see solvers.StartApproxer#getTour(main.Graph)
 * /
 public Tourable getTour(Graph g)
 {
 // Special case for one node.
 if (g.countNodes() == 1)
 {
 Tourable tour = new ShortTour(1);
 tour.addNode((short) 0);
 return tour;
 }

 @SuppressWarnings("unused")
 HashSet<Edge> mst = findMinimumSpanningTree(g);

 double time = Main.time();

 // ...
 if (verbose)
 {
 System.out.println("Built result in: " + Main.timeDiff(Main.time(), time) + " ms.");
 }
 return null;
 }

 /**
 * @param g
 * /
 @SuppressWarnings("unchecked")
 private HashSet<Edge> findMinimumSpanningTree(Graph g)
 {
 Edge[] edges = g.getSortedEdgeList();

 double time;
 if (verbose)
 {
 System.out.println("Graph: " + g);
 // System.out.println("Edges: " + Arrays.toString(edges));
 time = Main.time();
 }


 HashSet<Edge>[] forest = new HashSet[g.countNodes()];
 HashSet<Edge> treeA;
 HashSet<Edge> treeB;
 for (int ep = 0; ep < edges.length; ep++)
 {
 // remove an edge with minimum weight from S
 Edge e = edges[ep];

 // make sure that edge connects two different trees.
 treeA = forest[e.nodeA];
 treeB = forest[e.nodeB];
 if (treeA != null && treeA == treeB)
 {
 continue;
 }

 // combine the two trees into a single tree
 HashSet<Edge> tree = null;
 if (treeA != null && treeB == null)
 {
 tree = treeA;
 }
 else if (treeA == null && treeB != null)
 {
 tree = treeB;
 }
 else if (treeA != null && treeB != null)
 {
 treeA.addAll(treeB);
 for (Edge te : treeB)
 {
 forest[te.nodeA] = treeA;
 forest[te.nodeB] = treeA;
 }
 tree = treeA;
 }
 else
 // treeA == null && treeB == null
 {
 tree = new HashSet<Edge>();
 }

 // then add it to the tree and update the forest
 tree.add(e);
 forest[e.nodeA] = tree;
 forest[e.nodeB] = tree;

 if (tree.size() == g.countNodes() - 1)
 {
 if (verbose)
 {
 System.out.println("MST: " + tree);
 System.out.println("Found MST in: " + Main.timeDiff(Main.time(), time) + " ms.");
 }
 return tree;
 }
 }
 throw new RuntimeException("Could not find a minimum spanning tree. This should be impossible in a complete graph.");
 }

 @SuppressWarnings("unused")
 private Tourable buildTour(HashSet<Edge> mst, Graph graph)
 {
 // Find the node with the most edges
 int centerNode = 0;
 int highestDegree = -1;

 for (int node = 0; node < graph.countNodes(); node++)
 {
 int degree = 0;
 for (Edge edge : mst)
 {
 if (edge.nodeA == node)
 degree++;
 if (edge.nodeB == node)
 degree++;
 }

 if (degree > highestDegree)
 {
 highestDegree = degree;
 centerNode = node;
 }
 }

 // System.out.println("Center node: " + centerNode + " (degree " +
 // highestDegree + ")");

 // Draw a tour
 Tourable tour = new ShortTour((graph.countNodes() - 1) * 2);
 for (int node = 0; node < graph.countNodes(); node++)
 {
 if (node == centerNode)
 continue;

 tour.addNode((short) centerNode);
 tour.addNode((short) node);
 tour.addNode((short) centerNode);
 }

 return tour;
 }

 public static void main(String[] args) throws Exception
 {
 /* Kattis * /
 Problem p = new Problem(new File("testdata/pr2392.tsp"), new File("testdata/pr2392.opt.tour"));
 Graph g = new Graph(p.coordinates);
 StartApproxer sa = new KruskalApproximation();
 Tour t = sa.getTour(g);
 System.out.println(t);
 /**/
		/* Simple graph * /
		double[][] coords = new double[][] { { 0, 3 }, { 3, 0 }, { 3, 3 }, { 3, 6 }, { 6, 3 } };
		Graph g = new Graph(coords);

		StartApproxer sa = new KruskalApproximation();
		Tourable t = sa.getTour(g);
		System.out.println(t);
		/**/
		/* Graph with branching * /
		double[][] coords = new double[][] { { 2, 2 }, { 2, 4 }, { 3, 3 }, { 6, 3 } };
		Graph g = new Graph(coords);
		StartApproxer sa = new KruskalApproximation();
		System.out.println(sa.getTour(g));
		/** /
		new GraphVisualizer(g, 10).setTour(t);
	}
}
/**/
