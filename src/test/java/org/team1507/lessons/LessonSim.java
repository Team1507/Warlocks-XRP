//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.lessons;

import org.wpilib.driverstation.internal.DriverStationBackend;
import org.wpilib.simulation.AnalogInputSim;
import org.wpilib.simulation.DriverStationSim;

// ─────────────────────────────────────────────────────────────────────────────
// LessonSim
//
// Pretend sensor readings and button presses for the lesson checks, so they
// can test your code without a robot. (Students don't need to read this.)
//
// The XRP's sensors are analog inputs: the rangefinder is channel 2 (0–5 V =
// 0–4 m), and the line sensors are channels 0 (left) and 1 (right) (0–5 V =
// 0.0–1.0 reflectance).
// ─────────────────────────────────────────────────────────────────────────────
final class LessonSim {

    private static final AnalogInputSim RANGE = new AnalogInputSim(2);
    private static final AnalogInputSim LEFT_LINE = new AnalogInputSim(0);
    private static final AnalogInputSim RIGHT_LINE = new AnalogInputSim(1);

    private LessonSim() {}

    /** Makes the rangefinder read {@code meters}. */
    static void setRange(double meters) {
        RANGE.setVoltage(meters / 4.0 * 5.0);
    }

    /** Makes the line sensors read these reflectance values (0.0 = white, 1.0 = black). */
    static void setLine(double left, double right) {
        LEFT_LINE.setVoltage(left * 5.0);
        RIGHT_LINE.setVoltage(right * 5.0);
    }

    /** Presses (or releases) a button on gamepad 0, and tells the robot. */
    static void setButton(int button, boolean pressed) {
        DriverStationSim.setJoystickIsGamepad(0, true);
        DriverStationSim.setJoystickButtonsMaximumIndex(0, 32);
        DriverStationSim.setJoystickButtonsAvailable(0, 0xFFFFFFFFL);
        DriverStationSim.setJoystickButton(0, button, pressed);
        DriverStationSim.notifyNewData();
        DriverStationBackend.refreshData();   // the robot loop does this; tests must do it themselves
    }
}
