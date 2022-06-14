package jp.jaxa.iss.kibo.rpc.jebediah.pathfinding;

import android.util.Log;
import gov.nasa.arc.astrobee.types.Point;
import jp.jaxa.iss.kibo.rpc.jebediah.PointRange;

import java.util.ArrayList;

public class CellGrid {
    private final ArrayList<Cell> cells;

    public CellGrid(int maxCellDepth, PointRange[] keepInZones, PointRange[] keepOutZones) {
        Cell boundingBox = Cell.generateBoundingBox(keepInZones);
        Log.i("KiboRpcApi","INFO: Starting cell split");
        cells = boundingBox.subdivideCell(maxCellDepth, keepInZones, keepOutZones);
        Log.i("KiboRpcApi","INFO: " + cells.size() + " cells were generated");
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
}
