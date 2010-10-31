package solvers;

import main.Graph;
import main.Tour;

public class LinKernighan implements Improver {
	
	private short[] incl;

	public LinKernighan(Tour t, Graph g) {
		
	}

	public Tour improve(Graph g, Tour t) {
		int G0 = g.distance(1, 2), Gain, tmp; //TODO FIRST EDGE!
		do{
			tmp = getSeqMoves(t.getNode(0), t.getNode(1), G0, Gain);
			//TODO Update tour!
		}while (Gain <= 0 && t2 != null);
	}

	/*
	 * 
	 */
	private int getSeqMoves(int t1, int t2, int G0, int Gain) {
		t[1] = t1; t[2] = t2;
		int BestG2K = Integer.MIN_VALUE;
		Best_t[2 * K] = NULL;
		Gain = getSeqMovesRec(2, G0);
		if (Gain <= 0 && Best_t[2 * K] != NULL) {
			for (i = 1; i <= 2 * K; i++){
				t[i] = Best_t[i];
			}
			executeMove(K);
		}
		return Best_t[2 * K];
	}

	private int getSeqMovesRec(int k, int G0){
		int G1,G2,G3,Gain;
		double n1,n2,n3,n4;
		int i;
		
		for (each candidate edge (t2, t3)) {
			if (t3 != PRED(t2) && t3 != SUC(t2) &&
			!Added(t2, t3, k - 2) && (G1 = G0 – C(t2, t3)) > 0){
				t[2 * k - 1] = t3;
				for (each t4 in {PRED(t3), SUC(t3)}) {
					if (!Deleted(t3, t4, k - 2)) {
						t[2 * k] = t4;
						G2 = G1 + C(t3, t4);
						if (feasability(k) && (Gain = G2 - C(t4, t1)) > 0) {
							executeMove(k);
							return Gain;
						}
						if (k < K && (Gain = getSeqMovesRec(k + 1, G2)) > 0){
							return Gain;
						}
						if (k == K && G2 > BestG2K && Excludable(t3, t4)) {
							BestG2K = G2;
							for (i = 1; i <= 2 * K; i++){
								Best_t[i] = t[i];
							}
						}
						/*if (t4 != t1 && Patching_C >= 2 &&
								(Gain = G2 - C(t4, t1)) > 0 && // rule 5
								(Gain = getNonSeqMoves(k, Gain)) > 0){
							return Gain;
						}*/
					}
				}
			}
		}
	}
	private boolean Added(Node *ta, Node *tb, int k) {
		int i = 2 * k;
		while ((i -= 2) > 0)
		if ((ta == t[i] && tb == t[i + 1]) ||
		(ta == t[i + 1] && tb == t[i]))
		return 1;
		return 0;
	}
	private boolean Deleted(Node * ta, Node * tb, int k) {
		int i = 2 * k + 2;
		while ((i -= 2) > 0)
		if ((ta == t[i - 1] && tb == t[i]) ||
		(ta == t[i] && tb == t[i - 1]))
		return 1;
		return 0;
	}
	
	
	private GainType getNonSeqMoves(int k, GainType Gain) {
		Node *s1, *s2, *sStart, *sStop;
		GainType NewGain;
		int M, i;
		
		FindPermutation(k);
		M = Cycles(k);
		if (M == 1 || M > Patching_C){
			return 0;
		}
		CurrentCycle = ShortestCycle(M, k);
		for (i = 0; i < k; i++) {
			if (cycle[p[2 * i]] == CurrentCycle) {
				sStart = t[p[2 * i]];
				sStop = t[p[2 * i + 1]];
				for (s1 = sStart; s1 != sStop; s1 = s2) {
					t[2 * k + 1] = s1;
					t[2 * k + 2] = s2 = SUC(s1);
					if ((NewGain = PatchCyclesRec(k, 2, M, Gain + C(s1, s2))) > 0)
						return NewGain;
				}
			}
		}
		return 0;
	}
	int ShortestCycle(int M, int k) {
		int i, MinCycle, MinSize = Integer.MAX_VALUE;
		for (i = 1; i <= M; i++){
			size[i] = 0;
		}
		p[0] = p[2 * k];
		for (i = 0; i < 2 * k; i += 2){
			size[cycle[p[i]]] += SegmentSize(t[p[i]], t[p[i + 1]]);
		}
		for (i = 1; i <= M; i++) {
			if (size[i] < MinSize) {
				MinSize = size[i];
				MinCycle = i;
			}
		}
		return MinCycle;
	}

	
	/*
	 * Find the feasability of the move
	 */
	private boolean feasability(int k) {
		int Count = 1, i = 2 * k;
		FindPermutation(k);
		while ((i = q[incl[p[i]]] ^ 1) != 0){
			Count++;
		}
		return (Count == k);
	}
	
	private void FindPermutation(long k) {
		int i,j;
		for (i = j = 1; j <= k; i += 2, j++){
			p[j] = (SUC(t[i]) == t[i + 1]) ? i : i + 1;
		}
		qsort(p + 2, k - 1, sizeof(int), Compare); //TODO sort p!
		for (j = 2 * k; j >= 2; j -= 2) {
			p[j - 1] = i = p[j / 2];
			p[j] = i & 1 ? i + 1 : i - 1;
		}
		for (i = 1; i <= 2 * k; i++)
			q[p[i]] = i;
	}
	int Compare(const void *pa, const void *pb) {
		return BETWEEN(t[p[1]], t[*(int *) pa], t[*(int *) pb]) ? -1 : 1;
	}

	/*
	 * Execute move!
	 */
	private void executeMove(long k) {
		int i, j, Best_i, Best_j, BestScore, s;
		FindPermutation(k);
		FindNextReversal:
		BestScore = -1;
		for (i = 1; i <= 2 * k - 2; i++) {
			j = q[incl[p[i]]];
			if (j >= i + 2 && (i & 1) == (j & 1) &&
				(s = (i & 1) == 1 ? Score(i + 1, j, k) :
				Score(i, j - 1, k)) > BestScore) {
				BestScore = s; 
				Best_i = i; 
				Best_j = j;
			}
		}
		if (BestScore >= 0) {
			i = Best_i; j = Best_j;
			if ((i & 1) == 1) {
				FLIP(t[p[i + 1]], t[p[i]], t[p[j]], t[p[j + 1]]);
				Reverse(i + 1, j);
			}
			else{
				FLIP(t[p[i - 1]], t[p[i]], t[p[j]], t[p[j - 1]]);
				Reverse(i, j - 1);
			}
			goto FindNextReversal;
		}
		for (i = 1; i <= 2 * k - 1; i += 2) {
			j = q[incl[p[i]]];
			if (j >= i + 2) {
				FLIP(t[p[i]], t[p[i + 1]], t[p[j]], t[p[j - 1]]);
				Reverse(i + 1, j - 1);
				goto FindNextReversal;
			}
		}
	}
	
	private void Reverse(int i, int j) {
		for (; i < j; i++, j--) {
			int pi = p[i];
			q[p[i] = p[j]] = i;
			q[p[j] = pi] = j;
		}
	}
	
	int Score(int left, int right, int k) {
		int count = 0, i, j;
		Reverse(left, right);
		for (i = 1; i <= 2 * k - 2; i++) {
			j = q[incl[p[i]]];
			if (j >= i + 2 && (i & 1) == (j & 1))
				count++;
		}
		Reverse(left, right);
		return count;
	}
	/*
	 * Flips (a,b,c,d) into (b,c,a,d)
	 */
	private void flip() {

	}
}
