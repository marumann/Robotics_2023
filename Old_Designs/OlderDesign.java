/*
 * I believe this code is from even earlier than last year.
 * One of the team's code was adapted from this.
 *
 * This design has:
 * 4 wheel motors
 * 2 arm motors
 * 2 claw servos
 * 
 * It is controlled using both joysticks, the triggers, and the bumpers
 */
  
// FIRST Robotics imports
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp

public class DriverControlledOpMode extends LinearOpMode {
    // Instantiate wheel motors
    private DcMotor motor_fl;
    private DcMotor motor_fr;
    private DcMotor motor_bl;
    private DcMotor motor_br;

    // Arm motors
    private DcMotor arm_base_motor;
    private DcMotor arm_joint_motor;
    
        
    // Claw servos
    private Servo claw_left;
    private Servo claw_right;
    
    // Methods for getting the power for the wheels
    void mfl(float speed) { // speed from -1 -> 1
      motor_fl.setPower(-1f*speed*0.75f);
    }
    void mfr(float speed) { // speed from -1 -> 1
      motor_fr.setPower(speed*0.75f);
    }
    void mbl(float speed) { // speed from -1 -> 1
      motor_bl.setPower(-1f*speed*0.75f);
    }
    void mbr(float speed) { // speed from -1 -> 1
      motor_br.setPower(speed*0.75f);
    }

    // Methods for setting the positions and power for the arm motors
    void arm_base(int target) { // target in encoder ticks
      arm_base_motor.setTargetPosition(target);
      arm_base_motor.setPower(0.75);
    }
    void arm_joint(int target) {
      arm_joint_motor.setTargetPosition(target);
      arm_joint_motor.setPower(0.75);
    }

    // Methods for setting positions for the claws
    void claw(double target) { // pos from 0.0 (closed) - 1.0 (open)
      if (target < 0) { // 0.25
          target = 0;
      } else if (target > 1) { // 0.95
          target = 1;
      }
      claw_left.setPosition(target);
      claw_right.setPosition(1f-target);
    }
    
