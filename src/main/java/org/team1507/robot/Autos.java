//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot;

// WPI libraries
import org.wpilib.command2.ParallelRaceGroup;
import org.wpilib.command2.SequentialCommandGroup;

// Subsystems
import org.team1507.robot.subsystems.XRPDrivetrain;
import org.team1507.robot.subsystems.XRPSensors;

// Custom Commands
import org.team1507.robot.commands.CmdDrive;
import org.team1507.robot.commands.CmdDriveLine;
import org.team1507.robot.commands.CmdWaitUntilDistance;
import org.team1507.robot.commands.CmdWaitUntilMarker;
import org.team1507.robot.commands.CmdWaitUntilRange;
import org.team1507.robot.commands.CmdTurnByAngle;
import org.team1507.robot.commands.CmdTurnToAngle;
import org.team1507.robot.commands.CmdWaitUntilStopped;
import org.team1507.robot.commands.CmdWaitUntilTime;

public final class Autos {
    /**
     * Drives forward for 1 second, then backward for 1 second.
     *
     * <p>Forward motion is timed for 1 second at 0.8 speed,
     * followed by backward motion for 1 second at -0.8 speed.
     *
     * <p>Useful as a simple demo of sequential autonomous movement.
     */
    public static SequentialCommandGroup forwardAndBack(XRPDrivetrain drivetrain, XRPSensors sensors) {
        return new SequentialCommandGroup(
            new ParallelRaceGroup(
                new CmdDrive(drivetrain, sensors, 0.8),
                new CmdWaitUntilTime(1)
            ),
            new ParallelRaceGroup(
                new CmdDrive(drivetrain, sensors, -0.8),
                new CmdWaitUntilTime(1)
            )
        );
    }

    /**
     * Drives forward until 0.5 m distance is reached, then backward until 0.1 m.
     *
     * <p>Uses encoder distance instead of time to stop the drivetrain.
     * Demonstrates sensor‑based autonomous control.
     */
    public static SequentialCommandGroup forwardAndBackDistance(XRPDrivetrain drivetrain, XRPSensors sensors) {
        return new SequentialCommandGroup(
            new ParallelRaceGroup(
                new CmdDrive(drivetrain, sensors, 0.7),
                new CmdWaitUntilDistance(drivetrain, 0.5)
            ),
            new ParallelRaceGroup(
                new CmdDrive(drivetrain, sensors, -0.7),
                new CmdWaitUntilDistance(drivetrain, -0.1)
            )
        );
    }

    /**
     * Drives forward until 15 cm from an obstacle, then turns 180 degrees.
     *
     * <p>Uses range sensors to stop, then performs a turn‑by‑angle
     * followed by a wait until the drivetrain is stopped.
     */
    public static SequentialCommandGroup forwardUntil10cmThenTurn(XRPDrivetrain drivetrain, XRPSensors sensors) {
        return new SequentialCommandGroup(
            new ParallelRaceGroup(
                new CmdDrive(drivetrain, sensors, 0.75),
                new CmdWaitUntilRange(sensors, 0.15)
            ),
            new CmdTurnByAngle(drivetrain, sensors, 180),
            new CmdWaitUntilStopped(drivetrain)
        );
    }

    /**
     * Drives forward until 15 cm from an obstacle, then turns to 60 degrees.
     *
     * <p>Similar to forwardUntil10cmThenTurn, but uses a turn‑to‑angle
     * command to reach a specific heading.
     */
    public static SequentialCommandGroup forwardUntil10cmThenTurnToAngle(XRPDrivetrain drivetrain, XRPSensors sensors) {
        return new SequentialCommandGroup(
            new ParallelRaceGroup(
                new CmdDrive(drivetrain, sensors, 0.75),
                new CmdWaitUntilRange(sensors, 0.15)
            ),
            new CmdTurnToAngle(drivetrain, sensors, 60),
            new CmdWaitUntilStopped(drivetrain)
        );
    }

    /**
     * Follows a line until both sensors detect tape.
     *
     * <p>Uses CmdDriveLine for line following and CmdWaitUntilMarker
     * to stop when the marker condition is met.
     */
    public static SequentialCommandGroup followLine(XRPDrivetrain drivetrain, XRPSensors sensors) {
        return new SequentialCommandGroup(
            new ParallelRaceGroup(
                new CmdDriveLine(drivetrain, sensors, 0.45),
                new CmdWaitUntilMarker(sensors, 0.75)
            )
        );
    }    
}
