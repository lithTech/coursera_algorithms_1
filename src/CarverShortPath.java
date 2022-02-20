import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.IndexMinPQ;

import java.util.ArrayList;
import java.util.List;

// written by lithTech
public class CarverShortPath {

    private final DirectedEdge[] edgeTo;
    private final double[] distTo;
    private final IndexMinPQ<Double> pq;

    public CarverShortPath(EdgeWeightedDigraph graph, int sv) {
        edgeTo = new DirectedEdge[graph.V()];
        distTo = new double[graph.V()];
        pq = new IndexMinPQ<>(graph.V());

        for (int i = 0; i < graph.V(); i++)
            distTo[i] = Double.POSITIVE_INFINITY;
        distTo[sv] = 0.0;

        pq.insert(sv, 0.0);
        while (!pq.isEmpty()) {
            int v = pq.delMin();
            for (DirectedEdge e : graph.adj(v)) {
                relax(e);
            }
        }
    }

    public double distTo(int v) {
        return distTo[v];
    }

    public List<DirectedEdge> pathTo(int v) {
        List<DirectedEdge> path = new ArrayList<>();
        for (DirectedEdge e = edgeTo[v]; e != null; e = edgeTo[e.from()]) {
            path.add(e);
        }
        return path;
    }

    private void relax(DirectedEdge edge) {
        int v = edge.from();
        int w = edge.to();
        if (distTo[w] > distTo[v] + edge.weight()) {
            distTo[w] = distTo[v] + edge.weight();
            edgeTo[w] = edge;
            if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
            else pq.insert(w, distTo[w]);
        }
    }

}
