package jp.jaxa.iss.kibo.rpc.jebediah;

import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.ros.internal.util.Constants;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import gov.nasa.arc.astrobee.types.Vec3d;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfInt;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {

    private final int MOVE_TO_RETRIES = 5;
    private static long idCounter = 0;

    @Override
    protected void runPlan1() {
        api.startMission();
        // Make sure we're at the start point
        safeMoveTo(KiboConstants.ASTROBEE_START_POS, KiboConstants.ASTROBEE_START_ROT, false);
        // Move to point 1
        safeMoveTo(KiboConstants.POINT_1_POS, KiboConstants.POINT_1_ROT, false);
        api.reportPoint1Arrival();

        // Wait for camera refresh rate to catch up with final position in front of the target
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Line up bee with target 1
        double[] offset = getTargetOffset(KiboConstants.TARGET_1_IDS);
        safeRelativeMoveTo(new Point(-offset[1] * 0.9, -offset[0] * 0.9, 0), KiboConstants.POINT_1_ROT, false);

        // Shoot laser
        api.laserControl(true);
        api.takeTarget1Snapshot();
        api.laserControl(false);

        // Move to point 2
        safeMoveTo(new Point(11.30, -8.45, 4.58), KiboConstants.POINT_2_ROT, false);
        safeMoveTo(new Point(11.30,-9.60, 4.58), KiboConstants.POINT_2_ROT, false);
        safeMoveTo(KiboConstants.POINT_2_POS, KiboConstants.POINT_2_ROT, false);

        // Wait for camera refresh rate to catch up with final position in front of the target
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Line up bee with target 2
        offset = getTargetOffset(KiboConstants.TARGET_2_IDS);
        safeRelativeMoveTo(new Point(-offset[0] * 0.9, 0, -offset[0] * 0.9), KiboConstants.POINT_2_ROT, false);

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

    /**
     * Move Astrobee to a point, and deal with having to retry if the movement fails.
     * @param point {@link Point} to move to
     * @param quaternion {@link Quaternion} to rotate to
     * @param print_position {@code boolean} representing if Astrobee's position should be logged to the console
     */
    public void safeMoveTo(Point point, Quaternion quaternion, boolean print_position) {
        Result result = api.moveTo(point, quaternion, print_position);
        if (!result.hasSucceeded()) { // TODO?: maybe try slightly changing goal point/rot to make movements work if needed
            for (int i = 0; i < MOVE_TO_RETRIES || result.hasSucceeded(); i++) {
                result = api.moveTo(point, quaternion, print_position);
            }
        }
    }
    public void safeRelativeMoveTo(Point point, Quaternion quaternion, boolean print_position) {
        Result result = api.relativeMoveTo(point, quaternion, print_position);
        if (!result.hasSucceeded()) { // TODO?: maybe try slightly changing goal point/rot to make movements work if needed
            for (int i = 0; i < MOVE_TO_RETRIES || result.hasSucceeded(); i++) {
                result = api.relativeMoveTo(point, quaternion, print_position);
            }
        }
    }

    public double[] getTargetOffset(List<Integer> search_ids) {
        // Get image from camera and undistort
        Mat raw = api.getMatNavCam();
        Mat img = new Mat();
        double[][] camIntrinsics = api.getNavCamIntrinsics();
        Imgproc.undistort(raw, img, arrayToMat(3, 3, camIntrinsics[0]), new MatOfDouble(camIntrinsics[1]));

        // Copy image into RGB colorspace for colored annotations
        Mat outputImg = new Mat();
        Imgproc.cvtColor(img, outputImg, Imgproc.COLOR_GRAY2RGB);

        // Find ArUco markers in image
        List<Mat> corners = new ArrayList<>();
        Mat ids = new MatOfInt();
        Aruco.detectMarkers(img, Dictionary.get(Aruco.DICT_5X5_250), corners, ids);

        List<Double> xCoords = new ArrayList<>();
        List<Double> yCoords = new ArrayList<>();
        org.opencv.core.Point topRightPos = null, topLeftPos = null, bottomLeftPos = null, bottomRightPos = null;

        // Extract all x and y coordinates of AR tag corners
        for (int i = 0; i < corners.size(); i++) {
            if (search_ids.contains((int) ids.get(i, 0)[0])) {
                Mat corner = corners.get(i);
                List<Double> tmpXCoords = new ArrayList<>();
                List<Double> tmpYCoords = new ArrayList<>();
                for (int c = 0; c < corner.cols(); c++) {
                    tmpXCoords.add(corner.get(0, c)[0]);
                    tmpYCoords.add(corner.get(0, c)[1]);
                }
                org.opencv.core.Point avgPos = new org.opencv.core.Point(avg(tmpXCoords), avg(tmpYCoords));

                switch (((int) ids.get(i, 0)[0]) % 10) {
                    case 1: topRightPos = avgPos; break;
                    case 2: topLeftPos = avgPos; break;
                    case 3: bottomLeftPos = avgPos; break;
                    case 4: bottomRightPos = avgPos; break;
                }
                xCoords.addAll(tmpXCoords);
                yCoords.addAll(tmpYCoords);
            }
        }

        if (xCoords.size() == 0 || yCoords.size() == 0) {
            System.err.println("Specified target not in frame");
            return null;
        }

        if (topLeftPos == null || topRightPos == null || bottomRightPos == null || bottomLeftPos == null) {
            System.err.println("ArUco tags couldn't be detected");
            return null;
        }

        // Find conversion factor between physical and optical distances
        double horizPxPerMeter = 2 * (distance(topLeftPos, topRightPos) + distance(bottomLeftPos, bottomRightPos)) / 0.2;
        System.out.println("Horiz: " + horizPxPerMeter);
        double vertPxPerMeter = 2 * (distance(topLeftPos, bottomLeftPos) + distance(topRightPos, bottomRightPos)) / 0.075;
        System.out.println("Vert: " + vertPxPerMeter);
        double pxPerMeter = (horizPxPerMeter + vertPxPerMeter) / 2;
        System.out.println("Px/Meter: " + pxPerMeter);

        // Figure out the approximate optical radius of the target circle
        double avgX = avg(xCoords);
        double avgY = avg(yCoords);
        int scaledRadius = (int) Math.max(
                max(diff(xCoords, avgX)),
                max(diff(yCoords, avgY))
        ) / 2;

        Mat circles = new MatOfInt();
        Imgproc.HoughCircles(img, circles, Imgproc.HOUGH_GRADIENT, 1, 20, 100, 100, scaledRadius / 2, scaledRadius);

        for (int c = 0; c < circles.cols(); c++) {
            double[] circle = circles.get(0, c);
            Imgproc.circle(outputImg, new org.opencv.core.Point(circle[0], circle[1]), (int) circle[2], new Scalar(0, 255, 0), 2);
        }

        // TODO: Just in case, get closest circle to avgX, avgY
        double[] circlePos = circles.get(0, 0);
        org.opencv.core.Point targetPoint = new org.opencv.core.Point(640 + pxPerMeter * 0.0994, 480 - pxPerMeter * 0.0285);
        org.opencv.core.Point circlePoint = new org.opencv.core.Point(circlePos[0], circlePos[1]);
        Imgproc.circle(outputImg, targetPoint, 6, new Scalar(0, 0, 255), 2);
        Imgproc.line(outputImg, targetPoint, circlePoint, new Scalar(255, 0, 255), 2);

        api.saveMatImage(outputImg, "target" + idCounter + ".png");
        idCounter++;

        return new double[]{ (targetPoint.x - circlePoint.x) / pxPerMeter, (targetPoint.y - circlePoint.y) / pxPerMeter };
    }

    public double avg(List<Double> nums) {
        double sum = 0;
        for (double num: nums) {
            sum += num;
        }
        return sum / nums.size();
    }

    public double max(List<Double> nums) {
        double max = Integer.MIN_VALUE;
        for (double num: nums) {
            if (num > max) {
                max = num;
            }
        }
        return max;
    }

    public List<Double> diff(List<Double> nums, double center) {
        List<Double> out = new ArrayList<>();
        for (double num: nums) {
            out.add(Math.abs(center - num));
        }
        return out;
    }

    public double distance(org.opencv.core.Point p1, org.opencv.core.Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    public Mat arrayToMat(int rows, int cols, double[] arr) {
        assert arr.length >= rows * cols;
        Mat mat = new Mat(rows, cols, CvType.CV_64F);
        for (int i = 0; i < rows * cols; i++) {
            mat.put(i / rows, i % cols, arr[i]);
        }
        return mat;
    }
}

