//  ██╗    ██╗ █████╗ ██████╗ ██╗      ██████╗  ██████╗██╗  ██╗███████╗
//  ██║    ██║██╔══██╗██╔══██╗██║     ██╔═══██╗██╔════╝██║ ██╔╝██╔════╝
//  ██║ █╗ ██║███████║██████╔╝██║     ██║   ██║██║     █████╔╝ ███████╗
//  ██║███╗██║██╔══██║██╔══██╗██║     ██║   ██║██║     ██╔═██╗ ╚════██║
//  ╚███╔███╔╝██║  ██║██║  ██║███████╗╚██████╔╝╚██████╗██║  ██╗███████║
//   ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝╚══════╝
//                           TEAM 1507 WARLOCKS

package org.team1507.lessons;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.team1507.robot.Robot;

// ─────────────────────────────────────────────────────────────────────────────
// Lesson01Test — checks Lesson 1 (Getting Started). See the wiki.
//
// Run it: .\gradlew lessonTest --tests "*Lesson01*"
// (or click the green ▶ next to the class name in VS Code).
// ─────────────────────────────────────────────────────────────────────────────
@Tag("lesson")
class Lesson01Test {

    @Test
    void programmerNameIsFilledIn() {
        assertNotEquals("TODO", Robot.PROGRAMMER_NAME,
            "Lesson 1: put your name in PROGRAMMER_NAME at the top of Robot.java");
        assertFalse(Robot.PROGRAMMER_NAME.isBlank(),
            "Lesson 1: PROGRAMMER_NAME is empty. Put your name between the quotes.");
    }
}
