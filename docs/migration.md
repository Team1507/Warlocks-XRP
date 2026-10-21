# Migrating Warlocks-XRP to WPILib 2027

This is the story of how our 2026 XRP code (WPILib 2025, Commands v2, package `frc.robot`) became 2027 code that matches [1507Base](https://github.com/Team1507/1507Base)'s `SystemCore` branch. Every section below is **one commit**, in order, so you can follow along and see exactly what changed:

```
git log --oneline --reverse 70060b2..HEAD
git show <commit>            # what one step changed
```

It's the XRP version of 1507Base's own [2027 migration story](https://github.com/Team1507/1507Base/blob/SystemCore/docs/2027-migration.md). Read that one next: the first steps are the same, and the big ideas (OpModes, Commands v3) are the same.

> **"Does not build yet" (steps 1–6).** A 2027 toolchain can't compile 2025 code, and once Commands v2 is removed, the old command classes can't compile until they're replaced. So the build is red from step 1 to step 6, and each of those commit messages says so. It turns green at step 7 and stays green. 1507Base's migration had the same gap ("After the rename, the build still had 44 errors. This is normal."). In a real migration, you do this on its own branch, never on `main`.

---

## 1. Project files (`Update project files to WPILib 2027 alpha 7`)

The **build files** tell Gradle which WPILib to download, which Java to use, and how to run the program. We **copied them from the official 2027 templates** instead of hand-editing the old ones:

| File | 2026 | 2027 | Copied from |
|---|---|---|---|
| `build.gradle` plugin | `edu.wpi.first.GradleRIO` 2025.2.1 | `org.wpilib.GradleRIO` 2027.0.0-alpha-7 | XRP template (`javaxrp`) |
| Java version | 17 | **25** | XRP template |
| Gradle (wrapper) | 8.9 | 9.4.1 | 1507Base |
| `settings.gradle` | looks in `wpilib/2025` | looks in `wpilib/2027_alpha7` | 1507Base |
| `.wpilib/wpilib_preferences.json` | `"projectYear": "2025"` | `"projectYear": "2027_alpha7"` | — |

`projectYear` is what VS Code checks when it says a project "is not imported for 2027".

Two things were kept or added on purpose:
- `options.encoding = 'UTF-8'`, so the Warlocks ASCII-art header at the top of every file compiles on every computer.
- The XRP Client lines (`HALSIMXRP_HOST 192.168.42.1`). The XRP template has them: the robot program runs on your laptop and talks to the XRP over Wi-Fi.

## 2. Vendor libraries (`Swap vendor libraries for 2027 alpha 7 versions`)

Vendor libraries (vendordeps) are code that isn't part of core WPILib. **Each one has to be rebuilt for 2027**, because WPILib renamed all of its packages (step 3). Both of ours ship with the 2027 installer, in `C:\Users\Public\wpilib\2027_alpha7\vendordeps`:

| Library | Result |
|---|---|
| XRP | ✅ 2027 build copied in (classes now in `org.wpilib.xrp`) |
| Commands v3 | ✅ Added (same file as 1507Base) |
| Commands v2 (`WPILibNewCommands`) | ❌ Removed. A project uses v2 **or** v3, not both. |

After this step Gradle accepts the project. It refused before, because the 2025 vendordep said "year 2025".

## 3. The big rename (`Rename packages: frc.robot -> org.team1507.robot, edu.wpi.first -> org.wpilib`)

Two renames, both mechanical:

```java
// 2026
package frc.robot.subsystems;
import edu.wpi.first.wpilibj.xrp.XRPMotor;
import edu.wpi.first.math.MathUtil;

// 2027
package org.team1507.robot.subsystems;
import org.wpilib.xrp.XRPMotor;
import org.wpilib.math.util.MathUtil;
```

- **Our package** moved to `org.team1507.robot`, the same as 1507Base.
- **WPILib's packages** moved from `edu.wpi.first.*` to `org.wpilib.*`. Many classes also moved to new sub-packages (`Encoder` is now in `org.wpilib.hardware.rotation`). WPILib's VS Code extension contains the list of about 400 find-and-replace rules it uses to import old projects (`java_replacements.json`). We ran **those exact rules, in order**, over every file, just like 1507Base did.

## 4. The framework (`Add the 1507Base framework: LoggedRobot, AutoOpMode, Subsystem1507, logging`)

The same base classes the competition robot uses, in the same `lib/core` packages:

| Class | What it does | Compared to 1507Base |
|---|---|---|
| `LoggedRobot` | Extends `OpModeRobot`, starts the log, runs the v3 scheduler, **cancels every command on disable** | Same, minus the power distribution hub |
| `AutoOpMode` | Base class for `@Autonomous` routines | Same |
| `Subsystem1507` | Base class for every subsystem: `log`, `warnIf`/`faultIf`, `all()`, `periodic` | Same API, minus `motor()`/Motor1507/CAN (XRP motors aren't CAN motors) |
| `CommandLog` | Logs every command start and end | Same |
| `RobotHealthLog` | Loop time and battery | Same, minus CAN and the PDH |

The old `XRPSubsystem` (SmartDashboard logging) and the old `LoggedRobot` (TimedRobot) were deleted. **SmartDashboard doesn't exist in 2027**: logging now goes through WPILib's `Telemetry`, and you can watch it live in AdvantageScope or Elastic.

**Why cancel on disable?** Commands v2 stopped every command when the robot disabled. Commands v3 doesn't. Without `LoggedRobot` doing it, a half-finished auto would pick up where it left off the next time you enabled.

## 5. Subsystems (`Rebuild the subsystems on Subsystem1507: Drivetrain, Sensors, Arm`)

| 2026 | 2027 | Change |
|---|---|---|
| `XRPDrivetrain` | `Drivetrain` | Now **owns the gyro** too, the way Swerve owns the Pigeon in 1507Base |
| `XRPSensors` | `Sensors` | Rangefinder + line sensors only |
| `XRPServoArm` | `Arm` | Same limits (50°–130°) |

2027 API changes the compiler found:

| 2026 | 2027 |
|---|---|
| `motor.set(x)` | `motor.setThrottle(x)` |
| `new DifferentialDrive(left::set, right::set)` | `new DifferentialDrive(left::setThrottle, right::setThrottle)` |
| `rangefinder.getDistanceMeters()` | `rangefinder.getDistance()` (still meters) |
| `gyro.getAngle()` | `gyro.getAngleZ()` (the same value, said clearly) |

Two old bugs were found while reading the code (neither one is a 2027 change):
- **`isOnLine()` was backwards.** The XRP's line sensors read 0.0 over white and 1.0 over black, and 2026 said "on line" when the reading was *low*. It was only logged, so nothing drove differently.
- **The arm could never finish moving to 0°.** `isAtAngle(0)` compared the servo (clamped to 50°) with 0°. It now compares with the clamped angle.

## 6. Commands v3 (`Turn the Cmd* classes into Commands v3 methods on the subsystems`)

The biggest change in *how you write code*. In v2, each command was a class with four methods that WPILib called in turn. In v3, a command is a **method on its subsystem** that reads top to bottom:

```java
// 2026: CmdTurnByAngle.java (one of ten command classes)
public void initialize() { targetAngle = sensors.getHeading() + targetDelta; }
public void execute()    { ... drivetrain.turnInPlace(speed); }
public boolean isFinished() { return Math.abs(wrappedError(...)) < TOLERANCE_DEG; }
public void end(boolean interrupted) { drivetrain.turnInPlace(0); }

// 2027: one method in Drivetrain.java
public Command turnByAngle(double degrees) {
    return run(coroutine -> {
        double target = getHeading() + degrees;          // "initialize"
        while (Math.abs(headingErrorTo(target)) >= TOL) { // "isFinished"
            turnInPlace(speedFor(target));                // "execute"
            coroutine.yield();                            // wait for the next loop
        }
        turnInPlace(0);                                   // "end" (finished)
    }).whenCanceled(this::stopMotors)                     // "end" (interrupted)
      .named("Drivetrain.turnByAngle");
}
```

(Simplified; the real one is split into `turnToHeading`.)

| 2026 class | 2027 method |
|---|---|
| `CmdDrive` | `drivetrain.driveStraight(speed)` |
| `CmdDriveLine` | `drivetrain.followLine(sensors, speed)` |
| `CmdTurnByAngle` / `CmdTurnToAngle` | `drivetrain.turnByAngle(deg)` / `turnToAngle(deg)` |
| `CmdWaitUntilStopped` | `drivetrain.waitUntilStopped()` |
| `CmdWaitUntilDistance` | `drivetrain.waitUntilDistance(m)` |
| `CmdWaitUntilRange` / `CmdWaitUntilMarker` | `sensors.waitUntilRange(m)` / `waitUntilMarker(t)` |
| `CmdServoAngle` | `arm.goTo(deg)`, `arm.up()`, `arm.down()` |
| `CmdWaitUntilTime` | v3's built-in `Command.waitFor(...)` (used by AutoSequence) |

Three v3 rules, all followed everywhere:
1. **`coroutine.yield()` in every loop.** `yield()` means "I'm done for this 20 ms loop, come back next time". A loop without it never gives control back, and the **whole robot program freezes**.
2. **`.named("Subsystem.action")` on every command.** v3 requires it, and it's what shows up in the logs.
3. **Stop the motors in `.whenCanceled(...)`.** Motor safety is off on the XRP (step 5), so nothing else stops them when a command is interrupted.

The 2026 bug fixes were carried over, and tests now guard them (step 11):
- Turns use `turnInPlace`, which **doesn't boost small outputs** to 65%. With the boost, every correction became 65%+ power and the turn could never settle. kP 0.015, minimum 0.5, 5° tolerance.
- The heading error is **wrapped to ±180°** in the loop *and* in the "am I done" check, so the robot turns the short way. The wrap now uses `MathUtil.inputModulus`. The 2026 formula `((e + 180) % 360) - 180` was wrong for errors below -180°, because Java's `%` keeps the minus sign.
- `driveStraight` and `followLine` create their **PID controller inside the command**, so each run starts fresh with no leftover state from the last run.

New: a turn **gives up after 3 s** and raises a Drivetrain alert, so a robot stuck against something can't freeze an auto (1507Base does the same).

## 7. Robot.java and teleop (`Robot.java and DriverTeleop: the 2027 OpMode framework`)

In 2026, `Robot.java` had a dashboard chooser and `autonomousInit()`/`teleopInit()`. In 2027, **each mode is its own class**, and the Driver Station shows drop-downs to pick them (similar to FTC):

| File | Job |
|---|---|
| `Robot.java` | Subsystems, the gamepad, each subsystem's safe default command |
| `teleop/DriverTeleop.java` | `@Teleop(name = "Driver")`: arcade drive, arm bumpers, failsafe on Back |

`RobotContainer.java` (empty) and `Autos.java` (the chooser) were deleted. `RobotBehaviors.java` was started with just `failsafe()`, because the Back button needs it.

**Bindings live in the OpMode.** Commands v3 ties bindings and default commands to the OpMode that creates them, and removes them when you pick a different one. So joystick driving is set in `DriverTeleop`, and when you switch to an auto it goes away by itself.

**`CommandXboxController` → `CommandGamepad`.** 2027 names buttons by position, so any gamepad works: `faceDown()` is A on an Xbox pad.

**The turn stick was backwards.** `DifferentialDrive` treats counterclockwise (left) as positive rotation, and a gamepad reads right as positive X. 2026 passed `+leftX`, so pushing right turned the robot left. It's now `-leftX`, the same as the official 2027 XRP example and 1507Base.

The deadband (0.12) is still applied **before** anything scales the stick value, so drift is removed instead of amplified.

## 8. Autos (`Autos: AutoSequence builder and one @Autonomous class per routine`)

Each 2026 auto is now its own `@Autonomous` class in `auto/routines/`, built with `AutoSequence`, a small version of 1507Base's:

```java
// 2026: Autos.java
new SequentialCommandGroup(
    new ParallelRaceGroup(new CmdDrive(drivetrain, sensors, 0.8), new CmdWaitUntilTime(1)),
    new ParallelRaceGroup(new CmdDrive(drivetrain, sensors, -0.8), new CmdWaitUntilTime(1)));

// 2027: auto/routines/ForwardAndBackAuto.java
@Autonomous(name = "Forward and Back")
public final class ForwardAndBackAuto extends AutoOpMode {
    @Override
    protected Command build() {
        return new AutoSequence()
            .driveStraight(0.8).waitUntilTime(1.0)
            .driveStraight(-0.8).waitUntilTime(2.0)
            .stop()
            .build();
    }
}
```

A **drive step** keeps going until the **next wait step** finishes. Under the hood, that pair is `Command.race(drive, wait)`, the v3 version of `ParallelRaceGroup`. `waitUntilTime` reads the **auto clock** (0 when auto starts), like 1507Base. That's why the second leg says 2.0: "until two seconds into auto".

All five autos kept their speeds and distances:

| Auto | Steps |
|---|---|
| Forward and Back | 0.8 until t = 1 s, then -0.8 until t = 2 s |
| Forward and Back Distance | 0.7 for 0.5 m, then -0.7 for 0.1 m |
| Forward Until 10cm Then Turn | 0.75 until 0.15 m from a wall, turn by 180°, wait until stopped |
| Forward Until 10cm Then Turn 60 | 0.75 until 0.15 m, turn to 60°, wait until stopped |
| Follow Line | follow the line at 0.45 until both sensors read above 0.75 |

**One name had to change.** 2027 limits OpMode names to 32 characters (the build refuses longer ones), and "Forward Until 10cm Then Turn to 60" is 34. It's now "Forward Until 10cm Then Turn 60", with the old name in the annotation's `description`.

**To add an auto:** copy `ForwardAndBackAuto.java`, rename the class and the `@Autonomous` name, and change the steps. There's no list to register it in.

## 9. RobotBehaviors (`RobotBehaviors: failsafe plus a drivetrain + arm example`)

`RobotBehaviors.java` holds actions that use **more than one subsystem**, defined once and used from both teleop and auto. The example, `celebrate()`, raises the arm while turning around, then lowers it:

```java
return Command.requiring(robot.drivetrain, robot.arm).executing(coroutine -> {
    coroutine.awaitAll(robot.arm.up(), robot.drivetrain.turnByAngle(180)); // both at once
    coroutine.await(robot.arm.down());                                     // then this
}).named("Celebrate");
```

Teleop runs it with the A button. Auto runs it with `.celebrate()` in the new "Drive and Celebrate" routine. Same method, both places.

## 10. Constants (`Constants: the 1507Base layout (RobotMap, kDrivetrain.kTuning, kArm, kSensors)`)

Every number someone might want to change moved into `Constants.java`, laid out like 1507Base: `RobotMap` for ports, `kDrivetrain.kTuning` for driving knobs, `kArm`, `kSensors`. Hardware facts that never change (585 encoder counts per wheel turn, 60 mm wheels) stay in `Drivetrain.java`, the way 1507Base keeps them in `SwerveConfig.java`. **No values changed.**

`kArm.UP_DEG`/`DOWN_DEG` now say 50°/130° instead of 0°/180°. The limits always clamped 0°/180° to exactly 50°/130°, so the arm moves the same; the numbers just tell the truth now.

## 11. Tests and CI (`Tests and CI: OpModes, every auto, v3 patterns, the turning fixes`)

`./gradlew build` now runs 15 tests, and GitHub runs them on every push:

| Test | Catches |
|---|---|
| `OpModeTest` | The real robot loop with a simulated Driver Station: all autos + Driver show up, an auto drives when enabled and stops when disabled, teleop driving starts |
| `AutoRoutinesTest` | Any auto that doesn't build; Forward and Back drives forward, back, then stops, at the right times |
| `AutoSequenceTest` | AutoSequence accepting a broken routine |
| `DrivetrainTest` | The ±180° wrap and the turn settling (the 2026 bug fixes) |
| `CommandsV3PatternsTest` | A Commands v3 update changing how race/sequence/yield/failsafe behave |

## 12. A subsystem template (`Add a Subsystem1507 template for "Create a new class/command"`)

Right-click a folder → **Create a new class/command** → **Subsystem1507** gives you a new subsystem with the v3 command shapes already written. Exercise 7 uses it.

---

## What changed in how the robot behaves

Almost everything above changes how the code is *written*, not what the robot *does*. These are the exceptions:

| Change | Why |
|---|---|
| Turn stick direction (right now turns right) | 2026 had it backwards (step 7) |
| Turns give up after 3 s | So a stuck robot can't freeze an auto (step 6) |
| `arm.goTo` waits 0.5 s before finishing | A servo can't report its position (step 6) |
| Turn error wrap correct below -180° | Java's `%` (step 6) |
| "Forward Until 10cm Then Turn to 60" → "…Turn 60" | 32-character OpMode name limit (step 8) |
| New "Drive and Celebrate" auto, A button | RobotBehaviors example (step 9) |

**None of this has been tried on a real XRP yet.** The tests run in simulation only. The first real drive should be done with a mentor, checking directions, distances and turns before running the autos.

## Checklist: doing this yourself next year

1. Install the new WPILib and read the **yearly changelog** and **removed features** pages.
2. Make a new branch. Never migrate on `main`.
3. Check that every vendor library has a build for the new year.
4. Copy the build files from the new project template.
5. Run the importer's rename rules (or use the VS Code importer).
6. Build, then fix errors one group at a time, and commit each group.
7. Look for changes the compiler can't catch (a renamed method that still compiles, a sign convention).
8. Run the tests, then run on the robot.
9. Write down what you changed, like this file.
