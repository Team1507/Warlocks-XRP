//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot.subsystems;

import org.wpilib.xrp.XRPGyro;
import org.wpilib.xrp.XRPRangefinder;
import org.wpilib.xrp.XRPReflectanceSensor;

/**
 * Subsystem wrapper for all onboard XRP sensors: rangefinder, reflectance
 * sensors, and gyro. Publishes readings to SmartDashboard via
 * {@link XRPSubsystem#log} every scheduler cycle.
 */
public class XRPSensors extends XRPSubsystem {

    private final XRPRangefinder      rangefinder  = new XRPRangefinder();
    private final XRPReflectanceSensor reflectance  = new XRPReflectanceSensor();
    private final XRPGyro             gyro         = new XRPGyro();

    public XRPSensors() {
        super("Sensors");
        gyro.reset();
    }

    // =========================================================================
    // Sensor reads
    // =========================================================================

    /** @return distance to nearest obstacle in meters */
    public double getRangeMeters() { return rangefinder.getDistanceMeters(); }

    /** @return left reflectance sensor value [0.0–1.0] */
    public double getLeftReflectance()  { return reflectance.getLeftReflectanceValue(); }

    /** @return right reflectance sensor value [0.0–1.0] */
    public double getRightReflectance() { return reflectance.getRightReflectanceValue(); }

    /**
     * @return true if either sensor reads below the line threshold (0.7)
     */
    public boolean isOnLine() {
        return getLeftReflectance() < 0.7 || getRightReflectance() < 0.7;
    }

    /** @return accumulated gyro heading in degrees */
    public double getHeading() { return gyro.getAngle(); }

    /** Resets the gyro heading to zero. */
    public void resetGyro() { gyro.reset(); }

    // =========================================================================
    // Periodic
    // =========================================================================

    @Override
    public void periodic() {
        log("Rangefinder (m)",     getRangeMeters());
        log("Reflectance Left",    getLeftReflectance());
        log("Reflectance Right",   getRightReflectance());
        log("Gyro Heading (deg)",  getHeading());
    }
}
