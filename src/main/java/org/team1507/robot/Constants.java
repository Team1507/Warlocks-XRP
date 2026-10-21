//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.robot;

// ─────────────────────────────────────────────────────────────────────────────
// Constants
//
// Every number someone might need to find or change, in one place, laid out
// the same way as 1507Base:
//   RobotMap       which port each device is plugged into
//   kDrivetrain    driving knobs (kTuning: adjusted while tuning)
//   kArm           arm angles and limits
//   kSensors       sensor thresholds
//
// WHY A CONSTANTS FILE: when the robot turns too far, you want ONE place to
// look for "how hard does it turn", not a number buried in a command. And a
// number used in two places can't get out of sync.
//
// Hardware facts that can never change (encoder counts per wheel revolution,
// wheel size) stay in Drivetrain.java, the way 1507Base keeps them in
// SwerveConfig.java.
// ─────────────────────────────────────────────────────────────────────────────
public final class Constants {

    private Constants() {}

    // ============================================================
    // Hardware Map — ports. On the XRP most of these are wired
    // on the board and can't be changed; they are listed here so
    // nothing in the code is a mystery number.
    // ============================================================

    public static final class RobotMap {

        // Driver Station USB ports
        public static final int DRIVER_CONTROLLER = 0;

        // Drive motors (XRP motor channels, fixed by the board)
        public static final int LEFT_MOTOR  = 0;
        public static final int RIGHT_MOTOR = 1;

        // Wheel encoders (DIO channels, fixed by the board)
        public static final int LEFT_ENCODER_A  = 4;
        public static final int LEFT_ENCODER_B  = 5;
        public static final int RIGHT_ENCODER_A = 6;
        public static final int RIGHT_ENCODER_B = 7;

        // Servo port the arm is plugged into
        public static final int ARM_SERVO = 4;
    }

    // ============================================================
    // Drivetrain tuning: knobs adjusted while tuning.
    // ============================================================

    public static final class kDrivetrain {

        public static final class kTuning {

            /** Joystick deadband for teleop driving: stick input below this counts as zero. */
            public static final double DRIVER_DEADBAND = 0.12;

            /**
             * Straight driving in auto boosts any forward power to at least this
             * much, so the XRP's motors overcome static friction. The heading
             * correction is NOT boosted (see Drivetrain.arcadeDriveWithCorrection).
             */
            public static final double MIN_FORWARD_OUTPUT = 0.65;

            /** Heading-hold PID for driveStraight (power per degree off course). */
            public static final double HEADING_KP = 0.002;
            public static final double HEADING_KI = 0.0;
            public static final double HEADING_KD = 0.001;

            /**
             * Turn commands: power = TURN_KP × degrees of error, raised to at least
             * MIN_TURN_SPEED (or the XRP doesn't move) and capped at MAX_TURN_SPEED.
             */
            public static final double TURN_KP        = 0.015;
            public static final double MIN_TURN_SPEED = 0.5;
            public static final double MAX_TURN_SPEED = 0.9;

            /** A turn is done when the heading is within this many degrees of the target. */
            public static final double TURN_TOLERANCE_DEG = 5.0;

            /** A turn gives up after this long, so a robot stuck against a wall can't freeze an auto (seconds). */
            public static final double MAX_TURN_SECONDS = 3.0;

            /** Below this wheel speed on both sides, the robot counts as stopped (m/s). */
            public static final double STOPPED_METERS_PER_SECOND = 0.01;
        }
    }

    // ============================================================
    // Arm (servo)
    // ============================================================

    public static final class kArm {

        /**
         * The arm's safe range. Every angle request is clamped to it (Arm.setAngle).
         */
        public static final double MIN_DEG = 50.0;
        public static final double MAX_DEG = 130.0;

        /**
         * Where the bumpers send the arm. (2026 asked for 0° and 180°, and the
         * limits above turned that into 50° and 130°. Same movement; now the
         * numbers say where the arm really goes.)
         */
        public static final double UP_DEG   = MIN_DEG;
        public static final double DOWN_DEG = MAX_DEG;

        /** How long Arm.goTo waits for the servo to get there. The servo can't report its position (seconds). */
        public static final double MOVE_SECONDS = 0.5;

        /** Arm.isAtAngle tolerance (degrees). */
        public static final double AT_ANGLE_TOLERANCE_DEG = 2.0;
    }

    // ============================================================
    // Sensors
    // ============================================================

    public static final class kSensors {

        // ---- Ultrasonic rangefinder (Lesson 3) ----

        /** isObjectClose() is true at this distance or closer (meters). 15 cm = the course's wall stop. */
        public static final double CLOSE_METERS = 0.15;

        /** getRangeZone(): closer than this is "NEAR" (meters). */
        public static final double NEAR_METERS = 0.30;

        /** getRangeZone(): closer than this is "TOO_CLOSE" (meters). */
        public static final double TOO_CLOSE_METERS = 0.05;

        // ---- Line (reflectance) sensors (Lessons 4 and 5) ----

        /**
         * Reflectance above this means that sensor is over black tape
         * (0.0 = white, 1.0 = black).
         * LESSON 5: measure YOUR floor and tape, then set this between them.
         */
        public static final double LINE_THRESHOLD = 0.7;
    }
}
