package solvers;

import main.Edge;
import main.Graph;
import main.Tour;

public class TwoOpt implements Improver {
	private Graph G;
	
	public TwoOpt(){
		
	}

	@Override
	public Tour improve(Graph g, Tour t) {
		G=g;
		Edge E1,tmpE; //EDGE
		int e1 = fetchFirstEdge(t);
		E1 = t.getEdge(e1);
		short a1 = E1.nodeA;
		short b1 = E1.nodeB;
		short a2, b2;
		
		for(int i=0; i < t.getLength(); i++){
			tmpE = t.getEdge(i);
			a2=tmpE.nodeA;
			b2=tmpE.nodeB;
			if(t.getPredecessorNode(a1) != b2 && t.getSuccessorNode(b2) != a2
					&& t.getPredecessorNode(a1) != a2 && t.getPredecessorNode(b2) != b2){
				if((g.distance(a1, a2)+g.distance(b1, b2)-tmpE.length+E1.length) > 0){
					return flip(t,a1,b1,a2,b2, e1,i);
				}
			}
		}
		return null;
	}
	/*
	 * t, a1,b1,a2,b2
	 * Flips edge a1,b1 and a2,b2 into a1,a2 and b1,b2
	 */

	private Tour flip(Tour t, short a1, short b1, short a2, short b2, int e1,
			int e2) {
		t.setEdge(e1, G.getEdge(a1, a2), G);
		t.setEdge(e2, G.getEdge(b1, b2), G);
		return t;
	}

	private int fetchFirstEdge(Tour t){
		//TODO MAGIC!
		
		return 0;
	}
	
	private Edge findCandidate(){
		
		return null;
	}
}
