package solvers;

import main.Graph;
import main.GraphVisualizer;
import main.Tourable;

public class ThreeOpt implements Improver {
	private short minGain = 1;

	public ThreeOpt(){}
	
	public boolean improve(Graph g, Tourable t)
	{
		short a1,b1,a2, b2,a3,b3,i,j;
		boolean res = false;
		
		for(a1 = 0; a1 < t.countNodes(); a1++){
			b1 = (short) (a1+1);
			if (b1 == t.countNodes())
			{
				b1 = 0;
			}
			short[] neigh1 = g.getNeighbours(t.getNode(a1));
			for(i = 0;i < neigh1.length; i++){
				a2 = t.indexOf(neigh1[i]);
				if(a2 == a1 || b1 == a2 ){
					break;
				}
				b2=(short) (a2+1);
				if(b2 == t.countNodes()){
					b2 = 0;
				}
				short[] neigh2 = g.getNeighbours(t.getNode(a2));
				for(j=0; j < neigh2.length; j++){
					a3 = t.indexOf(neigh2[j]);
					if(a3 == a1 || a3 == a2 || a3 == b1 || a3 == b2){
						break;
					}
					b3=(short) (a3+1);
					if(b3 == t.countNodes()){
						b3 = 0;
					}
					if(checkFeasibility(t, a1,b1,a2,b2,a3,b3)){
						int gain = gotGain(g,t, a1,b1,a2,b2,a3,b3);
						if(gain == 0){
							continue;
						}
						if(gain == 1){
							threeOptbyTwoOpt(1,t,a1,b1,a2,b2,a3,b3);
							res = true;
						}
						if(gain == 2){
							threeOptbyTwoOpt(2,t,a1,b1,a2,b2,a3,b3);
							res = true;
						}
						if(gain == 3){
							threeOptbyTwoOpt(3,t,a1,b1,a2,b2,a3,b3);
							res = true;
						}
						if(gain == 4){
							threeOptbyTwoOpt(4,t,a1,b1,a2,b2,a3,b3);
							res = true;
						}
						if(gain == 5){
							threeOptbyTwoOpt(5,t,a1,b1,a2,b2,a3,b3);
							res = true;
						}
						if(gain == 6){
							threeOptbyTwoOpt(6,t,a1,b1,a2,b2,a3,b3);
							res = true;
						}
					}
				}
			}
		}
		return res;
	}

	private boolean inBetween(short a1, short a2, short a3){
		if((a1 < a3 && a2 > a3) || (a1 > a3 && a2 < a3)){
			return true;
		}
		return false;
	}
	private boolean toTheLeft(short a1, short a2, short a3){
		if(a3 < a1 && a3 < a2){
			return true;
		}
		return false;
	}
	private boolean toTheRight(short a1, short a2, short a3){
		if(a3 > a1 && a3 > a2){
			return true;
		}
		return false;
	}
		
	private int gotGain(Graph g, Tourable t, short a1, short b1, short a2,
			short b2, short a3, short b3) {
		if(inBetween(a1, a2, a3)){
			if(b1 > a2 && (
					(gotGainHelpMethod(g, t, a1, b1, a2, b2)
					+
					gotGainCalc(g,t,a1,a2)+gotGainCalc(g,t,a3,b3)
					-gotGainCalc(g,t,a1,a3)-gotGainCalc(g,t,a2,b3)
					)> minGain)){
				return 1;
			}
			if(b1 < a2 && (
					(gotGainHelpMethod(g, t, a1, b1, a2, b2)
					+
					gotGainCalc(g,t,a2,a1)+gotGainCalc(g,t,a3,b3)
					-gotGainCalc(g,t,a2,a3)-gotGainCalc(g,t,a1,b3)
					)> minGain)){
				return 2;
			}
		}
		if(toTheLeft(a1, a2, a3)){
			if(b1 > a2 && ( //b2, a1
					(gotGainHelpMethod(g, t, a1, b1, a2, b2)
					+
					gotGainCalc(g,t,a2,a1)+gotGainCalc(g,t,a3,b3)
					-gotGainCalc(g,t,a2,a3)-gotGainCalc(g,t,a1,b3)
					)> minGain)){
				return 3;
			}
			if(b1 < a2 && (//b1, a2
					(gotGainHelpMethod(g, t, a1, b1, a2, b2)
					+
					gotGainCalc(g,t,a1,a2)+gotGainCalc(g,t,a3,b3)
					-gotGainCalc(g,t,a1,a3)-gotGainCalc(g,t,a2,b3)
					)> minGain)){
				return 4;
			}
		}
		if(toTheRight(a1, a2, a3)){
			if(b1 > a2 && ( //b2, a1
					(gotGainHelpMethod(g, t, a1, b1, a2, b2)
					+
					gotGainCalc(g,t,b2,b1)+gotGainCalc(g,t,a3,b3)
					-gotGainCalc(g,t,b2,a3)-gotGainCalc(g,t,b1,b3)
					)> minGain)){
				return 5;
			}
			if(b1 < a2 && (//b1, a2
					(gotGainHelpMethod(g, t, a1, b1, a2, b2)
					+
					gotGainCalc(g,t,b1,b2)+gotGainCalc(g,t,a3,b3)
					-gotGainCalc(g,t,b1,a3)-gotGainCalc(g,t,b2,b3)
					)> minGain)){
				return 6;
			}
		}
		return 0;
	}

