package solvers;

import main.Graph;
import main.Tour;
import main.Graph.Edge;

public class TwoOpt implements Improver {
	private Graph G;
	
	public TwoOpt(){
		
	}

	@Override
	public Tour improve(Graph g, Tour t) {
		G=g;
		boolean foundGain;
		Edge n1 = fetchFirstEdge(t);
		
		for(Edge e : G.getEdges()[n1.nodeA]){
			for(Edge f : G.getEdges()[n1.nodeB]){
				
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
