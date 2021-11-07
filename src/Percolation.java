import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    final private WeightedQuickUnionUF uf;
    private int openedSites = 0;
    final private int n;
    final private int ufn;
    final private boolean[][] sites;

    private void assertBounds(int n) {
        if (n <= 0) throw new IllegalArgumentException();
    }

    private void assertBounds(int row, int col) {
        if (row < 1 || row > n) throw new IllegalArgumentException();
        if (col < 1 || col > n) throw new IllegalArgumentException();
    }

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        assertBounds(n);
        this.n = n;
        this.ufn = n * n + 2;   //reserve 1 top and 1 bottom

        this.sites = new boolean[n][n];
        this.uf = new WeightedQuickUnionUF(ufn);
        for (int i = 0; i < this.sites.length; i++) {
            boolean[] row = this.sites[i];
            for (int j = 0; j < row.length; j++) {
                row[j] = false;
            }
        }
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        assertBounds(row, col);
        if (isOpen(row, col)) return;

        int r = row - 1;
        int c = col - 1;
        if (getOpenResult(r - 1, c) == 1) connectSites(r, c, r - 1, c);
        if (getOpenResult(r + 1, c) == 1) connectSites(r, c, r + 1, c);
        if (getOpenResult(r, c - 1) == 1) connectSites(r, c, r, c - 1);
        if (getOpenResult(r, c + 1) == 1) connectSites(r, c, r, c + 1);

        if (r == 0) uf.union(ufn - 2, flatId(r, c));
        if (r == this.n - 1) uf.union(ufn - 1, flatId(r, c));

        sites[r][c] = true;
        openedSites++;
    }

    private void connectSites(int r1, int c1, int r2, int c2) {
        int id1 = flatId(r1, c1);
        int id2 = flatId(r2, c2);

        uf.union(id1, id2);
    }

    /**
     * Safe call isOpen
     * @param row - row - 1
     * @param col - col - 1
     * @return -1 - out of bounds, 0 - false, 1 - true
     */
    private int getOpenResult(int row, int col) {
        if (row < 0 || row >= n) return -1;
        if (col < 0 || col >= n) return -1;
        return sites[row][col] ? 1 : 0;
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        assertBounds(row, col);
        return getOpenResult(row - 1, col - 1) == 1;
    }

    private int flatId(int row, int col) {
        return this.n * (row) + col;
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        assertBounds(row, col);
        return connected(ufn - 2, flatId(row - 1, col - 1));
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openedSites;
    }

    private boolean connected(int p, int q) {
        return uf.find(p) == uf.find(q);
    }

    // does the system percolate?
    public boolean percolates() {
        return connected(ufn - 2, ufn - 1);
    }

    // test client (optional)
    public static void main(String[] args) {
        int n = 8;
        Percolation perc = new Percolation(n);

        while (!perc.percolates()) {
            int r = StdRandom.uniform(1, n + 1);
            int c = StdRandom.uniform(1, n + 1);
            if (c == 8 || r == 8)
                System.out.println(r + " : " + c);
            perc.open(r, c);
        }

        System.out.println(perc.percolates());
        System.out.println(perc.numberOfOpenSites());
        System.out.println(perc.isFull(1,1));
        System.out.println(perc.isOpen(1, 1));
        for (boolean[] site : perc.sites) {
            for (boolean b : site) {
                System.out.print(b ? '■' : '□');
            }
            System.out.println();
        }
    }
}