import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class BruteCollinearPoints {

    private final Point[] points;
    private LineSegment[] segments = null;

    public BruteCollinearPoints(Point[] points) {
        if (points == null)
            throw new IllegalArgumentException("Points array must not be null");

        this.points = new Point[points.length];
        for (int i = 0; i < points.length; i++)
            if (points[i] == null)
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

    public int numberOfSegments() {
        return segments().length;
    }

    private Point min(Point p1, Point p2, Point p3, Point p4) {
        Point min = p1;
        if (min.compareTo(p2) > 0)
            min = p2;
        if (min.compareTo(p3) > 0)
            min = p3;
        if (min.compareTo(p4) > 0)
            min = p4;
        return min;
    }

    private Point max(Point p1, Point p2, Point p3, Point p4) {
        Point max = p1;
        if (max.compareTo(p2) < 0)
            max = p2;
        if (max.compareTo(p3) < 0)
            max = p3;
        if (max.compareTo(p4) < 0)
            max = p4;
        return max;
    }

    private LineSegment[] getSegmentsCopy() {
        LineSegment[] copy = new LineSegment[segments.length];
        for (int i = 0; i < segments.length; i++)
            copy[i] = segments[i];
        return copy;
    }

    public LineSegment[] segments() {
        if (segments != null)
            return getSegmentsCopy();

        Point[] sortedPoints = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            sortedPoints[i] = points[i];
        }
        Arrays.sort(sortedPoints);

        int segment = 0;
        LineSegment[] lineSegments = new LineSegment[points.length];
        for (int i = 0; i < sortedPoints.length; i++) {
            Point p1 = sortedPoints[i];
            for (int j = i + 1; j < sortedPoints.length; j++) {
                Point p2 = sortedPoints[j];
                for (int k = j + 1; k < sortedPoints.length; k++) {
                    Point p3 = sortedPoints[k];
                    for (int m = k + 1; m < sortedPoints.length; m++) {
                        Point p4 = sortedPoints[m];
                        if (p1 == p2 || p2 == p3 || p3 == p4 || p1 == p3 ||
                                p2 == p4 || p1 == p4)
                            continue;

                        double s1 = p1.slopeTo(p2);
                        double s2 = p2.slopeTo(p3);
                        double s3 = p3.slopeTo(p4);
                        if (s1 == s2 && s2 == s3)
                            lineSegments[segment++] = new LineSegment(min(p1, p2, p3, p4),
                                    max(p1, p2, p3, p4));
                    }
                }
            }
        }

        segments = new LineSegment[segment];
        for (int i = 0; i < segment; i++) {
            segments[i] = lineSegments[i];
        }

        return getSegmentsCopy();
    }

    public static void main(String[] args) {
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
        StdDraw.setXscale(-5000, 32768);
        StdDraw.setYscale(-5000, 32768);
        StdDraw.setPenRadius(0.004);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints brute = new BruteCollinearPoints(points);
        for (LineSegment segment : brute.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}