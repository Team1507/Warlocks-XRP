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
// ForwardAndBackAuto
//
// The example auto routine: drive forward for 1 second, then backward for 1
// second. It's finished and working, so use it as the reference for how a
// routine file should look.
//
// To write your own, start from TemplateAuto.java (same folder). The Driver
// Station lists every @Autonomous class automatically: there is no chooser to
// add it to. (2026: you had to add every auto to a chooser in Robot.java.)
// ─────────────────────────────────────────────────────────────────────────────
@Autonomous(name = "Forward and Back")
public final class ForwardAndBackAuto extends AutoOpMode {

    /**
     * Steps:
     *   1. Drive forward at 0.8 until the auto clock reads 1 s.
     *   2. Drive backward at 0.8 until it reads 2 s (one more second).
     *   3. Stop.
     */
    @Override
    protected Command build() {
        return new AutoSequence()
            .driveStraight(0.8).waitUntilTime(1.0)
            .driveStraight(-0.8).waitUntilTime(2.0)
            .stop()
            .build();
    }
}
