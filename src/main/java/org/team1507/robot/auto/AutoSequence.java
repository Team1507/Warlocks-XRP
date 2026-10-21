//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot.auto;

import static org.wpilib.units.Units.Seconds;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.wpilib.command3.Command;
import org.wpilib.system.Timer;

import org.team1507.robot.Robot;
import org.team1507.robot.RobotBehaviors;

// ─────────────────────────────────────────────────────────────────────────────
// AutoSequence
//
// Builds an autonomous routine as a list of steps, top to bottom, one line per
// thing that happens. build() turns the list into the command the auto runs.
//
//   new AutoSequence()
//       .driveStraight(0.7).waitUntilDistance(1.0)  // drive 1 meter
//       .turnBy(180)                                // turn around
//       .waitUntilStopped()
//       .armUp()
//       .build();
//
// This is the XRP's small version of 1507Base's AutoSequence: same idea (a
// fluent list of steps, mistakes caught at build), but driving uses simple
// commands instead of field routes.
//
// DRIVE STEPS keep the robot moving until the NEXT WAIT STEP finishes:
//   .driveStraight(speed)       hold the starting heading at this power
//   .followLine(speed)          follow black tape
//
// WAIT STEPS (right after a drive step, they decide when it stops; on their
// own, the robot just waits):
//   .waitUntilTime(seconds)     until the auto clock reads this (starts at 0 when auto starts)
//   .waitSeconds(seconds)       for this long
//   .waitUntilDistance(meters)  until the wheels travel this far (either direction)
//   .waitUntil(() -> ...)       until YOUR condition is true, e.g.
//                                 .waitUntil(() -> AutoBuilder.robot.sensors.isObjectClose())
//
//   Under the hood, a drive step + its wait step is a Commands v3 race:
//   Command.race(drive, wait). Whichever ends first (the wait; the drive never
//   ends by itself) cancels the other. 2026 wrote the same thing as
//   new ParallelRaceGroup(new CmdDrive(...), new CmdWaitUntilTime(...)).
//
// ACTION STEPS (the robot must not be in the middle of a drive):
//   .turnBy(degrees)            turn relative to now (+ = left)
//   .turnTo(degrees)            turn to an absolute heading
//   .waitUntilStopped()         hold still until the wheels stop
//   .stop()                     motors to 0
//   .armUp() / .armDown() / .armTo(degrees)
//   .celebrate()                a RobotBehaviors behavior (drivetrain + arm)
//
// MISTAKES ARE CAUGHT BEFORE THE ROBOT MOVES
//   build() refuses a routine with a drive step that nothing stops, or an
//   action in the middle of a drive, with a message saying what to fix.
//   AutoRoutinesTest builds every routine on every build.
//
// HOW TO ADD NEW AUTO STEPS
//   1. Write the command on your subsystem (or in RobotBehaviors.java if it uses
//      several subsystems). Subsystems are reachable as AutoBuilder.robot.<name>.
//   2. Add a one-line wrapper method in the ROBOT ACTIONS section below.
//   3. Use it in your routine file.
// ─────────────────────────────────────────────────────────────────────────────
public final class AutoSequence {

    // =========================================================================
    // Fields
    // =========================================================================

    /** The steps in order. Each one makes its command when build() runs. */
    private final List<Supplier<Command>> steps = new ArrayList<>();
    private final List<String> mistakes = new ArrayList<>();

    /** A drive step waiting for the wait step that will stop it (null when not driving). */
    private Supplier<Command> pendingDrive = null;
    private String pendingDriveName = null;

    /** The auto clock: restarted when the routine starts, read by waitUntilTime(). */
    private final Timer autoTimer = new Timer();

    // =========================================================================
    // DRIVE STEPS — keep driving until the next wait step finishes
    // =========================================================================

    /** Drives at {@code speed} (negative = backward), holding the starting heading. */
    public AutoSequence driveStraight(double speed) {
        return drive("driveStraight(" + speed + ")", () -> robot().drivetrain.driveStraight(speed));
    }

    /** Follows a black tape line at {@code speed} (your line follower from Lesson 11, in RobotBehaviors). */
    public AutoSequence followLine(double speed) {
        return drive("followLine(" + speed + ")",
            () -> RobotBehaviors.followLine(robot(), speed));
    }

    // =========================================================================
    // WAIT STEPS — end the drive step before them, or just wait
    // =========================================================================

    /** Waits until the auto clock reaches {@code autoSeconds} (0 = when auto started). */
    public AutoSequence waitUntilTime(double autoSeconds) {
        return waitStep("waitUntilTime(" + autoSeconds + ")",
            () -> Command.waitUntil(() -> autoTimer.get() >= autoSeconds)
                .named("until t=" + autoSeconds + "s"));
    }

    /** Waits a fixed number of seconds. */
    public AutoSequence waitSeconds(double seconds) {
        return waitStep("waitSeconds(" + seconds + ")",
            () -> Command.waitFor(Seconds.of(seconds)).named("wait " + seconds + "s"));
    }

