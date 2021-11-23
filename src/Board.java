import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Arrays;

public class Board {
    private final char[] tiles;
    private final int size;
    private int zeroPos = -1;
    private int h = -1;
    private int m = -1;
    private Board twin = null;

    private Board(char[] tiles, int size, int zeroPos) {
        this.tiles = tiles;
        this.size = size;
        this.zeroPos = zeroPos;
    }

    public Board(int[][] tiles) {
        this.tiles = new char[tiles.length * tiles.length];
        this.size = tiles.length;
        int k = 0;
        for (int[] tile : tiles) {
            for (int i : tile) {
                if (i == 0)
                    zeroPos = k;
                this.tiles[k++] = (char) i;
            }
        }
    }

    // string representation of this board
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(size).append("\n");
        int nextRow = 0;
        for (int tile : tiles) {
            out.append(String.format("%2d ", tile));
            ++nextRow;
            if (nextRow == size) {
                out.append("\n");
                nextRow = 0;
            }
        }
        return out.toString();
    }

    // board dimension n
    public int dimension() {
        return size;
    }

    // number of tiles out of place
    public int hamming() {
        if (this.h != -1) return this.h;
        this.h = 0;
        for (int i = 0; i < this.tiles.length; i++) {
            if (this.tiles[i] == 0)
                continue;
            if (isWrong(i))
                this.h++;
        }
        return this.h;
    }

    private boolean isWrong(int i) {
        return this.tiles[i] != i + 1;
    }

    private int abs(int a) {
        if (a < 0) return -a;
        return a;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        if (this.m != -1) return this.m;
        this.m = 0;
        for (int i = 0; i < this.tiles.length; i++) {
            if (this.tiles[i] == 0) continue;
            if (isWrong(i)) {
                int id = this.tiles[i] - 1;
                int dist = abs(getRow(i) - getRow(id)) + abs(getCol(i) - getCol(id));
                this.m += dist;
            }
        }
        return this.m;
    }

    // is this board the goal board?
    public boolean isGoal() {
        if (tiles[tiles.length - 1] != 0)
            return false;
        return manhattan() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == null) return false;
        if (this == y) return true;
        if (this.getClass() == y.getClass()) {
            Board comp = (Board) y;
            return Arrays.equals(this.tiles, comp.tiles);
        }
        return false;
    }

    private int getCol(int id) {
        return 1 + (id - (size * (getRow(id) - 1)));
    }

    private int getRow(int id) {
        return (id / size) + 1;
    }

    private void exchange(char[] ar, int fr, int to) {
        char v = ar[fr];
        ar[fr] = ar[to];
        ar[to] = v;
    }

    private int getPos(int r, int c)
    {
        if (r < 1 || r > size) return -1;
        if (c < 1 || c > size) return -1;
        return (r - 1) * size + c - 1;
    }

    public Iterable<Board> neighbors() {
        Stack<Board> boards = new Stack<>();
        int c = getCol(zeroPos);
        int r = getRow(zeroPos);

        int p = getPos(r, --c);
        if (p >= 0) {
            char[] newTiles = Arrays.copyOf(tiles, tiles.length);
            exchange(newTiles, zeroPos, p);
            boards.push(new Board(newTiles, size, p));
        }
        c += 2;
        p = getPos(r, c);
        if (p >= 0) {
            char[] newTiles = Arrays.copyOf(tiles, tiles.length);
            exchange(newTiles, zeroPos, p);
            boards.push(new Board(newTiles, size, p));
        }
        p = getPos(--r, --c);
        if (p >= 0) {
            char[] newTiles = Arrays.copyOf(tiles, tiles.length);
            exchange(newTiles, zeroPos, p);
            boards.push(new Board(newTiles, size, p));
        }
        r += 2;
        p = getPos(r, c);
        if (p >= 0) {
            char[] newTiles = Arrays.copyOf(tiles, tiles.length);
            exchange(newTiles, zeroPos, p);
            boards.push(new Board(newTiles, size, p));
        }

        return boards;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        if (this.twin != null)
            return this.twin;

        char[] copy = Arrays.copyOf(this.tiles, this.tiles.length);
        int id1 = zeroPos, id2 = zeroPos;
        while (copy[id1] == 0)
            id1 = StdRandom.uniform(0, copy.length);
        while (copy[id2] == 0 || id1 == id2)
            id2 = StdRandom.uniform(0, copy.length);
        exchange(copy, id1, id2);

        this.twin = new Board(copy, size, zeroPos);

        return this.twin;
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        int[][] tiles = {{1, 2, 3}, {4, 0, 5}, {8, 7, 6}};
        Board board = new Board(tiles);
        StdOut.println("toString");
        StdOut.println(board);
        StdOut.println("getRow and getCol");
        char[] ints = board.tiles;
        for (int i = 0; i < ints.length; i++) {
            StdOut.println(board.getRow(i) + "x" + board.getCol(i));
        }
        StdOut.println("hamming "+board.hamming());
        StdOut.println("manhattan "+board.manhattan());
        StdOut.println("isGoal " + board.isGoal());

        StdOut.println("Neighbors:\n");
        for (Board neighbor : board.neighbors()) {
            StdOut.println(neighbor);
        }
        StdOut.println("Equals to me? "+board.equals(board));
        StdOut.println("Equals to same? "+board.equals(new Board(tiles)));
        int v = tiles[1][1];
        tiles[1][1] = tiles[2][2];
        tiles[2][2] = v;
        StdOut.println("Equals to different? " + board.equals(new Board(tiles)));

        StdOut.println("twin 1");
        StdOut.println(board.twin());
        StdOut.println("twin 2");
        StdOut.println(board.twin());
        StdOut.println("twin 3");
        StdOut.println(board.twin());

    }
}
