package solvers;

import main.Graph;
import main.GraphVisualizer;
import main.Tourable;

public class TwoOpt implements Improver {

	private short searchSize = -1;
	private int minGain = 0;
	
	public TwoOpt(){
	}
	public TwoOpt(short tourSize){
		searchSize = setSearchSize(tourSize);
	}
	public TwoOpt(int minimumGain){
		minGain = minimumGain;
	}
	public TwoOpt(int tourSize, int minimumGain){
		searchSize = (short) setSearchSize(tourSize);
		minGain = minimumGain;
	}
	private short setSearchSize(int s){
		short res = 4;
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
		short a1 = fetchFirstEdge(t), b1 = (short) (a1+1 % (t.countNodes()-1));
		short a2, b2;
		
		
		
		if(searchSize == -1){
			for( a2=0; a2 < t.countNodes()-1; a2++){
				b2=(short) (a2+1);
				if(b2 == t.countNodes()-1){
					b2 = 0;
				}
				if(t.getNode(a1) != t.getNode(a2) && 
					gotGain(g, t, a1, b1, a2, b2) 
					&& checkFeasbility(a1,b1,a2,b2,t)){
					t.switch2EdgesOpted(a1, b1, a2, b2);
					return;
				}
			}
		}else{
			a2 = (short) (a1-searchSize);
			for(int i=0; i < 2*searchSize; i++, a2++){
				if(a2 < 0){
					a2 = (short) (t.countNodes()-1+a2);
				}
				if(a2 > t.countNodes()-2){
					a2 = 0;
				}
				b2=(short) (a2+1);
				if(t.getNode(a1) != t.getNode(a2) && 
						gotGain(g, t, a1, b1, a2, b2) 
						&& checkFeasbility(a1,b1,a2,b2,t)){
						t.switch2EdgesOpted(a1, b1, a2, b2);
						return;
				}
			}
		}
		return;
	}
	private boolean gotGain(Graph g, Tourable t, short a1, short b1, short a2,
			short b2) {
		return (g.distance(t.getNode(a1), t.getNode(b1))+g.distance(t.getNode(a2), t.getNode(b2))
				-(g.distance(t.getNode(a1), t.getNode(a2))+g.distance(t.getNode(b1), t.getNode(b2)))) > minGain;
	}
	
	private boolean checkFeasbility(short a1, short b1, short a2, short b2, Tourable t)
	{
		int tmp = Math.abs(b1-a2);
		if(tmp == 0 ){
			return false;
		}
		/*if((a1 == 0 && a2 == t.countNodes()-2) || (a1 == 1 && a2 == t.countNodes()-2)
				|| (a2 == 0 && a1 == t.countNodes()-3) || (a2 == 1 && a2 == t.countNodes()-2)){
			return false;
		}*/
		return true;
	}

	private short fetchFirstEdge(Tourable t)
	{
		return (short) (Math.random()*(t.countNodes()-1));
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
		Improver imp = new TwoOpt();
		for(int i = 0; i < 1000; i++){
			Thread.sleep(1);
			//System.out.println(g.calculateLength(t));
			imp.improve(g, t);
			//System.out.println(g.calculateLength(t));
			vis.updateUI();
		}
		System.out.println("Length of tour: "+g.calculateLength(t));
		System.out.println(t);
	}
}
