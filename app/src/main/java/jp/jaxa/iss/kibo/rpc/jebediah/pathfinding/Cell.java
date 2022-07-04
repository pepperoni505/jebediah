package jp.jaxa.iss.kibo.rpc.jebediah.pathfinding;

import gov.nasa.arc.astrobee.types.Point;
import jp.jaxa.iss.kibo.rpc.jebediah.PointRange;

import java.util.ArrayList;

public class Cell {
    private final int depth;
    private final Point min;
    private final Point max;
    private boolean opened;
    private boolean closed;
    private int gScore;
    private int fScore;
    private Cell parent;
    private boolean unsafe; // Similar to closed, but only used to keep track of state internally

    public Cell() {
        this(0, new Point(), new Point());
    }

    public Cell(int depth, Point min, Point max) {
        this.depth = depth;
        this.min = min;
        this.max = max;
    }

    /**
     * @return {@link Point} Center point of the Cell.
     */
    public Point getCenter() {
        return new Point(
            (min.getX() + max.getX()) / 2,
            (min.getY() + max.getY()) / 2,
            (min.getZ() + max.getZ()) / 2
        );
    }

    public boolean getOpened() {
        return opened;
    }
    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public boolean getClosed() {
        return closed;
    }
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Point getMin() {
        return min;
    }
    public Point getMax() {
        return max;
    }

    public int getgScore() {
        return gScore;
    }
    public void setgScore(int gScore) {
        this.gScore = gScore;
    }

    public int getfScore() {
        return fScore;
    }
    public void setfScore(int fScore) {
        this.fScore = fScore;
    }

    public Cell getParent() {
        return parent;
    }
    public void setParent(Cell parent) {
        this.parent = parent;
    }

    public int getDepth() {
        return depth;
    }

    /**
     * Calculate the bounding box for a collection of point ranges and return a {@link Cell}
     *
     * @param pointRanges 1-dimensional array of {@code PointRange}
     * @return {@link Cell} containing the bounding box information
     */
    public static Cell generateBoundingBox(PointRange[] pointRanges) {
        double[] min = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE}; // Need to use max values for min and vice versa so we can properly compare values
        double[] max = {Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE};

        for (PointRange pointRange : pointRanges) {
            double[] pointRangeMin = pointRange.getMin().toArray();
            double[] pointRangeMax = pointRange.getMax().toArray();

            for (int i = 0; i < 3; i++) {
                if (pointRangeMin[i] < min[i]) {
                    min = pointRangeMin;
                }

                if (pointRangeMax[i] > max[i]) {
                    max = pointRangeMax;
                }
            }
        }

        return new Cell(0, new Point(min[0], min[1], min[2]), new Point(max[0], max[1], max[2]));
    }

    /**
     * Determine if the Cell intersects any of the specified geometries.
     *
     * @param geometries 1-dimensional array of {@code PointRange}
     * @return {boolean} representing whether the Cell comes into contact with the given geometries.
     */
    public boolean intersectsGeometries(PointRange[] geometries) {
        for (PointRange geometry : geometries) {
            Point geometryMin = geometry.getMin();
            Point geometryMax = geometry.getMax();

            if ((min.getX() <= geometryMax.getX() && max.getX() >= geometryMin.getX()) &&
                    (min.getY() <= geometryMax.getY() && max.getY() >= geometryMin.getY()) &&
                    (min.getZ() <= geometryMax.getZ() && max.getZ() >= geometryMin.getZ())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determine if the Cell is completely within the bounds of **ANY** specified geometries.
     *
     * @param geometries 1-dimensional array of {@code PointRange}
     * @return {boolean} representing whether the Cell is completely within the bounds of any of the specified geometries.
     */
    public boolean isWithinGeometry(PointRange[] geometries) {
        for (PointRange geometry : geometries) {
            Point geometryMin = geometry.getMin();
            Point geometryMax = geometry.getMax();
            if ((min.getX() >= geometryMin.getX() && max.getX() <= geometryMax.getX()) &&
                    (min.getY() >= geometryMin.getY() && max.getY() <= geometryMax.getY()) &&
                    (min.getZ() >= geometryMin.getZ() && max.getZ() <= geometryMax.getZ())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Divide the cell recursively until it does not intersect with closed geometry or until it has reached the maximum recursion depth
     *
     * @return {@link ArrayList<Cell>} of split cells
     */
    public ArrayList<Cell> subdivideCell(int maxCellDepth, PointRange[] keepInZones, PointRange[] keepOutZones) {
        if (isWithinGeometry(keepOutZones) || !((depth <= maxCellDepth) && (intersectsGeometries(keepInZones)) &&
                (intersectsGeometries(keepOutZones) || !isWithinGeometry(keepInZones)))) {
            if (intersectsGeometries(keepOutZones) || !isWithinGeometry(keepInZones)) {
                closed = true;
                unsafe = true;
            }
            return new ArrayList<>();
        }

        Point center = getCenter();

        ArrayList<Cell> newCells = new ArrayList<>();
        newCells.add(new Cell(depth + 1, center, max));
        newCells.add(new Cell(depth + 1,
                new Point(min.getX(), center.getY(), center.getZ()),
                new Point(center.getX(), max.getY(), max.getZ())
        ));
        newCells.add(new Cell(depth + 1,
                new Point(min.getX(), center.getY(), min.getZ()),
                new Point(center.getX(), max.getY(), center.getZ())
        ));
        newCells.add(new Cell(depth + 1,
                new Point(center.getX(), center.getY(), min.getZ()),
                new Point(max.getX(), max.getY(), center.getZ())));
        newCells.add(new Cell(depth + 1,
                new Point(center.getX(), min.getY(), center.getZ()),
                new Point(max.getX(), center.getY(), max.getZ())));
        newCells.add(new Cell(depth + 1,
                new Point(min.getX(), min.getY(), center.getZ()),
                new Point(center.getX(), center.getY(), max.getZ())));
        newCells.add(new Cell(depth + 1, min, center));
        newCells.add(new Cell(depth + 1,
                new Point(center.getX(), min.getY(), min.getZ()),
                new Point(max.getX(), center.getY(), center.getZ())));

        for (Cell newCell : new ArrayList<>(newCells)) { // TODO: might not need to create a new arraylist?
            ArrayList<Cell> subdividedCells = newCell.subdivideCell(maxCellDepth, keepInZones, keepOutZones);

            if (!subdividedCells.isEmpty()) {
                newCells.addAll(subdividedCells);
                newCells.remove(newCell);
            }
        }

        return newCells;
    }

    /**
     * Reset pathfinding values to prepare for new search
     */
    public void reset() {
        opened = false;
        closed = unsafe; // If cell is unsafe, closed needs to reflect that for future runs
        gScore = 0;
        fScore = 0;
        parent = null;
    }
}
