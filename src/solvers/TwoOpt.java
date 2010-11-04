package solvers;

import main.Edge;
import main.Graph;
import main.GraphVisualizer;
import main.Tour;

public class TwoOpt implements Improver {
	private Graph G;
	private int searchSize = -1;;
	
	public TwoOpt(){
	}
	public TwoOpt(int tourSize){
		searchSize = setSearchSize(tourSize);
	}
	private int setSearchSize(int s){
		int res = 4;
		if(s > 10){
			res = 6;
		}
		if(s > 100){
			res = 10;
		}
		if(s > 500){
			res = 20;
		}
		return res;
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
		
		if(searchSize == -1){
			for(int i=0; i < t.countEdges(); i++){
				tmpE = t.getEdge(i);
				a2=tmpE.nodeA;
				b2=tmpE.nodeB;
				if(E1 != tmpE && 
					(tmpE.length+E1.length-(g.distance(a1, a2)+g.distance(b1, b2))) > 0 
					&& checkFeasbility(e1,i,t)){
					flip2opt(t,a1,b1,a2,b2, e1,i);
					return;
				}
			}
		}else{
			int j = e1-searchSize;
			for(int i=0; i < 2*searchSize; i++, j++){
				if(j < 0){
					j = t.countEdges()+j;
				}
				if(j > t.countEdges()-1){
					j = 0;
				}
				tmpE = t.getEdge(j);
				a2=tmpE.nodeA;
				b2=tmpE.nodeB;
				if(E1 != tmpE && 
					(tmpE.length+E1.length-(g.distance(a1, a2)+g.distance(b1, b2))) > 0 
					&& checkFeasbility(e1,j,t)){
					flip2opt(t,a1,b1,a2,b2, e1,j);
					return;
				}
			}
		}
		
		return;
	}
	private boolean checkFeasbility(int e1, int e2, Tour t){
		int tmp = Math.abs(e1-e2);
		if(tmp <= 1 || tmp >= t.countEdges()-3){
			return false;
		}
		if((e1 == 0 && e2 == t.countEdges()-2) || (e1 == 1 && e2 == t.countEdges()-1)
				|| (e2 == 0 && e1 == t.countEdges()-2) || (e2 == 1 && e2 == t.countEdges()-1)){
			return false;
		}
		return true;
	}

	private void flip2opt(Tour t, short a1, short b1, short a2, short b2, int e1,
			int e2) {
		if(e1 < e2){
			t.switchEdges(G, e1, e2, G.getEdge(a1, a2), G.getEdge(b1, b2));
		}
		else{
			t.switchEdges(G, e2, e1, G.getEdge(a2, a1), G.getEdge(b2, b1));
		}
		//System.out.println("Changed " + a1+ " "+b1 + " and " + a2 + " "+b2+" Edge:"+e1+" and "+e2 );
		return;
	}

	private int fetchFirstEdge(Tour t){
		return (int) (Math.random()*(t.countEdges()-1));
	}
	
	private int findCandidate(){
		//TODO
		return 0;
	}
	public static void main(String[] args) throws InterruptedException
	{
		/* Simple graph */
		double[][] coords = new double[][] { { 0, 3 }, { 3, 0 }, { 4, 3 }, { 3, 10 }, { 10, 3 }, { 25, 4 }, { 10, 10 }, { 25, 11 }, {12,23},{8,6} };
		Graph g = new Graph(coords);
		GraphVisualizer vis = new GraphVisualizer(g);

		StartApproxer sa = new NaiveSolver();
		Tour t = sa.getTour(g);
		vis.setTour(t);
		Improver imp = new TwoOpt(t.countEdges());
		for(int i = 0; i < 1000; i++){
			Thread.sleep(1);
			//System.out.println(g.calculateLength(t));
			imp.improve(g, t);
			//System.out.println(g.calculateLength(t));
			vis.updateUI();
		}
		System.out.println(t);
	}
}
