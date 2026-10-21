//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot.subsystems;

import org.wpilib.drive.DifferentialDrive;
import org.wpilib.hardware.rotation.Encoder;
import org.wpilib.xrp.XRPGyro;
import org.wpilib.xrp.XRPMotor;

import org.team1507.lib.core.framework.Subsystem1507;

// ─────────────────────────────────────────────────────────────────────────────
// Drivetrain
//
// The XRP's two drive motors, their wheel encoders, and the gyro.
//
// WHY THE GYRO LIVES HERE (and not in Sensors):
//   The gyro tells us which way the ROBOT is pointing, and only driving uses
//   it. Keeping it with the motors means one subsystem owns "where am I
//   facing and how do I turn". 1507Base does the same: Swerve owns the Pigeon.
//
// HARDWARE (fixed by the XRP board, can't be changed):
//   Left motor = channel 0, right motor = channel 1
//   Left encoder = DIO 4/5, right encoder = DIO 6/7
//   585 encoder counts per wheel revolution, 60 mm wheels
//
// MOTOR OUTPUT HELPERS:
//   The XRP's little motors need about 40-65% power before they move at all
//   (static friction). Each way of driving below deals with that differently:
//     arcadeDrive()               teleop: the driver's stick, as-is
//     arcadeDriveWithCorrection() auto straight driving: boosts the forward
//                                 speed, but NOT the small heading correction
//     tankDriveLinear()           line following: exact wheel speeds
//     turnInPlace()               auto turns: no boost at all, so the turn
//                                 can slow down and settle on the target
// ─────────────────────────────────────────────────────────────────────────────
public final class Drivetrain extends Subsystem1507 {

    // -------------------------------------------------------------------------
    // Hardware facts
    // -------------------------------------------------------------------------

    /** Encoder counts per wheel revolution: 12 counts per motor turn × 48.75:1 gearbox. */
    private static final double COUNTS_PER_WHEEL_REV = 585.0;
    private static final double WHEEL_DIAMETER_METERS = 0.060;
    private static final double METERS_PER_COUNT = Math.PI * WHEEL_DIAMETER_METERS / COUNTS_PER_WHEEL_REV;

    /** Below this wheel speed (m/s) on both sides, the robot counts as stopped. */
    private static final double STOPPED_METERS_PER_SECOND = 0.01;

    /** arcadeDriveWithCorrection boosts any forward speed to at least this much power. */
    private static final double MIN_FORWARD_OUTPUT = 0.65;

    // -------------------------------------------------------------------------
    // Hardware
    // -------------------------------------------------------------------------

    private final XRPMotor leftMotor  = new XRPMotor(0);
    private final XRPMotor rightMotor = new XRPMotor(1);

    private final Encoder leftEncoder  = new Encoder(4, 5);
    private final Encoder rightEncoder = new Encoder(6, 7);

    private final XRPGyro gyro = new XRPGyro();

    // 2027: XRPMotor is no longer a MotorController, so DifferentialDrive is
    // given each motor's setThrottle method instead of the motor itself.
    private final DifferentialDrive diffDrive =
        new DifferentialDrive(leftMotor::setThrottle, rightMotor::setThrottle);

    // =========================================================================
    // Constructor
    // =========================================================================

    public Drivetrain() {
        super("Drivetrain");

        // The motors face opposite ways, so one must be reversed for "forward"
        // to turn both wheels forward.
        rightMotor.setInverted(true);

        leftEncoder.setDistancePerPulse(METERS_PER_COUNT);
        rightEncoder.setDistancePerPulse(METERS_PER_COUNT);
        resetEncoders();

        // Safety OFF: motor safety stops the motors if they aren't updated every
        // loop. Switching modes leaves a short gap that tripped it and made the
        // robot stutter, so it stays off. That is why every command below sets
        // the motors to 0 itself when it ends.
        diffDrive.setSafetyEnabled(false);

        gyro.reset();
    }

    // =========================================================================
    // Driving
    // =========================================================================

