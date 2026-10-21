//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot;

import java.util.ArrayList;
import java.util.List;

import org.wpilib.command3.Command;
import org.wpilib.command3.Mechanism;

import org.team1507.lib.core.framework.Subsystem1507;

// ─────────────────────────────────────────────────────────────────────────────
// RobotBehaviors
//
// A single shared library of coordinated, multi-subsystem robot behaviors.
// A behavior defined here is the ONE definition used everywhere: a driver
// button in teleop and a step in an auto routine call the same method.
// ─────────────────────────────────────────────────────────────────────────────
public final class RobotBehaviors {

    // Prevent instantiation — this is a static utility class.
    private RobotBehaviors() {}

    // ─────────────────────────────────────────────────────────────────
    // FAILSAFE
    // ─────────────────────────────────────────────────────────────────

    /**
     * Interrupts every command that is using a subsystem.
     *
     * <p>Use when a mechanism appears stuck or unresponsive. This command
     * requires EVERY subsystem at the highest priority, so starting it
     * interrupts whatever was using them. It then finishes immediately, and
     * each subsystem's default command (e.g. joystick driving) starts again on
     * the next loop.
     *
     * <p>Commands that use no subsystem (for example a plain wait) are not affected.
     *
     * <p>Binding (DriverTeleop): {@code robot.driver.back().onTrue(RobotBehaviors.failsafe());}
     */
    public static Command failsafe() {
        List<Mechanism> everything = new ArrayList<>(Subsystem1507.all());
        return Command.requiring(everything)
            .executing(coroutine -> {})
            .withPriority(Command.HIGHEST_PRIORITY)
            .named("Failsafe");
    }
}
