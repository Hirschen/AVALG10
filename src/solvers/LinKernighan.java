package solvers;

import main.Graph;
import main.Tour;

public class LinKernighan implements Improver {
	
	public LinKernighan(){
		
	}
	
	
	public Tour improve(Graph g){
		int gain = 1;
		while(gain >= 0){ //gör medan man tjänar på något
			
			//Sök igenom kanter för att se om det finns någon kombo man tjänar på
			getSeqMoves();
			// or getNonSeqMoves();
			
			feasability();
			//Gör flipp:en
			
			executeMove();
		}
		return new Tour(); //TODO
	}
	
	/*
	 * 
	 */
	private void getSeqMoves(){
		
	}
	
	
	
	private void getNonSeqMoves(){
		
	}
	
	
	
	private void feasability(){
		
	}
	
	
	
	/*
	 * General k-opt
	 */
	private void executeMove(){
		
	}
	
	
	
	/*
	 * Flips (a,b,c,d) into (b,c,a,d)
	 */
	private void flip(){
		
	}
}
