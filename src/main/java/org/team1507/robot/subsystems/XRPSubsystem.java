//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot.subsystems;

import org.wpilib.driverstation.MatchState;
import org.wpilib.driverstation.RobotState;
import org.wpilib.driverstation.Alliance;
import org.wpilib.driverstation.MatchType;
import org.wpilib.driverstation.DriverStationErrors;
import org.wpilib.smartdashboard.SmartDashboard;
import org.wpilib.command2.SubsystemBase;

/**
 * Base class for all XRP subsystems.
 *
 * <p>Provides telemetry helpers that auto-prefix keys with the subsystem name
 * (e.g. {@code log("Speed", 0.5)} publishes at {@code "Drivetrain/Speed"}).
 * This mirrors the {@code Subsystem1507} pattern from the 1507Base repo but
 * uses SmartDashboard instead of NetworkTables publishers, which is appropriate
 * for the XRP simulation environment.
 *
 * <p>Usage:
 * <pre>
 *   public class XRPDrivetrain extends XRPSubsystem {
 *       public XRPDrivetrain() { super("Drivetrain"); }
 *
 *       public void periodic() {
 *           log("LeftDistance", getLeftDistance());
 *           log("Stopped", isStopped());
 *       }
 *   }
 * </pre>
 */
public abstract class XRPSubsystem extends SubsystemBase {

    protected XRPSubsystem(String name) {
        setName(name);
    }

    // =========================================================================
    // Telemetry helpers
    // =========================================================================

    /** Publishes a double value prefixed with the subsystem name. */
    protected void log(String field, double value) {
        SmartDashboard.putNumber(getName() + "/" + field, value);
    }

    /** Publishes a boolean value prefixed with the subsystem name. */
    protected void log(String field, boolean value) {
        SmartDashboard.putBoolean(getName() + "/" + field, value);
    }

    /** Publishes a string value prefixed with the subsystem name. */
    protected void log(String field, String value) {
        SmartDashboard.putString(getName() + "/" + field, value);
    }

    // =========================================================================
    // Driver Station reporting
    // =========================================================================

    /** Reports a recoverable warning to the Driver Station with the subsystem name prepended. */
    protected void warn(String message) {
        DriverStationErrors.reportWarning("[" + getName() + "] " + message, false);
    }

    /** Reports a serious fault to the Driver Station with the subsystem name prepended. */
    protected void fault(String message) {
        DriverStationErrors.reportError("[" + getName() + "] " + message, false);
    }
}
