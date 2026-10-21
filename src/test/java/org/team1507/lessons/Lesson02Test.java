//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.lessons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.wpilib.simulation.EncoderSim;

import org.team1507.robot.Constants.RobotMap;
import org.team1507.robot.TestRobot;
import org.team1507.robot.subsystems.Drivetrain;

// ─────────────────────────────────────────────────────────────────────────────
// Lesson02Test — checks Lesson 2 (Variables and Math). See the wiki.
//
// Pretends the wheels turned by setting the simulated encoder counts, then
// checks the distance the Drivetrain reports. That only comes out right if
// METERS_PER_COUNT is right.
//
// Run it: .\gradlew lessonTest --tests "*Lesson02*"
// (or click the green ▶ next to the class name in VS Code).
// ─────────────────────────────────────────────────────────────────────────────
@Tag("lesson")
class Lesson02Test {

    /** One full wheel turn: the wheel's circumference, π × 0.060 m ≈ 0.188 m. */
    private static final double ONE_TURN_METERS = Math.PI * 0.060;

    @Test
    void oneWheelTurnIsOneCircumference() {
        Drivetrain drivetrain = TestRobot.get().drivetrain;
        EncoderSim left = EncoderSim.createForChannel(RobotMap.LEFT_ENCODER_A);

        left.setCount(585);   // one full turn of the wheel

        assertNotEquals(585.0, drivetrain.getLeftDistance(), 1e-9,
            "Lesson 2: METERS_PER_COUNT is still the placeholder 1.0. Work it out in Drivetrain.java.");
        assertEquals(ONE_TURN_METERS, drivetrain.getLeftDistance(), 0.0005,
            "Lesson 2: one wheel turn (585 counts) should be one circumference, about 0.188 m");
    }

    @Test
    void tenWheelTurnsIsTenCircumferences() {
        Drivetrain drivetrain = TestRobot.get().drivetrain;
        EncoderSim right = EncoderSim.createForChannel(RobotMap.RIGHT_ENCODER_A);

        right.setCount(5850);   // ten full turns

        assertEquals(10 * ONE_TURN_METERS, drivetrain.getRightDistance(), 0.005,
            "Lesson 2: ten wheel turns should be about 1.88 m");
    }
}
