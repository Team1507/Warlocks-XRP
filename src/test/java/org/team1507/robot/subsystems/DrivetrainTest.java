//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot.subsystems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.wpilib.command3.Command;
import org.wpilib.command3.Scheduler;
import org.wpilib.hardware.hal.SimDouble;
import org.wpilib.simulation.SimDeviceSim;

import org.team1507.robot.TestRobot;

import static org.team1507.robot.Constants.kDrivetrain.kTuning.TURN_TOLERANCE_DEG;

// ─────────────────────────────────────────────────────────────────────────────
// DrivetrainTest
//
// Guards the 2026 turning bug fixes, so a later change can't quietly undo them:
//   - the heading error wraps to ±180°, so the robot turns the short way
//     (2026's ((e + 180) % 360) - 180 got this wrong below -180°)
//   - a turn settles within tolerance and stops the motors
//
// There is no real gyro in a test, so the test plays the gyro: every loop it
// turns the simulated heading by an amount that follows the motor outputs.
// ─────────────────────────────────────────────────────────────────────────────
class DrivetrainTest {

    /** Simulated turn rate: degrees per loop for a full wheel-speed difference of 1. */
    private static final double DEGREES_PER_LOOP = 4.0;

    private Drivetrain drivetrain;
    private SimDouble gyroAngle;

    @BeforeEach
    void setUp() {
        drivetrain = TestRobot.get().drivetrain;
        gyroAngle = new SimDeviceSim("Gyro:XRPGyro").getDouble("angle_z");
        Scheduler.getDefault().cancelAll();
        drivetrain.resetHeading();
        TestRobot.pauseTime();
    }

    @AfterEach
    void tearDown() {
        Scheduler.getDefault().cancelAll();
        TestRobot.resumeTime();
    }

    /** Sets the heading the drivetrain reads (degrees, relative to its last reset). */
    private void setHeading(double degrees) {
        drivetrain.resetHeading();
        double zero = gyroAngle.get();
        gyroAngle.set(zero + degrees);
    }

    @Test
    void headingErrorTakesTheShortWay() {
        setHeading(350);
        assertEquals(20, drivetrain.headingErrorTo(10), 1e-9);    // 350 -> 10 is +20, not -340

        setHeading(10);
        assertEquals(-20, drivetrain.headingErrorTo(350), 1e-9);

        setHeading(0);
        assertEquals(160, drivetrain.headingErrorTo(-200), 1e-9); // 2026's formula said -200
    }

    @Test
    void turnByAngleSettlesWithinToleranceAndStops() {
        setHeading(0);
        Command turn = drivetrain.turnByAngle(90);
        Scheduler.getDefault().schedule(turn);

        for (int i = 0; i < 100 && Scheduler.getDefault().isScheduledOrRunning(turn); i++) {
            TestRobot.step();
            // Counterclockwise rate follows (right wheel forward - left wheel forward).
            // The right motor is inverted, so its forward is -getRightThrottle().
            double spin = -drivetrain.getRightThrottle() - drivetrain.getLeftThrottle();
            gyroAngle.set(gyroAngle.get() + spin * DEGREES_PER_LOOP);
        }

        assertFalse(Scheduler.getDefault().isScheduledOrRunning(turn), "turn never finished");
        assertTrue(Math.abs(drivetrain.headingErrorTo(90)) < TURN_TOLERANCE_DEG,
            "ended at " + drivetrain.getHeading() + "°, wanted 90°");
        assertEquals(0.0, drivetrain.getLeftThrottle(), 1e-9);
        assertEquals(0.0, drivetrain.getRightThrottle(), 1e-9);
    }
}
