//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

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
        DriverStation.reportWarning("[" + getName() + "] " + message, false);
    }

    /** Reports a serious fault to the Driver Station with the subsystem name prepended. */
    protected void fault(String message) {
        DriverStation.reportError("[" + getName() + "] " + message, false);
    }
}
