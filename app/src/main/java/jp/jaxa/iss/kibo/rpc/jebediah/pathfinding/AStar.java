package jp.jaxa.iss.kibo.rpc.jebediah.pathfinding;

import gov.nasa.arc.astrobee.types.Point;

import java.util.ArrayList;

public class AStar {
    private final CellGrid grid;
    private final Cell goal;
    private ArrayList<Cell> open;
    private ArrayList<Cell> closed;

    public AStar(CellGrid grid, Cell goal) {
        this.grid = grid;
        this.goal = goal;
    }

    public static int hScore(Cell start, Cell end) {
        return (int) Math.sqrt(
                Math.pow((end.getCenter().getX() - start.getCenter().getX()), 2) +
                        Math.pow((end.getCenter().getY() - start.getCenter().getY()), 2) +
                        Math.pow((end.getCenter().getZ() - start.getCenter().getZ()), 2)
        ) * 10000; // Since our coordinate system is very small, we need to multiply by a large factor so that we don't lose precision when converting to an int
    }

    public static ArrayList<Cell> reconstructPath(Cell s) {
        ArrayList<Cell> totalPath = new ArrayList<>();
        totalPath.add(s);
        if (s.getParent() != s) {
            totalPath.addAll(reconstructPath(s.getParent()));
        }

        return totalPath;
    }

    public ArrayList<Cell> postSmoothPath(ArrayList<Cell> path) {
        int k = 0;
        ArrayList<Cell> t = new ArrayList<>();
        t.add(path.get(0));
        for (int i = 1; i < path.size() - 1; i++) {
            if (!lineOfSight(t.get(k), path.get(i + 1))) {
                k++;
                t.add(path.get(i));
            }
        }

        t.add(path.get(path.size() - 1)); // TODO: this might be wrong

        return t;
    }

    public boolean lineOfSight(Cell start, Cell end) {
        Point startCenter = start.getCenter();
        Point endCenter = end.getCenter();
        ArrayList<Point> points = AStar.Bresenham3D(startCenter.getX(), startCenter.getY(), startCenter.getZ(), endCenter.getX(), endCenter.getY(), endCenter.getZ());

        for (Point point : points) {
            if (grid.getCellFromPoint(point).getUnsafe()) {
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

    public ArrayList<Cell> aStar(Cell start) {
        open = new ArrayList<>();
        closed = new ArrayList<>();
        for (Cell cell : grid.getCells()) { // This could be a stream, but for some reason Gradle throws a fit when trying to use Java 1.8 features
            if (cell.getUnsafe()) {
                closed.add(cell);
            }
        }
        start.setgScore(0);
        start.setParent(start);
        safeAddToOpen(AStar.hScore(start, goal), start);
        while (!open.isEmpty()) {
            Cell s = open.remove(open.size() - 1);

            if (s == goal) {
                ArrayList<Cell> path = AStar.reconstructPath(s);
                return postSmoothPath(path);
            }

            closed.add(s);

            for (Cell neighbor : s.getNeighbors(grid.getCells())) {
                if (!closed.contains(neighbor)) {
                    if (!open.contains(neighbor)) {
                        neighbor.setgScore(Integer.MAX_VALUE); // Should be infinite, but max value *should* work
                        neighbor.setParent(null);
                    }

                    updateVertex(s, neighbor);
                }
            }
        }

        return null;
    }

    public void updateVertex(Cell s, Cell s2) {
        int oldgScore = s2.getgScore();
        computeCost(s, s2);
        if (s2.getgScore() < oldgScore) {
            open.remove(s2);
            safeAddToOpen(s2.getgScore() + AStar.hScore(s2, goal), s2);
        }
    }

    public void computeCost(Cell s, Cell s2) {
        if (s.getgScore() + AStar.hScore(s, s2) < s2.getgScore()) {
            s2.setParent(s);
            s2.setgScore(s.getgScore() + AStar.hScore(s, s2));
        }
    }

    public static ArrayList<Point> Bresenham3D(double x1, double y1, double z1, double x2, double y2, double z2) {
        ArrayList<Point> listOfPoints = new ArrayList<>();
        listOfPoints.add(new Point(x1, y1, z1));

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
                listOfPoints.add(new Point(x1, y1, z1));
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

                listOfPoints.add(new Point(x1, y1, z1));
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
                listOfPoints.add(new Point(x1, y1, z1));
            }
        }

        return listOfPoints;
    }
}
