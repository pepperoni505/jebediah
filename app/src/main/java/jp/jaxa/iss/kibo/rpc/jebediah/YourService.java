package jp.jaxa.iss.kibo.rpc.jebediah;

import android.util.Log;
import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import jp.jaxa.iss.kibo.rpc.jebediah.pathfinding.AStar;
import jp.jaxa.iss.kibo.rpc.jebediah.pathfinding.Cell;
import jp.jaxa.iss.kibo.rpc.jebediah.pathfinding.CellGrid;

import java.util.ArrayList;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {

    CellGrid cells = new CellGrid(KiboConstants.MAX_CELL_DEPTH, KiboConstants.KEEP_IN_ZONE, KiboConstants.KEEP_OUT_ZONE);

    @Override
    protected void runPlan1() {
        api.startMission();
        // Make sure we're at the start point
        safe_move_to(KiboConstants.ASTROBEE_START_POS, KiboConstants.ASTROBEE_START_ROT, false);
        // Move to point 1
        safe_move_to(KiboConstants.POINT_1_POS, KiboConstants.POINT_1_ROT, false);
        api.reportPoint1Arrival();

        // Shoot laser
        api.laserControl(true);
        api.takeTarget1Snapshot();
        api.laserControl(false);

        // Move to point 2
        safe_move_to(KiboConstants.POINT_2_POS, KiboConstants.POINT_2_ROT, false);

        // Shoot laser
        api.laserControl(true);
        api.takeTarget2Snapshot();
        api.laserControl(false);

        // Move to goal point
        safe_move_to(KiboConstants.GOAL_POS, KiboConstants.GOAL_ROT, false);

        api.reportMissionCompletion();
    }

    @Override
    protected void runPlan2() {
        // write here your plan 2
    }

    @Override
    protected void runPlan3() {
        // write here your plan 3
    }

    /**
     * Move Astrobee to a point using A* pathfinding, and deal with having to retry if the movement fails.
     * @param point {@link Point} to move to
     * @param quaternion {@link Quaternion} to rotate to
     * @param print_position {@code boolean} representing if Astrobee's position should be logged to the console
     */
    public void safe_move_to(Point point, Quaternion quaternion, boolean print_position) {
        AStar aStar = new AStar(cells);
        Point start = api.getRobotKinematics().getPosition();
        Log.i("KiboRpcApi","INFO: Starting A* search");
        ArrayList<Cell> segments = aStar.aStar(cells.getCellFromPoint(start), cells.getCellFromPoint(point));
        Log.i("KiboRpcApi","INFO: A* search finished! Route is " + segments.size() + " cells long");
        for (Cell cell : segments) {
            Point moveToPoint = cell.getCenter();
            Result result = api.moveTo(moveToPoint, quaternion, print_position);
            if (!result.hasSucceeded()) {
                for (int i = 0; i < KiboConstants.MOVE_TO_RETRIES || result.hasSucceeded(); i++) {
                    result = api.moveTo(moveToPoint, quaternion, print_position);
                }
            }
        }
    }
}

