# Draws the Warlocks-XRP course diagram used on the wiki (The-Course page).
# Change the measurements below, run `python draw_course.py` (needs matplotlib),
# and commit the new images/course.png to the wiki repo
# (https://github.com/Team1507/Warlocks-XRP.wiki.git). Keep The-Course page's
# measurement table in step with any change.
import os
os.makedirs("images", exist_ok=True)

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.patches import Rectangle, Arc, FancyArrowPatch
import numpy as np

fig, ax = plt.subplots(figsize=(9, 10), dpi=130)
W, H = 240, 270
ax.set_xlim(-5, W + 5); ax.set_ylim(-5, H + 5); ax.set_aspect("equal")
ax.set_xticks(range(0, W + 1, 20)); ax.set_yticks(range(0, H + 1, 20))
ax.set_xticks(range(0, W + 1, 10), minor=True); ax.set_yticks(range(0, H + 1, 10), minor=True)
ax.grid(which="minor", color="#eeeeee", lw=0.5); ax.grid(which="major", color="#dddddd", lw=0.8)
ax.add_patch(Rectangle((0, 0), W, H, fill=False, ec="#999999", lw=1.5, ls="--"))
ax.set_xlabel("x (cm)"); ax.set_ylabel("y (cm)")
ax.set_title("Warlocks-XRP Course (top view, to scale, 10 cm grid)", fontsize=12, weight="bold")

BLUE = "#3b7dd8"; LINE = "#111111"; WALL = "#8b5a2b"; PATH = "#d33"

def robot(cx, cy, heading, label=None):
    r = Rectangle((cx - 10.5, cy - 10.5), 21, 21, fill=True, fc="#f3c94d", ec="#a07d00", lw=1, alpha=0.45)
    ax.add_patch(r)
    dx, dy = {"N": (0, 8), "E": (8, 0)}[heading]
    ax.annotate("", (cx + dx, cy + dy), (cx, cy), arrowprops=dict(arrowstyle="-|>", color="#7a5a00", lw=1.2))
    if label:
        ax.text(cx, cy - 15, label, ha="center", va="top", fontsize=7.5, color="#7a5a00")

# Start box (painter's tape outline)
ax.add_patch(Rectangle((27.5, 10), 25, 25, fill=False, ec=BLUE, lw=2.5))
ax.text(40, 6, "START box 25×25\n(back edge on y=10)", ha="center", va="top", fontsize=8, color=BLUE)
robot(40, 20.5, "N")

# Section A: lane guide + target A
ax.plot([26, 26], [35, 131], color=BLUE, lw=1.5)
ax.text(24, 80, "lane guide x=26", rotation=90, ha="right", va="center", fontsize=7.5, color=BLUE)
ax.plot([12, 24], [131, 131], color=BLUE, lw=3)
ax.text(10, 134, "TARGET A\nfront edge y=131", ha="left", va="bottom", fontsize=8, color=BLUE)
robot(40, 120.5, "N", "1")
ax.annotate("", (60, 131), (60, 31), arrowprops=dict(arrowstyle="<->", color="#555"))
ax.text(62, 81, "100 cm", fontsize=8, color="#555", va="center")

# Section B: wall + stop mark
ax.add_patch(Rectangle((150, 90), 5, 60, fc=WALL, ec=WALL))
ax.text(158, 120, "WALL 1\nx=150, y=90–150\n≥20 cm tall", fontsize=8, color=WALL, va="center")
ax.plot([135, 135], [95, 105], color=BLUE, lw=3)
ax.text(135, 92, "stop mark x=135", ha="center", va="top", fontsize=7.5, color=BLUE)
ax.annotate("", (150, 99), (135, 99), arrowprops=dict(arrowstyle="<->", color="#555"))
ax.text(142.5, 101, "15", ha="center", fontsize=7.5, color="#555")
robot(124.5, 120.5, "E", "2")

# Section C: line
ax.plot([124.5, 124.5], [151, 191], color=LINE, lw=3.5, solid_capstyle="butt")
t = np.linspace(np.pi, np.pi / 2, 60)
ax.plot(174.5 + 50 * np.cos(t), 191 + 50 * np.sin(t), color=LINE, lw=3.5)
ax.plot([174.5, 214.5], [241, 241], color=LINE, lw=3.5, solid_capstyle="butt")
ax.add_patch(Rectangle((212.5, 231), 4, 20, fc=LINE, ec=LINE))
ax.text(114, 171, "LINE\nstarts y=151", ha="right", va="center", fontsize=8)
ax.text(160, 200, "curve r = 50 cm\ncenter (174.5, 191)", fontsize=7.5, ha="left", color="#333")
ax.text(219, 241, "FINISH\nmarker", fontsize=8, va="center", weight="bold")
ax.annotate("", (128, 151), (128, 131), arrowprops=dict(arrowstyle="<->", color="#555"))
ax.text(130, 141, "20", fontsize=7.5, color="#555", va="center")
# finish box corners
for (x, y, sx, sy) in [(188, 224, 1, 1), (228, 224, -1, 1), (188, 258, 1, -1), (228, 258, -1, -1)]:
    ax.plot([x, x + 6 * sx], [y, y], color=BLUE, lw=2); ax.plot([x, x], [y, y + 6 * sy], color=BLUE, lw=2)
ax.text(208, 262, "finish box (corner marks)", ha="center", fontsize=7.5, color=BLUE)
robot(204, 241, "E", "3")

# Path
ax.plot([40, 40], [36, 108], color=PATH, lw=1.2, ls=":")
ax.plot([52, 112], [120.5, 120.5], color=PATH, lw=1.2, ls=":")
ax.text(46, 60, "A: drive 100 cm\n(encoders)", color=PATH, fontsize=8.5, weight="bold")
ax.text(66, 108, "B: turn right, drive to 15 cm\nfrom the wall (ultrasonic)", color=PATH, fontsize=8.5, weight="bold")
ax.text(70, 225, "C: turn left, find the line,\nfollow it to the marker\n(reflectance)", color=PATH, fontsize=8.5, weight="bold")

fig.text(0.5, 0.01, "Yellow squares = the robot (21×21 cm) at each stop.   Blue = painter's tape marks (keep them away from the line).\n"
         "Black = 19 mm black electrical tape.   Brown = a wall (cardboard box or foam board).", ha="center", va="bottom", fontsize=8)
fig.tight_layout(rect=(0, 0.04, 1, 1))
fig.savefig("images/course.png", bbox_inches="tight")
print("ok")
