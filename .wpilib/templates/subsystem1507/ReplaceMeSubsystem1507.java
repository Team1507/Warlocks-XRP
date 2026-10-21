//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.wpilib.commands.subsystem1507;

import org.wpilib.command3.Command;
import org.team1507.lib.core.framework.Subsystem1507;

// ─────────────────────────────────────────────────────────────────────────────
// ReplaceMeSubsystem1507
//
// TODO: one or two sentences: what this mechanism is, and WHY it is its own
// subsystem (what would go wrong if two commands used it at once?).
//
// After creating it:
//   1. Add a field and construct it in Robot.java (subsystems section).
//   2. Set its default command there: replaceMe.setDefaultCommand(replaceMe.idle());
//   3. Put its port in Constants.RobotMap and its numbers in a kReplaceMe class.
// ─────────────────────────────────────────────────────────────────────────────
public class ReplaceMeSubsystem1507 extends Subsystem1507 {

  // TODO: Declare hardware as private fields, using ports from Constants.RobotMap, e.g.
  // private final XRPServo servo = new XRPServo(RobotMap.MY_SERVO);
  // private final XRPMotor motor = new XRPMotor(RobotMap.MY_MOTOR);   // XRP motor channels 2 or 3

  /** Creates a new ReplaceMeSubsystem1507. */
  public ReplaceMeSubsystem1507() {
    super("ReplaceMeSubsystem1507");
  }

  // ---- Commands (Commands v3) ----
  //
  // Every command needs a name: .named("ReplaceMeSubsystem1507.action")
  // runRepeatedly(...) runs every loop until interrupted (it yields for you).
  // run(coroutine -> ...) runs top to bottom; call coroutine.yield() in every
  // loop, or the whole robot program freezes.

  /** Runs until interrupted (bind with whileTrue). */
  public Command exampleRunCommand() {
    return runRepeatedly(() -> {
          // TODO: set the output every loop, e.g. motor.setThrottle(0.5);
        })
        .whenCanceled(() -> {
          // TODO: stop, e.g. motor.setThrottle(0);
        })
        .named("ReplaceMeSubsystem1507.run");
  }

  /** Moves to a target and finishes when it gets there (bind with onTrue). */
  public Command exampleMoveCommand() {
    return run(coroutine -> {
          // TODO: e.g. servo.setAngle(90);
          // coroutine.waitUntil(this::isThere);   // or coroutine.wait(Seconds.of(0.5)) with no sensor
        })
        .named("ReplaceMeSubsystem1507.move");
  }

  /** What this subsystem does when no command is using it. Set it as the default in Robot.java. */
  @Override
  public Command idle() {
    return run(coroutine -> {
          // TODO: stop, e.g. motor.setThrottle(0);
          coroutine.park();
        })
        .withPriority(Command.LOWEST_PRIORITY)
        .named("ReplaceMeSubsystem1507.idle");
  }

  @Override
  public void periodic() {
    // Runs every loop, before commands.
    // TODO: Log telemetry, e.g. log("AngleDegrees", servo.getAngle());
    // TODO: Flag problems, e.g. warnIf(servo.getAngle() > 170, "Servo near its end stop");
  }
}
