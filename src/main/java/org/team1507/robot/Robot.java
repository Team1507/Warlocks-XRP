//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot;

import org.wpilib.command3.button.CommandGamepad;

import org.team1507.lib.core.framework.LoggedRobot;
import org.team1507.robot.subsystems.Arm;
import org.team1507.robot.subsystems.Drivetrain;
import org.team1507.robot.subsystems.Sensors;

// ─────────────────────────────────────────────────────────────────────────────
// Robot
//
// Holds everything shared by all OpModes: subsystems, controllers, and each
// subsystem's safe default command. What the robot DOES in each mode lives in
// OpMode classes, which WPILib finds automatically and lists in the Driver
// Station:
//   - Autonomous: robot/auto/routines/  (@Autonomous, extend AutoOpMode)
//   - Teleop:     robot/teleop/         (@Teleop) — driver button bindings live here
//
// Commands v3 ties bindings and default commands to the OpMode that creates
// them, and removes them when a different OpMode is selected. So bindings go in
// the OpMode that uses them, and the defaults set here are what every
// subsystem falls back to when no OpMode overrides them.
//
// (2026: this file also had the auto chooser and autonomousInit/teleopInit.
// In 2027 each mode is its own class, so none of that is needed here.)
// ─────────────────────────────────────────────────────────────────────────────
public final class Robot extends LoggedRobot {

    // -------------------------------------------------------------------------
    // Subsystems
    // -------------------------------------------------------------------------

    public final Drivetrain drivetrain;
    public final Sensors    sensors;
    public final Arm        arm;

    // -------------------------------------------------------------------------
    // Controllers
    // -------------------------------------------------------------------------

    public final CommandGamepad driver;

    // =========================================================================
    // Constructor
    // =========================================================================

    public Robot() {

        // Subsystems
        drivetrain = new Drivetrain();
        sensors    = new Sensors();
        arm        = new Arm();

        // Safe defaults: what each subsystem does when no command is using it.
        // OpModes can override these (DriverTeleop sets joystick driving).
        // Drivetrain.idle() sets the motors to 0; the arm's idle leaves the servo
        // where it is (a servo holds its position by itself).
        drivetrain.setDefaultCommand(drivetrain.idle());
        arm.setDefaultCommand(arm.idle());

        // Controllers (button bindings are made in the OpModes, e.g. DriverTeleop).
        // CommandGamepad replaced CommandXboxController in 2027: buttons are named
        // by position (faceDown is A on an Xbox pad), so any gamepad works.
        driver = new CommandGamepad(Constants.M_CONTROLLER_PORT);
    }
}
