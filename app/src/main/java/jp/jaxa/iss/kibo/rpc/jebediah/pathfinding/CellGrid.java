package jp.jaxa.iss.kibo.rpc.jebediah.pathfinding;

import android.util.Log;
import gov.nasa.arc.astrobee.types.Point;
import jp.jaxa.iss.kibo.rpc.jebediah.PointRange;

import java.util.ArrayList;

public class CellGrid {
    private final ArrayList<Cell> cells;
    private final int maxCellDepth;

    public CellGrid(int maxCellDepth, PointRange[] keepInZones, PointRange[] keepOutZones) {
        Cell boundingBox = Cell.generateBoundingBox(keepInZones);
        cells = boundingBox.subdivideCell(maxCellDepth, keepInZones, keepOutZones);
        this.maxCellDepth = maxCellDepth;
    }

    public ArrayList<Cell> getCells() {
        return cells;
    }

    public Cell getCellFromPoint(Point point) {
        for (Cell cell : cells) {
            Point min = cell.getMin();
            Point max = cell.getMax();
            if ((min.getX() <= point.getX() && max.getX() >= point.getX()) &&
                    (min.getY() <= point.getY() && max.getY() >= point.getY()) &&
                    (min.getZ() <= point.getZ() && max.getZ() >= point.getZ())) {
                return cell;
            }
        }
        return null;
    }

    /**
     * Find the immediate neighbors of a cell
     * @param cell {@link Cell} to search neighbors for
     * @return {@link ArrayList<Cell>} of cells that are neighbors
     */
    public ArrayList<Cell> getCellNeighbors(Cell cell) {
        // The easiest way to quickly calculate our neighbors is to slightly scale up our current cell by half the size of the smallest possible cell, then check for any intersections with other cells.
        Point min = cell.getMin();
        Point max = cell.getMax();
        double[] scale = {max.getX() - min.getX(), max.getY() - min.getY(), max.getZ() - min.getZ()};
        for (int i = 0; i < (cell.getDepth() - maxCellDepth) + 1; i++) {
            for (int axis = 0; axis < scale.length; axis++) {
                scale[axis] /= 2;
            }
        }

        PointRange resizedCell = new PointRange(
                new Point(min.getX() - scale[0], min.getY() - scale[1], min.getZ() - scale[2]),
                new Point(max.getX() + scale[0], max.getY() + scale[1], max.getZ() + scale[2])
        );

        ArrayList<Cell> neighbors = new ArrayList<>();
        for (Cell gridCell : cells) {
            if (cell != gridCell && gridCell.intersectsGeometries(new PointRange[] {resizedCell})) {
                neighbors.add(gridCell);
            }
        }

        return neighbors;
    }

    /**
     * Reset all cells to their default pathfinding values
     */
    public void resetAll() {
        for (Cell cell : cells) {
            cell.reset();
        }
    }
}
