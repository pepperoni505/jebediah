package jp.jaxa.iss.kibo.rpc.jebediah;

import gov.nasa.arc.astrobee.types.Point;

public class PointRange {
    protected final Point min;
    protected final Point max;

    public PointRange() {
        this(new Point(0, 0, 0), new Point(0, 0, 0));
    }

    public PointRange(final Point minPoint, final Point maxPoint) {
        min = minPoint;
        max = maxPoint;
    }

    public Point getMin() {
        return min;
    }

    public Point getMax() {
        return max;
    }
}
