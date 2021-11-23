import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;

public class Solver {

    private boolean solvable = false;
    private boolean initialized = false;
    private Stack<Board> path = null;
    private final Board initial;

    private static class Node {
        private final Board board;
        private final Node prev;
        private final int cnt;
        private int m = -1;

        public Node(Board board, Node prev) {
            this.board = board;
            this.prev = prev;
            if (prev != null)
                this.cnt = prev.cnt + 1;
            else this.cnt = 0;
        }

        public int getM() {
            if (m < 0)
                m = board.manhattan();
            return m;
        }

    }

    private static class BoardCompareByManhattanHamming implements Comparator<Node> {
        @Override
        public int compare(Node b1, Node b2) {
            int r = Integer.compare(b1.getM() + b1.cnt, b2.getM() + b2.cnt);
            if (r == 0)
                r = Integer.compare(b1.board.hamming(), b2.board.hamming());
            return r;
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new IllegalArgumentException();
        this.initial = initial;
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        if (!initialized)
            getSolution(true);
        return solvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (!initialized)
            getSolution(true);
        if (!solvable)
            return -1;
        return path.size() - 1;
    }

    private static class IterateSolver {
        private final MinPQ<Node> pq;
        private Node found = null;

        public IterateSolver(Board initial, Comparator<Node> comparator) {
            this.pq = new MinPQ<Node>(initial.dimension() * 2, comparator);
            this.pq.insert(new Node(initial, null));
        }

        public boolean iterate() {
            Node candidate = pq.delMin();
            if (candidate.board.isGoal()) {
                found = candidate;
                return true;
            }
            Node criticalNode = candidate.prev;
            for (Board neighbor : candidate.board.neighbors()) {
                if (criticalNode != null && criticalNode.board.equals(neighbor))
                    continue;
                pq.insert(new Node(neighbor, candidate));
            }

            return false;
        }
    }

    private Iterable<Board> getSolution(boolean checkSolvable) {
        if (initialized) {
            if (solvable)
                return path;
            else
                return null;
        }
        initialized = true;
        boolean isMainGoal = false;
        boolean isSecGoal = false;
        IterateSolver mainSolver = new IterateSolver(this.initial, new BoardCompareByManhattanHamming());
        IterateSolver secondarySolver = null;
        if (checkSolvable)
            secondarySolver = new IterateSolver(this.initial.twin(), new BoardCompareByManhattanHamming());
        while (!isMainGoal && !isSecGoal) {
            isMainGoal = mainSolver.iterate();
            if (checkSolvable)
                isSecGoal = secondarySolver.iterate();
        }
        if (!isMainGoal) {
            solvable = false;
            return null;
        }
        solvable = true;
        Node prev = mainSolver.found;
        path = new Stack<>();
        while (prev != null)
        {
            path.push(prev.board);
            prev = prev.prev;
        }

        return path;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        return getSolution(true);
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
