//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot;

import org.wpilib.system.DataLogManager;
import org.wpilib.driverstation.MatchState;
import org.wpilib.driverstation.RobotState;
import org.wpilib.driverstation.Alliance;
import org.wpilib.driverstation.MatchType;
import org.wpilib.driverstation.DriverStationErrors;
import org.wpilib.framework.TimedRobot;
import org.wpilib.command2.CommandScheduler;

/**
 * Base robot class that enables data logging on startup.
 *
 * <p>Starts {@link DataLogManager} and pipes DriverStation data into the log
 * so any crashes or warnings are captured for post-run review. All robot
 * classes extend this instead of {@link TimedRobot} directly.
 */
public abstract class LoggedRobot extends TimedRobot {

    protected LoggedRobot() {
        DataLogManager.start();
        DriverStation.startDataLog(DataLogManager.getLog());
    }

    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();
    }
}
