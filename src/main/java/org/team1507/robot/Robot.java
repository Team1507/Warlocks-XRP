//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot;

import org.wpilib.math.util.MathUtil;
import org.wpilib.smartdashboard.SendableChooser;
import org.wpilib.smartdashboard.SmartDashboard;
import org.wpilib.command2.Command;
import org.wpilib.command2.RunCommand;
import org.wpilib.command2.button.CommandXboxController;

import static org.team1507.robot.Constants.*;
import static org.team1507.robot.Constants.ServoArm.*;

import org.team1507.robot.commands.CmdServoAngle;
import org.team1507.robot.subsystems.XRPDrivetrain;
import org.team1507.robot.subsystems.XRPSensors;
import org.team1507.robot.subsystems.XRPServoArm;

/**
 * Main robot class — subsystems, controller bindings, and autonomous selection
 * all live here, matching the 1507Base single-class pattern.
 *
 * <p>Extends {@link LoggedRobot} so all robot data (warnings, errors, DS events)
 * is captured in a DataLog file for post-run review.
 */
public final class Robot extends LoggedRobot {

    // -------------------------------------------------------------------------
    // Subsystems
    // -------------------------------------------------------------------------

    public final XRPDrivetrain drivetrain = new XRPDrivetrain();
    public final XRPSensors    sensors    = new XRPSensors();
    public final XRPServoArm   servoArm   = new XRPServoArm(SERVO_ARM_PORT, UPPER_LIMIT, LOWER_LIMIT);

    // -------------------------------------------------------------------------
    // Controllers
    // -------------------------------------------------------------------------

    private final CommandXboxController driver = new CommandXboxController(M_CONTROLLER_PORT);

    // -------------------------------------------------------------------------
    // Autonomous
    // -------------------------------------------------------------------------

    private Command m_autoCommand = null;
    private final SendableChooser<Command> autoChooser = new SendableChooser<>();

    // =========================================================================
    // Constructor
    // =========================================================================

    public Robot() {
        configureAutos();
        configureBindings();
        configureDefaultBindings();
    }

    // =========================================================================
    // Bindings
    // =========================================================================

    private void configureBindings() {
        driver.leftBumper().onTrue(new CmdServoAngle(servoArm, ARM_UP));
        driver.rightBumper().onTrue(new CmdServoAngle(servoArm, ARM_DOWN));
    }

    private void configureDefaultBindings() {
        drivetrain.setDefaultCommand(
            new RunCommand(
                () -> {
                    // Apply deadband before scaling so stick drift doesn't move the robot
                    double x   = MathUtil.applyDeadband(-driver.getLeftY(),  0.12);
                    double rot = MathUtil.applyDeadband( driver.getLeftX(),  0.12);
                    drivetrain.arcadeDrive(x, rot);
                },
                drivetrain
            )
        );
    }

    private void configureAutos() {
        autoChooser.setDefaultOption("Forward and Back",
            Autos.forwardAndBack(drivetrain, sensors));
        autoChooser.addOption("Forward and Back Distance",
            Autos.forwardAndBackDistance(drivetrain, sensors));
        autoChooser.addOption("Forward Until 10cm Then Turn",
            Autos.forwardUntil10cmThenTurn(drivetrain, sensors));
        autoChooser.addOption("Forward Until 10cm Then Turn to 60",
            Autos.forwardUntil10cmThenTurnToAngle(drivetrain, sensors));
        autoChooser.addOption("Follow Line",
            Autos.followLine(drivetrain, sensors));

        SmartDashboard.putData("Auto choices", autoChooser);
    }

    // =========================================================================
    // Mode callbacks
    // =========================================================================

    @Override
    public void autonomousInit() {
        m_autoCommand = autoChooser.getSelected();
        if (m_autoCommand != null) {
            m_autoCommand.schedule();
        }
    }

    @Override
    public void teleopInit() {
        if (m_autoCommand != null) {
            m_autoCommand.cancel();
        }
    }

    @Override
    public void utilityInit() {
        org.wpilib.command2.CommandScheduler.getInstance().cancelAll();
    }
}
