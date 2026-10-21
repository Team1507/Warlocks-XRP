//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot.commands;

import org.wpilib.math.controller.PIDController;
import org.wpilib.command2.Command;
import org.team1507.robot.Constants;
import org.team1507.robot.subsystems.XRPDrivetrain;
import org.team1507.robot.subsystems.XRPSensors;

/**
 * Follows a line using the reflectance sensors.
 *
 * <p>A PID controller balances left/right reflectance to keep the robot
 * centered on the line. This command never finishes on its own — wrap it in
 * a {@link org.wpilib.command2.ParallelRaceGroup} with a marker
 * condition.
 */
public class CmdDriveLine extends Command {

    private final XRPDrivetrain drivetrain;
    private final XRPSensors    sensors;
    private final double        baseSpeed;
    private final PIDController lineController;

    public CmdDriveLine(XRPDrivetrain drivetrain, XRPSensors sensors, double baseSpeed) {
        this.drivetrain = drivetrain;
        this.sensors    = sensors;
        this.baseSpeed  = Math.max(Constants.Drive.MIN_SPEED, Math.abs(baseSpeed));

        lineController = new PIDController(0.5, 0.0, 0.05);
        lineController.setTolerance(0.05);

        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        lineController.reset();
    }

    @Override
    public void execute() {
        double error      = sensors.getRightReflectance() - sensors.getLeftReflectance();
        double correction = lineController.calculate(error, 0.0);

        // Speed up one side to steer rather than slow the other side down.
        // This keeps both wheels at or above baseSpeed so the robot never stalls.
        double leftSpeed  = Math.min(1.0, baseSpeed + Math.max(0,  correction));
        double rightSpeed = Math.min(1.0, baseSpeed + Math.max(0, -correction));
        drivetrain.tankDriveLinear(leftSpeed, rightSpeed);
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
