//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╔╝╚╝  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.XRPDrivetrain;
import frc.robot.subsystems.XRPSensors;

/**
 * Turns the robot to an absolute heading (e.g. 60°) using a proportional
 * controller.
 *
 * <p>Uses {@link XRPDrivetrain#turnInPlace} to bypass the drivetrain's
 * scaleMotorOutput, which would otherwise map any non-zero correction to ≥65%
 * and make precise settling impossible. A dead zone of ±{@value #TOLERANCE_DEG}°
 * stops forcing rotation near the target so the robot can coast to rest.
 */
public class CmdTurnToAngle extends Command {

    private static final double TOLERANCE_DEG  = 5.0;
    private static final double KP             = 0.015;
    private static final double MIN_TURN_SPEED = 0.5;

    private final XRPDrivetrain drivetrain;
    private final XRPSensors    sensors;
    private final double        targetAngle;

    public CmdTurnToAngle(XRPDrivetrain drivetrain, XRPSensors sensors, double targetAngle) {
        this.drivetrain  = drivetrain;
        this.sensors     = sensors;
        this.targetAngle = targetAngle;
        addRequirements(drivetrain);
    }

    @Override
    public void execute() {
        double error = wrappedError(targetAngle, sensors.getHeading());

        if (Math.abs(error) < TOLERANCE_DEG) {
            drivetrain.turnInPlace(0);
            return;
        }

        double speed = MathUtil.clamp(KP * error, -0.9, 0.9);
        // Ensure enough duty cycle to overcome static friction
        if (Math.abs(speed) < MIN_TURN_SPEED) {
            speed = Math.copySign(MIN_TURN_SPEED, speed);
        }
        drivetrain.turnInPlace(speed);
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.turnInPlace(0);
    }

    @Override
    public boolean isFinished() {
        return Math.abs(wrappedError(targetAngle, sensors.getHeading())) < TOLERANCE_DEG;
    }

    /** Wraps the error into [-180, 180] so the robot always takes the short path. */
    private static double wrappedError(double target, double current) {
        double error = target - current;
        return ((error + 180) % 360) - 180;
    }
}
