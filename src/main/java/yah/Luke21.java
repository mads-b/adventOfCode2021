package yah;

import java.util.HashMap;
import java.util.Map;

public class Luke21 {
  private static final Map<Integer, Integer> SAMPLE_SPACE = new HashMap<>();

  public static void main(String[] args) {
    for (int i = 1; i < 4; i++) {
      for (int j = 1; j < 4; j++) {
        for (int k = 1; k < 4; k++) {
          SAMPLE_SPACE.compute(i+j+k, (kk, v) -> v == null ? 1 : v+1);
        }
      }
    }
    System.out.println("My sample space: " + SAMPLE_SPACE);

    // Sample
    //int p1Pos = 3;
    //int p2Pos = 7;
    int p1Pos = 6;
    int p2Pos = 3;

    int p1Points = 0;
    int p2Points = 0;

    final Die d = new Die();
    while (true) {
      p1Pos = (p1Pos + d.get() + d.get() + d.get()) % 10;
      p1Points += p1Pos+1;
      if (p1Points >= 1000) {
        break;
      }
      p2Pos = (p2Pos + d.get() + d.get() + d.get()) % 10;
      p2Points += p2Pos+1;
      if (p2Points >= 1000) {
        break;
      }
    }
    System.out.println("Part 1: " + Math.min(p1Points, p2Points) * d.numRolls);

    // Sample
    //p1Pos = 3;
    //p2Pos = 7;
    p1Pos = 6;
    p2Pos = 3;
    System.out.println("Part 2: " + splitUniverse(p1Pos, p2Pos, 0,0, true));
    // Got p1: 570239341223618
    // Got p2: 371697814511699
  }

  // 21 max tree height isn't too bad. I'll just solve with recursion. The method returns the number of times P1 wins
  private static long splitUniverse(final int p1Pos, final int p2Pos, final int p1Points, final int p2Points, final boolean isP1Turn) {
    if (p1Points > 20) {
      return 0;
    } else if (p2Points > 20) {
      return 1;
    }
    long totalPoints = 0;
    for (final Map.Entry<Integer, Integer> sample : SAMPLE_SPACE.entrySet()) {
      if (isP1Turn) {
        final int newP1Pos = (p1Pos + sample.getKey()) % 10;
        final int newP1Points = p1Points + newP1Pos + 1;
        totalPoints += sample.getValue() * splitUniverse(newP1Pos, p2Pos, newP1Points, p2Points, false);
      } else {
        final int newP2Pos = (p2Pos + sample.getKey()) % 10;
        final int newP2Points = p2Points + newP2Pos + 1;
        totalPoints += sample.getValue() * splitUniverse(p1Pos, newP2Pos, p1Points, newP2Points, true);
      }
    }
    return totalPoints;
  }

  private static class Die {
    int num = 1;
    int numRolls = 0;

    public int get() {
      numRolls++;
      int n = num;
      if (num == 100) {
        num = 1;
      } else {
        num++;
      }
      return n;
    }
  }

}
