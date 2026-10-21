//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot.subsystems;

import org.wpilib.xrp.XRPServo;

/**
 * Subsystem wrapper for an XRP servo arm.
 *
 * <p>Angle requests are clamped between {@code lowerLimit} and
 * {@code upperLimit} to protect the servo from moving outside its safe range.
 * The current angle is published to SmartDashboard each scheduler cycle.
 */
public class XRPServoArm extends XRPSubsystem {

    private final XRPServo mServo;
    private final double upperLimit;
    private final double lowerLimit;

    /**
     * @param deviceNum  servo port on the XRP
     * @param upperLimit maximum allowed angle in degrees
     * @param lowerLimit minimum allowed angle in degrees
     */
    public XRPServoArm(int deviceNum, double upperLimit, double lowerLimit) {
        super("ServoArm");
        mServo = new XRPServo(deviceNum);
        this.upperLimit = upperLimit;
        this.lowerLimit = lowerLimit;
    }

    /**
     * Moves the servo to the requested angle, clamped to the configured limits.
     *
     * @param angle desired angle in degrees [0–180]
     */
    public void setArmAngle(double angle) {
        double clamped = Math.max(lowerLimit, Math.min(upperLimit, angle));
        mServo.setAngle(clamped);
    }

    /** @return current servo angle in degrees */
    public double getArmAngle() { return mServo.getAngle(); }

    /**
     * @param target target angle in degrees
     * @return true if the servo is within 2° of the target
     */
    public boolean isAtAngle(double target) {
        return Math.abs(getArmAngle() - target) < 2.0;
    }

    @Override
    public void periodic() {
        log("Angle (deg)", getArmAngle());
    }
}
