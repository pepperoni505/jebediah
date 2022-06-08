package jp.jaxa.iss.kibo.rpc.jebediah.pathfinding;

import gov.nasa.arc.astrobee.types.Point;
import jp.jaxa.iss.kibo.rpc.jebediah.PointRange;

class Cell {
    int depth;
    Point min;
    Point max;

    public Cell() {
        this(0, new Point(), new Point());
    }

    public Cell(int depth, Point min, Point max) {
        this.depth = depth;
        this.min = min;
        this.max = max;
    }

    /**
     * Calculate the bounding box for a collection of point ranges
     * @param point_ranges 1-dimensional array of {@code PointRange}
     * @return {@link Cell} containing the bounding box information
     */
    public static Cell calculate_bounding_box(PointRange[] point_ranges) {
        double[] min = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE}; // Need to use max values for min and vice versa so we can properly compare values
        double[] max = {Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE};
        for (PointRange point_range : point_ranges) {
            double[] point_range_min = point_range.getMin().toArray();
            double[] point_range_max = point_range.getMax().toArray();
            for (int i = 0; i < 3; i++) {
                if (point_range_min[i] < min[i]) {
                    min = point_range_min;
                }
                if (point_range_max[i] > max[i]) {
                    max = point_range_max;
                }
            }
        }
        return new Cell(0, new Point(min[0], min[1], min[2]), new Point(max[0], max[1], max[2]));
    }
}
