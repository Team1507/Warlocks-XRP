//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot.teleop;

import org.wpilib.math.util.MathUtil;
import org.wpilib.opmode.OpMode;
import org.wpilib.opmode.Teleop;

import org.team1507.robot.Robot;
import org.team1507.robot.RobotBehaviors;

import static org.team1507.robot.Constants.kDrivetrain.kTuning.DRIVER_DEADBAND;

// ─────────────────────────────────────────────────────────────────────────────
// DriverTeleop
//
// The teleop mode (2027 OpMode framework, Commands v3). The Driver Station
// lists every @Teleop class in its teleop drop-down.
//
// Everything is set up in the constructor, which WPILib runs when this mode is
// SELECTED on the Driver Station. Commands v3 ties the default command and the
// button bindings made here to this OpMode: they work while it is selected
// (enabled or disabled) and are removed automatically when another OpMode is
// selected. There is nothing to clean up.
//
// CONTROLS
//   Left stick       arcade drive (up/down = forward/back, left/right = turn)
//   Left bumper      arm up
//   Right bumper     arm down
//   A (faceDown)     celebrate: arm up while turning around, then arm down
//   Back             failsafe: interrupt everything (see RobotBehaviors)
// ─────────────────────────────────────────────────────────────────────────────
@Teleop(name = "Driver")
public final class DriverTeleop implements OpMode {

    /** WPILib passes in the Robot instance when this mode is selected. */
    public DriverTeleop(Robot robot) {

        // ── Driving ────────────────────────────────────────────────────────

        // Joystick driving replaces the drivetrain's idle default while this mode
        // is selected. Sticks never rest at exactly 0, so DRIVER_DEADBAND
        // (Constants) treats small values as 0. The deadband is applied FIRST, before anything else scales
        // the value, so drift is removed instead of being amplified.
        //
        // Both axes are negated. A gamepad reads up as negative Y and right as
        // positive X, while the drivetrain wants forward and counterclockwise
        // (left) as positive. (The 2026 code didn't negate X, so pushing right
        // turned the robot left.)
        robot.drivetrain.setDefaultCommand(
            robot.drivetrain.drive(
                () -> MathUtil.applyDeadband(-robot.driver.getLeftY(), DRIVER_DEADBAND),
                () -> MathUtil.applyDeadband(-robot.driver.getLeftX(), DRIVER_DEADBAND)
            )
        );

        // ── Arm ────────────────────────────────────────────────────────────

        robot.driver.leftBumper().onTrue(robot.arm.up());
        robot.driver.rightBumper().onTrue(robot.arm.down());

        // ── Behaviors (multi-subsystem, see RobotBehaviors) ────────────────

        // The same behavior the DriveAndCelebrate auto uses: one definition.
        robot.driver.faceDown().onTrue(RobotBehaviors.celebrate(robot));

        // ── Failsafe ───────────────────────────────────────────────────────

        // Interrupts every subsystem command (see RobotBehaviors for details)
        robot.driver.back().onTrue(RobotBehaviors.failsafe());
    }
}
