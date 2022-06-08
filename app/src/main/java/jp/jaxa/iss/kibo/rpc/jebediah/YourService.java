package jp.jaxa.iss.kibo.rpc.jebediah;

import gov.nasa.arc.astrobee.Kinematics;
import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;
import jp.jaxa.iss.kibo.rpc.jebediah.KiboConstants.*;

import org.opencv.core.Mat;

import java.util.ArrayList;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {

    private final int MOVE_TO_RETRIES = 5;

    @Override
    protected void runPlan1() {
        api.startMission();
        // Make sure we're at the start point
        safeMoveTo(KiboConstants.ASTROBEE_START_POS, KiboConstants.ASTROBEE_START_ROT, false);
        // Move to point 1
        safeMoveTo(KiboConstants.POINT_1_POS, KiboConstants.POINT_1_ROT, false);
        api.reportPoint1Arrival();

        // Shoot laser
        api.laserControl(true);
        api.takeTarget1Snapshot();
        api.laserControl(false);

        // Move to point 2
        safeMoveTo(new Point(11.30, -8.45, 4.58), KiboConstants.POINT_2_ROT, false);
        safeMoveTo(new Point(11.30,-9.60, 4.58), KiboConstants.POINT_2_ROT, false);
        safeMoveTo(KiboConstants.POINT_2_POS, KiboConstants.POINT_2_ROT, false);

        // Shoot laser
        api.laserControl(true);
        api.takeTarget2Snapshot();
        api.laserControl(false);

        // Move to goal point
        safeMoveTo(new Point(10.696, -9.409, 5.299), KiboConstants.GOAL_ROT, false);
        safeMoveTo(KiboConstants.GOAL_POS, KiboConstants.GOAL_ROT, false);

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

    public void safeMoveTo(Point point, Quaternion quaternion, boolean print_position) {
        Result result = api.moveTo(point, quaternion, print_position);
        if (!result.hasSucceeded()) { // TODO?: maybe try slightly changing goal point/rot to make movements work if needed
            for (int i = 0; i < MOVE_TO_RETRIES || result.hasSucceeded(); i++) {
                result = api.moveTo(point, quaternion, print_position);
            }
        }
    }
}