    /**
     * Arcade drive for teleop. Apply the deadband to the stick values before
     * calling this (DriverTeleop does).
     *
     * @param forward  forward/backward [-1, 1]
     * @param rotation turning [-1, 1], counterclockwise (left) is positive
     */
    public void arcadeDrive(double forward, double rotation) {
        diffDrive.arcadeDrive(forward, rotation);
    }

    /**
     * Arcade drive for driving straight in auto.
     *
     * <p>The forward speed is boosted by {@link #boostForward} so the robot moves,
     * but the heading correction is passed through <em>raw</em>. (A bug we hit:
     * boosting the correction too turned every tiny PID output into 65%+ power
     * and spun the robot.)
     *
     * @param forward    forward/backward [-1, 1]
     * @param correction raw heading correction from a PID controller [-1, 1]
     */
    public void arcadeDriveWithCorrection(double forward, double correction) {
        diffDrive.arcadeDrive(boostForward(forward), correction);
    }

    /**
     * Tank drive with linear (not squared) inputs, for line following: the power
     * sent to each wheel is exactly what is passed in.
     */
    public void tankDriveLinear(double leftSpeed, double rightSpeed) {
        diffDrive.tankDrive(leftSpeed, rightSpeed, false);
    }

    /**
     * Spins in place, for auto turns. No boost is applied: the turn command
     * picks a speed big enough to move but small enough not to overshoot.
     *
     * @param rotation turn speed [-1, 1], counterclockwise (left) is positive
     */
    public void turnInPlace(double rotation) {
        diffDrive.arcadeDrive(0, rotation);
    }

    /** Sets both motors to 0. (With motor safety off, nothing else will.) */
    public void stopMotors() {
        diffDrive.arcadeDrive(0, 0);
    }

    /**
     * Boosts small forward speeds to at least {@link #MIN_FORWARD_OUTPUT} so the
     * XRP's motors overcome static friction. Below 0.01 counts as stop.
     */
    private static double boostForward(double input) {
        double magnitude = Math.abs(input);
        if (magnitude < 0.01) {
            return 0.0;
        }
        double boosted = MIN_FORWARD_OUTPUT + (1.0 - MIN_FORWARD_OUTPUT) * magnitude;
        return Math.copySign(boosted, input);
    }

    // =========================================================================
    // Sensors: encoders and gyro
    // =========================================================================

    public void resetEncoders() {
        leftEncoder.reset();
        rightEncoder.reset();
    }

    /** Left wheel distance since the last reset (meters, forward is positive). */
    public double getLeftDistance()  { return leftEncoder.getDistance(); }

    /** Right wheel distance since the last reset (meters, forward is positive). */
    public double getRightDistance() { return rightEncoder.getDistance(); }

    /** Average of both wheels (meters). This is "how far the robot drove". */
    public double getAverageDistance() { return (getLeftDistance() + getRightDistance()) / 2.0; }

    /** Left wheel speed (m/s). */
    public double getLeftRate()  { return leftEncoder.getRate(); }

    /** Right wheel speed (m/s). */
    public double getRightRate() { return rightEncoder.getRate(); }

    /** True when both wheels have (almost) stopped turning. */
    public boolean isStopped() {
        return Math.abs(getLeftRate()) < STOPPED_METERS_PER_SECOND
            && Math.abs(getRightRate()) < STOPPED_METERS_PER_SECOND;
    }

    /**
     * Which way the robot faces (degrees, counterclockwise is positive). Keeps
     * counting past 360, so two full turns left reads 720.
     */
    public double getHeading() { return gyro.getAngleZ(); }

    /** Makes the direction the robot faces right now read as 0°. */
    public void resetHeading() { gyro.reset(); }

    // =========================================================================
    // Periodic
    // =========================================================================

    @Override
    public void periodic() {
        log("LeftDistanceMeters",  getLeftDistance());
        log("RightDistanceMeters", getRightDistance());
        log("LeftRateMps",         getLeftRate());
        log("RightRateMps",        getRightRate());
        log("LeftThrottle",        leftMotor.getThrottle());
        log("RightThrottle",       rightMotor.getThrottle());
        log("HeadingDegrees",      getHeading());
        log("Stopped",             isStopped());
    }
}
