package solvers;

import main.Edge;
import main.Graph;
import main.GraphVisualizer;
import main.Tour;

public class TwoOpt implements Improver {
	private Graph G;
	
	public TwoOpt(){
		
	}
	/*
	 * (non-Javadoc)
	 * @see solvers.Improver#improve(main.Graph, main.Tour)
	 */
	public void improve(Graph g, Tour t) {
		G=g;
		Edge E1,tmpE; //EDGE
		int e1 = fetchFirstEdge(t);
		E1 = t.getEdge(e1);
		short a1 = E1.nodeA;
		short b1 = E1.nodeB;
		short a2, b2;
		
		for(int i=0; i < t.countEdges(); i++){
			tmpE = t.getEdge(i);
			a2=tmpE.nodeA;
			b2=tmpE.nodeB;
			if(E1 != tmpE && tmpE.nodeB != E1.nodeA && tmpE.nodeA != E1.nodeB
					&& tmpE.nodeB != E1.nodeB && tmpE.nodeA != E1.nodeA){
				if((tmpE.length+E1.length-(g.distance(a1, a2)+g.distance(b1, b2))) > 0){
					flip2opt(t,a1,b1,a2,b2, e1,i);
					return;
				}
			}
		}
		return;
	}
	/*
	 * t, a1,b1,a2,b2
	 * Flips edge a1,b1 and a2,b2 into a1,a2 and b1,b2
	 */

	private void flip2opt(Tour t, short a1, short b1, short a2, short b2, int e1,
			int e2) {
		t.switchEdges(G, e1, e2, G.getEdge(a1, a2), G.getEdge(b1, b2));
		//System.out.println("Changed " + a1+ " "+b1 + " and " + a2 + " "+b2+" Edge:"+e1+" and "+e2 );
		return;
	}

	private int fetchFirstEdge(Tour t){
		return (int) (Math.random()*(t.countEdges()-1));
	}
	
	private Edge findCandidate(){
		return null;
	}
	public static void main(String[] args) throws InterruptedException
	{
		/* Simple graph */
		double[][] coords = new double[][] { { 0, 3 }, { 3, 0 }, { 4, 3 }, { 3, 10 }, { 10, 3 }, { 25, 4 }, { 10, 10 }, { 25, 11 } };
		Graph g = new Graph(coords);
		GraphVisualizer vis = new GraphVisualizer(g);

		StartApproxer sa = new NaiveSolver();
		Tour t = sa.getTour(g);
		vis.setTour(t);
		Improver imp = new TwoOpt();
		for(int i = 0; i < 100; i++){
			Thread.sleep(1500);
			System.out.println(g.calculateLength(t));
			imp.improve(g, t);
			System.out.println(g.calculateLength(t));
			GraphVisualizer tmp = new GraphVisualizer(g);
			tmp.setTour(t);
		}
		System.out.println(t);
		/**/
		/* Graph with branching * /
		double[][] coords = new double[][] { { 2, 2 }, { 2, 4 }, { 3, 3 }, { 6, 3 } };
		Graph g = new Graph(coords);
		StartApproxer sa = new KruskalApproximation();
		System.out.println(sa.getTour(g));
		/**/
	}
}
