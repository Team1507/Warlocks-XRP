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

public class CmdWaitUntilStopped extends Command {
    private final XRPDrivetrain drivetrain;

    public CmdWaitUntilStopped(XRPDrivetrain drivetrain) {
        this.drivetrain = drivetrain;
        addRequirements(drivetrain); // optional, since it doesn't drive
    }

    @Override
    public boolean isFinished() {
        return drivetrain.isStopped();
    }
}
