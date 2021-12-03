import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

public class PointSET {

    private final SET<Point2D> pointSet;

    public PointSET() {
        pointSet = new SET<Point2D>();
    }

    public boolean isEmpty() {
        return pointSet.isEmpty();
    }

    public int size() {
        return pointSet.size();
    }

    public void insert(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();
        if (contains(p))
            return;
        pointSet.add(p);
    }

    public boolean contains(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();
        return pointSet.contains(p);
    }

    public void draw() {
        for (Point2D point2D : pointSet) {
            point2D.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException();

        Queue<Point2D> rangeOfPoints = new Queue<Point2D>();
        for (Point2D setPoint : pointSet) {
            if (rect.contains(setPoint)) {
                rangeOfPoints.enqueue(setPoint);
            }
        }
        return rangeOfPoints;
    }

    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();
        double min = Double.MAX_VALUE;
        Point2D nearest = null;
        for (Point2D setPoint : pointSet) {
            double dist = setPoint.distanceSquaredTo(p);
            if (min > dist) {
                min = dist;
                nearest = setPoint;
            }
        }
        return nearest;
    }

    public static void main(String[] args) {
        // no tests
    }
}
