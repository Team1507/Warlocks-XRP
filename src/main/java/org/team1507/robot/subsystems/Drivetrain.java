//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot.subsystems;

import java.util.function.DoubleSupplier;

import org.wpilib.command3.Command;
import org.wpilib.command3.Coroutine;
import org.wpilib.drive.DifferentialDrive;
import org.wpilib.hardware.rotation.Encoder;
import org.wpilib.math.controller.PIDController;
import org.wpilib.math.util.MathUtil;
import org.wpilib.system.Timer;
import org.wpilib.xrp.XRPGyro;
import org.wpilib.xrp.XRPMotor;

import org.team1507.lib.core.framework.Subsystem1507;
import org.team1507.robot.Constants.RobotMap;

import static org.team1507.robot.Constants.kDrivetrain.kTuning.*;

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
//
// COMMANDS (Commands v3, bottom of this file):
//   drive(...)            teleop joystick driving (runs until interrupted)
//   driveStraight(speed)  holds the starting heading, runs until interrupted
//   followLine(...)       follows black tape, runs until interrupted
//   turnByAngle(deg)      turns relative to where it's facing now, then ends
//   turnToAngle(deg)      turns to an absolute heading, then ends
//   waitUntilStopped()    holds the motors at 0 until the wheels stop
//   waitUntilDistance(m)  waits for the wheels to travel m (doesn't drive)
//   stop()                motors to 0, then ends
//   idle()                the default: motors to 0, then waits forever
// "Runs until interrupted" commands are meant to be raced against a wait
// (see AutoSequence), the same way 2026's ParallelRaceGroups were.
// ─────────────────────────────────────────────────────────────────────────────
public final class Drivetrain extends Subsystem1507 {

    // -------------------------------------------------------------------------
    // Hardware facts
    // -------------------------------------------------------------------------

    /** Encoder counts per wheel revolution: 12 counts per motor turn × 48.75:1 gearbox. */
    private static final double COUNTS_PER_WHEEL_REV = 585.0;
    private static final double WHEEL_DIAMETER_METERS = 0.060;
    private static final double METERS_PER_COUNT = Math.PI * WHEEL_DIAMETER_METERS / COUNTS_PER_WHEEL_REV;

    // Tuning knobs (PID gains, turn speeds, the forward boost) are in
    // Constants.kDrivetrain.kTuning.

    // -------------------------------------------------------------------------
    // Hardware
    // -------------------------------------------------------------------------

    private final XRPMotor leftMotor  = new XRPMotor(RobotMap.LEFT_MOTOR);
    private final XRPMotor rightMotor = new XRPMotor(RobotMap.RIGHT_MOTOR);

    private final Encoder leftEncoder  = new Encoder(RobotMap.LEFT_ENCODER_A, RobotMap.LEFT_ENCODER_B);
    private final Encoder rightEncoder = new Encoder(RobotMap.RIGHT_ENCODER_A, RobotMap.RIGHT_ENCODER_B);

    private final XRPGyro gyro = new XRPGyro();

    // 2027 renamed the motor method set() to setThrottle(). DifferentialDrive
    // is given each motor's setThrottle method, the same way 2026 passed ::set.
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
        // loop. Switching modes leaves a short gap that tripped the safety
        // watchdog, so it stays off. That is why every command below sets
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
     * Boosts small forward speeds to at least MIN_FORWARD_OUTPUT (Constants) so the
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

    /** Power the left motor was last given [-1, 1]. */
    public double getLeftThrottle()  { return leftMotor.getThrottle(); }

    /**
     * Power the right motor was last given [-1, 1]. Because the right motor is
     * inverted, this reads NEGATIVE when the right wheel drives forward.
     */
    public double getRightThrottle() { return rightMotor.getThrottle(); }

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

    /**
     * How far the robot must turn to face {@code targetDegrees}, wrapped to
     * -180..180 so it always turns the short way. (Turning from 350° to 10°
     * is +20°, not -340°.)
     */
    public double headingErrorTo(double targetDegrees) {
        return MathUtil.inputModulus(targetDegrees - getHeading(), -180.0, 180.0);
    }

    // =========================================================================
    // Commands (Commands v3)
    //
    // Every command is named "Drivetrain.<action>" so logs and the scheduler
    // say exactly what is running. Every loop calls coroutine.yield(): that
    // hands control back to the scheduler for the rest of this 20 ms loop. A
    // loop WITHOUT yield() never gives control back, and the whole robot
    // program freezes (including the disable button's effect on commands).
    //
    // With motor safety off, a canceled command must stop the motors itself:
    // that's what .whenCanceled(this::stopMotors) is for.
    // =========================================================================

    /**
     * Teleop arcade driving from two suppliers (e.g. joystick axes). Runs until
     * interrupted. runRepeatedly() calls the lambda once every loop and yields
     * for you.
     */
    public Command drive(DoubleSupplier forward, DoubleSupplier rotation) {
        return runRepeatedly(() -> arcadeDrive(forward.getAsDouble(), rotation.getAsDouble()))
            .whenCanceled(this::stopMotors)
            .named("Drivetrain.drive");
    }

    /**
     * Drives at {@code speed} while holding the heading it had when it started.
     * Never finishes on its own: race it against a wait (AutoSequence does).
     *
     * <p>The PID controller is created INSIDE the command, so each run starts
     * fresh. (A bug we hit: a controller kept between runs carried leftover
     * state from the last run into the next one.)
     *
     * @param speed forward power [-1, 1], negative drives backward
     */
    public Command driveStraight(double speed) {
        return run(coroutine -> {
            PIDController headingPid = new PIDController(HEADING_KP, HEADING_KI, HEADING_KD);
            headingPid.enableContinuousInput(-180, 180);
            headingPid.setSetpoint(getHeading());

            while (true) {
                double correction = headingPid.calculate(getHeading());
                arcadeDriveWithCorrection(speed, correction);
                coroutine.yield();
            }
        }).whenCanceled(this::stopMotors).named("Drivetrain.driveStraight");
    }

