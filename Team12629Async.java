/*
 * Robot design for 2023-2024 FTC - Asynchronous period
 * 
 * Hardware:
 * 4 motors, for wheels
 * 1 motor and 2 servos, for claws
 * 1 servo, for claws
 * 
 +-+-+ PLAN FOR AUTONOMOUS PERIOD +-+-+
1. Drive forward to arms distance from each spike mark
2. Rotate the camera 360 degrees to scan the apriltag on the backboard
3. Program decides whether to drive the long or short distance
4. Turn in the direction of the apriltag.
5. Place down the purple pixel.
6. Drive forward to backboard.
7. Place yellow pixel on backboard.
8. Stay parked in backboard area.
*/

// FIRST Robotics imports
package org.firstinspires.ftc.teamcode;

import java.util.List;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
// Motors and Servos
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
// Webcam and April Tags
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@TeleOp
public class Team13645Async extends LinearOpMode {
    // Wheel Init
    private DcMotor[] drivingMotors = new DcMotor[4];
    // Claw Init
    private DcMotor clawMotor;
    private CRServo clawRotServo;
    private CRServo clawCloseServo;
    // Webcam Servo Init
    private CRServo camServo;
    // April Tag Init
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;
    // Misc Init
    private ElapsedTime time = new ElapsedTime();
    private int aprilIterations = 0;

    // Function Definitions ----------------------------------------------
    private void clawPos(int position){
        if (position == 1) {
            // Claw rest
        } else if (position == 2) {
            // Claw pick up from ground?
        } else if (position == 3) {
            // Claw pick up from board
        }
    }

    private void goDistance(float power){
        drivingMotors[0].setDirection(DcMotor.Direction.REVERSE);
        drivingMotors[3].setDirection(DcMotor.Direction.REVERSE);

        drivingMotors[0].setPower(power);
        drivingMotors[2].setPower(power);
        drivingMotors[1].setPower(power);
        drivingMotors[3].setPower(power);
    }

    private void rotate(float power){
        drivingMotors[0].setDirection(DcMotor.Direction.FORWARD);
        drivingMotors[3].setDirection(DcMotor.Direction.FORWARD);

        drivingMotors[0].setPower(power);
        drivingMotors[2].setPower(power);
        drivingMotors[1].setPower(power);
        drivingMotors[3].setPower(power);
    }

    // Code Running ------------------------------------------------------
    @Override
    public void runOpMode(){
        // Mapping
        drivingMotors[0] = hardwareMap.get(DcMotor.class, "wheel_fl");
        drivingMotors[1] = hardwareMap.get(DcMotor.class, "wheel_fr");
        drivingMotors[2] = hardwareMap.get(DcMotor.class, "wheel_bl");
        drivingMotors[3] = hardwareMap.get(DcMotor.class, "wheel_br");

        clawMotor = hardwareMap.get(DcMotor.class, "claw_motor");
        clawRotServo = hardwareMap.get(CRServo.class, "claw_rot");
        clawCloseServo = hardwareMap.get(CRServo.class, "claw_close");

        camServo = hardwareMap.get(CRServo.class, "camera");
        aprilTag = AprilTagProcessor.easyCreateWithDefaults();
        visionPortal = VisionPortal.easyCreateWithDefaults(
            hardwareMap.get(WebcamName.class, "Webcam 1"), aprilTag);

        // Setting motor and servo settings ------------------------------
        // Universal wheel properties
        for (DcMotor i : drivingMotors){
            // Tells motors to run based on power level, not velocity
            i.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            // Attempts to turn motor not resisted
            i.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }

        // Set to starting positions -------------------------------------

        // Ready to start ------------------------------------------------
        telemetry.addData(">", "Ready to start");
        waitForStart();
        time.reset();

        while (opModeIsActive()){
            goDistance(1.0f);
            // Output any debug or other info to controller
            telemetry.addData("Time", time.toString());
            telemetry.update();
        }
    }
}
