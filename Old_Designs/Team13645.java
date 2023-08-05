/*
 * The design for this robot had:
 * 4 Tetrix wheel motors
 * 2 Bilda servos, used as grabbers
 * 
 * This robot was controlled using the d-pad
 */

// FIRST Robotics imports
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp // This is an annotation type. Google it.
public class Team13645 extends LinearOpMode {
	
    // Motor Vars    
    private DcMotor[] drivingMotors = new DcMotor[4];
    private Servo[] armServos = new Servo[2];

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
        
        /* initializes motors according to names assigned on phone
        motor 0: front left
        motor 1: front right
        motor 2: back left
        motor 3: back right
        */
        drivingMotors[0] = hardwareMap.get(DcMotor.class, "motor_fl");
        drivingMotors[1] = hardwareMap.get(DcMotor.class, "motor_fr");
        drivingMotors[2] = hardwareMap.get(DcMotor.class, "motor_bl");
        drivingMotors[3] = hardwareMap.get(DcMotor.class, "motor_br");

        // Servos
        armServos[0] = hardwareMap.get(Servo.class, "servo_r");
        armServos[1] = hardwareMap.get(Servo.class, "servo_l");
        
        // Universal wheel properties
        for (DcMotor i : drivingMotors){
            // Tells motors to run based on power level, not velocity
            i.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            // Attempts to turn motor not resisted
            i.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
        
        // Universal servo properties
        for (Servo i : armServos){
            i.scaleRange(0.8d, 1d);
        }
			
        // Claw starting position
        double clawPos = 0d;

        waitForStart();
        time.reset();

        while (opModeIsActive()){
            // Vars
            float leftPower;
            float rightPower;
            float speedFactor = 0.75f;

			// Driving calculations
            float trigger = gamepad1.left_trigger - gamepad1.right_trigger;
            float yInput = whichWay(gamepad1.dpad_up, gamepad1.dpad_down);
            float xInput = whichWay(gamepad1.dpad_right, gamepad1.dpad_left);

            /* The following code snippet implements joystick control, instead of d-pad
             * 
             * float yInput = -gamepad1.left_stick_y;
             * float xInput = gamepad1.left_stick_x;
            */

            // Restricts the power level to -1 to 1
            leftPower = Range.clip(yInput + xInput, -1.0f, 1.0f);
            rightPower = Range.clip(yInput - xInput, -1.0f, 1.0f);

			// Checks if a or y button pressed, to speed up or slow down
			if (gamepad1.a) {
				speedFactor = 0.5f
			} else if (gamepad1.y) {
				speedFactor = 1.0f
			}

            // Uses DcMotor.Direction to change direction of wheels
            if (trigger == 0) { // General motion (front, back, turn)
				drivingMotors[0].setDirection(DcMotor.Direction.REVERSE);
                drivingMotors[3].setDirection(DcMotor.Direction.REVERSE);
				
                // Sets speed
                drivingMotors[0].setPower(leftPower * speedFactor);
                drivingMotors[2].setPower(leftPower * speedFactor);
                drivingMotors[1].setPower(rightPower * speedFactor);
                drivingMotors[3].setPower(rightPower * speedFactor);
							
            } else { // Side-to-side motion
                drivingMotors[0].setDirection(DcMotor.Direction.FORWARD);
                drivingMotors[3].setDirection(DcMotor.Direction.FORWARD);
				
                // Sets speed
                for (DcMotor i : drivingMotors){
                    i.setPower(trigger * speedFactor);
                }
            }

            //  Servo movement, triggered by pressing "a" or "b"
            if ((gamepad1.x) && (clawPos <= 1d)) {
                clawPos += 0.005d;
            } else if ((gamepad1.b) && (clawPos >= 0d)) {
                clawPos -= 0.005d;
            }
            armServos[0].setPosition(1d - clawPos);
            armServos[1].setPosition(clawPos);

            // Output any debug or other info to controller
            telemetry.addData("Time", time.toString());
            telemetry.update();
        }
    }
}