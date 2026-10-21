//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package frc.robot.commands;

// WPILib core libraries
import edu.wpi.first.wpilibj2.command.Command;

// Robot subsystems
import frc.robot.subsystems.XRPServoArm;

/**
 * Command to move the XRPServoArm to a specified angle.
 *
 * <p>Declares the servo arm subsystem as a requirement so that no other
 * command can use it while this one is scheduled.
 */
public class CmdServoAngle extends Command {
  public final XRPServoArm servoArm;
  public final double angle;

  /**
   * Constructs a new CmdServoAngle.
   *
   * @param inServos The servo arm subsystem to control.
   * @param inAngle  The target angle in degrees.
   */
  public CmdServoAngle(XRPServoArm inServos, double inAngle) {
    this.servoArm = inServos;
    this.angle = inAngle;

    // Prevents other commands from using the servo arm at the same time
    addRequirements(servoArm);
  }

  /** 
   * Sets the servo to the target angle once when the command starts. 
   */
  @Override
  public void initialize() {
    servoArm.setArmAngle(angle);
  }

  /** 
   * Ends the command when the servo is close enough to the target angle. 
   */
  @Override
  public boolean isFinished() {
    return servoArm.isAtAngle(angle);
  }
}