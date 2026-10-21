//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot.auto.routines;

import org.wpilib.command3.Command;
import org.wpilib.opmode.Autonomous;

import org.team1507.lib.core.framework.AutoOpMode;
import org.team1507.robot.auto.AutoSequence;

// ─────────────────────────────────────────────────────────────────────────────
// DriveAndCelebrateAuto
//
// Shows a RobotBehaviors behavior used as an auto step. The A button in
// DriverTeleop runs the exact same celebrate() behavior.
// ─────────────────────────────────────────────────────────────────────────────
@Autonomous(name = "Drive and Celebrate")
public final class DriveAndCelebrateAuto extends AutoOpMode {

    /**
     * Steps:
     *   1. Drive forward at 0.7 until the wheels have gone 0.3 m.
     *   2. Celebrate: arm up while turning around, then arm down.
     */
    @Override
    protected Command build() {
        return new AutoSequence()
            .driveStraight(0.7).waitUntilDistance(0.3)
            .celebrate()
            .build();
    }
}
