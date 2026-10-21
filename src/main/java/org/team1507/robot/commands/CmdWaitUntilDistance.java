//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot.commands;

import org.wpilib.command2.Command;
import org.team1507.robot.subsystems.XRPDrivetrain;

public class CmdWaitUntilDistance extends Command {
    private final XRPDrivetrain drivetrain;
    private final double targetMeters;
    private double startLeft, startRight;

    public CmdWaitUntilDistance(XRPDrivetrain drivetrain, double targetMeters) {
        this.drivetrain = drivetrain;
        this.targetMeters = targetMeters;
    }

    @Override
    public void initialize() {
        startLeft = drivetrain.getLeftDistance();
        startRight = drivetrain.getRightDistance();
    }

    @Override
    public boolean isFinished() {
        double left = drivetrain.getLeftDistance() - startLeft;
        double right = drivetrain.getRightDistance() - startRight;
        double avg = (left + right) / 2.0;
        return Math.abs(avg) >= Math.abs(targetMeters);
    }
}
