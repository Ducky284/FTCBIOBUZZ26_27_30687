
package org.firstinspires.ftc.robotcontroller;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "RedDirect.java")
public class RedDirect extends LinearOpMode {

    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;

    static final double COUNTS_PER_MOTOR_REV = 312.0;
    static final double DRIVE_GEAR_REDUCTION = 1.0;
    static final double WHEEL_DIAMETER_INCHES = 4.0;
    static final double COUNTS_PER_INCH =
            (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION)
                    / (WHEEL_DIAMETER_INCHES * Math.PI);

    static final double DRIVE_SPEED = 0.2;

    @Override
    public void runOpMode() {

        leftDrive = hardwareMap.get(
                DcMotor.class, "BottomLeftDCMotor");
        rightDrive = hardwareMap.get(
                DcMotor.class, "BottomRightDCMotor");

        leftDrive.setDirection(DcMotor.Direction.FORWARD);
        rightDrive.setDirection(DcMotor.Direction.REVERSE);

        leftDrive.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE);

        leftDrive.setMode(
                DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(
                DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addLine("Auto ready: drive forward 24 inches");
        telemetry.addData("Target distance", "24 inches");
        telemetry.update();

        waitForStart();

        if (!opModeIsActive()) {
            return;
        }

        driveStraight(DRIVE_SPEED, 24.0, 5.0);
        stopAll();

        while (opModeIsActive()) {
            idle();
        }
    }

    private void driveStraight(
    double speed, double inches, double timeoutS) {
        driveTankInches(speed, inches, inches, timeoutS);
    }

    private void driveTankInches(
    double speed,
    double leftInches,
    double rightInches,
    double timeoutS) {

        int newLeftTarget = leftDrive.getCurrentPosition()
                + (int) (leftInches * COUNTS_PER_INCH);
        int newRightTarget = rightDrive.getCurrentPosition()
                + (int) (rightInches * COUNTS_PER_INCH);

        leftDrive.setTargetPosition(newLeftTarget);
        rightDrive.setTargetPosition(newRightTarget);

        leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        speed = Math.abs(speed);
        leftDrive.setPower(speed);
        rightDrive.setPower(speed);

        ElapsedTime runtime = new ElapsedTime();
        runtime.reset();

        while (opModeIsActive()
                && runtime.seconds() < timeoutS
                && (leftDrive.isBusy() || rightDrive.isBusy())) {

            telemetry.addData(
                    "Left and Right Inches",
                    "L: %.2f  R: %.2f",
                    leftInches, rightInches);

            telemetry.addData(
                    "Targets",
                    "L: %d  R: %d",
                    newLeftTarget, newRightTarget);

            telemetry.addData(
                    "Current Positions",
                    "L: %d  R: %d",
                    leftDrive.getCurrentPosition(),
                    rightDrive.getCurrentPosition());

            telemetry.update();
        }

        leftDrive.setPower(0);
        rightDrive.setPower(0);

        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void stopAll() {
        leftDrive.setPower(0);
        rightDrive.setPower(0);
    }
}

