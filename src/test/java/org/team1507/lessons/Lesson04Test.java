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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.team1507.robot.TestRobot;
import org.team1507.robot.subsystems.Sensors;

import static org.team1507.robot.Constants.kSensors.LINE_THRESHOLD;

// ─────────────────────────────────────────────────────────────────────────────
// Lesson04Test — checks Lesson 4 (Methods and Booleans). See the wiki.
//
// Pretends the line sensors see white or black, and checks what your methods
// say. 0.0 = white, 1.0 = black.
//
// Run it: .\gradlew lessonTest --tests "*Lesson04*"
// ─────────────────────────────────────────────────────────────────────────────
@Tag("lesson")
class Lesson04Test {

    private final Sensors sensors = TestRobot.get().sensors;

    @Test
    void isBlackUsesTheThreshold() {
        assertTrue(sensors.isBlack(0.95), "Lesson 4: 0.95 is very dark, so isBlack(0.95) should be true");
        assertFalse(sensors.isBlack(0.05), "Lesson 4: 0.05 is almost white, so isBlack(0.05) should be false");
        assertTrue(sensors.isBlack(LINE_THRESHOLD + 0.05), "Lesson 4: just above LINE_THRESHOLD counts as black");
        assertFalse(sensors.isBlack(LINE_THRESHOLD - 0.05), "Lesson 4: just below LINE_THRESHOLD is not black");
    }

    @Test
    void onlyLeftOnTheLine() {
        LessonSim.setLine(1.0, 0.0);
        assertTrue(sensors.isLeftOnLine(), "Lesson 4: left sees black, so isLeftOnLine() should be true");
        assertFalse(sensors.isRightOnLine(), "Lesson 4: right sees white, so isRightOnLine() should be false");
        assertTrue(sensors.isOnLine(), "Lesson 4: one sensor on the line is enough for isOnLine()");
        assertFalse(sensors.isOnMarker(), "Lesson 4: isOnMarker() needs BOTH sensors on black");
    }

    @Test
    void onlyRightOnTheLine() {
        LessonSim.setLine(0.0, 1.0);
        assertFalse(sensors.isLeftOnLine(), "Lesson 4: left sees white, so isLeftOnLine() should be false");
        assertTrue(sensors.isRightOnLine(), "Lesson 4: right sees black, so isRightOnLine() should be true");
        assertTrue(sensors.isOnLine(), "Lesson 4: one sensor on the line is enough for isOnLine()");
        assertFalse(sensors.isOnMarker(), "Lesson 4: isOnMarker() needs BOTH sensors on black");
    }

    @Test
    void bothOnBlackIsTheMarker() {
        LessonSim.setLine(1.0, 1.0);
        assertTrue(sensors.isOnLine(), "Lesson 4: both on black: isOnLine() should be true");
        assertTrue(sensors.isOnMarker(), "Lesson 4: both on black: isOnMarker() should be true");
    }

    @Test
    void bothOnWhiteIsNothing() {
        LessonSim.setLine(0.0, 0.0);
        assertFalse(sensors.isLeftOnLine(), "Lesson 4: all white: isLeftOnLine() should be false");
        assertFalse(sensors.isRightOnLine(), "Lesson 4: all white: isRightOnLine() should be false");
        assertFalse(sensors.isOnLine(), "Lesson 4: all white: isOnLine() should be false");
        assertFalse(sensors.isOnMarker(), "Lesson 4: all white: isOnMarker() should be false");
    }
}
