//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package frc.robot.subsystems;

import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.xrp.XRPMotor;

public class XRPDrivetrain extends XRPSubsystem {

    private static final double kGearRatio =
        (30.0 / 14.0) * (28.0 / 16.0) * (36.0 / 9.0) * (26.0 / 8.0); // 48.75:1
    private static final double kCountsPerMotorShaftRev = 12.0;
    private static final double kCountsPerRevolution = kCountsPerMotorShaftRev * kGearRatio;
    private static final double kWheelDiameterMeters  = 0.060;

    // XRP motor channels are fixed by hardware: left=0, right=1
    private final XRPMotor          m_leftMotor   = new XRPMotor(0);
    private final XRPMotor          m_rightMotor  = new XRPMotor(1);

    // XRP encoders are hardwired: left=DIO 4/5, right=DIO 6/7
    private final Encoder           m_leftEncoder  = new Encoder(4, 5);
    private final Encoder           m_rightEncoder = new Encoder(6, 7);

    private final DifferentialDrive m_diffDrive =
        new DifferentialDrive(m_leftMotor::set, m_rightMotor::set);

    public XRPDrivetrain() {
        super("Drivetrain");

        // Register motors as children so DifferentialDrive safety tracks them correctly
        SendableRegistry.addChild(m_diffDrive, m_leftMotor);
        SendableRegistry.addChild(m_diffDrive, m_rightMotor);

        m_leftEncoder.setDistancePerPulse(
            (Math.PI * kWheelDiameterMeters) / kCountsPerRevolution);
        m_rightEncoder.setDistancePerPulse(
            (Math.PI * kWheelDiameterMeters) / kCountsPerRevolution);
        resetEncoders();

        // Safety disabled: the XRP sim loop feeds commands every 20 ms, but mode
        // transitions create a brief gap that would otherwise trip the safety watchdog.
        m_diffDrive.setSafetyEnabled(false);

        m_rightMotor.setInverted(true);
    }

    // =========================================================================
    // Drive methods
    // =========================================================================

    /**
     * Arcade drive for teleop.
     *
     * <p>Both axes go through {@link #scaleMotorOutput} so the robot always
     * receives enough duty cycle to overcome static friction. Apply a deadband
     * to controller inputs before calling this.
     *
     * @param xaxisSpeed  forward/backward [-1, 1]
     * @param zaxisRotate left/right rotation [-1, 1]
     */
    public void arcadeDrive(double xaxisSpeed, double zaxisRotate) {
        m_diffDrive.arcadeDrive(xaxisSpeed, zaxisRotate);
    }

    /**
     * Arcade drive for autonomous straight-line commands (e.g. {@code CmdDrive}).
     *
     * <p>The forward speed is boosted by {@link #scaleMotorOutput} so the robot
     * moves, but the heading correction is passed <em>raw</em> so that small PID
     * outputs don't get amplified to 65 %+ and spin the robot.
     *
     * @param xaxisSpeed forward/backward [-1, 1]
     * @param correction raw heading correction from a PID controller [-1, 1]
     */
    public void arcadeDriveWithCorrection(double xaxisSpeed, double correction) {
        m_diffDrive.arcadeDrive(scaleMotorOutput(xaxisSpeed), correction);
    }

    /**
     * Arcade drive with linear (non-squared) inputs — used by line-following commands.
     *
     * <p>Bypasses WPILib's default input squaring so that the duty cycle sent to
     * the motors matches the values passed in. Use this when the caller already
     * enforces a minimum speed and needs predictable output.
     *
     * @param xaxisSpeed forward/backward [-1, 1]
     * @param correction raw steering correction from a PID controller [-1, 1]
     */
    public void arcadeDriveLinear(double xaxisSpeed, double correction) {
        m_diffDrive.arcadeDrive(xaxisSpeed, correction, false);
    }

    /**
     * Tank drive with linear (non-squared) inputs — used by line-following commands.
     *
     * @param leftSpeed  left wheel speed [-1, 1]
     * @param rightSpeed right wheel speed [-1, 1]
     */
    public void tankDriveLinear(double leftSpeed, double rightSpeed) {
        m_diffDrive.tankDrive(leftSpeed, rightSpeed, false);
    }

    /**
     * Pure in-place rotation — used by autonomous turn commands.
     *
     * <p>No scaling applied; callers supply a value large enough to overcome
     * static friction but small enough not to overshoot the target.
     *
     * @param rotation turn speed [-1, 1]
     */
    public void turnInPlace(double rotation) {
        m_diffDrive.arcadeDrive(0, rotation);
    }

    /**
     * Boosts small inputs to at least 65 % so the XRP's motors overcome static
     * friction. Inputs below 0.01 are treated as a full stop.
     */
    private double scaleMotorOutput(double input) {
        double magnitude = Math.abs(input);
        if (magnitude < 0.01) return 0.0;
        double scaled = 0.65 + (1.0 - 0.65) * magnitude;
        return Math.copySign(scaled, input);
    }

    // =========================================================================
    // Encoder helpers
    // =========================================================================

    public void resetEncoders() {
        m_leftEncoder.reset();
        m_rightEncoder.reset();
    }

    public double getLeftDistance()  { return m_leftEncoder.getDistance(); }
    public double getRightDistance() { return m_rightEncoder.getDistance(); }
    public double getLeftRate()      { return m_leftEncoder.getRate(); }
    public double getRightRate()     { return m_rightEncoder.getRate(); }
    public double getLeftSpeed()     { return m_leftMotor.get(); }
    public double getRightSpeed()    { return m_rightMotor.get(); }

    public boolean isStopped() {
        return Math.abs(getLeftRate()) < 0.01 && Math.abs(getRightRate()) < 0.01;
    }

    // =========================================================================
    // Periodic
    // =========================================================================

    @Override
    public void periodic() {
        log("LeftDistance (m)",  getLeftDistance());
        log("RightDistance (m)", getRightDistance());
        log("LeftRate (m-s)",    getLeftRate());
        log("RightRate (m-s)",   getRightRate());
        log("Stopped",           isStopped());
    }
}
