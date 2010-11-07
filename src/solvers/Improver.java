package solvers;

import main.Graph;
import main.Tourable;

public interface Improver {

	boolean improve(Graph g, Tourable t);
}
