package solvers;

import main.Graph;
import main.GraphVisualizer;
import main.Tourable;

public class ThreeOpt implements Improver {

	
	public void improve(Graph g, Tourable t) {
		short a1 = fetchEdge(t);
		short b1 = (short) (a1+1);
		short a2, b2,a3,b3;
		
		for( a2=0; a2 < t.countNodes()-1; a2++){
			while(a2 == a1 || b1 == a2){
				a2 = fetchEdge(t);
			}
			b2=(short) (a2+1);
			for( a3=0; a3 < (t.countNodes()-1); a3++){
				while(a3 == a1 || a3 == a2 || a3 == b1 || a3 == b2){
					a3 = fetchEdge(t);
				}
				b3=(short) (a3+1);
			
				if(checkFeasibility(t, a1,b1,a2,b2,a3,b3)){
					int gain = gotGain(g,t, a1,b1,a2,b2,a3,b3);
					if(gain == 0){
						continue;
					}
					if(gain == 1){
						threeOptbyTwoOpt(1,t,a1,b1,a2,b2,a3,b3);
						return;
					}
					if(gain == 2){
						threeOptbyTwoOpt(2,t,a1,b1,a2,b2,a3,b3);
						return;
					}
				}
			}			
		}
	}
		
	private int gotGain(Graph g, Tourable t, short a1, short b1, short a2,
			short b2, short a3, short b3) {
		if((g.distance(t.getNode(a1), t.getNode(b1))+g.distance(t.getNode(a2), t.getNode(b2))
				-(g.distance(t.getNode(a1), t.getNode(a2))+g.distance(t.getNode(b1), t.getNode(b2))))
				+
				(g.distance(t.getNode(a1), t.getNode(a2))+g.distance(t.getNode(a3), t.getNode(b3))
						-(g.distance(t.getNode(a1), t.getNode(a3))+g.distance(t.getNode(a2), t.getNode(b3)))
				) > 0){
			return 1;
		}
		if((g.distance(t.getNode(a1), t.getNode(b1))+g.distance(t.getNode(a2), t.getNode(b2))
				-(g.distance(t.getNode(a1), t.getNode(a2))+g.distance(t.getNode(b1), t.getNode(b2))))
				+
				(g.distance(t.getNode(b1), t.getNode(b2))+g.distance(t.getNode(a3), t.getNode(b3))
						-(g.distance(t.getNode(a2), t.getNode(a3))+g.distance(t.getNode(b2), t.getNode(b3)))
				) > 0){
			return 2;
		}
		return 0;
	}
	
	private boolean checkFeasibility(Tourable t, short a1, short b1, short a2, short b2, short a3, short b3){
		int tmp = Math.abs(a1-a2), tmp2 = Math.abs(a2-a3), tmp3 = Math.abs(a1-a3);
		
		if(tmp <= 1 || tmp2 <= 1 || tmp3 <= 1){
			return false;
		}
		return true;
	}
	
	private void threeOptbyTwoOpt(int version, Tourable t, short a1, short b1, short a2, short b2, short a3, short b3){
		if(version == 1){
			t.switch2Edges(a1, b1, a2, b2);
			t.switch2Edges(a1, a2, a3, b3);
		}
		else{
			t.switch2Edges(a1, b1, a2, b2);
			t.switch2Edges(b1, b2, a3, b3);
		}
		
	}
	
	private short fetchEdge(Tourable t)
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
		t.addNode(t.getNode(0));
		vis.setTour(t);
		Improver imp = new ThreeOpt();
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
