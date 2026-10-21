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
// ForwardUntil10cmThenTurnTo60Auto
//
// Same as Forward Until 10cm Then Turn, but turns TO a heading instead of BY an
// amount. turnTo(60) means "face 60° left of the way the robot faced when the
// program started", wherever it is facing now. (Stops at 0.15 m, see
// ForwardUntil10cmThenTurnAuto for why.)
//
// NAME: 2027 limits OpMode names to 32 characters, and the 2026 name
// ("Forward Until 10cm Then Turn to 60") is 34, so "to" was dropped. The
// full old name is in the description.
// ─────────────────────────────────────────────────────────────────────────────
@Autonomous(
    name = "Forward Until 10cm Then Turn 60",
    description = "2026 name: Forward Until 10cm Then Turn to 60")
public final class ForwardUntil10cmThenTurnTo60Auto extends AutoOpMode {

    /**
     * Steps:
     *   1. Drive forward at 0.75 until something is 0.15 m away.
     *   2. Turn to face 60° (absolute heading).
     *   3. Wait until the wheels stop.
     */
    @Override
    protected Command build() {
        return new AutoSequence()
            .driveStraight(0.75).waitUntilRange(0.15)
            .turnTo(60)
            .waitUntilStopped()
            .build();
    }
}
