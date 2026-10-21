//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot.auto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.team1507.robot.TestRobot;

// ─────────────────────────────────────────────────────────────────────────────
// AutoSequenceTest
//
// AutoSequence promises to refuse a broken routine at build(), before the
// robot moves. These tests hold it to that promise.
// ─────────────────────────────────────────────────────────────────────────────
class AutoSequenceTest {

    @BeforeAll
    static void robot() {
        TestRobot.get();   // AutoSequence reaches the subsystems through AutoBuilder
    }

    @Test
    void driveWithAWaitBuilds() {
        assertNotNull(new AutoSequence().driveStraight(0.5).waitSeconds(1).build());
    }

    @Test
    void driveThatNothingStopsIsRefused() {
        IllegalStateException e = assertThrows(IllegalStateException.class,
            () -> new AutoSequence().driveStraight(0.5).build());
        assertTrue(e.getMessage().contains("nothing would ever stop it"), e.getMessage());
    }

    @Test
    void actionInTheMiddleOfADriveIsRefused() {
        IllegalStateException e = assertThrows(IllegalStateException.class,
            () -> new AutoSequence().driveStraight(0.5).turnBy(90).build());
        assertTrue(e.getMessage().contains("still driving"), e.getMessage());
    }

    @Test
    void twoDrivesInARowAreRefused() {
        IllegalStateException e = assertThrows(IllegalStateException.class,
            () -> new AutoSequence().driveStraight(0.5).followLine(0.5).waitSeconds(1).build());
        assertTrue(e.getMessage().contains("never stops"), e.getMessage());
    }
}
