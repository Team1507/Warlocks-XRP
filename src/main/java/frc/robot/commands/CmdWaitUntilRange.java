//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.XRPSensors;

public class CmdWaitUntilRange extends Command {
    private final XRPSensors sensors;
    private final double stopDistanceMeters;

    public CmdWaitUntilRange(XRPSensors sensors, double stopDistanceMeters) {
        this.sensors = sensors;
        this.stopDistanceMeters = stopDistanceMeters;
        // No drivetrain requirement here — this command doesn’t drive
    }

    @Override
    public boolean isFinished() {
        // End when the measured range is less than or equal to the target
        return sensors.getRangeMeters() <= stopDistanceMeters;
    }
}
