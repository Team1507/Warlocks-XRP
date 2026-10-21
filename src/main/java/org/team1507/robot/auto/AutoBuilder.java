//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot.auto;

import org.team1507.robot.Robot;

// ─────────────────────────────────────────────────────────────────────────────
// AutoBuilder
//
// Gives AutoSequence access to the robot, so auto routines never pass
// subsystems around. Robot.java calls AutoBuilder.init(this) once, at the end of
// its constructor.
//
// Every subsystem on the Robot is reachable as AutoBuilder.robot.<name>
// (e.g. AutoBuilder.robot.drivetrain, AutoBuilder.robot.arm). Adding a
// subsystem to Robot.java is all it takes; there is nothing to register here.
// (2026's Autos.java took the drivetrain and sensors as parameters in every
// method, and every new subsystem meant changing every call.)
//
// Same as 1507Base's AutoBuilder, minus the policy driver (the XRP drives
// with simple commands, not routes).
//
// Students should NEVER instantiate this class; use the static field.
// ─────────────────────────────────────────────────────────────────────────────
public final class AutoBuilder {

    /** The robot, with every subsystem. Set once by Robot.java. */
    public static Robot robot;

    /** Called ONCE from the Robot.java constructor, after all subsystems are built. */
    public static void init(Robot robot) {
        AutoBuilder.robot = robot;
    }

    // Prevent instantiation — this is a static utility class.
    private AutoBuilder() {}
}