	private int gotGainHelpMethod(Graph g, Tourable t, short a1, short b1,
			short a2, short b2) {
		return gotGainCalc(g,t,a1,b1)+gotGainCalc(g,t,a2,b2)
		-gotGainCalc(g,t,a1,a2)-gotGainCalc(g,t,b1,b2);
	}

	private int gotGainCalc(Graph g, Tourable t, short n1, short n2) {
		return g.distance(t.getNode(n1), t.getNode(n2));
	}
	
	private boolean checkFeasibility(Tourable t, short a1, short b1, short a2, short b2, short a3, short b3){
		int tmp = Math.abs(a1-a2), tmp2 = Math.abs(a2-a3), tmp3 = Math.abs(a1-a3);
		
		if(tmp <= 0 || tmp2 <= 0 || tmp3 <= 0){
			return false;
		}
		return true;
	}
	
	private void threeOptbyTwoOpt(int version, Tourable t, short a1, short b1, short a2, short b2, short a3, short b3){
		if(version == 1){
			t.switchEdgesNonOptedWithLeap(a1, b1, a2, b2);
			t.switch2EdgesOpted(a1, b1, a3, b3);
		}
		if(version == 2){
			t.switchEdgesNonOptedWithLeap(a1, b1, a2, b2);
			t.switch2EdgesOpted(a2, b2, a3, b3);
		}
		if(version == 3){
			t.switchEdgesNonOpted(a1, b1, a2, b2);
			t.switch2EdgesOpted(a2, b2, a3, b3);
		}
		if(version == 4){
			t.switchEdgesNonOpted(a1, b1, a2, b2);
			t.switch2EdgesOpted(a1, b1, a3, b3);
		}
		if(version == 5){
			t.switchEdgesNonOpted(a1, b1, a2, b2);
			t.switch2EdgesOpted(a1, b1, a3, b3);
		}
		if(version == 6){
			t.switchEdgesNonOpted(a1, b1, a2, b2);
			t.switch2EdgesOpted(a2, b2, a3, b3);
		}
	}
	private short fetchEdge(Tourable t)
	{
		return (short) (Math.random()*(t.countNodes()-1));
	}
	public static void main(String[] args) throws InterruptedException
	{
		/* Simple graph */
		double[][] coords = new double[][] { { 0, 3 }, { 3, 0 }, { 4, 3 }, { 3, 10 }, { 10, 3 }, { 25, 4 }, { 10, 10 }, { 25, 11 }, {12,23},{8,6},{1,1},{10,5}, {25,1},{16,11},{15,15}, {25,25}, {20,20}, {23,23},  { 24, 1 }, {4,23},{5,6},{12,12},{1,30}, {25,4},{15,11},{17,15}, {20,25}, {19,21}, {25,23}  };
		Graph g = new Graph(coords);

		GraphVisualizer vis = GraphVisualizer.getGraphVisualizer(g, null);
		StartApproxer sa = new NaiveSolver();
		Tourable t = sa.getTour(g);
		vis.setTour(t);
		Improver imp = new ThreeOpt();
		System.out.println(g.calculateLength(t));
		for(int i = 0; i < 1000; i++){
			Thread.sleep(1);
			if(imp.improve(g, t)){
				System.out.println(g.calculateLength(t));
			}
			vis.updateUI();
		}
		System.out.println("Length of tour: "+g.calculateLength(t));
		System.out.println(t);
	}
}
