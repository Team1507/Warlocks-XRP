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
// TemplateAuto
//
// YOUR auto starts here. It already shows up in the Driver Station as
// "My Auto (Template)" and runs; it just doesn't do anything yet.
//
// To make it yours:
//   1. Rename the class (and this file) to say what the auto does, e.g. SquareAuto.
//   2. Change the @Autonomous name to what drivers should see in the drop-down
//      (32 characters at most).
//   3. Fill in build() below, one step per line, top to bottom.
//   4. Update the comment above build() so it lists your steps.
//   Need a second auto? Copy this file again. There's no list to register it in.
//
// ForwardAndBackAuto.java is a finished example to compare against.
//
// STEPS YOU CAN USE (see AutoSequence.java for details):
//   Drive steps keep the robot moving until the NEXT wait step finishes:
//     .driveStraight(speed)         drive, holding the starting heading (negative = backward)
//     .followLine(speed)            follow black tape
//   Wait steps end the drive step before them (or just wait):
//     .waitUntilTime(seconds)       until the auto clock reads this (0 = auto started)
//     .waitSeconds(seconds)         for this long
//     .waitUntilDistance(meters)    until the wheels travel this far
//     .waitUntilRange(meters)       until something is this close in front
//     .waitUntilMarker(threshold)   until both line sensors read above threshold
//   Actions (not in the middle of a drive):
//     .turnBy(degrees)              turn relative to now (+ = left)
//     .turnTo(degrees)              turn to face an absolute heading
//     .waitUntilStopped()           hold still until the wheels stop
//     .armUp() .armDown() .armTo(degrees)
//     .celebrate()                  a RobotBehaviors behavior
//     .stop()
//
// If you make a mistake (a drive that nothing stops, a turn in the middle of a
// drive), the build fails and tells you what to fix, before the robot moves.
// ─────────────────────────────────────────────────────────────────────────────
@Autonomous(name = "My Auto (Template)")
public final class TemplateAuto extends AutoOpMode {

    /**
     * Steps:
     *   TODO: list your steps here, e.g.
     *   1. Drive forward at 0.7 until the wheels have gone 0.5 m.
     *   2. Turn left 90°.
     *   3. Stop.
     */
    @Override
    protected Command build() {
        return new AutoSequence()
            // TODO: your steps go here, one per line. For example:
            //   .driveStraight(0.7).waitUntilDistance(0.5)
            //   .turnBy(90)
            .stop()
            .build();
    }
}
