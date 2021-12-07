package yah;

import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Luke6 {

  public static void main(String[] args) throws Exception {
    final String[] lines = Resources.toString(Resources.getResource("luke6.txt"), StandardCharsets.UTF_8).trim().split(",");

    final Gens gens = new Gens(lines);
    for (long i = 1; i<=256; i++) {
      gens.addTime();
    }
    System.out.println("FishNum " + Arrays.stream(gens.buckets).sum());
  }

  private static class Gens {

    final long[] buckets = new long[9];

    public Gens(final String[] seeds) {
      for (final String item : seeds) {
        buckets[Integer.parseInt(item)]++;
      }
    }

    private void addTime() {
      final long zeroBucket = buckets[0];

      for (int i = 1; i < buckets.length; i++) {
        buckets[i-1] = buckets[i];
      }
      buckets[buckets.length-1] = zeroBucket; // Spawn new gens
      buckets[6] += zeroBucket; // Add zero fish to timer 6
    }

    public String toString() {
      return Arrays.toString(buckets);
    }
  }
}
