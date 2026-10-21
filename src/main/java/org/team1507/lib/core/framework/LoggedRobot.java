//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.lib.core.framework;

import org.wpilib.system.DataLogManager;
import org.wpilib.driverstation.DriverStation;
import org.wpilib.framework.OpModeRobot;
import org.wpilib.command3.Scheduler;

import org.team1507.lib.core.logging.CommandLog;
import org.team1507.lib.core.logging.RobotHealthLog;

/**
 * Base robot class for Team 1507 (XRP version of 1507Base's LoggedRobot).
 *
 * <p>2027: extends {@link OpModeRobot} instead of TimedRobot. Autonomous and teleop
 * behavior live in OpMode classes (see AutoOpMode and robot/teleop/), which the
 * Driver Station lists in its drop-downs.
 *
 * <h2>Logging</h2>
 * Everything is recorded to one log (a {@code .wpilog} file) that
 * AdvantageScope opens, and is also live on NetworkTables under
 * {@code /Telemetry}:
 * <ul>
 *   <li>Driver Station: joysticks, enabled/mode ({@code DriverStation.startDataLog})</li>
 *   <li>Every subsystem's values (Subsystem1507)</li>
 *   <li>Battery and loop timing (RobotHealthLog)</li>
 *   <li>Every command start/end (CommandLog)</li>
 * </ul>
 * The XRP robot program runs on the laptop, so the log goes to {@code logs/} in
 * the project folder. The program's console output is recorded in the same file.
 *
 * <h2>Commands</h2>
 * Runs the Commands v3 {@link Scheduler} every loop. Two v3 behaviors to know:
 * <ul>
 *   <li>Commands scheduled and triggers bound inside an OpMode are removed
 *       automatically when a different OpMode is selected.</li>
 *   <li>The v3 scheduler does not stop commands when the robot disables. This
 *       class cancels every running command on the enabled-to-disabled edge, so a
 *       half-finished auto never resumes on the next enable. (v2 did the same.)
 *       Commands started while disabled (e.g. zero heading) still run.</li>
 * </ul>
 *
 * <p>Same as 1507Base, minus the power distribution hub (the XRP has none).
 */
public abstract class LoggedRobot extends OpModeRobot {

    private final Scheduler scheduler = Scheduler.getDefault();
    private final RobotHealthLog health;
    private boolean wasDisabled = true;

    protected LoggedRobot() {
        DataLogManager.start();
        DriverStation.startDataLog(DataLogManager.getLog());
        CommandLog.start(scheduler);
        health = new RobotHealthLog();
    }

    @Override
    public void robotPeriodic() {
        long loopStart = health.loopStart();

        // Cancel outside scheduler.run(): cancelAll() is not safe while a command is running.
        boolean disabled = isDisabled();
        if (disabled && !wasDisabled) {
            scheduler.cancelAll();
        }
        wasDisabled = disabled;

        scheduler.run();
        health.loopEnd(loopStart);
    }
}
