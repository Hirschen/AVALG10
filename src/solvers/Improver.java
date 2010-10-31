package solvers;

import main.Graph;
import main.Tour;

public interface Improver {

	Tour improve(Graph g, Tour t);
}
