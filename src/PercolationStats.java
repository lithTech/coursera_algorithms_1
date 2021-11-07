import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;

public class PercolationStats {

    private static final double MAGIC = 1.96;
    private final int n;
    private final int trials;
    private final double[] stats;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        this.n = n;
        this.trials = trials;

        if (n <= 0 || trials <= 0) throw new IllegalArgumentException();
        stats = new double[trials];

        compute();
    }

    private void compute() {
        int total = n * n;
        for (int i = 0; i < trials; i++) {
            Stopwatch stopwatch = new Stopwatch();
            int cnt = 0;
            Percolation perc = new Percolation(n);
            while (!perc.percolates()) {
                int r = StdRandom.uniform(1, n + 1);
                int c = StdRandom.uniform(1, n + 1);
                if (!perc.isOpen(r, c)) {
                    cnt++;
                    perc.open(r, c);
                }
            }
            stats[i] = (double) cnt / (double) total;
            System.out.println("Time spend: " + stopwatch.elapsedTime());
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(stats);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(stats);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - MAGIC * stddev() / Math.sqrt(trials);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + MAGIC * stddev() / Math.sqrt(trials);
    }

    // test client (see below)
    public static void main(String[] args) {
        if (args.length < 2) throw new IllegalArgumentException();
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);

        PercolationStats stats = new PercolationStats(n, t);
        System.out.printf("mean = %f%n", stats.mean());
        System.out.printf("stddev = %f%n", stats.stddev());
        System.out.printf("95%% confidence interval = [%f, %f]%n", stats.confidenceLo(),
                 stats.confidenceHi());
    }

}