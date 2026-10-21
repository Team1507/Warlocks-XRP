# Exercises

Do these **in order**, on **your own branch** (`student/<firstname>`; see the README). Each one builds on the ones before it. Commit after each exercise, with a message that says what you changed and why.

After every change:
1. Run `.\gradlew build` (or just **Simulate Robot Code**). A red build tells you what's wrong before anything moves.
2. Try it on the XRP: pick the mode in the Driver Station drop-down, Enable, watch, Disable.

To **see logged values**, open AdvantageScope (in the WPILib 2027 tools) → connect to the simulator → look under `Telemetry/`, e.g. `Telemetry/Drivetrain/LeftThrottle`.

---

## 1. Tune the deadband ⭐

**Goal:** understand what a deadband does by changing it.

A gamepad stick never rests at exactly 0; it might read 0.03 or -0.05. Without a deadband, that tiny value slowly drives the robot. The **deadband** says "anything smaller than this counts as 0".

1. Open `Constants.java` and find `DRIVER_DEADBAND` (in `kDrivetrain.kTuning`). It's `0.12`.
2. Run the **Driver** teleop. Let go of the stick and watch `Telemetry/Drivetrain/LeftThrottle`. It should be exactly 0.
3. Change the deadband to `0.0` and run again. Let go of the stick. What does LeftThrottle show? Does the robot creep?
4. Change it to `0.4`. How does driving feel now? Try to drive slowly.
5. Pick the smallest value that keeps the robot still when you let go, and commit it.

**Think about it:** in `DriverTeleop.java`, the deadband is applied *before* the value goes to the drivetrain. Why would applying it *after* a scaling step be worse?

## 2. Log a new value ⭐

**Goal:** add your own number to the logs.

Every subsystem has a `periodic()` method that runs every 20 ms. Anything you `log(...)` there shows up under `Telemetry/<Subsystem>/`.

1. Open `subsystems/Drivetrain.java` and find `periodic()`.
2. Add a line that logs the average distance driven:
   ```java
   log("AverageDistanceMeters", getAverageDistance());
   ```
3. Run **Forward and Back** and watch your new value. It should climb while the robot drives forward, then come back down toward 0.
4. Now log something nobody logs yet: is the robot turning left right now? (Hint: `getLeftThrottle()` and `getRightThrottle()`, and remember the right one reads negative when that wheel goes forward.)

**Name values by what they mean, with the unit:** `AverageDistanceMeters`, not `dist` or `value2`.

## 3. Write your own auto from the template ⭐⭐

**Goal:** write autonomous routines yourself.

`auto/routines/` has two files: `ForwardAndBackAuto.java`, a finished example, and `TemplateAuto.java`, a blank auto for you to fill in. The template's comment lists every step you can use.

1. Read `ForwardAndBackAuto.java` and run **Forward and Back**. Match each line of `build()` to what the robot does.
2. Open `TemplateAuto.java`. Rename the class and file to `SquareAuto`, and change `@Autonomous(name = ...)` to `"Square"`.
3. Fill in `build()` to drive a square: drive 0.3 m, turn 90°, four times, then stop.
4. Run it. "Square" appears in the Driver Station by itself. You didn't register it anywhere.
5. **Break it on purpose:** delete one of your wait steps and build. Read the error. `AutoRoutinesTest` caught your mistake before the robot moved. Put it back.

**Think about it:** does the robot end up exactly where it started? If not, which steps are adding the error?

**More autos to write.** Copy the template again for each one. These are the autos our 2026 XRP code had; work out the steps yourself:

| Auto | What the robot should do |
|---|---|
| Forward and Back Distance | Drive forward at 0.7 for 0.5 m, then backward at 0.7 for 0.1 m. (Like Forward and Back, but by distance. Why is that better when the battery gets low?) |
| Wall Then Turn Around | Drive forward at 0.75 until something is 0.15 m in front, turn around, wait until the robot has stopped. |
| Wall Then Face 60 | Same, but end up facing 60° (absolute) instead of turning around. What's the difference between `turnBy` and `turnTo`? |
| Follow Line | Follow a black tape line at 0.45 until both line sensors read above 0.75 (a crossbar of tape at the end). |
| Drive and Celebrate | Drive 0.3 m, then run the `celebrate` behavior. |

## 4. Write a coroutine command ⭐⭐

**Goal:** write a Commands v3 command, and understand `coroutine.yield()`.

You'll add a command that spins the robot in place for a number of seconds.

1. In `Drivetrain.java`, in the Commands section, add:
   ```java
   /** Spins in place for {@code seconds}, then stops. */
   public Command spinFor(double seconds) {
       return run(coroutine -> {
           double start = Timer.getTimestamp();
           while (Timer.getTimestamp() - start < seconds) {
               turnInPlace(0.6);
               coroutine.yield();
           }
           stopMotors();
       }).whenCanceled(this::stopMotors).named("Drivetrain.spinFor");
   }
   ```
2. Bind it in `DriverTeleop.java`: `robot.driver.faceRight().onTrue(robot.drivetrain.spinFor(1.0));`
3. Run **Driver** and press B (the right face button).

