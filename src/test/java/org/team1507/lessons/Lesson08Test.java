//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.lessons;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.wpilib.command3.Command;
import org.wpilib.command3.Scheduler;

import org.team1507.lib.core.framework.AutoOpMode;
import org.team1507.robot.Robot;
import org.team1507.robot.TestRobot;
import org.team1507.robot.auto.routines.TemplateAuto;

// ─────────────────────────────────────────────────────────────────────────────
// Lesson08Test — checks Lesson 8 (Autonomous Routines). See the wiki.
//
// Runs your TemplateAuto in simulated time and checks that it makes the robot
// DO something (drive or move the arm), and that it finishes by itself.
//
// Run it: .\gradlew lessonTest --tests "*Lesson08*"
// ─────────────────────────────────────────────────────────────────────────────
@Tag("lesson")
class Lesson08Test {

    @AfterEach
    void cleanUp() {
        Scheduler.getDefault().cancelAll();
        TestRobot.resumeTime();
    }

    @Test
    void templateAutoMovesTheRobotAndFinishes() throws Exception {
        Robot robot = TestRobot.get();
        Scheduler scheduler = Scheduler.getDefault();
        scheduler.cancelAll();
        TestRobot.pauseTime();

        Method build = AutoOpMode.class.getDeclaredMethod("build");
        build.setAccessible(true);
        Command auto = (Command) build.invoke(new TemplateAuto());

        double startArm = robot.arm.getAngle();
        boolean drove = false;
        boolean movedArm = false;
        scheduler.schedule(auto);
        for (int i = 0; i < 1500 && scheduler.isScheduledOrRunning(auto); i++) {   // up to 30 s
            TestRobot.step();
            drove |= robot.drivetrain.getLeftThrottle() != 0 || robot.drivetrain.getRightThrottle() != 0;
            movedArm |= robot.arm.getAngle() != startArm;
        }

        assertTrue(drove || movedArm,
            "Lesson 8: TemplateAuto didn't make the robot do anything. Add steps to build().");
        assertFalse(scheduler.isScheduledOrRunning(auto),
            "Lesson 8: TemplateAuto was still running after 30 s. Every drive step needs a wait step after it.");
    }
}
