package solvers;

import main.Graph;
import main.Tour;

public class LinKernighan implements Improver {
	
	public LinKernighan(){
		
	}
	
	
	public Tour improve(Graph g){
		int gain = 1;
		while(gain >= 0){ //g�r medan man tj�nar p� n�got
			
			//S�k igenom kanter f�r att se om det finns n�gon kombo man tj�nar p�
			getSeqMoves();
			// or getNonSeqMoves();
			
			feasability();
			//G�r flipp:en
			
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