    /** Waits until the wheels travel {@code meters} from where this step started (either direction). */
    public AutoSequence waitUntilDistance(double meters) {
        return waitStep("waitUntilDistance(" + meters + ")",
            () -> robot().drivetrain.waitUntilDistance(meters));
    }

    /**
     * Waits until {@code condition} is true. You write the condition, e.g.
     * {@code .waitUntil(() -> AutoBuilder.robot.sensors.isObjectClose())}
     * (Lesson 10). After a drive step, it decides when the drive stops.
     */
    public AutoSequence waitUntil(BooleanSupplier condition) {
        return waitStep("waitUntil", () -> Command.waitUntil(condition).named("waitUntil"));
    }

    // =========================================================================
    // DRIVETRAIN ACTIONS — the robot must not be in the middle of a drive
    // =========================================================================

    /** Turns in place by {@code degrees} from where the robot faces now (+ = left). */
    public AutoSequence turnBy(double degrees) {
        return action("turnBy(" + degrees + ")", () -> robot().drivetrain.turnByAngle(degrees));
    }

    /** Turns in place to face {@code degrees} (absolute; 0 = the way it faced at startup). */
    public AutoSequence turnTo(double degrees) {
        return action("turnTo(" + degrees + ")", () -> robot().drivetrain.turnToAngle(degrees));
    }

    /** Holds the motors at 0 until the wheels have stopped. */
    public AutoSequence waitUntilStopped() {
        return action("waitUntilStopped()", () -> robot().drivetrain.waitUntilStopped());
    }

    /** Sets the drive motors to 0. */
    public AutoSequence stop() {
        return action("stop()", () -> robot().drivetrain.stop());
    }

    // =========================================================================
    // ROBOT ACTIONS — one-line wrappers around subsystem commands
    // =========================================================================

    /** Raises the arm. */
    public AutoSequence armUp() {
        return action("armUp()", () -> robot().arm.up());
    }

    /** Lowers the arm. */
    public AutoSequence armDown() {
        return action("armDown()", () -> robot().arm.down());
    }

    /** Moves the arm to {@code degrees} (clamped to its limits). */
    public AutoSequence armTo(double degrees) {
        return action("armTo(" + degrees + ")", () -> robot().arm.goTo(degrees));
    }

    // =========================================================================
    // ROBOT BEHAVIORS — multi-subsystem actions from RobotBehaviors.java
    // =========================================================================

    /** Raises the arm while turning around, then lowers it (RobotBehaviors.celebrate). */
    public AutoSequence celebrate() {
        return action("celebrate()", () -> RobotBehaviors.celebrate(robot()));
    }

    // =========================================================================
    // BUILD
    // =========================================================================

    /**
     * Builds the command the auto runs. Call it at the end of every routine's
     * build() method. Throws with a list of mistakes to fix if the routine
     * isn't valid.
     */
    public Command build() {
        if (pendingDrive != null) {
            mistakes.add(pendingDriveName + " is the last step, so nothing would ever stop it: "
                + "add a wait step after it (e.g. .waitUntilTime(...))");
        }
        if (!mistakes.isEmpty()) {
            throw new IllegalStateException("Auto has mistakes:\n  - " + String.join("\n  - ", mistakes));
        }

        List<Command> commands = new ArrayList<>();
        // First step: start the auto clock, so waitUntilTime counts from here.
        commands.add(Command.noRequirements(coroutine -> autoTimer.restart()).named("startTimer"));
        for (Supplier<Command> step : steps) {
            commands.add(step.get());
        }
        return Command.sequence(commands.toArray(Command[]::new)).withAutomaticName();
    }

    // =========================================================================
    // Internals
    // =========================================================================

    private AutoSequence drive(String name, Supplier<Command> make) {
        if (pendingDrive != null) {
            mistakes.add(name + " comes right after " + pendingDriveName
                + ", so " + pendingDriveName + " never stops: put a wait step between them");
        }
        pendingDrive = make;
        pendingDriveName = name;
        return this;
    }

    private AutoSequence waitStep(String name, Supplier<Command> makeWait) {
        if (pendingDrive == null) {
            steps.add(makeWait);
        } else {
            Supplier<Command> makeDrive = pendingDrive;
            String raceName = pendingDriveName + "." + name;
            steps.add(() -> Command.race(makeDrive.get(), makeWait.get()).named(raceName));
            pendingDrive = null;
            pendingDriveName = null;
        }
        return this;
    }

    private AutoSequence action(String name, Supplier<Command> make) {
        if (pendingDrive != null) {
            mistakes.add(name + " comes right after " + pendingDriveName
                + ", which is still driving: put a wait step (e.g. .waitUntilDistance(...)) before it");
            pendingDrive = null;
            pendingDriveName = null;
        }
        steps.add(make);
        return this;
    }

    private static Robot robot() {
        if (AutoBuilder.robot == null) {
            throw new IllegalStateException("AutoBuilder.init(this) was not called in Robot.java");
        }
        return AutoBuilder.robot;
    }
}
