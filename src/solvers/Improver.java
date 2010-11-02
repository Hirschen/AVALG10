package solvers;

import main.Graph;
import main.Tour;

public interface Improver {

	void improve(Graph g, Tour t);
}
