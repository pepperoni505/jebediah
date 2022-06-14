package jp.jaxa.iss.kibo.rpc.jebediah;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.jebediah.PointRange;

public final class KiboConstants {

    // Class should not be instantiated
    private KiboConstants() {}

    // Points
    public static final Point ASTROBEE_START_POS = new Point(10.76150, -6.88490, 5.31647);
    public static final Quaternion ASTROBEE_START_ROT = new Quaternion(0, 0, -0.707F, 0.707F);

    public static final Point POINT_1_POS = new Point(10.71000, -7.70000, 4.48000);
    public static final Quaternion POINT_1_ROT = new Quaternion(0, 0.707F, 0, 0.707F);

    public static final Point POINT_2_POS = new Point(11.27460, -9.92284, 5.29881);
    public static final Quaternion POINT_2_ROT = new Quaternion(0, 0, -0.707F, 0.707F);

    public static final Point GOAL_POS = new Point(11.27460, -7.89178, 4.96538);
    public static final Quaternion GOAL_ROT = new Quaternion(0, 0, -0.707F, 0.707F);

    // Target info
    public static final int TARGET_1_LENGTH_CM = 27;
    public static final int TARGET_1_HEIGHT_CM = 15;
    public static final int TARGET_1_AR_TAG_SIZE_CM = 5;
    public static final int TARGET_1_RADIUS_CM = 5;
    public static final double[] TARGET_1_CENTER_TAG_TO_BULLSEYE_CM_XY = {10, 3.75}; // Distance from center of an AR tag to the center of the bullseye. NOTE: the X & Y flip signs depending on tag number. See 2.2.3 in the rulebook

    // For target 2, we aren't given explicit length and height, so let's not rely on that for now. Maybe we should email to confirm?
    public static final int TARGET_2_AR_TAG_SIZE_CM = 5;
    public static final int TARGET_2_RADIUS_CM = 5;
    public static final double[] TARGET_2_CENTER_TAG_TO_BULLSEYE_CM_XY = {11.25, 4.15};
    public static final double TARGET_2_BULLSEYE_ERROR_CM = 2.5; // The center of the target can move +- 2.5cm in X/Y from the distance specified above

    // Keep out/in zones
    public static final PointRange[] KEEP_OUT_ZONE = {
        new PointRange(new Point(9.8585, -9.4500, 4.82063), new Point(12.0085, -8.5000, 4.87063)),
        new PointRange(new Point(9.8673, -9.18813, 3.81957), new Point(10.7673, -8.28813, 4.81957)),
        new PointRange(new Point(11.1067, -9.44819, 4.87385), new Point(12.0067, -8.89819, 5.87385))
    };
    public static final PointRange[] KEEP_IN_ZONE = {
        new PointRange(new Point(10.3, -10.2, 4.32), new Point(11.55, -6.4, 5.57)),
        new PointRange(new Point(9.5, -10.5, 4.02), new Point(10.5, -9.6, 4.8))
    };

    // Astrobee info
    public static final Point NAV_CAM_DISTANCE_FROM_CENTER = new Point(0.1177, -0.0422, -0.0826);
    public static final Point HAZ_CAM_DISTANCE_FROM_CENTER = new Point(0.1328, 0.0362, -0.0826);
    public static final Point LASER_POINTER_DISTANCE_FROM_CENTER = new Point(0.1302, 0.0572, -0.1111);
    public static final Point DOCK_CAM_DISTANCE_FROM_CENTER = new Point(-0.1061, -0.054, -0.0064);
    public static final Point PERCH_CAM_DISTANCE_FROM_CENTER = new Point(-0.1331, 0.0509, -0.0166);

    public static final int ASTROBEE_MASS_KG = 10;
    public static final double ASTROBEE_MAX_VELOCITY_MS = 0.5;
    public static final double ASTROBEE_MAX_THRUST_N = 0.6; // Only for X axis per 3.6.2 in the guidebook
    public static final double ASTROBEE_MIN_MOVING_DISTANCE_M = 0.05;
    public static final double ASTROBEE_MIN_ROTATING_ANGLE_DEG = 7.5;

    // Custom constants
    public static final int MAX_CELL_DEPTH = 4;
    public static final int MOVE_TO_RETRIES = 5;
}
