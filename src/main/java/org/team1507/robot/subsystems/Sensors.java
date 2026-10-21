//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot.subsystems;

import org.wpilib.xrp.XRPRangefinder;
import org.wpilib.xrp.XRPReflectanceSensor;

import org.team1507.lib.core.framework.Subsystem1507;

import static org.team1507.robot.Constants.kSensors.*;

// ─────────────────────────────────────────────────────────────────────────────
// Sensors
//
// The XRP's two kinds of sensor:
//   - the ULTRASONIC rangefinder on the front: how far away the nearest thing
//     in front of the robot is (it times a sound echo, like a bat)
//   - the two REFLECTANCE (line) sensors underneath: how dark the floor is,
//     0.0 over white and 1.0 over black, for following black tape
//
// WHY THIS IS A SUBSYSTEM EVEN THOUGH NOTHING "RUNS" ON IT:
//   Extending Subsystem1507 gives it periodic() logging and alerts for free.
//   No command ever REQUIRES Sensors: reading a sensor doesn't need to stop
//   anyone else from reading it, so many commands can read these at once.
//
// LESSONS: you write most of this file's decisions yourself.
//   Lesson 3  isObjectClose(), getRangeZone()        (ultrasonic)
//   Lesson 4  the line sensor methods                (reflectance)
//   Lesson 5  periodic(): logging and an alert
// Each one is a placeholder until you do its lesson; see the wiki.
//
// The wheel encoders and the gyro are NOT here: they belong to the Drivetrain.
// ─────────────────────────────────────────────────────────────────────────────
public final class Sensors extends Subsystem1507 {

    private final XRPRangefinder       rangefinder = new XRPRangefinder();
    private final XRPReflectanceSensor reflectance = new XRPReflectanceSensor();

    public Sensors() {
        super("Sensors");
    }

    // =========================================================================
    // Raw sensor readings (given)
    // =========================================================================

    /** Distance to the nearest thing in front of the robot (meters; reads 4 m when nothing is close). */
    public double getRangeMeters() { return rangefinder.getDistance(); }

    /** Left reflectance sensor: 0.0 = white, 1.0 = black. */
    public double getLeftReflectance()  { return reflectance.getLeftReflectanceValue(); }

    /** Right reflectance sensor: 0.0 = white, 1.0 = black. */
    public double getRightReflectance() { return reflectance.getRightReflectanceValue(); }

    // =========================================================================
    // LESSON 3: Decisions, with the ultrasonic sensor
    // =========================================================================

    /**
     * True when something is CLOSE_METERS (Constants.kSensors) or closer in
     * front of the robot.
     */
    public boolean isObjectClose() {
        // LESSON 3: TODO. Use getRangeMeters() and CLOSE_METERS.
        return false;
    }

    /**
     * Which zone the nearest object is in:
     *   "TOO_CLOSE"  closer than TOO_CLOSE_METERS
     *   "NEAR"       closer than NEAR_METERS (but not too close)
     *   "FAR"        anything else
     */
    public String getRangeZone() {
        // LESSON 3: TODO. Use if / else if / else.
        return "TODO";
    }

    // =========================================================================
    // LESSON 4: Methods and booleans, with the line sensors
    // =========================================================================

    /** True if {@code reflectanceValue} is dark enough to be black tape (above LINE_THRESHOLD). */
    public boolean isBlack(double reflectanceValue) {
        // LESSON 4: TODO
        return false;
    }

    /** True if the LEFT line sensor is over the black line. */
    public boolean isLeftOnLine() {
        // LESSON 4: TODO. Use isBlack(...).
        return false;
    }

    /** True if the RIGHT line sensor is over the black line. */
    public boolean isRightOnLine() {
        // LESSON 4: TODO
        return false;
    }

    /** True if EITHER line sensor is over the line. */
    public boolean isOnLine() {
        // LESSON 4: TODO
        return false;
    }

    /** True if BOTH line sensors are over black: the crossbar marker at the end of a line. */
    public boolean isOnMarker() {
        // LESSON 4: TODO
        return false;
    }

    // =========================================================================
    // Periodic
    // =========================================================================

    @Override
    public void periodic() {
        log("RangeMeters",      getRangeMeters());
        log("LeftReflectance",  getLeftReflectance());
        log("RightReflectance", getRightReflectance());

        // LESSON 5: TODO. Log the answers from your Lesson 3 and 4 methods here,
        // and add a warnIf(...) alert for when something is too close.
    }
}
