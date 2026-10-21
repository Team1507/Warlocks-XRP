//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.lib.core.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wpilib.command3.Mechanism;
import org.wpilib.command3.Scheduler;
import org.wpilib.driverstation.DriverStationErrors;
import org.wpilib.framework.RobotBase;
import org.wpilib.telemetry.Telemetry;
import org.wpilib.telemetry.TelemetryTable;
import org.wpilib.util.Alert;

/**
 * Base class for all Team 1507 subsystems. Everything every subsystem needs,
 * so it isn't copied into each one.
 *
 * <p><b>XRP version.</b> Same class name and same API as 1507Base's
 * Subsystem1507, so code you write here moves to the real robot unchanged. The
 * full robot's version also creates and logs CTRE motors ({@code motor(...)},
 * {@code Motor1507}, CAN buses); the XRP's motors are plain PWM-style motors
 * with no CAN bus, so that part is left out. Each XRP subsystem creates its
 * hardware itself (see Drivetrain).
 *
 * <p>Logged for every subsystem: {@code <Subsystem>/Command} (the command
 * using it; see CommandLog) and {@code <Subsystem>/PeriodicMs} (how long this
 * loop took, for finding loop overruns).
 *
 * <h2>The loop, in order</h2>
 * <ol>
 *   <li>{@link #periodic()} — your code: read sensors, log values, flag problems</li>
 *   <li>simulation only: {@link #simulationPeriodic()}</li>
 *   <li>log the loop's time</li>
 * </ol>
 * All of this runs before any commands, the same as v2 subsystems did.
 *
 * <p>XRP note: the XRP robot program always runs on your laptop and talks to
 * the XRP over Wi-Fi, so WPILib counts it as "simulation" even when a real XRP
 * is connected. {@link #simulationPeriodic()} therefore runs on the real XRP
 * too. Don't put pretend physics there that would fight the real sensors.
 *
 * <h2>Also provides</h2>
 * <ul>
 *   <li>{@link #log} — record a value under this subsystem's name</li>
 *   <li>{@link #warnIf} / {@link #faultIf} — an alert on the dashboard while a
 *       problem lasts; {@link #warn} / {@link #fault} — one-time problems</li>
 *   <li>Commands v3: a subsystem is a {@link Mechanism}. Commands that use it
 *       are created with {@code run(...)} / {@code runRepeatedly(...)} and must
 *       be named with {@code .named("Subsystem.action")}.</li>
 * </ul>
 */
public abstract class Subsystem1507 implements Mechanism {

    /** Every Subsystem1507 ever constructed, in construction order. */
    private static final List<Subsystem1507> ALL = new ArrayList<>();

    private final String name;

    /** Where this subsystem's values are logged ({@code <name>/...}). */
    private final TelemetryTable table;

    /** Dashboard alerts, created once per message and reused. */
    private final Map<String, Alert> alerts = new HashMap<>();

    /**
     * @param name subsystem name — used as the telemetry root for everything
     *             this subsystem logs, and as the name Commands v3 reports for
     *             its commands.
     */
    protected Subsystem1507(String name) {
        this.name = name;
        this.table = Telemetry.getTable(name);
        ALL.add(this);
        // Commands v3 has no built-in periodic() for mechanisms. Registering here
        // means every subsystem gets one without having to remember it.
        Scheduler.getDefault().addPeriodic(this::runEveryLoop);
    }

    /** Returns every subsystem on the robot (used by the failsafe and logging). */
    public static List<Subsystem1507> all() {
        return Collections.unmodifiableList(ALL);
    }

    @Override
    public String getName() {
        return name;
    }

    /** Runs every robot loop, before any commands. Read sensors and log here. */
    public void periodic() {}

    /** Runs every robot loop in simulation only (on the XRP: always, see the class comment). */
    public void simulationPeriodic() {}

    /** The loop described in the class comment. Registered with the scheduler. */
    private void runEveryLoop() {
        long start = System.nanoTime();

        periodic();

        if (RobotBase.isSimulation()) {
            simulationPeriodic();
        }

        table.log("PeriodicMs", (System.nanoTime() - start) / 1e6);
    }

