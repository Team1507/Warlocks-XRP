//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

import static frc.robot.Constants.*;
import static frc.robot.Constants.ServoArm.*;

import frc.robot.commands.CmdServoAngle;
import frc.robot.subsystems.XRPDrivetrain;
import frc.robot.subsystems.XRPSensors;
import frc.robot.subsystems.XRPServoArm;

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
    public void testInit() {
        edu.wpi.first.wpilibj2.command.CommandScheduler.getInstance().cancelAll();
    }
}