**Why `yield()` matters.** A command doesn't get its own thread. The scheduler runs every command, one after another, in the same 20 ms loop. `coroutine.yield()` means "I'm done for this loop, come back to me next loop". If your `while` loop never yields, the scheduler never gets control back: no other command runs, `periodic()` stops, logging stops, and the robot program is frozen.

**See it safely:** with the XRP **turned off** (simulation only), delete the `coroutine.yield();` line, run, and press B. Watch the Sim GUI and `Telemetry/Robot/LoopPeriodMs`. Then put `yield()` back. (Don't try this with the XRP on: a frozen program can't stop the motors.)

**Questions:**
- Why does `spinFor` call `stopMotors()` in two places?
- What happens if you press Back (failsafe) while it's spinning? Which of the two `stopMotors()` calls runs?
- Why is it `.named("Drivetrain.spinFor")` and not just `"spin"`?

## 5. Add a warnIf alert ⭐⭐

**Goal:** make the robot tell the driver about a problem.

`warnIf(condition, message)` shows a yellow alert on the dashboard **while** the condition is true, clears it when it's false, and prints the message to the Driver Station once each time it turns on.

1. In `Sensors.java`, in `periodic()`, add:
   ```java
   warnIf(getRangeMeters() < 0.10, "Something is closer than 10 cm in front");
   ```
2. Run any mode and put your hand in front of the rangefinder. Watch the Driver Station messages, and the alert in your dashboard (in Elastic, add an Alerts widget; this alert is in the "Sensors" group).
3. Move your hand away. The alert clears by itself.
4. Add a second alert of your own. Ideas: the arm was asked for an angle outside its limits (look at `requestedDegrees` in `Arm.java`), or the battery is low (`RobotController.getBatteryVoltage()`).

**`warnIf` vs `warn`:** use `warnIf` for a problem that can go away by itself (a hand in front of the sensor). Use `warn` for something that happened once and the driver should know about (a turn that gave up; see `turnToHeading` in `Drivetrain.java`).

## 6. Write a RobotBehavior ⭐⭐⭐

**Goal:** combine two subsystems into one action, used from teleop **and** auto.

You'll write `backAway()`: lower the arm while backing up 0.2 m, then raise the arm.

1. Read `celebrate()` in `RobotBehaviors.java`. Yours has the same shape.
2. Write `backAway(Robot robot)`. Backing up 0.2 m is a race between a drive and a wait, the same thing AutoSequence builds:
   ```java
   Command backUp = Command.race(
       robot.drivetrain.driveStraight(-0.6),
       robot.drivetrain.waitUntilDistance(0.2)
   ).named("BackAway.backUp");
   ```
   Then `awaitAll(robot.arm.down(), backUp)`, then `await(robot.arm.up())`. Name it `"BackAway"`.
3. **Teleop:** bind it to X (`faceLeft()`) in `DriverTeleop`.
4. **Auto:** add a one-line wrapper to `AutoSequence.java` next to `celebrate()`, and use it in a new routine (copy `TemplateAuto.java`): drive forward until 0.15 m from a wall, then back away.
5. Press Back (failsafe) in the middle of it. Does everything stop? Why? (Hint: what does `Command.requiring(...)` do that `Command.noRequirements(...)` doesn't?)

**Name behaviors by what the robot does:** `backAway()`, not `armDownDriveBackArmUp()`.

## 7. Stretch: add a new subsystem from the template ⭐⭐⭐⭐

**Goal:** build a whole subsystem the way it's done on the competition robot.

The XRP has an onboard LED and a user button (`XRPOnBoardIO`) that nothing uses yet. Make an `OnBoard` subsystem for them.

1. Right-click the `subsystems` folder → **Create a new class/command** → **Subsystem1507**. Name it `OnBoard`.
2. Fix the package line to `org.team1507.robot.subsystems`, and replace the class comment's TODO with *why* this is its own subsystem.
3. Hardware: `private final XRPOnBoardIO io = new XRPOnBoardIO();`
4. `periodic()`: log the button (`io.getUserButtonPressed()`) and whether the LED is on (`io.getLed()`).
5. Commands:
   - `blink(int times)`: LED on, wait 0.2 s, off, wait 0.2 s, repeated. Use `coroutine.wait(Seconds.of(0.2))`.
   - `idle()`: LED off, then `coroutine.park()`.
6. Add it to `Robot.java` (a field, construct it, `setDefaultCommand(onBoard.idle())`).
7. Use it: blink twice at the end of one of your autos (add an AutoSequence wrapper), and blink once in teleop when the arm moves.
8. **Use the button as a trigger:** `new Trigger(robot.onBoard::isButtonPressed).onTrue(...)` in `DriverTeleop`. Pressing the button on the robot runs a command.
9. Write a test in `src/test/` that schedules `blink(2)`, steps the robot 1 second (see `TestRobot.step`), and checks the LED ended up off.

When it works, open 1507Base's `Subsystem1507.java` and its template. The only real difference is how motors are created. You've just written a 1507Base-style subsystem.
