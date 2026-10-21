//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.lib.core.logging;

import org.wpilib.system.RobotController;
import org.wpilib.telemetry.Telemetry;
import org.wpilib.telemetry.TelemetryTable;

/**
 * Logs the whole robot's health every loop: the data needed to explain a
 * sluggish robot or a loop overrun after a run. LoggedRobot runs it; nothing
 * else needs to.
 *
 * <p>Logged under {@code Robot/}, every loop:
 * <table>
 *   <tr><td>BatteryVoltage, BrownedOut</td><td>the XRP's battery (low batteries make it slow and weak)</td></tr>
 *   <tr><td>LoopPeriodMs</td><td>time since the last loop started (20 = on time; more = overrun)</td></tr>
 *   <tr><td>RobotPeriodicMs</td><td>time spent in the scheduler (subsystems + commands)</td></tr>
 * </table>
 *
 * <p>XRP version of 1507Base's RobotHealthLog. The full robot also logs CAN bus
 * load, the power distribution hub and motor currents; the XRP has no CAN bus
 * and its motors don't report current, so those are left out. The loop-time
 * part is identical, and it matters even here: a command that forgets
 * {@code coroutine.yield()} shows up as a huge LoopPeriodMs.
 */
public final class RobotHealthLog {

    private final TelemetryTable table = Telemetry.getTable("Robot");

    private long lastLoopStartNanos = 0;

    /** Call first thing in every loop. Returns the start time for {@link #loopEnd}. */
    public long loopStart() {
        long now = System.nanoTime();
        if (lastLoopStartNanos != 0) {
            table.log("LoopPeriodMs", (now - lastLoopStartNanos) / 1e6);
        }
        lastLoopStartNanos = now;
        return now;
    }

    /** Call last thing in every loop, after the scheduler has run. */
    public void loopEnd(long loopStartNanos) {
        table.log("BatteryVoltage", RobotController.getBatteryVoltage());
        table.log("BrownedOut", RobotController.isBrownedOut());

        // Last, so it includes the logging above.
        table.log("RobotPeriodicMs", (System.nanoTime() - loopStartNanos) / 1e6);
    }
}
