//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot.auto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.wpilib.command3.Command;
import org.wpilib.command3.Scheduler;
import org.wpilib.opmode.Autonomous;

import org.team1507.lib.core.framework.AutoOpMode;
import org.team1507.robot.Robot;
import org.team1507.robot.TestRobot;
import org.team1507.robot.auto.routines.ForwardAndBackAuto;

// ─────────────────────────────────────────────────────────────────────────────
// AutoRoutinesTest
//
// Builds EVERY auto routine in robot/auto/routines on every build, the same way
// the robot does when the routine is enabled. The build fails if a routine has
// a mistake AutoSequence refuses (a drive that nothing stops, an action in the
// middle of a drive, ...). So a broken auto never reaches the robot. Nothing to
// register: new routine files are found automatically.
//
// It also runs "Forward and Back" in simulated time and checks the robot
// drives forward, then backward, then stops, at the right times.
// ─────────────────────────────────────────────────────────────────────────────
class AutoRoutinesTest {

    private static final String PACKAGE = "org.team1507.robot.auto.routines";

    @Test
    void everyAutoRoutineBuilds() throws Exception {
        TestRobot.get();
        List<Class<?>> routines = findRoutines();
        assertFalse(routines.isEmpty(), "no @Autonomous classes found in " + PACKAGE);

        List<String> problems = new ArrayList<>();
        for (Class<?> routine : routines) {
            try {
                if (build(routine) == null) {
                    problems.add(routine.getSimpleName() + ": build() returned null");
                }
            } catch (InvocationTargetException e) {
                problems.add(routine.getSimpleName() + ": " + e.getCause().getMessage());
            }
        }
        if (!problems.isEmpty()) {
            fail("Auto routines with problems:\n  " + String.join("\n  ", problems));
        }
    }

    @Test
    void forwardAndBackDrivesForwardThenBackThenStops() throws Exception {
        Robot robot = TestRobot.get();
        Scheduler scheduler = Scheduler.getDefault();
        scheduler.cancelAll();
        TestRobot.pauseTime();
        try {
            Command auto = build(ForwardAndBackAuto.class);
            scheduler.schedule(auto);

            TestRobot.step(25);   // 0.5 s
            assertTrue(robot.drivetrain.getLeftThrottle() > 0, "should drive forward at 0.5 s");

            TestRobot.step(50);   // 1.5 s
            assertTrue(robot.drivetrain.getLeftThrottle() < 0, "should drive backward at 1.5 s");

            TestRobot.step(50);   // 2.5 s
            assertFalse(scheduler.isScheduledOrRunning(auto), "should be done by 2.5 s");
            assertEquals(0.0, robot.drivetrain.getLeftThrottle(), 1e-9);
        } finally {
            scheduler.cancelAll();
            TestRobot.resumeTime();
        }
    }

    /** Calls a routine's build(), as AutoOpMode does at enable. */
    private static Command build(Class<?> routine) throws Exception {
        AutoOpMode opMode = (AutoOpMode) routine.getConstructor().newInstance();
        Method build = AutoOpMode.class.getDeclaredMethod("build");
        build.setAccessible(true);
        return (Command) build.invoke(opMode);
    }

    /** Every @Autonomous class in the routines package. */
    private static List<Class<?>> findRoutines() throws Exception {
        List<Class<?>> found = new ArrayList<>();
        String path = PACKAGE.replace('.', '/');
        for (URL url : Collections.list(AutoRoutinesTest.class.getClassLoader().getResources(path))) {
            if (!"file".equals(url.getProtocol())) {
                continue;
            }
            File[] files = new File(url.toURI()).listFiles((dir, file) -> file.endsWith(".class") && !file.contains("$"));
            if (files == null) {
                continue;
            }
            for (File file : files) {
                Class<?> cls = Class.forName(PACKAGE + "." + file.getName().replace(".class", ""));
                if (cls.isAnnotationPresent(Autonomous.class) && AutoOpMode.class.isAssignableFrom(cls)) {
                    found.add(cls);
                }
            }
        }
        return found;
    }
}
