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
// ForwardAndBackDistanceAuto
//
// Like Forward and Back, but stops by distance (wheel encoders) instead of
// time, so a weak battery doesn't change how far it goes.
// ─────────────────────────────────────────────────────────────────────────────
@Autonomous(name = "Forward and Back Distance")
public final class ForwardAndBackDistanceAuto extends AutoOpMode {

    /**
     * Steps:
     *   1. Drive forward at 0.7 until the wheels have gone 0.5 m.
     *   2. Drive backward at 0.7 until they have gone 0.1 m back.
     *      (2026 wrote -0.1 here; the distance counts either direction, so it's
     *      the same thing.)
     *   3. Stop.
     */
    @Override
    protected Command build() {
        return new AutoSequence()
            .driveStraight(0.7).waitUntilDistance(0.5)
            .driveStraight(-0.7).waitUntilDistance(0.1)
            .stop()
            .build();
    }
}
