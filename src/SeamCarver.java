import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.Picture;

import java.util.List;

public class SeamCarver {

    private static final int CORNER_ENERGY = 1000;
    private final int[][] data;
    private final double[][] energy;
    private int cw, ch;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        assertNotNull(picture);
        cw = picture.width();
        ch = picture.height();
        data = new int[ch][cw];
        energy = new double[ch][cw];
        for (int col = 0; col < cw; col++)
            for (int row = 0; row < ch; row++)
                data[row][col] = picture.getRGB(col, row);
        for (int col = 0; col < cw; col++)
            for (int row = 0; row < ch; row++)
                energy[row][col] = calcEnergy(col, row);
    }

    // current picture
    public Picture picture() {
        Picture picture = new Picture(cw, ch);
        for (int col = 0; col < cw; col++) {
            for (int row = 0; row < ch; row++) {
                int v = data[row][col];
                picture.setRGB(col, row, v);
            }
        }
        return picture;
    }

    // width of current picture
    public int width() {
        return this.cw;
    }

    // height of current picture
    public int height() { return this.ch; }

    private int getRGB(int x, int y, int dx, int dy) {
        y += dy;
        x += dx;

        if (x < 0 || y < 0 || x >= cw || y >= ch)
            throw new IllegalArgumentException("Index out of bounds");

        return data[y][x];
    }

    private int r(int rgb) { return (rgb >> 16) & 0xff; }

    private int g(int rgb) { return (rgb >> 8) & 0xff; }

    private int b(int rgb) { return (rgb) & 0xff; }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || y < 0 || x >= cw || y >= ch)
            throw new IllegalArgumentException();
        return energy[y][x];
    }

    private double calcEnergy(int x, int y) {
        if (x == 0 || y == 0 || x == cw - 1 || y == ch - 1)
            return CORNER_ENERGY;

        int xp = getRGB(x, y, -1, 0);
        int xn = getRGB(x, y, 1, 0);
        int yp = getRGB(x, y, 0, -1);
        int yn = getRGB(x, y, 0, 1);

        double dx = Math.pow(r(xn) - r(xp), 2) + Math.pow(b(xn) - b(xp), 2) + Math.pow(g(xn) - g(xp), 2);
        double dy = Math.pow(r(yn) - r(yp), 2) + Math.pow(b(yn) - b(yp), 2) + Math.pow(g(yn) - g(yp), 2);

        return Math.sqrt(dx + dy);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (ch < 1) return new int[0];
        EdgeWeightedDigraph horizontalGraph = getLeftToRightDg();
        return findSeam(horizontalGraph, false);
    }

    private int[] findSeam(EdgeWeightedDigraph graph, boolean isVert) {
        int start = ch * cw;
        int end = ch * cw + 1;
        CarverShortPath sp = new CarverShortPath(graph, start);
        return edgesToArray(sp.pathTo(end), isVert);
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (cw < 1) return new int[0];
        EdgeWeightedDigraph verticalGraph = getTopToBottomDg();
        return findSeam(verticalGraph, true);
    }

    private EdgeWeightedDigraph getTopToBottomDg() {
        EdgeWeightedDigraph dg = new EdgeWeightedDigraph(cw * ch + 2);
        for (int row = 1; row < ch; row++) {
            for (int col = 0; col < cw; col++) {
                int v = ((row - 1) * cw) + col;

                dg.addEdge(new DirectedEdge(v, (row * cw) + col, energy[row][col]));
                if (col > 0)
                    dg.addEdge(new DirectedEdge(v, (row * cw) + col - 1, energy[row][col - 1]));
                if (col < cw - 1)
                    dg.addEdge(new DirectedEdge(v, (row * cw) + col + 1, energy[row][col + 1]));
            }
        }
        int v = ch * cw;
        for (int row = 0; row < cw; row++) {
            dg.addEdge(new DirectedEdge(v, row, 0.0));
        }
        v++;
        for (int abs = (ch - 1) * cw; abs < v - 1; abs++) {
            dg.addEdge(new DirectedEdge(abs, v, 0.0));
        }
        return dg;
    }

    private EdgeWeightedDigraph getLeftToRightDg() {
        EdgeWeightedDigraph dg = new EdgeWeightedDigraph(cw * ch + 2);
        for (int col = 1; col < cw; col++) {
            for (int row = 0; row < ch; row++) {
                int v = ((row) * cw) + (col - 1);

                dg.addEdge(new DirectedEdge(v, (row * cw) + col, energy[row][col]));
                if (row > 0)
                    dg.addEdge(new DirectedEdge(v, ((row - 1) * cw) + col, energy[row - 1][col]));
                if (row < ch - 1)
                    dg.addEdge(new DirectedEdge(v, ((row + 1) * cw) + col, energy[row + 1][col]));
            }
        }
        int v = ch * cw;
        for (int firstCol = 0; firstCol < v; firstCol += cw) {
            dg.addEdge(new DirectedEdge(v, firstCol, 0.0));
        }
        v++;
        for (int abs = cw - 1; abs < v - 1; abs += cw) {
            dg.addEdge(new DirectedEdge(abs, v, 0.0));
        }
        return dg;
    }

    private int[] edgesToArray(List<DirectedEdge> edges, boolean isVert) {
        int[] res = new int[edges.size() - 1];
        int resIndex = 0;
        int reverseIndex = edges.size() - 1;
        DirectedEdge e = edges.get(reverseIndex);
        do {
            int c;
            if (isVert) c = e.to() % cw;
            else c = e.to() / cw;

            res[resIndex++] = c;

            e = edges.get(--reverseIndex);
        } while (reverseIndex > 0);

        return res;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        assertNotNull(seam);
        assertValidSeam(seam, false);

        for (int col = 0; col < seam.length; col++) {
            for (int row = seam[col]; row < ch - 1; row++) {
                data[row][col] = data[row + 1][col];
                energy[row][col] = energy[row + 1][col];
            }
            data[ch - 1][col] = Integer.MAX_VALUE;
            energy[ch - 1][col] = Double.POSITIVE_INFINITY;
        }
        ch--;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        assertNotNull(seam);
        assertValidSeam(seam, true);

        for (int row = 0; row < seam.length; row++) {
            for (int col = seam[row]; col < cw - 1; col++) {
                data[row][col] = data[row][col + 1];
                energy[row][col] = energy[row][col + 1];
            }
            data[row][cw - 1] = Integer.MAX_VALUE;
            energy[row][cw - 1] = Double.POSITIVE_INFINITY;
        }
        cw--;
    }

    private void assertValidSeam(int[] seam, boolean isVert) {
        int[] len = {cw, ch};
        int axis = 0;
        if (isVert) axis = 1;

        if (seam.length != len[axis])
            throw new IllegalArgumentException("Invalid length of the seam");

        axis = 1;
        if (isVert) axis = 0;

        if (seam[0] < 0 || seam[0] >= len[axis]) throw new IllegalArgumentException();
        for (int i = 1; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= len[axis]) throw new IllegalArgumentException();
            if (Math.abs(seam[i] - seam[i - 1]) > 1) throw new IllegalArgumentException();
        }
    }

    private void assertNotNull(Object seam) {
        if (seam == null) throw new IllegalArgumentException("Parameter should be not null");
    }

    // unit testing (optional)
    public static void main(String[] args) {
        double[][] v = new double[1][8];
        for (int i = 0; i < v[0].length; i++) {
            v[0][i] = i + 5;
        }
        SeamCarver c = new SeamCarver(SCUtility.doubleToPicture(v));
        c.findHorizontalSeam();
        c.findVerticalSeam();

        Picture picture = new Picture(args[0]);
        SeamCarver carver = new SeamCarver(picture);
        System.out.println(carver.width());
        System.out.println(carver.height());
        System.out.println("------------");
        SCUtility.showEnergy(carver);
    }

}