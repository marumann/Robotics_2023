/*
 * Robot design for 2023-2024 FTC - Asyncronous period
 * 
 * Hardware:
 * 4 motors, for wheels
 * 1 motor and 2 servos, for claws
 */

// FIRST Robotics imports
package org.firstinspires.ftc.teamcode;

import java.util.List;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
// Motors and Servos
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
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
    private Servo clawRotServo;
    private Servo clawCloseServo;
    // April Tag Init
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;
    // Misc Init
    private ElapsedTime time = new ElapsedTime();
    private int aprilIterations = 0;

    // Function Definitions ----------------------------------------------
    private void ClawRest(){}
    private void ClawToGround(){}
    private void ClawToBoard(){}
    private void GoDistance(){}
    private void Rotate(){}

    public class Team13645 extends LinearOpMode {
	
    // Motor Vars    
    private DcMotor[] drivingMotors = new DcMotor[4];

    // To track time
    private ElapsedTime time = new ElapsedTime();

    // Decides which way the bot is moving
	float whichWay(boolean x, boolean y) {
		if ((x == true) && (y == false)) {
			return 0.75f;
		} else if ((x == false) && (y == true)) {
			return -0.75f;
		} else {
			return 0f;
		}
	}


    @Override
    public void runOpMode(){
        // Mapping
        drivingMotors[0] = hardwareMap.get(DcMotor.class, "motor_fl");
        drivingMotors[1] = hardwareMap.get(DcMotor.class, "motor_fr");
        drivingMotors[2] = hardwareMap.get(DcMotor.class, "motor_bl");
        drivingMotors[3] = hardwareMap.get(DcMotor.class, "motor_br");
        drivingMotors[4] = hardwareMap.get(DcMotor.class, "motor_arm");

        // Note to self: Add claw motors and servos

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
            // Output any debug or other info to controller
            telemetry.addData("Time", time.toString());
            telemetry.update();
        }
    }
}
