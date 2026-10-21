//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.lessons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.wpilib.command3.Command;
import org.wpilib.command3.Scheduler;

import org.team1507.robot.Robot;
import org.team1507.robot.RobotBehaviors;
import org.team1507.robot.TestRobot;

// ─────────────────────────────────────────────────────────────────────────────
// Lesson11Test — checks Lesson 11 (Following a Line). See the wiki.
//
// Pretends the line sensors see the line on one side or the other, runs your
// line follower for a moment, and checks which way it steers.
//
// This uses your Lesson 4 methods (isLeftOnLine, ...). If it fails, first
// check that Lesson 4 passes.
//
// Run it: .\gradlew lessonTest --tests "*Lesson11*"
// ─────────────────────────────────────────────────────────────────────────────
@Tag("lesson")
class Lesson11Test {

    private Robot robot;
    private Command follow;

    @BeforeEach
    void start() {
        robot = TestRobot.get();
        Scheduler.getDefault().cancelAll();
        TestRobot.pauseTime();
        follow = RobotBehaviors.followLine(robot, 0.5);
        Scheduler.getDefault().schedule(follow);
    }

    @AfterEach
    void cleanUp() {
        Scheduler.getDefault().cancelAll();
        TestRobot.resumeTime();
    }

    /** Left wheel's forward power. */
    private double left() {
        return robot.drivetrain.getLeftThrottle();
    }

    /** Right wheel's forward power. (The right motor is inverted, so forward reads negative.) */
    private double right() {
        return -robot.drivetrain.getRightThrottle();
    }

    @Test
    void lineUnderTheLeftSensorSteersLeft() {
        LessonSim.setLine(1.0, 0.0);
        TestRobot.step(2);
        assertTrue(right() > left(),
            "Lesson 11: the line is under the LEFT sensor, so steer LEFT: the right wheel should go faster "
            + "than the left. Left = " + left() + ", right = " + right());
    }

    @Test
    void lineUnderTheRightSensorSteersRight() {
        LessonSim.setLine(0.0, 1.0);
        TestRobot.step(2);
        assertTrue(left() > right(),
            "Lesson 11: the line is under the RIGHT sensor, so steer RIGHT: the left wheel should go faster "
            + "than the right. Left = " + left() + ", right = " + right());
    }

    @Test
    void lineUnderBothDrivesStraightForward() {
        LessonSim.setLine(1.0, 1.0);
        TestRobot.step(2);
        assertTrue(left() > 0, "Lesson 11: on the line, the robot should drive forward");
        assertEquals(left(), right(), 0.01, "Lesson 11: centered on the line, both wheels should go the same speed");
    }

    @Test
    void stopsWhenCanceled() {
        LessonSim.setLine(1.0, 0.0);
        TestRobot.step(2);
        Scheduler.getDefault().cancel(follow);
        TestRobot.step();
        assertEquals(0.0, left(), 1e-9, "Lesson 11: the motors must stop when the line follower is canceled");
        assertEquals(0.0, right(), 1e-9, "Lesson 11: the motors must stop when the line follower is canceled");
    }
}
