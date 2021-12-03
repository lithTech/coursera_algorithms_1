import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class KdTree {

    private static final double BOUNDS_MIN_X  = 0;
    private static final double BOUNDS_MIN_Y  = 0;
    private static final double BOUNDS_MAX_X  = 1.0;
    private static final double BOUNDS_MAX_Y  = 1.0;

    private static class Node {
        Point2D point;
        Node left;
        Node right;
        RectHV rectLeft;
        RectHV rectRight;

        public Node(Point2D point, RectHV rectLeft, RectHV rectRight) {
            this.point = point;
            this.rectLeft = rectLeft;
            this.rectRight = rectRight;
        }
    }

    private static class MutableNearestArg {
        Point2D point;
        double distance;

        public MutableNearestArg(Point2D nearestSoFar, double distance) {
            this.point = nearestSoFar;
            this.distance = distance;
        }
    }

    private int size = 0;

    private Node root = null;

    public KdTree() {

    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private boolean isEven(int v) {
        return v % 2 == 0;
    }

    private boolean isToTheLeft(Point2D p, Node node, int level) {
        if (!isEven(level))
            return p.x() < node.point.x();
        else
            return p.y() < node.point.y();
    }

    private void insertNode(Point2D p, Node parent, int level) {
        if (parent.point.equals(p))
            return;
        boolean toLeft = isToTheLeft(p, parent, level);
        if (parent.left == null && toLeft)
            parent.left = getNode(p, level, parent.rectLeft);
        else if (parent.right == null && !toLeft)
            parent.right = getNode(p, level, parent.rectRight);
        else if (parent.left != null && toLeft)
            insertNode(p, parent.left, level + 1);
        else
            insertNode(p, parent.right, level + 1);
    }

    private Node getNode(Point2D p, int parentLevel, RectHV parentBoundary) {
        int level = parentLevel + 1;
        size++;
        double minX = parentBoundary.xmin();
        double minY = parentBoundary.ymin();
        double maxX = parentBoundary.xmax();
        double maxY = parentBoundary.ymax();

        boolean even = isEven(level);
        RectHV rectLeft;
        RectHV rectRight;
        if (!even) {
            rectLeft = new RectHV(minX, minY, p.x(), maxY);
            rectRight = new RectHV(p.x(), minY, maxX, maxY);
        }
        else {
            rectLeft = new RectHV(minX, minY, maxX, p.y());
            rectRight = new RectHV(minX, p.y(), maxX, maxY);
        }

        return new Node(p, rectLeft, rectRight);
    }

    public void insert(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();

        if (root == null)
            root = getNode(p, 0, new RectHV(BOUNDS_MIN_X, BOUNDS_MIN_Y, BOUNDS_MAX_X, BOUNDS_MAX_Y));
        else
            insertNode(p, root, 1);
    }

    private Node containsInNode(Point2D p, Node parentBranch, int level) {
        if (parentBranch == null) return null;
        if (parentBranch.point.equals(p)) return parentBranch;

        boolean isLeft = isToTheLeft(p, parentBranch, level);
        Node next;
        if (isLeft) next = parentBranch.left;
        else next = parentBranch.right;
        return containsInNode(p, next, level + 1);
    }

    public boolean contains(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();
        return containsInNode(p, root, 1) != null;
    }

    private void drawBranch(Node branch, int level) {
        if (branch == null) return;

        if (!isEven(level)) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(branch.rectLeft.xmax(), branch.rectLeft.ymin(),
                    branch.rectLeft.xmax(), branch.rectLeft.ymax());
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(branch.rectLeft.xmin(), branch.rectLeft.ymax(),
                    branch.rectLeft.xmax(), branch.rectLeft.ymax());
        }
        branch.point.draw();

        drawBranch(branch.right, level + 1);
        drawBranch(branch.left, level + 1);
    }

    public void draw() {
        StdDraw.setPenColor(StdDraw.BLACK);
        drawBranch(root, 1);
    }

    private void addIfInRect(Node branch, Queue<Point2D> listAddTo, RectHV rect) {
        if (branch == null)
            return;
        if (rect.contains(branch.point))
            listAddTo.enqueue(branch.point);
        if (branch.rectLeft.intersects(rect))
            addIfInRect(branch.left, listAddTo, rect);
        if (branch.rectRight.intersects(rect))
            addIfInRect(branch.right, listAddTo, rect);
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException();

        Queue<Point2D> list = new Queue<Point2D>();
        if (root != null)
            addIfInRect(root, list, rect);

        return list;
    }

    private void findNearest(Node branch, Point2D queryPoint, MutableNearestArg nArg) {
        if (branch == null)
            return;

        if (nArg.distance > branch.point.distanceSquaredTo(queryPoint))
        {
            nArg.point = branch.point;
            nArg.distance = branch.point.distanceSquaredTo(queryPoint);
        }

        double distanceToInLeft = branch.rectLeft.distanceSquaredTo(queryPoint);
        double distanceToInRight = branch.rectRight.distanceSquaredTo(queryPoint);
        if (distanceToInLeft < nArg.distance)
            findNearest(branch.left, queryPoint, nArg);
        if (distanceToInRight < nArg.distance)
            findNearest(branch.right, queryPoint, nArg);
    }

    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();
        if (root == null)
            return null;

        MutableNearestArg nArg = new MutableNearestArg(root.point, p.distanceSquaredTo(root.point));
        findNearest(root, p, nArg);
        return nArg.point;
    }

    public static void main(String[] args) {
        KdTree sample = new KdTree();
        sample.insert(new Point2D(0.5, 0.5));
        sample.insert(new Point2D(1.0, 0.0));
        sample.insert(new Point2D(0.5, 0.5));
        sample.insert(new Point2D(0.5, 0.0));
        sample.insert(new Point2D(1.0, 1.0));

        StdOut.println(sample.range(new RectHV(0.25, 0.25, 0.75, 0.75)));
        StdOut.println(sample.nearest(new Point2D(0.19, 0.29)));

        KdTree kdTree = new KdTree();
        Queue<Point2D> testPoints = new Queue<>();
        for (int i = 0; i < 10000; i++) {
            Point2D p = new Point2D(StdRandom.uniform(),
                    StdRandom.uniform());
            kdTree.insert(p);
            if (StdRandom.uniform() > 0.8)
                testPoints.enqueue(p);
        }

        while (!testPoints.isEmpty()) {
            Point2D testPoint = testPoints.dequeue();
            StdOut.println("Point "+testPoint+" should contain in struct: " + kdTree.contains(testPoint));
        }
    }
}