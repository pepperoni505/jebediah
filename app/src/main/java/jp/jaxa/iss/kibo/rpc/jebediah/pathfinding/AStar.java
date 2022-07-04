package jp.jaxa.iss.kibo.rpc.jebediah.pathfinding;

import gov.nasa.arc.astrobee.types.Point;

import java.util.ArrayList;
import java.util.Collections;

public class AStar {
    private final CellGrid grid;
    private final Cell goal;
    private ArrayList<Cell> open;

    public AStar(CellGrid grid, Cell goal) {
        this.grid = grid;
        this.goal = goal;
    }

    public static int heuristic(Cell start, Cell end) {
        return (int) (Math.sqrt(
                Math.pow((end.getCenter().getX() - start.getCenter().getX()), 2) +
                        Math.pow((end.getCenter().getY() - start.getCenter().getY()), 2) +
                        Math.pow((end.getCenter().getZ() - start.getCenter().getZ()), 2)
        ) * 10000); // Since our coordinate system is very small, we need to multiply by a large factor so that we don't lose precision when converting to an int
    }

    public ArrayList<Cell> backtrace(Cell node) {
        ArrayList<Cell> path = new ArrayList<>();
        path.add(node);
        while (node.getParent() != null) {
            node = node.getParent();
            path.add(node);
        }
        Collections.reverse(path);
        return path;
    }

    public ArrayList<Cell> smoothPath(ArrayList<Cell> path) {
        ArrayList<Cell> newPath = new ArrayList<>();
        newPath.add(path.get(0));

        Cell node = path.get(0);
        for (int i = 2; i < path.size(); ++i) {
            Cell otherNode = path.get(i);
            if (lineOfSight(node, otherNode)) {
                Cell lastValidNode = path.get(i - 1);
                newPath.add(lastValidNode);
                node = lastValidNode;
            }
        }
        newPath.add(path.get(path.size() - 1));
        return newPath;
    }

    public boolean lineOfSight(Cell start, Cell end) {
        ArrayList<Cell> cells = getCellsInBetween(start, end);

        for (Cell cell : cells) {
            if (cell.getClosed()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Since Java doesn't support passing an index that is out of range of the list, we have to check if we can insert the value at the specified index, and if not,
     * add to the last position in the open ArrayList
     * @param index {@code int} representing the index to insert at
     * @param value {@link Cell} to insert
     */
    private void safeAddToOpen(int index, Cell value) {
        if (index > open.size()) {
            index = open.size();
        }
        open.add(index, value);
    }

    /**
     * Find a path in between two cells that is safe for Astrobee to traverse. Uses the A* algorithm as detailed in the first part of http://idm-lab.org/bib/abstracts/papers/aaai10b.pdf
     * @param start {@link Cell} to start search from
     * @return {@link ArrayList<Cell>} of cells to traverse to
     */
    public ArrayList<Cell> pathfind(Cell start) {
        open = new ArrayList<>();
        start.setgScore(0);
        start.setfScore(0);
        open.add(start);
        start.setOpened(true);
        while (!open.isEmpty()) {
            Cell node = open.remove(open.size() - 1);
            node.setClosed(true);
            if (node == goal) {
                return smoothPath(backtrace(node));
            }

            ArrayList<Cell> neighbors = grid.getCellNeighbors(node);
            for (Cell neighbor : neighbors) {
                if (neighbor.getClosed()) {
                    continue;
                }

                int ng = neighbor.getgScore() + heuristic(node, neighbor);
                if (!neighbor.getOpened() || ng < neighbor.getgScore()) {
                    neighbor.setgScore(ng);
                    neighbor.setfScore(neighbor.getgScore() + heuristic(neighbor, goal));
                    neighbor.setParent(node);

                    if (neighbor.getOpened()) {
                        open.remove(neighbor);
                        safeAddToOpen(neighbor.getfScore(), neighbor);
                    } else {
                        safeAddToOpen(neighbor.getfScore(), neighbor);
                        neighbor.setOpened(true);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Uses Bresenham's algorithm adapted for 3D environments to search for all cells in a linear path between two specified cells
     * @param start {@link Cell} to start from
     * @param end {@link Cell} to end at
     * @return {@link ArrayList<Cell>} containing cells in between the specified range
     */
    public ArrayList<Cell> getCellsInBetween(Cell start, Cell end) {
        Point startCenter = start.getCenter();
        double x1 = startCenter.getX();
        double y1 = startCenter.getY();
        double z1 = startCenter.getZ();

        Point endCenter = end.getCenter();
        double x2 = endCenter.getX();
        double y2 = endCenter.getY();
        double z2 = endCenter.getZ();

        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(x1, y1, z1));

        double dx = Math.abs(x2 - x1);
        double dy = Math.abs(y2 - y1);
        double dz = Math.abs(z2 - z1);

        double xs, ys, zs;

        if (x2 > x1) {
            xs = 0.01;
        } else {
            xs = -0.01;
        }
        if (y2 > y1){
            ys = 0.01;
        } else {
            ys = -0.01;
        }
        if (z2 > z1){
            zs = 0.01;
        } else {
            zs = -0.01;
        }

        double p1, p2;

        // Driving axis is X
        if (dx >= dy && dx >= dz) {
            p1 = 2 * dy - dx;
            p2 = 2 * dz - dx;

            while (Math.abs(x2 - x1) >= Math.abs(xs) / 2) {
                x1 += xs;
                if (p1 >= 0){
                    y1 += ys;
                    p1 -= 2 * dx;
                }
                if (p2 >= 0) {
                    z1 += zs;
                    p2 -= 2 * dx;
                }
                p1 += 2 * dy;
                p2 += 2 * dz;
                points.add(new Point(x1, y1, z1));
            }
        // Driving axis is Y
        } else if(dy >= dx && dy >= dz) {
            p1 = 2 * dx - dy;
            p2 = 2 * dz - dy;

            while(Math.abs(y2-y1) >= Math.abs(y2) / 2) {
                y1 += ys;
                if(p1 >= 0) {
                    x1 += xs;
                    p1 -= 2 * dy;
                }
                if(p2 >= 0) {
                    z1 += zs;
                    p2 -= 2 * dy;
                }
                p1 += 2 * dx;
                p2 += 2 * dz;

                points.add(new Point(x1, y1, z1));
            }
        // Driving axis is Z
        } else {
            p1 = 2 * dy - dz;
            p2 = 2 * dx - dz;
            while (Math.abs(z2 - z1) >= Math.abs(zs) / 2) {
                z1 += zs;
                if (p1 >= 0) {
                    y1 += ys;
                    p1 -= 2 * dz;
                }
                if (p2 >= 0) {
                    x1 += xs;
                    p2 -= 2 * dz;
                }
                p1 += 2 * dy;
                p2 += 2 * dx;
                points.add(new Point(x1, y1, z1));
            }
        }

        ArrayList<Cell> cells = new ArrayList<>();
        for (Point point : points) {
            Cell cell = grid.getCellFromPoint(point);
            if (!cells.contains(cell)) {
                cells.add(cell);
            }
        }

        return cells;
    }
}
