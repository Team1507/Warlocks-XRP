//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;

import org.junit.jupiter.api.Test;

import org.wpilib.command3.Command;
import org.wpilib.command3.Scheduler;
import org.wpilib.hardware.hal.OpModeOption;
import org.wpilib.simulation.DriverStationSim;

// ─────────────────────────────────────────────────────────────────────────────
// OpModeTest
//
// Runs the REAL robot loop (OpModeRobot.startCompetition) on a background
// thread and drives it the way the Driver Station would: pick an OpMode from
// the drop-down, enable, disable. Checks that:
//   - every 2026 auto and the Driver teleop show up in the Driver Station
//   - enabling an auto starts it driving
//   - disabling stops the auto (LoggedRobot cancels everything on disable)
//   - the Driver teleop starts joystick driving
//
// This is the closest a test gets to clicking through the Driver Station.
// ─────────────────────────────────────────────────────────────────────────────
class OpModeTest {

    private static final List<String> EXPECTED = List.of(
        "Driver",
        "Forward and Back",
        "Forward and Back Distance",
        "Forward Until 10cm Then Turn",
        "Forward Until 10cm Then Turn 60",
        "Follow Line");

    @Test
    void autosAndTeleopRunThroughTheOpModeFramework() throws Exception {
        Robot robot = TestRobot.get();
        Scheduler.getDefault().cancelAll();
        Thread loop = new Thread(robot::startCompetition, "robot loop");
        loop.setDaemon(true);
        loop.start();

        try {
            waitFor(() -> DriverStationSim.getOpModeOptions().length > 0, "no OpModes published");
            OpModeOption[] options = DriverStationSim.getOpModeOptions();
            List<String> names = Arrays.stream(options).map(o -> o.name).toList();
            for (String name : EXPECTED) {
                assertTrue(names.contains(name), name + " missing from the Driver Station: " + names);
            }

            // Select an auto, then enable: it starts driving.
            DriverStationSim.setDsAttached(true);
            select(options, "Forward and Back");
            setEnabled(true);
            waitFor(() -> running().contains("Drivetrain.driveStraight"), "auto never started driving");
            assertTrue(running().contains("ForwardAndBackAuto"), "running: " + running());

            // Disable mid-auto: everything stops.
            setEnabled(false);
            waitFor(() -> !running().contains("ForwardAndBackAuto")
                && !running().contains("Drivetrain.driveStraight"), "auto kept running after disable");

            // Teleop: joystick driving becomes the drivetrain's default.
            select(options, "Driver");
            setEnabled(true);
            waitFor(() -> running().contains("Drivetrain.drive"), "teleop driving never started");
            setEnabled(false);
        } finally {
            robot.endCompetition();
            loop.join(2000);
            Scheduler.getDefault().cancelAll();
        }
    }

    private static void select(OpModeOption[] options, String name) {
        for (OpModeOption option : options) {
            if (option.name.equals(name)) {
                DriverStationSim.setRobotMode(option.getMode());
                DriverStationSim.setOpMode(option.id);
                DriverStationSim.notifyNewData();
                return;
            }
        }
        fail("No OpMode named " + name);
    }

    private static void setEnabled(boolean enabled) {
        DriverStationSim.setEnabled(enabled);
        DriverStationSim.notifyNewData();
    }

    private static List<String> running() {
        return Scheduler.getDefault().getRunningCommands().stream().map(Command::name).toList();
    }

    /** Polls for up to 2 seconds (the robot loop runs on its own thread, in real time). */
    private static void waitFor(BooleanSupplier condition, String message) throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            if (condition.getAsBoolean()) {
                return;
            }
            Thread.sleep(20);
        }
        fail(message + " — running: " + running());
    }
}
