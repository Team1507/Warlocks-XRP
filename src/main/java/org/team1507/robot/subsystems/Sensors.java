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

// ─────────────────────────────────────────────────────────────────────────────
// Sensors
//
// The XRP's rangefinder (distance to whatever is in front) and its two
// reflectance sensors (light/dark under the front of the robot, for following
// black tape lines). Reflectance reads 0.0 over white and 1.0 over black.
//
// WHY THIS IS A SUBSYSTEM EVEN THOUGH NOTHING "RUNS" ON IT:
//   Extending Subsystem1507 gives it periodic() logging and alerts for free.
//   No command ever REQUIRES Sensors: reading a sensor doesn't need to stop
//   anyone else from reading it, so many commands can read these at once.
//
// The gyro is NOT here: it belongs to the Drivetrain (see Drivetrain).
// ─────────────────────────────────────────────────────────────────────────────
public final class Sensors extends Subsystem1507 {

    /**
     * Reflectance above this on either side means that sensor is over the black
     * line. (The 2026 code had this backwards: it said "on line" over white.
     * Nothing drove off it, it was only logged, so no robot behavior changes.)
     */
    private static final double LINE_THRESHOLD = 0.7;

    private final XRPRangefinder       rangefinder = new XRPRangefinder();
    private final XRPReflectanceSensor reflectance = new XRPReflectanceSensor();

    public Sensors() {
        super("Sensors");
    }

    // =========================================================================
    // Sensor reads
    // =========================================================================

    /** Distance to the nearest thing in front of the robot (meters, reads 4 m when nothing is close). */
    public double getRangeMeters() { return rangefinder.getDistance(); }

    /** Left reflectance sensor: 0.0 = white, 1.0 = black. */
    public double getLeftReflectance()  { return reflectance.getLeftReflectanceValue(); }

    /** Right reflectance sensor: 0.0 = white, 1.0 = black. */
    public double getRightReflectance() { return reflectance.getRightReflectanceValue(); }

    /** True if either reflectance sensor is over the black line. */
    public boolean isOnLine() {
        return getLeftReflectance() > LINE_THRESHOLD || getRightReflectance() > LINE_THRESHOLD;
    }

    /**
     * True when BOTH reflectance sensors read above {@code threshold} (both over
     * black): the crossbar marker at the end of a line-following course.
     */
    public boolean isOnMarker(double threshold) {
        return getLeftReflectance() > threshold && getRightReflectance() > threshold;
    }

    // =========================================================================
    // Periodic
    // =========================================================================

    @Override
    public void periodic() {
        log("RangeMeters",       getRangeMeters());
        log("LeftReflectance",   getLeftReflectance());
        log("RightReflectance",  getRightReflectance());
        log("OnLine",            isOnLine());
    }
}
