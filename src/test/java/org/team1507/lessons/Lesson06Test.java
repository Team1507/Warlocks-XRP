//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.lessons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.wpilib.command3.Command;
import org.wpilib.command3.Scheduler;

import org.team1507.robot.TestRobot;
import org.team1507.robot.subsystems.Arm;

import static org.team1507.robot.Constants.kArm.DOWN_DEG;
import static org.team1507.robot.Constants.kArm.UP_DEG;

// ─────────────────────────────────────────────────────────────────────────────
// Lesson06Test — checks Lesson 6 (Commands). See the wiki.
//
// Runs your arm.wave(2) command loop by loop and watches the arm: it should go
// up and down twice, take its time (wait for the servo), and then finish.
//
// Run it: .\gradlew lessonTest --tests "*Lesson06*"
// ─────────────────────────────────────────────────────────────────────────────
@Tag("lesson")
class Lesson06Test {

    @AfterEach
    void cleanUp() {
        Scheduler.getDefault().cancelAll();
        TestRobot.resumeTime();
    }

    @Test
    void waveGoesUpAndDownTwiceThenEnds() {
        Arm arm = TestRobot.get().arm;
        Scheduler scheduler = Scheduler.getDefault();
        scheduler.cancelAll();
        TestRobot.pauseTime();
        arm.setAngle(DOWN_DEG);

        Command wave = arm.wave(2);
        scheduler.schedule(wave);

        int ups = 0;
        int downs = 0;
        double last = arm.getAngle();
        int loops = 0;
        while (scheduler.isScheduledOrRunning(wave) && loops < 500) {   // 500 loops = 10 s
            TestRobot.step();
            loops++;
            double now = arm.getAngle();
            if (now != last) {
                if (Math.abs(now - UP_DEG) < 1) ups++;
                if (Math.abs(now - DOWN_DEG) < 1) downs++;
                last = now;
            }
        }

        assertFalse(scheduler.isScheduledOrRunning(wave), "Lesson 6: wave(2) should finish by itself (it ran 10 s)");
        assertEquals(2, ups, "Lesson 6: wave(2) should move the arm UP 2 times");
        assertEquals(2, downs, "Lesson 6: wave(2) should move the arm DOWN 2 times");
        assertTrue(loops >= 4 * 20, "Lesson 6: that was too fast for a real servo. "
            + "Wait for each move to finish (e.g. coroutine.await(up())) before the next one.");
    }
}
