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
		boolean foundGain = false;
		Edge e1 = fetchFirstEdge(t);
		int n1 = e1.nodeA;
		int n2 = e1.nodeB;
		
		for(Edge e : t){
			if(t.getPredecessorNode(n1) != e.nodeB && t.getSuccessorNode(n2) != e.nodeA){
				
			}
		}
		
		
		
		return null;
	}
	private Edge fetchFirstEdge(Tour t){
		//TODO MAGIC!
		
		return null;
	}
	
	private Edge findCandidate(){
		
		return null;
	}
}
