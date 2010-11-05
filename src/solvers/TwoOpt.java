package solvers;

import main.Edge;
import main.Graph;
import main.GraphVisualizer;
import main.Tourable;

public class TwoOpt implements Improver {
	private Graph G;
	private short searchSize = -1;;
	
	public TwoOpt(){
	}
	public TwoOpt(int tourSize){
		searchSize = (short) setSearchSize(tourSize);
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
	public void improve(Graph g, Tourable t)
	{
		G=g;
		short a1 = fetchFirstEdge(t);
		short b1 = (short) (a1+1);
		short a2, b2;
		
		
		if(searchSize == -1){
			for( a2=0; a2 < t.countNodes()-1; a2++){
				b2=(short) (a2+1);
				if(t.getNode(a1) != t.getNode(a2) && 
					(g.distance(t.getNode(a1), t.getNode(b1))+g.distance(t.getNode(a2), t.getNode(b2))-(g.distance(a1, a2)+g.distance(b1, b2))) > 0 
					&& checkFeasbility(a1,b1,a2,b2,t)){
					flip2opt(t,a1,b1,a2,b2);
					return;
				}
			}
		}else{
			a2 = (short) (a1-searchSize);
			for(int i=0; i < 2*searchSize; i++, a2++){
				if(a2 < 0){
					a2 = (short) (t.countNodes()-1+a2);
				}
				if(a2 > t.countNodes()-1-1){
					a2 = 0;
				}
				b2=(short) (a2+1);
				if(t.getNode(a1) != t.getNode(a2) && 
					(g.distance(t.getNode(a1), t.getNode(b1))+g.distance(t.getNode(a2), t.getNode(b2))-(g.distance(a1, a2)+g.distance(b1, b2))) > 0 
					&& checkFeasbility(a1,b1,a2,b2,t)){
					flip2opt(t,a1,b1,a2,b2);
					return;
				}
			}
		}
		
		return;
	}

	private boolean checkFeasbility(short a1, short b1, short a2, short b2, Tourable t)
	{
		int tmp = Math.abs(a1-a2);
		if(tmp <= 1 || tmp >= t.countNodes()-4){
			return false;
		}
		if((a1 == 0 && a2 == t.countNodes()-3) || (a1 == 1 && a2 == t.countNodes()-2)
				|| (a2 == 0 && a1 == t.countNodes()-3) || (a2 == 1 && a2 == t.countNodes()-2)){
			return false;
		}
		return true;
	}

	private void flip2opt(Tourable t, short a1, short b1, short a2, short b2) {
		if(a1 < a2){
			t.switchEdges(G, a1, a2, b1, b2);
		}
		else{
			t.switchEdges(G, a2, a1, b2, b1);
		}
		return;
	}

	private short fetchFirstEdge(Tourable t)
	{
		return (short) (Math.random()*(t.countNodes()-2));
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
		Tourable t = sa.getTour(g);
		vis.setTour(t);
		Improver imp = new TwoOpt(t.countNodes()-1);
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
