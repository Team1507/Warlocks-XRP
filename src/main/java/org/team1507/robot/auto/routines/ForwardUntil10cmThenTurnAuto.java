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
// ForwardUntil10cmThenTurnAuto
//
// Drives toward a wall until the rangefinder sees it, then turns around.
//
// The name says 10 cm but the robot stops at 15 cm (0.15 m), exactly like the
// 2026 auto of the same name: the extra 5 cm is room to coast and to turn
// without hitting the wall. The name is kept so it matches what drivers know.
// ─────────────────────────────────────────────────────────────────────────────
@Autonomous(name = "Forward Until 10cm Then Turn")
public final class ForwardUntil10cmThenTurnAuto extends AutoOpMode {

    /**
     * Steps:
     *   1. Drive forward at 0.75 until something is 0.15 m away.
     *   2. Turn around (180°, relative to where it faces).
     *   3. Wait until the wheels stop.
     */
    @Override
    protected Command build() {
        return new AutoSequence()
            .driveStraight(0.75).waitUntilRange(0.15)
            .turnBy(180)
            .waitUntilStopped()
            .build();
    }
}
