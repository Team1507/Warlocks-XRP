//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot.commands;

import org.wpilib.command2.Command;
import org.team1507.robot.subsystems.XRPSensors;

public class CmdWaitUntilMarker extends Command {
    private final XRPSensors sensors;
    private final double threshold;

    public CmdWaitUntilMarker(XRPSensors sensors, double threshold) {
        this.sensors = sensors;
        this.threshold = threshold;
    }

    @Override
    public boolean isFinished() {
        double leftSensor = sensors.getLeftReflectance();
        double rightSensor = sensors.getRightReflectance();
        return leftSensor > threshold && rightSensor > threshold;
    }
}