    // =========================================================================
    // Telemetry helpers
    // =========================================================================

    // Logging goes through WPILib 2027's Telemetry. It lands in NetworkTables
    // under /Telemetry/<Subsystem>/<field> (live in AdvantageScope and Elastic),
    // and DataLogManager records it into the log file (.wpilog).
    // A value that didn't change since the last loop isn't written again.

    /**
     * Returns the full telemetry path for a child, e.g. {@code key("Left")} in
     * a subsystem named {@code "Drivetrain"} returns {@code "Drivetrain/Left"}.
     * Use it to nest a group of values under the subsystem.
     */
    protected String key(String child) {
        return getName() + "/" + child;
    }

    /** Records a true/false value at {@code <Subsystem>/<field>}, e.g. {@code log("OnLine", isOnLine())}. */
    protected void log(String field, boolean value) {
        table.log(field, value);
    }

    /** Records a number at {@code <Subsystem>/<field>}, e.g. {@code log("AngleDeg", getAngle())}. */
    protected void log(String field, double value) {
        table.log(field, value);
    }

    /**
     * Records text at {@code <Subsystem>/<field>}. Good for state machine states:
     * <pre>
     *   log("State", state.name());   // e.g. "IDLE", "DRIVING", "TURNING"
     * </pre>
     */
    protected void log(String field, String value) {
        table.log(field, value);
    }

    /**
     * Records a WPILib object at {@code <Subsystem>/<field>}: a {@code Pose2d},
     * {@code Rotation2d}, ... AdvantageScope can draw poses on the field.
     */
    protected void log(String field, Object value) {
        table.log(field, value);
    }

    /** Records an array of WPILib objects. */
    protected void log(String field, Object[] values) {
        table.log(field, values);
    }

    // =========================================================================
    // Problems: alerts on the dashboard and Driver Station
    // =========================================================================

    /**
     * Shows a MEDIUM (yellow) alert on the dashboard while {@code condition} is
     * true, and clears it when it becomes false. Also reports to the Driver
     * Station each time it turns on. Call it every loop from {@link #periodic()}:
     *
     * <pre>
     *   warnIf(getRangeMeters() < 0.05, "Something is right in front of the rangefinder");
     * </pre>
     *
     * Use for problems that come and go and that the driver should know about.
     */
    protected void warnIf(boolean condition, String message) {
        setAlert(condition, message, Alert.Level.MEDIUM);
    }

    /**
     * Same as {@link #warnIf}, as a HIGH (red) alert, for problems that seriously
     * affect the robot (a motor not responding, a mechanism disabled).
     */
    protected void faultIf(boolean condition, String message) {
        setAlert(condition, message, Alert.Level.HIGH);
    }

    /**
     * Reports a one-time warning (e.g. "Arm angle was outside its limits"):
     * shown on the Driver Station and left as a MEDIUM alert on the dashboard.
     * For a condition that can recover, use {@link #warnIf} instead so the alert
     * clears itself.
     */
    protected void warn(String message) {
        setAlert(true, message, Alert.Level.MEDIUM);
    }

    /** Same as {@link #warn}, as a HIGH (red) alert. */
    protected void fault(String message) {
        setAlert(true, message, Alert.Level.HIGH);
    }

    /** True if the alert for {@code message} is currently showing. (Used by tests.) */
    boolean isAlertActive(String message) {
        Alert alert = alerts.get(message);
        return alert != null && alert.get();
    }

    private void setAlert(boolean active, String message, Alert.Level level) {
        Alert alert = alerts.get(message);
        if (alert == null) {
            if (!active) {
                return;   // never created an alert for a problem that never happened
            }
            alert = new Alert(getName(), message, "[" + getName() + "] " + message, level);
            alerts.put(message, alert);
        }
        if (active && !alert.get()) {
            // Just turned on: tell the Driver Station once, not every loop.
            String text = "[" + getName() + "] " + message;
            if (level == Alert.Level.HIGH) {
                DriverStationErrors.reportError(text, false);
            } else {
                DriverStationErrors.reportWarning(text, false);
            }
        }
        alert.set(active);
    }
}
