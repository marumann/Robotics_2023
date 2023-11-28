/*
 * The design for this robot had:
 * 4 Tetrix wheel motors
 * 2 Bilda servos, used as grabbers
 * 
 * This robot was controlled using the d-pad
 */

// FIRST Robotics imports
package org.firstinspires.ftc.teamcode;

import java.util.List;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
// Webcam stuff
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
// April Tag
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@TeleOp // This is an annotation type. Google it.
public class AprilTest extends LinearOpMode {

    // To track time
    private ElapsedTime time = new ElapsedTime();
    private int aprilIterations = 0;

    // Init April Tag processor and vision portal
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    @Override
    public void runOpMode(){

        // Initialize April Tag and vision portal
        aprilTag = AprilTagProcessor.easyCreateWithDefaults();
        // For webcam, use hardwareMap.get(WebcamName.class, "Webcam 1").
        // Currently using phone camera
        visionPortal = VisionPortal.easyCreateWithDefaults(
            hardwareMap.get(WebcamName.class, "Webcam 1"), aprilTag);

        // Tell display that things have been initiated
        telemetry.addData(">", "Ready to start");

        waitForStart();
        time.reset();

        while (opModeIsActive()){
            // Detects and sends April info every 10 seconds
            if ((int)time.seconds() > aprilIterations * 10) {
                runApril();
                aprilIterations += 1;
            }

            // Output any debug or other info to controller
            telemetry.addData("Time", time.toString());
            telemetry.update();
        }
    }

    private void runApril() {
        // Detect April Tags
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        telemetry.addData("# AprilTags Detected", currentDetections.size());

        // Step through the list of detections and display info for each one.
        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
                telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
            } else {
                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
                telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
            }
        }

        // Note: probably want to do some computations here

        // Add "key" information to telemetry
        telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
        telemetry.addLine("RBE = Range, Bearing & Elevation");
    }
}