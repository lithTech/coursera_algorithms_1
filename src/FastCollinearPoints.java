import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class FastCollinearPoints {

    private final Point[] points;
    private LineSegment[] segments = null;

    public FastCollinearPoints(Point[] points) {
        if (points == null)
            throw new IllegalArgumentException("Points array must not be null");

        this.points = new Point[points.length];
        for (Point value : points)
            if (value == null)
                throw new IllegalArgumentException("Point in points array must not be null");

        for (int i = 0; i < points.length; i++) {
            Point point = points[i];

            for (int j = i + 1; j < points.length; j++) {
                if (point == points[j] || point.compareTo(points[j]) == 0)
                    throw new IllegalArgumentException("Points in point array can't be the same");
            }
            this.points[i] = point;
        }
    }

    private LineSegment[] getSegmentsCopy() {
        LineSegment[] copy = new LineSegment[segments.length];
        for (int i = 0; i < segments.length; i++)
            copy[i] = segments[i];
        return copy;
    }

    public int numberOfSegments() {
        if (segments == null)
            return segments().length;
        return segments.length;
    }

    /**
     * Returns array with size 2 with minimum and maximum point from @param collinearPoints
     * @param collinearPoints - input array with all found similar slope points
     * @param size - size of an array @param collinearPoints
     * @return 2 size array. [0] - min point. [1] - max point
     */
    private Point[] getMinMaxPoints(Point[] collinearPoints, int size) {
        Arrays.sort(collinearPoints, 0, size);
        Point p;
        Point first = collinearPoints[0];
        Point last;
        int i = 0;
        do {
            last = collinearPoints[i];
            collinearPoints[i] = null;
            p = collinearPoints[++i];
        } while (p != null || i < size);
        return new Point[]{first, last};
    }

    /**
     * Detect duplicates in line segments. Replace points in line segments if necessary.
     * Detect based on slope comparison, when one of points (start or end) is the same
     * @param lineSegments - array with Start-End points
     * @param row - current start-end points to add
     * @param segmentsCount - current segments count in line segment array
     * @return new segmentsCount. If row was added, it will be @param segmentsCount + 1.
     * If row was not added, it will be the same as @param segmentsCount
     */
    private int addPoints(Point[][] lineSegments, Point[] row, int segmentsCount) {
        boolean updated = false;
        for (int i = 0; i < segmentsCount; i++) {
            Point[] point = lineSegments[i];
            double s1 = point[0].slopeTo(point[1]);
            double s2 = row[0].slopeTo(row[1]);

            if ((s1 == s2) && point[0].compareTo(row[0]) == 0) {
                int c = point[1].compareTo(row[1]);
                updated = true;
                if (c < 0) {
                    lineSegments[i][1] = row[1];
                    break;
                }
            }
            if ((s1 == s2) && point[1].compareTo(row[1]) == 0) {
                int c = point[0].compareTo(row[0]);
                updated = true;
                if (c < 0) {
                    lineSegments[i][0] = row[0];
                    break;
                }
            }
        }
        if (!updated)
            lineSegments[segmentsCount++] = row;
        return segmentsCount;
    }

    public LineSegment[] segments() {
        if (segments != null)
            return getSegmentsCopy();

        Point[] slopes = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            Point point = points[i];
            slopes[i] = point;
        }

        //lineSegments is an array with Point[2] subArray (min and max point)
        Point[][] lineSegments = new Point[points.length * 3][];
        int segmentCnt = 0;
        Point[] collinearPoints = new Point[points.length + 1];
        for (int i = 0; i < slopes.length; i++) {
            Point p = slopes[i];
            collinearPoints[0] = p;
            if (i + 1 < slopes.length)
                Arrays.sort(slopes, i + 1, slopes.length, p.slopeOrder());

            double lastSlope = Double.NEGATIVE_INFINITY;
            int slopeCnt = 0;
            for (int j = i + 1; j < slopes.length; j++) {
                Point p1 = slopes[j];
                double slope = p.slopeTo(p1);
                if (slope == lastSlope)
                    collinearPoints[1 + slopeCnt++] = slopes[j - 1];
                else
                    slopeCnt = 0;

                if (slopeCnt >= 2) {
                    if (j < slopes.length - 1 && slope == p1.slopeTo(slopes[j + 1]))
                        continue;
                    collinearPoints[1 + slopeCnt] = p1;
                    segmentCnt = addPoints(lineSegments, getMinMaxPoints(collinearPoints, 2 + slopeCnt),
                            segmentCnt);
                    collinearPoints[0] = p;
                    slopeCnt = 0;
                }

                lastSlope = slope;
            }
        }

        segments = new LineSegment[segmentCnt];
        for (int i = 0; i < segmentCnt; i++)
            segments[i] = new LineSegment(lineSegments[i][0], lineSegments[i][1]);

        return getSegmentsCopy();
    }

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}