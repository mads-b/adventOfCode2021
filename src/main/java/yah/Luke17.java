package yah;

import java.util.ArrayList;
import java.util.List;

public class Luke17 {

  public static void main(String[] args) {
    final String testPuzzleInput = "target area: x=20..30, y=-10..-5";
    final String puzzleInput = "target area: x=153..199, y=-114..-75";

    /* Notes on this one:
     * The projectile will always hit the same Ys on the way down as up.
     * Say vy = 5, path will be
     * y = 5, 9, 12, 14, 15, 15, 14, 12, 9, 5, 0, -6
     * s=vt+at²/2 <-- Experiments indicate that we have to use
     * s=vt+at(t-1)/2 instead because gravity does not work until AFTER the first tick.
     * E.g: pos at t=6 = 5*6-6*5/2 = 15
     *
     * Fun aside: the highest point is reached on t=v. Found by simple partial differentiation of the above..
     * Also: Since we have symmetry. y=0 is reached on t=v*2
     *
     * targetYMin < s < targetYmax for some t.
     * A < vt-t(t-1)/2 < B
     * Plonking this into wolframalpha gives us bounds on t and v for a firing solution to exist!
     */

    final int maxY = -75;
    final int minY = -114;
    final int minX = 153;
    final int maxX = 199;
    /*final int maxY = -5;
    final int minY = -10;
    final int minX = 20;
    final int maxX = 30;*/

    int largestVy = 0;
    int vx = Integer.MAX_VALUE;
    for (int vy = 1; vy < 1000; vy++) {
      int t = vy * 2 + 1; // Tick we are back at y=0
      // This loop can possibly eliminated by finding the integral of pos ds and plonking in the max and min y.
      while (pos(vy, t) < maxY) {
        t++;
      }
      if (pos(vy, t + 1) < minY) {
        break;
      }
      // We want a vx where vx = 0 when t=vy. Initial vx is 1+2+3+4...vy
      int mod = vy - 1;
      final int candVx = maxX - mod * mod / 2;
      vx = candVx > 0 && candVx < vx ? candVx : vx;
      largestVy = vy;
    }
    System.out.println("Highest Y: " + pos(largestVy, largestVy));

    // Fuck it, bruteforcing part two. We now know max y velocity is 113
    final Simulation sim = new Simulation(new Point(minX, maxY), new Point(maxX, minY));
    int uniques = 0;
    for (int nvx = 0; nvx < 10000; nvx++) {
      for (int nvy = -200; nvy < 114; nvy++) {
        final List<Point> fire = sim.simulateFire(new Point(nvx, nvy));
        if (sim.hitsTarget(fire)) {
          uniques++;
        }
      }
    }
    System.out.println("Number of unique firing solutions: " + uniques);
  }

  private static int pos(final int initialV, final int t) {
    return initialV*t -t*(t-1)/2;
  }

  public static class Simulation {

    private final Point targetStart;
    private final Point targetEnd;

    public Simulation(final Point targetStart, final Point targetEnd) {
      this.targetStart = targetStart;
      this.targetEnd = targetEnd;
    }

    public List<Point> simulateFire(final Point velocity) {
      final List<Point> sim = new ArrayList<>();
      Point cur = new Point(0,0);
      Point vel = new Point(velocity.x, velocity.y);
      do {
        // New sim point
        cur = new Point(cur.x + vel.x, cur.y + vel.y);
        sim.add(cur);

        // Adjust velocity
        final int dvx = vel.x == 0 ? 0 : (int)-Math.signum(vel.x);
        vel.x = vel.x + dvx;
        vel.y -=1;
      } while(cur.x <= targetEnd.x && cur.y >= targetEnd.y && !hitsTarget(sim));
      return sim;
    }

    public boolean hitsTarget(final List<Point> points) {
      for (final Point point : points) {
        if (point.x >= targetStart.x
            && point.x <= targetEnd.x
            && point.y <= targetStart.y
            && point.y >= targetEnd.y) {
          return true;
        }
      }
      return false;
    }

    public String showFirePlan(final List<Point> points) {
      int highestY = Math.max(
          points.stream().map(p -> p.y).mapToInt(i -> i).max().orElseThrow(),
          targetStart.y);
      int lowestY = Math.min(
          points.stream().map(p -> p.y).mapToInt(i -> i).min().orElseThrow(),
          targetEnd.y);
      int highestX = Math.max(
          points.stream().map(p -> p.x).mapToInt(i -> i).max().orElseThrow(),
          targetEnd.x);

      final char[][] board = new char[highestY-lowestY+1][highestX+1];

      for (int y = targetEnd.y; y <= targetStart.y; y++) {
        for (int x = targetStart.x; x <= targetEnd.x; x++) {
          board[y - targetEnd.y -targetStart.y + highestY][x] = 'T';
        }
      }
      board[highestY][0] = 'S';
      for (Point p : points) {
        board[highestY - p.y][p.x] = '#';
      }

      final StringBuilder bldr = new StringBuilder();
      for (int y = 0; y < board.length; y++) {
        for (int x = 0; x < board[y].length; x++) {
          bldr.append(board[y][x] != 0 ? board[y][x] : '.');
        }
        bldr.append('\n');
      }
      return bldr.toString();
    }
  }

  public static class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }
}
