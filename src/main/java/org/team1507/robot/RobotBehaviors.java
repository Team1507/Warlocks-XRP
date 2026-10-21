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
// These are complete robot actions that require two or more subsystems working
// together in sequence or in parallel.
//
// KEY PRINCIPLE:
//   A behavior defined here is the ONE definition used everywhere.
//   Whether triggered by a driver button in teleop or a step in an auto
//   routine, it calls the same method. You never write the same behavior twice.
//
// HOW TELEOP USES THIS:
//   In robot/teleop/DriverTeleop.java:
//     robot.driver.faceDown().onTrue(RobotBehaviors.celebrate(robot));
//
// HOW AUTO USES THIS:
//   In AutoSequence.java (a one-line wrapper):
//     public AutoSequence celebrate() {
//         return action("celebrate()", () -> RobotBehaviors.celebrate(robot()));
//     }
//   (that wrapper already exists). Then in a routine:
//     new AutoSequence().driveStraight(0.7).waitUntilDistance(0.3).celebrate().build();
//
// HOW TO ADD A NEW BEHAVIOR (Commands v3):
//   1. Identify which subsystems are involved.
//   2. Write a static method here that combines their commands. The simplest
//      way reads top to bottom:
//
//        return Command.requiring(robot.drivetrain, robot.arm).executing(coroutine -> {
//            coroutine.awaitAll(robot.arm.up(), robot.drivetrain.turnByAngle(90)); // both at once
//            coroutine.await(robot.arm.down());                                  // then this
//        }).named("MyBehavior");
//
//      Or chain them:  Command.sequence(a, b).named("...")
//                      Command.parallel(a, b).named("...")
//   3. Add a wrapper in AutoSequence.java if it's needed in auto routines.
//   4. Bind it in an OpMode (e.g. DriverTeleop) if it's a driver control.
//
// WHY requiring(...) AND NOT noRequirements(...):
//   A behavior that requires the subsystems it uses can be interrupted as a
//   whole (by the failsafe, or by the driver taking over). The commands it runs
//   inside may use those subsystems, because they belong to it.
//
// NAMING CONVENTION:
//   Name behaviors by what the robot DOES, not what the mechanism is.
//   GOOD:  celebrate(), scoreHigh(), pickUp()
//   AVOID: armUpAndTurn180ThenArmDown()
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

    // ─────────────────────────────────────────────────────────────────
    // LESSON 11: FOLLOWING A LINE (sensors + drivetrain)
    // ─────────────────────────────────────────────────────────────────

    /**
     * Follows a black tape line using the two line sensors. Runs until
     * interrupted: in an auto, a wait step ends it (AutoSequence.followLine).
     *
     * <p>It requires the drivetrain (it drives) but not Sensors (it only reads them).
     *
     * @param speed power for the wheels when going straight [0, 1]
     */
    public static Command followLine(Robot robot, double speed) {
        return Command.requiring(robot.drivetrain).executing(coroutine -> {
            while (true) {
                // LESSON 11: TODO. Every loop, look at the line sensors
                // (robot.sensors.isLeftOnLine(), isRightOnLine()) and decide how
                // fast each wheel goes. Right now it just drives straight.
                robot.drivetrain.tankDriveLinear(speed, speed);
                coroutine.yield();
            }
        }).whenCanceled(robot.drivetrain::stopMotors).named("FollowLine");
    }

    // ─────────────────────────────────────────────────────────────────
    // EXAMPLE: DRIVETRAIN + ARM
    // ─────────────────────────────────────────────────────────────────

    /**
     * Celebrate: raise the arm while turning around, then lower the arm.
     *
     * <p>An example of the two ways to combine commands inside a coroutine:
     * {@code awaitAll} runs commands at the same time and waits for all of
     * them; {@code await} runs one and waits for it.
     *
     * <p>Used from teleop (A / faceDown in DriverTeleop) and auto
     * ({@code .celebrate()} in AutoSequence, ready to use in a routine).
     */
    public static Command celebrate(Robot robot) {
        return Command.requiring(robot.drivetrain, robot.arm).executing(coroutine -> {
            coroutine.awaitAll(robot.arm.up(), robot.drivetrain.turnByAngle(180));
            coroutine.await(robot.arm.down());
        }).named("Celebrate");
    }
}
