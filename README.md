# Warlocks-XRP

**Warlocks-XRP** is where Team 1507 students learn our robot code on an [XRP](https://docs.wpilib.org/en/latest/docs/xrp-robot/index.html), a small robot that fits on a desk.

It is built exactly like our competition code, [1507Base](https://github.com/Team1507/1507Base) (`SystemCore` branch): WPILib 2027, Commands v3, OpModes, and the same base classes in the same folders. Anything you learn here carries straight over to the real robot. The difference is that the XRP is cheap, safe, and has only two wheels and an arm.

## What's in here

```
src/main/java/org/team1507/
  robot/
    Constants.java        Ports (RobotMap) and tuning knobs (kDrivetrain, kArm, kSensors)
    Robot.java            Subsystems, the gamepad, safe default commands
    RobotBehaviors.java   Multi-subsystem behaviors (used by teleop AND auto)
    auto/
      AutoSequence.java   The step-by-step auto builder
      routines/           One @Autonomous class per auto routine
    teleop/
      DriverTeleop.java   The teleop mode: driving and button bindings
    subsystems/
      Drivetrain.java     Motors, wheel encoders, gyro; driving and turning commands
      Sensors.java        Rangefinder and line (reflectance) sensors
      Arm.java            The servo arm, with angle limits
  lib/core/
    framework/            Base classes: LoggedRobot, Subsystem1507, AutoOpMode
    logging/              RobotHealthLog, CommandLog
src/test/                 Tests that run on every build
docs/
  migration.md            How this code was converted from 2026 to 2027, step by step
  exercises.md            Practice exercises, easiest first
```

## Setup

1. **Install WPILib 2027 alpha 7** from [SystemcoreTesting](https://github.com/wpilibsuite/SystemcoreTesting). It installs to `C:\Users\Public\wpilib\2027_alpha7` and comes with its own VS Code.
2. **Open this folder in the WPILib 2027 VS Code**, not the 2026 one or a normal VS Code. If VS Code says the project "is not imported for 2027", you opened the wrong VS Code.
3. **Turn on the XRP and join its Wi-Fi network** from your laptop. The XRP makes its own network; ask a mentor for the name and password. Your laptop loses internet while connected, which is normal.
4. **Run the code:** press `Ctrl+Shift+P` and choose **WPILib: Simulate Robot Code**. The robot program runs on your laptop and talks to the XRP over Wi-Fi (at `192.168.42.1`).
   - This project starts the **Sim GUI** and the **XRP Client** automatically. If VS Code ever asks which extensions to run, keep **both** checked: the Sim GUI is your Driver Station, and the XRP Client is the connection to the robot. Without it, the program runs but the XRP never moves.
5. **Pick a mode in the Driver Station drop-downs** in the Sim GUI: an autonomous routine (e.g. "Forward and Back") or the "Driver" teleop. Then click **Enable**. Click **Disable** to stop.
6. **Plug in a gamepad** for teleop, and drag it onto Joystick 0 in the Sim GUI.

You can build and test from a terminal too (VS Code's terminal is PowerShell). Point Gradle at the 2027 JDK first:

```
$env:JAVA_HOME = "C:\Users\Public\wpilib\2027_alpha7\jdk"
.\gradlew build
```

`gradlew build` also runs every test. A green build means your code compiles AND every auto routine still builds.

### Teleop controls

| Control | Does |
|---|---|
| Left stick | Arcade drive (up/down = forward/back, left/right = turn) |
| Left bumper | Arm up |
| Right bumper | Arm down |
| A (bottom face button) | Celebrate: arm up while turning around, then arm down |
| Back | Failsafe: stop every command |

## Your own branch

Everyone works on their **own branch**, named `student/<firstname>`. `main` is the shared starting point, and nobody changes it directly.

**First time:**

```
git checkout main
git pull
git checkout -b student/alex          # use your first name
git push -u origin student/alex
```

**While you work:** commit small steps often, with a message that says what you changed and why:

```
git add -A
git commit -m "Raise deadband to 0.15: the robot crept forward with the stick at rest"
git push
```

**Rules:**

- **Never merge your branch into `main`.** Your branch is yours to experiment on. Break things, it's fine.
- If a mentor updates `main` and you want the new version, bring `main` into your branch: `git fetch` and then `git merge origin/main`.
- GitHub builds and tests every push. A green check on your commit means it builds; a red X means look at the error before driving.

## Where each file lives in 1507Base

Once something makes sense here, open the matching file in 1507Base (`SystemCore` branch) and it should look familiar.

| Warlocks-XRP | 1507Base | Notes |
|---|---|---|
| `robot/Robot.java` | `robot/Robot.java` | Same job: subsystems, controllers, safe defaults |
| `robot/Constants.java` | `robot/Constants.java` | Same layout: `RobotMap` + `k<Subsystem>` classes |
| `robot/RobotBehaviors.java` | `robot/RobotBehaviors.java` | Same `failsafe()`; behaviors used by teleop and auto |
| `robot/teleop/DriverTeleop.java` | `robot/teleop/DriverTeleop.java` | Arcade drive here, swerve there |
| `robot/auto/AutoBuilder.java` | `robot/auto/AutoBuilder.java` | Same, minus the policy driver |
| `robot/auto/AutoSequence.java` | `robot/auto/AutoSequence.java` | Small version: drive/wait steps instead of field routes |
| `robot/auto/routines/*Auto.java` | `robot/auto/routines/*Auto.java` | Same pattern: one `@Autonomous` class per routine |
| `robot/subsystems/Drivetrain.java` | `robot/subsystems/Swerve.java` + `SwerveConfig.java` | Both own their gyro; XRP hardware facts stay in the subsystem |
| `robot/subsystems/Sensors.java`, `Arm.java` | (season mechanisms, e.g. an intake or arm) | Built from the same Subsystem1507 template |
| `lib/core/framework/LoggedRobot.java` | `lib/core/framework/LoggedRobot.java` | Same, minus power distribution hub logging |
| `lib/core/framework/AutoOpMode.java` | `lib/core/framework/AutoOpMode.java` | Same |
| `lib/core/framework/Subsystem1507.java` | `lib/core/framework/Subsystem1507.java` | Same API, minus `motor()` / Motor1507 (the XRP has no CAN motors) |
| `lib/core/logging/CommandLog.java` | `lib/core/logging/CommandLog.java` | Same |
| `lib/core/logging/RobotHealthLog.java` | `lib/core/logging/RobotHealthLog.java` | Loop time and battery only (no CAN bus on the XRP) |
| `src/test/.../TestRobot.java` | `src/test/.../TestRobot.java` | Same idea |
| `src/test/.../CommandsV3PatternsTest.java` | `src/test/.../CommandsV3PatternsTest.java` | Trimmed to the patterns used here |
| `src/test/.../AutoRoutinesTest.java` | `src/test/.../AutoRoutinesTest.java` | Same: every auto builds on every build |
| `.wpilib/templates/subsystem1507/` | `.wpilib/templates/subsystem1507/` | XRP hardware examples instead of Motor1507 |
| `.github/workflows/build.yml` | `.github/workflows/build.yml` | Same |

## Learning path

1. Read [docs/migration.md](docs/migration.md) with `git log` open. It walks through each conversion commit and explains why.
2. Do [docs/exercises.md](docs/exercises.md) in order on your own branch.

## License

Uses WPILib components. See [WPILib-License.md](WPILib-License.md).
