//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.lib.core.framework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.wpilib.command3.Command;
import org.wpilib.command3.Scheduler;
import org.wpilib.hardware.hal.HAL;
import org.wpilib.telemetry.DiscardTelemetryBackend;
import org.wpilib.telemetry.TelemetryRegistry;

import org.team1507.robot.RobotBehaviors;

// ─────────────────────────────────────────────────────────────────────────────
// CommandsV3PatternsTest
//
// Commands v3 is new (alpha) and its API may change. These tests run the
// command patterns this repo relies on through the real v3 scheduler, so an
// update that changes their behavior fails the build instead of the robot:
//
//   - Subsystem1507.periodic() runs every loop (v3 has no built-in periodic)
//   - A loop with coroutine.yield() runs one pass per robot loop
//   - A race ends when the wait ends and cancels the drive, running its
//     whenCanceled (AutoSequence's drive step + wait step)
//   - A sequence runs its steps in order (AutoSequence.build())
//   - A command that requires two subsystems can run commands on both at once
//     (RobotBehaviors.celebrate())
//   - RobotBehaviors.failsafe() interrupts running commands, and the default
//     command comes back afterward
//
// Copied from 1507Base and trimmed to the patterns used here.
// ─────────────────────────────────────────────────────────────────────────────
class CommandsV3PatternsTest {

    /** A do-nothing subsystem that counts its periodic() calls. */
    private static final class TestSubsystem extends Subsystem1507 {
        int periodicCalls = 0;

        TestSubsystem(String name) {
            super(name);
        }

        @Override
        public void periodic() {
            periodicCalls++;
        }
    }

    private final Scheduler scheduler = Scheduler.getDefault();

    @BeforeAll
    static void initHal() {
        HAL.initialize();
        // No robot program, so no telemetry destination: send logged values nowhere.
        TelemetryRegistry.registerBackend("", new DiscardTelemetryBackend());
    }

    @BeforeEach
    void resetScheduler() {
        scheduler.cancelAll();
    }

    private void runLoops(int loops) {
        for (int i = 0; i < loops; i++) {
            scheduler.run();
        }
    }

    /** A command on the subsystem that runs for a number of loops, recording its name when it finishes. */
    private static Command steps(TestSubsystem subsystem, String name, int loops, List<String> log) {
        return subsystem.run(coroutine -> {
            for (int i = 0; i < loops; i++) {
                coroutine.yield();
            }
            log.add(name);
        }).named(name);
    }

    @Test
    void subsystemPeriodicRunsEveryLoop() {
        TestSubsystem subsystem = new TestSubsystem("TestSubsystem");
        runLoops(5);
        assertEquals(5, subsystem.periodicCalls);
    }

    @Test
    void loopWithYieldRunsOncePerRobotLoop() {
        TestSubsystem subsystem = new TestSubsystem("TestSubsystem");
        int[] passes = { 0 };
        Command counter = subsystem.run(coroutine -> {
            while (true) {
                passes[0]++;
                coroutine.yield();   // hand control back until the next loop
            }
        }).named("counter");

        scheduler.schedule(counter);
        runLoops(4);

        assertEquals(4, passes[0]);
    }

    @Test
    void raceEndsWithTheWaitAndCancelsTheDrive() {
        TestSubsystem subsystem = new TestSubsystem("TestSubsystem");
        boolean[] driveCanceled = { false };
        int[] loops = { 0 };

        // Same shape as AutoSequence's .driveStraight(...).waitUntil...(...)
        Command drive = subsystem.runRepeatedly(() -> {})
            .whenCanceled(() -> driveCanceled[0] = true)
            .named("drive");
        Command wait = Command.waitUntil(() -> ++loops[0] >= 3).named("wait");
        Command race = Command.race(drive, wait).named("race");

        scheduler.schedule(race);
        runLoops(10);

        assertFalse(scheduler.isScheduledOrRunning(race));
        assertTrue(driveCanceled[0], "the drive's whenCanceled must run, or the motors keep going");
    }

    @Test
    void sequenceRunsStepsInOrder() {
        TestSubsystem subsystem = new TestSubsystem("TestSubsystem");
        List<String> log = new ArrayList<>();

        // Same shape as AutoSequence.build()
        Command sequence = Command.sequence(
            Command.noRequirements(coroutine -> log.add("timer")).named("startTimer"),
            steps(subsystem, "first", 2, log),
            steps(subsystem, "second", 2, log)
        ).withAutomaticName();

        scheduler.schedule(sequence);
        runLoops(20);

        assertEquals(List.of("timer", "first", "second"), log);
    }

    @Test
    void commandRequiringTwoSubsystemsRunsCommandsOnBoth() {
        TestSubsystem a = new TestSubsystem("A");
        TestSubsystem b = new TestSubsystem("B");
        List<String> log = new ArrayList<>();

        // Same shape as RobotBehaviors.celebrate()
        Command behavior = Command.requiring(a, b).executing(coroutine -> {
            coroutine.awaitAll(steps(a, "a", 2, log), steps(b, "b", 3, log));
            coroutine.await(steps(a, "a again", 1, log));
        }).named("behavior");

        scheduler.schedule(behavior);
        runLoops(20);

        assertEquals(List.of("a", "b", "a again"), log);
        assertFalse(scheduler.isScheduledOrRunning(behavior));
    }

    @Test
    void failsafeInterruptsAndDefaultComesBack() {
        TestSubsystem subsystem = new TestSubsystem("TestSubsystem");
        subsystem.setDefaultCommand(subsystem.idle());

        boolean[] canceled = { false };
        Command forever = subsystem.runRepeatedly(() -> {})
            .whenCanceled(() -> canceled[0] = true)
            .named("forever");

        scheduler.schedule(forever);
        runLoops(2);
        assertTrue(scheduler.isRunning(forever));

        scheduler.schedule(RobotBehaviors.failsafe());
        runLoops(3);

        assertTrue(canceled[0], "failsafe should interrupt the running command");
        assertFalse(scheduler.isRunning(forever));
        assertTrue(
            scheduler.getRunningCommandsFor(subsystem).stream()
                .anyMatch(c -> c.name().equals("TestSubsystem[IDLE]")),
            "default command should run again after the failsafe");
    }
}