    @Override
    public void runOpMode() {
        
        // Get the wheels according to motor name
        motor_fl = hardwareMap.get(com.qualcomm.robotcore.hardware.DcMotor.class, "motor_fl");
        motor_fr = hardwareMap.get(com.qualcomm.robotcore.hardware.DcMotor.class, "motor_fr");
        motor_bl = hardwareMap.get(com.qualcomm.robotcore.hardware.DcMotor.class, "motor_bl");
        motor_br = hardwareMap.get(com.qualcomm.robotcore.hardware.DcMotor.class, "motor_br");
        
        // Get the claws according to servo name
        claw_left = hardwareMap.get(com.qualcomm.robotcore.hardware.Servo.class, "claw_left");
        claw_right = hardwareMap.get(com.qualcomm.robotcore.hardware.Servo.class, "claw_right");
        
        // Get the arm according to motor name
        arm_base_motor = hardwareMap.get(com.qualcomm.robotcore.hardware.DcMotor.class, "arm_base");
        arm_joint_motor = hardwareMap.get(com.qualcomm.robotcore.hardware.DcMotor.class, "arm_joint");

        // Tell the controller that the robot's gotten the motors/servos
        telemetry.addData("Status", "Nominal");
        telemetry.update();
        
        // Set arm to start position
        arm_joint_motor.setTargetPosition(0);
        arm_base_motor.setTargetPosition(0);

        // Set run mode behavior
        // Run mode determines if the motor runs according to power, or velocity
        motor_fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor_fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor_bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor_br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        arm_base_motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm_joint_motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm_base_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        arm_joint_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Set zero mode behavior
        // Zero mode determines how the motor acts when power = 0
        motor_fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor_fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor_bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor_br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        arm_base_motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        arm_joint_motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        waitForStart();

        // Initialize various variables
        float stick_x = 0f;
        float stick_y = 0f;

        float target_mfl = 0f;
        float current_mfl = 0f;
        float target_mfr = 0f;
        float current_mfr = 0f;
        float target_mbl = 0f;
        float current_mbl = 0f;
        float target_mbr = 0f;
        float current_mbr = 0f;

        float rotation = 0f;
        float rotation_target = 0f;

        float clawpos = 0f;
        float clawmin = 0f;
        float clawmax = 0.9f;
        float clawdelta = 0.01f;
        
        int base_target = 100;
        int joint_target = -100;

        while (opModeIsActive()) {
            telemetry.addData("Status", "Operating as normal.");

            // Gets info from the controller
            stick_x = this.gamepad1.left_stick_x;
            stick_y = -1f*this.gamepad1.left_stick_y;
            
            float x_sign = Math.abs(stick_x)/stick_x;
            float y_sign = Math.abs(stick_y)/stick_y;

            // Math for determining if going left or right
            if (Math.abs(stick_x) > Math.abs(stick_y)) {
                if (stick_x > 0f) {
                    telemetry.addData("Direction", "Right");
                    telemetry.addData("Stick", stick_x+", "+stick_y);
                    target_mfl = x_sign*stick_x+0f;
                    target_mfr = x_sign*-stick_x+0f;
                    target_mbl = x_sign*-stick_x+0f;
                    target_mbr = x_sign*stick_x+0f;
                } else {
                    telemetry.addData("Direction", "Left");
                    telemetry.addData("Stick", stick_x+", "+stick_y);
                    target_mfl = x_sign*-stick_x+0f;
                    target_mfr = x_sign*stick_x+0f;
                    target_mbl = x_sign*stick_x+0f;
                    target_mbr = x_sign*-stick_x+0f;
                }
            // If not going left or right, determines if going front or back
            } else {
                if (stick_y > 0f) {
                    telemetry.addData("Direction", "Forward");
                    telemetry.addData("Stick", stick_x+", "+stick_y);
                    target_mfl = y_sign*stick_y+0f;
                    target_mfr = y_sign*stick_y+0f;
                    target_mbl = y_sign*stick_y+0f;
                    target_mbr = y_sign*stick_y+0f;
                } else {
                    telemetry.addData("Direction", "Backward");
                    telemetry.addData("Stick", stick_x+", "+stick_y);
                    target_mfl = y_sign*-1f*stick_y+0f;
                    target_mfr = y_sign*-1f*stick_y+0f;
                    target_mbl = y_sign*-1f*stick_y+0f;
                    target_mbr = y_sign*-1f*stick_y+0f;
                }
            }

            // Rotation
            if (Math.abs(current_mfl) < 0.001f) {
                current_mfl = 0f;
            }
            if (Math.abs(current_mfr) < 0.001f) {
                current_mfr = 0f;
            }
            if (Math.abs(current_mbl) < 0.001f) {
                current_mbl = 0f;
            }
            if (Math.abs(current_mbr) < 0.001f) {
                current_mbr = 0f;
            }
            
            // If the joystick is unused, check if triggers are used for left/right movement
            if (stick_y == 0 && stick_x == 0) {
                rotation = this.gamepad1.right_trigger - this.gamepad1.left_trigger;
                mfl(rotation);
                mfr(-1f*rotation);
                mbl(rotation);
                mbr(-1f*rotation);
            } else {
                mfl(target_mfl);
                mfr(target_mfr);
                mbl(target_mbl);
                mbr(target_mbr);
            }

            // Arm (controlled with right joystick)
            base_target += Math.round(this.gamepad1.right_stick_y*7);
            joint_target += Math.round(this.gamepad1.right_stick_x*7);
            if (base_target > 1500) {
                base_target = 1500;
            } else if (base_target < 0) {
                base_target = 0;
            }
            if (joint_target > 0) {
                joint_target = 0;
            } else if (joint_target < -1300) {
                joint_target = -1300;
            }
            arm_base(base_target);
            arm_joint(joint_target);
            
            telemetry.addData("Arm Base Target", base_target);
            telemetry.addData("Arm Joint Target", joint_target);
              
            // Claw (controlled with bumpers)
            if (this.gamepad1.left_bumper) {
                clawpos += clawdelta;
            } else if (this.gamepad1.right_bumper) {
                clawpos -= clawdelta;
            } 
            if (clawpos > clawmax) {
                clawpos = clawmax + 0f;
            } 
            if (clawpos < clawmin) {
                clawpos = clawmin + 0f;
            }
            claw(clawpos);

            telemetry.addData("Arm Base", arm_base_motor.getCurrentPosition());
            telemetry.addData("Arm Joint", arm_joint_motor.getCurrentPosition());
            telemetry.update();
        }
    }
}
}