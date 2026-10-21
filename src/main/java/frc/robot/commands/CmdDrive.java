//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.XRPDrivetrain;
import frc.robot.subsystems.XRPSensors;

/**
 * Drives at a fixed speed while using a PID controller to maintain the heading
 * captured at command start.
 *
 * <p>This command never finishes on its own — wrap it in a
 * {@link edu.wpi.first.wpilibj2.command.ParallelRaceGroup} with a time or
 * distance condition to limit its duration.
 */
public class CmdDrive extends Command {

    private final XRPDrivetrain drivetrain;
    private final XRPSensors    sensors;
    private final double        speed;
    private double              targetHeading;

    private final PIDController headingController;

    public CmdDrive(XRPDrivetrain drivetrain, XRPSensors sensors, double speed) {
        this.drivetrain = drivetrain;
        this.sensors    = sensors;
        this.speed      = speed;

        headingController = new PIDController(0.002, 0.0, 0.001);
        headingController.enableContinuousInput(-180, 180);
        headingController.setTolerance(2.0);

        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        targetHeading = sensors.getHeading();
        headingController.reset();
        headingController.setSetpoint(targetHeading);
    }

    @Override
    public void execute() {
        double correction = headingController.calculate(sensors.getHeading());
        // Pass correction raw — arcadeDriveWithCorrection scales the forward speed
        // but NOT the correction, so a small PID output stays small instead of
        // being inflated to 65%+ and spinning the robot.
        drivetrain.arcadeDriveWithCorrection(speed, correction);
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.arcadeDrive(0, 0);
    }

    @Override
    public boolean isFinished() {
        return false; // ends only when the enclosing race group finishes
    }
}