    /**
     * Follows a black tape line with the two reflectance sensors. Never finishes
     * on its own: race it against a wait (e.g. the end-of-line marker).
     *
     * <p>Steers by speeding up one side, never by slowing the other, so both
     * wheels stay at or above {@code baseSpeed} and the robot never stalls.
     * Like driveStraight, the PID is new on every run.
     *
     * @param sensors   where to read reflectance (read only, not required)
     * @param baseSpeed power for both wheels, raised to at least MIN_LINE_SPEED
     */
    public Command followLine(Sensors sensors, double baseSpeed) {
        double base = Math.max(MIN_LINE_SPEED, Math.abs(baseSpeed));
        return run(coroutine -> {
            PIDController linePid = new PIDController(LINE_KP, LINE_KI, LINE_KD);

            while (true) {
                double error = sensors.getRightReflectance() - sensors.getLeftReflectance();
                double correction = linePid.calculate(error, 0.0);
                double left  = Math.min(1.0, base + Math.max(0,  correction));
                double right = Math.min(1.0, base + Math.max(0, -correction));
                tankDriveLinear(left, right);
                coroutine.yield();
            }
        }).whenCanceled(this::stopMotors).named("Drivetrain.followLine");
    }

    /**
     * Turns in place by {@code degrees} from wherever the robot faces when the
     * command starts (+90 = a quarter turn left, -90 = right), then ends.
     */
    public Command turnByAngle(double degrees) {
        return run(coroutine -> turnToHeading(coroutine, getHeading() + degrees))
            .whenCanceled(this::stopMotors)
            .named("Drivetrain.turnByAngle");
    }

    /** Turns in place to face {@code degrees} (absolute: 0 = the way it faced at startup), then ends. */
    public Command turnToAngle(double degrees) {
        return run(coroutine -> turnToHeading(coroutine, degrees))
            .whenCanceled(this::stopMotors)
            .named("Drivetrain.turnToAngle");
    }

    /**
     * The turn loop shared by turnByAngle and turnToAngle. Proportional
     * control: the farther from the target, the faster it turns.
     *
     * <p>Two bugs this avoids (both happened on the 2026 XRP):
     * <ul>
     *   <li>It uses {@link #turnInPlace}, which does NOT boost small outputs. With
     *       the boost, every correction became 65%+ power, so the robot could
     *       never settle precisely on the target.</li>
     *   <li>The error is wrapped to ±180° (headingErrorTo) both in the loop AND in
     *       the "am I done" check, so the robot turns the short way and stops.</li>
     * </ul>
     */
    private void turnToHeading(Coroutine coroutine, double targetDegrees) {
        double start = Timer.getTimestamp();

        while (Math.abs(headingErrorTo(targetDegrees)) >= TURN_TOLERANCE_DEG) {
            if (Timer.getTimestamp() - start > MAX_TURN_SECONDS) {
                warn("Turn gave up after " + MAX_TURN_SECONDS + " s (stuck, or the gyro isn't reading)");
                break;
            }
            double error = headingErrorTo(targetDegrees);
            double speed = Math.clamp(TURN_KP * error, -MAX_TURN_SPEED, MAX_TURN_SPEED);
            // Too little power and the XRP doesn't move at all (static friction).
            if (Math.abs(speed) < MIN_TURN_SPEED) {
                speed = Math.copySign(MIN_TURN_SPEED, speed);
            }
            turnInPlace(speed);
            coroutine.yield();
        }
        turnInPlace(0);
    }

    /** Holds the motors at 0 and ends once the wheels have stopped turning. */
    public Command waitUntilStopped() {
        return run(coroutine -> {
            stopMotors();
            coroutine.waitUntil(this::isStopped);
        }).named("Drivetrain.waitUntilStopped");
    }

    /**
     * Ends once the wheels have traveled {@code meters} (average of both sides,
     * either direction) since this command started. Doesn't drive and doesn't
     * require the drivetrain, so it can run alongside driveStraight.
     */
    public Command waitUntilDistance(double meters) {
        return Command.noRequirements(coroutine -> {
            double start = getAverageDistance();
            coroutine.waitUntil(() -> Math.abs(getAverageDistance() - start) >= Math.abs(meters));
        }).named("Drivetrain.waitUntilDistance");
    }

    /** Sets the motors to 0 and ends at once. */
    public Command stop() {
        return run(coroutine -> stopMotors()).named("Drivetrain.stop");
    }

    /**
     * What the drivetrain does when no command is using it: motors to 0, then
     * wait forever (park) at the lowest priority, so any other command can take
     * over. Robot.java makes this the default.
     */
    @Override
    public Command idle() {
        return run(coroutine -> {
            stopMotors();
            coroutine.park();
        }).withPriority(Command.LOWEST_PRIORITY).named("Drivetrain.idle");
    }

    // =========================================================================
    // Periodic
    // =========================================================================

    @Override
    public void periodic() {
        log("LeftDistanceMeters",  getLeftDistance());
        log("RightDistanceMeters", getRightDistance());
        log("LeftRateMps",         getLeftRate());
        log("RightRateMps",        getRightRate());
        log("LeftThrottle",        getLeftThrottle());
        log("RightThrottle",       getRightThrottle());
        log("HeadingDegrees",      getHeading());
        log("Stopped",             isStopped());
    }
}
