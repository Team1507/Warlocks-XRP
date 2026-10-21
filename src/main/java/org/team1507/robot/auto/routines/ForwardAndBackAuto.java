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
// The simplest auto routine: drive forward for 1 second, then backward for 1
// second. Use this as the reference for how every routine file should look.
//
// To create a new routine:
//   1. Copy this file into the routines/ folder.
//   2. Rename the class, change the @Autonomous name, and edit build()'s steps.
//   That's it — the Driver Station lists every @Autonomous class automatically.
//   (2026: you also had to add it to the chooser in Robot.java. Not any more.)
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
