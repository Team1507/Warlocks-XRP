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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.wpilib.command3.Scheduler;
import org.wpilib.command3.SchedulerEvent;
import org.wpilib.driverstation.Gamepad;

import org.team1507.robot.Robot;
import org.team1507.robot.TestRobot;
import org.team1507.robot.teleop.DriverTeleop;

// ─────────────────────────────────────────────────────────────────────────────
// Lesson07Test — checks Lesson 7 (Buttons and Teleop). See the wiki.
//
// Sets up the Driver teleop, presses pretend gamepad buttons, and checks that
// the right command starts.
//
// Run it: .\gradlew lessonTest --tests "*Lesson07*"
// ─────────────────────────────────────────────────────────────────────────────
@Tag("lesson")
class Lesson07Test {

    private final List<String> started = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        LessonSim.setButton(Gamepad.Button.FACE_RIGHT.value, false);
        LessonSim.setButton(Gamepad.Button.FACE_LEFT.value, false);
        Scheduler.getDefault().cancelAll();
    }

    /** Presses a button for a few loops and lets the scheduler see it. */
    private void press(int button) {
        LessonSim.setButton(button, true);
        for (int i = 0; i < 3; i++) {
            Scheduler.getDefault().run();
        }
        LessonSim.setButton(button, false);
        Scheduler.getDefault().run();
    }

    @Test
    void bWavesAndXMovesTheArmTo90() {
        Robot robot = TestRobot.get();
        Scheduler scheduler = Scheduler.getDefault();
        scheduler.cancelAll();
        scheduler.addEventListener(event -> {
            if (event instanceof SchedulerEvent.Scheduled e) {
                started.add(e.command().name());
            }
        });
        new DriverTeleop(robot);   // what WPILib does when "Driver" is picked in the Driver Station

        started.clear();
        press(Gamepad.Button.FACE_RIGHT.value);
        assertTrue(started.contains("Arm.wave"),
            "Lesson 7: pressing B (faceRight) should start Arm.wave. Started: " + started);

        started.clear();
        press(Gamepad.Button.FACE_LEFT.value);
        assertTrue(started.contains("Arm.goTo"),
            "Lesson 7: pressing X (faceLeft) should start Arm.goTo. Started: " + started);
        assertEquals(90.0, robot.arm.getAngle(), 1.0, "Lesson 7: X should move the arm to 90°");
    }
}
