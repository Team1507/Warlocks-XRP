//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot.subsystems;

import static org.wpilib.units.Units.Seconds;

import org.wpilib.command3.Command;
import org.wpilib.xrp.XRPServo;

import org.team1507.lib.core.framework.Subsystem1507;
import org.team1507.robot.Constants.RobotMap;
import org.team1507.robot.Constants.kArm;

// ─────────────────────────────────────────────────────────────────────────────
// Arm
//
// The servo arm on the XRP (servo port 4).
//
// ANGLE LIMITS:
//   Every angle request is clamped between the lower and upper limit, to keep
//   the servo inside the arm's safe range. (A servo pushing against something
//   it can't move gets hot and can strip its gears.) The limit lives here, in
//   the ONE method that moves the servo, so no command can forget it.
//
// NO POSITION SENSOR:
//   A hobby servo doesn't report where it is. getAngle() returns the angle we
//   last ASKED for, not where the arm really is. So goTo() can't wait for
//   "arrived"; it waits a fixed kArm.MOVE_SECONDS instead. (Check on the real XRP
//   that it's long enough for the full 50°-130° swing.)
//
// COMMANDS: goTo(deg), up(), down(), and wave(times), which you write in Lesson 6. The default (idle) does nothing, because a
// servo holds its last position by itself.
// ─────────────────────────────────────────────────────────────────────────────
public final class Arm extends Subsystem1507 {

    private final XRPServo servo = new XRPServo(RobotMap.ARM_SERVO);

    /** The last angle requested, before clamping (logged so you can see when the limits kicked in). */
    private double requestedDegrees = Double.NaN;

    public Arm() {
        super("Arm");
    }

    // =========================================================================
    // Control
    // =========================================================================

    /**
     * Moves the servo to {@code degrees}, clamped to the arm's limits.
     *
     * @param degrees desired angle [0–180]
     */
    public void setAngle(double degrees) {
        requestedDegrees = degrees;
        servo.setAngle(clamp(degrees));
    }

    /** The angle the servo was last told to go to (degrees). See the class comment. */
    public double getAngle() { return servo.getAngle(); }

    /** True if the servo was told to go within kArm.AT_ANGLE_TOLERANCE_DEG of {@code degrees} (after clamping). */
    public boolean isAtAngle(double degrees) {
        return Math.abs(getAngle() - clamp(degrees)) < kArm.AT_ANGLE_TOLERANCE_DEG;
    }

    /** {@code degrees}, limited to the arm's safe range. */
    public static double clamp(double degrees) {
        return Math.clamp(degrees, kArm.MIN_DEG, kArm.MAX_DEG);
    }

    // =========================================================================
    // Commands (Commands v3)
    // =========================================================================

    /**
     * Moves the arm to {@code degrees} (clamped to the limits), waits for the
     * servo to get there, then ends.
     */
    public Command goTo(double degrees) {
        return run(coroutine -> {
            setAngle(degrees);
            coroutine.wait(Seconds.of(kArm.MOVE_SECONDS));
        }).named("Arm.goTo");
    }

    /**
     * Waves the arm: up, then down, {@code times} times, then ends.
     */
    public Command wave(int times) {
        return run(coroutine -> {
            // LESSON 6: TODO. A loop that runs `times` times. Inside it, move the
            // arm up and wait for it, then down and wait for it.
        }).named("Arm.wave");
    }

    /** Raises the arm. */
    public Command up() {
        return goTo(kArm.UP_DEG);
    }

    /** Lowers the arm. */
    public Command down() {
        return goTo(kArm.DOWN_DEG);
    }

    // =========================================================================
    // Periodic
    // =========================================================================

    @Override
    public void periodic() {
        log("AngleDegrees",          getAngle());
        log("RequestedAngleDegrees", requestedDegrees);
    }
}
