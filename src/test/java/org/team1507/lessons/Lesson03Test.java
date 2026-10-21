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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.team1507.robot.TestRobot;
import org.team1507.robot.subsystems.Sensors;

// ─────────────────────────────────────────────────────────────────────────────
// Lesson03Test — checks Lesson 3 (Decisions). See the wiki.
//
// Pretends something is at different distances in front of the ultrasonic
// sensor, and checks what your methods decide.
//
// Run it: .\gradlew lessonTest --tests "*Lesson03*"
// ─────────────────────────────────────────────────────────────────────────────
@Tag("lesson")
class Lesson03Test {

    private final Sensors sensors = TestRobot.get().sensors;

    @Test
    void isObjectCloseAtFifteenCentimetersOrLess() {
        LessonSim.setRange(0.10);
        assertTrue(sensors.isObjectClose(), "Lesson 3: something 10 cm away IS close (CLOSE_METERS is 15 cm)");

        LessonSim.setRange(0.149);
        assertTrue(sensors.isObjectClose(), "Lesson 3: 14.9 cm is closer than 15 cm, so it IS close");

        LessonSim.setRange(0.20);
        assertFalse(sensors.isObjectClose(), "Lesson 3: something 20 cm away is NOT close");

        LessonSim.setRange(2.0);
        assertFalse(sensors.isObjectClose(), "Lesson 3: something 2 m away is NOT close");
    }

    @Test
    void rangeZones() {
        LessonSim.setRange(0.03);
        assertEquals("TOO_CLOSE", sensors.getRangeZone(), "Lesson 3: 3 cm should be TOO_CLOSE");

        LessonSim.setRange(0.10);
        assertEquals("NEAR", sensors.getRangeZone(), "Lesson 3: 10 cm should be NEAR");

        LessonSim.setRange(0.25);
        assertEquals("NEAR", sensors.getRangeZone(), "Lesson 3: 25 cm should be NEAR");

        LessonSim.setRange(0.50);
        assertEquals("FAR", sensors.getRangeZone(), "Lesson 3: 50 cm should be FAR");

        LessonSim.setRange(3.0);
        assertEquals("FAR", sensors.getRangeZone(), "Lesson 3: 3 m should be FAR");
    }
}
