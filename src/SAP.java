import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SAP {

    private final Digraph digraph;

    private static class Min {
        private final int[] res = {-1, -1};
        private final Object key1;
        private final Object key2;

        public Min(Object key1, Object key2) {
            this.key1 = key1;
            this.key2 = key2;
        }

        public boolean hit(Object k1, Object k2) {
            return k1 == key1 && k2 == key2;
        }

        public boolean putIfLess(int dist, int ancestor) {
            if (res[0] == -1 || res[0] > dist) {
                res[0] = dist;
                res[1] = ancestor;
                return true;
            }
            return false;
        }

        public int[] getResult() {
            return new int[]{ res[0], res[1] };
        }
    }

    private Min cache = new Min(null, null);

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph g) {
        if (g == null) throw new IllegalArgumentException();
        digraph = new Digraph(g);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (cache.hit(v, w)) return cache.getResult()[0];
        cache = getAncestorAndDistance(Collections.singletonList(v), Collections.singletonList(w));
        return cache.getResult()[0];
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (cache.hit(v, w)) return cache.getResult()[1];
        cache = getAncestorAndDistance(Collections.singletonList(v), Collections.singletonList(w));
        return cache.getResult()[1];
    }

    // return array of 2 elements. [0] - element id, [1] - distance length. If no path exists, return -1,-1.
    // Must not return null at any case
    private Min getAncestorAndDistance(Iterable<Integer> v, Iterable<Integer> w) {
        assertNotNull(v, w);

        Min min = new Min(v, w);
        if (!v.iterator().hasNext() || !w.iterator().hasNext()) return min;

        BreadthFirstDirectedPaths vPath = new BreadthFirstDirectedPaths(digraph, v);
        // if there is no root, try to find direct path
        for (Integer id : w)
            if (vPath.hasPathTo(id))
                min.putIfLess(vPath.distTo(id), id);

        BreadthFirstDirectedPaths wPath = new BreadthFirstDirectedPaths(digraph, w);
        for (Integer id : v)
            if (wPath.hasPathTo(id))
                min.putIfLess(wPath.distTo(id), id);

        Queue<Integer> queue = new Queue<>();
        for (Integer id : v) queue.enqueue(id);

        Set<Integer> visited = new HashSet<>();
        while (!queue.isEmpty()) {
            int id = queue.dequeue();
            visited.add(id);
            if (digraph.outdegree(id) > 0) {
                for (Integer adjId : digraph.adj(id)) {
                    if (visited.contains(adjId)) continue;
                    queue.enqueue(adjId);
                    if (wPath.hasPathTo(adjId)) {
                        min.putIfLess(wPath.distTo(adjId) + vPath.distTo(adjId), adjId);
                    }
                }
            }
        }

        return min;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        assertNotNull(v, w);
        if (cache.hit(v, w)) return cache.getResult()[0];
        cache = getAncestorAndDistance(v, w);
        return cache.getResult()[0];
    }

    private void assertNotNull(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException();

        Iterator<Integer> vIt = v.iterator();
        while (vIt.hasNext()) {
            Integer i = vIt.next();
            if (i == null) throw new IllegalArgumentException("Member of a iterable must not be null");
        }
        Iterator<Integer> wIt = w.iterator();
        while (wIt.hasNext()) {
            Integer i = wIt.next();
            if (i == null) throw new IllegalArgumentException("Member of a iterable must not be null");
        }
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        assertNotNull(v, w);
        if (cache.hit(v, w)) return cache.getResult()[1];
        cache = getAncestorAndDistance(v, w);
        return cache.getResult()[1];
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);

        sap.length(new ArrayList<>(), new ArrayList<>());
        sap.length(Collections.singletonList(2), new ArrayList<>());
        sap.length(new ArrayList<>(), Collections.singletonList(4));

        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}