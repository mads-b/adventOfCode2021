package yah;

import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Luke7 {

  public static void main(String[] args) throws Exception {
    final List<Integer> pos = Arrays.stream(Resources.toString(Resources.getResource("luke7.txt"), StandardCharsets.UTF_8)
        .trim()
        .split(","))
        .map(Integer::parseInt)
        .collect(Collectors.toList());

    long min = Long.MAX_VALUE;
    long max = 0;
    long sum = 0;
    for (int num : pos) {
      min = Math.min(num, min);
      max = Math.max(num, max);
      sum += num;
    }
    System.out.println("Average: " + (sum / pos.size()));
   final long avg = sum/pos.size(); // Good upper bound

    long smallestDist = Long.MAX_VALUE;
    long smallestNum = -1;
    for (long i = min; i < max; i++) {

      long dist = 0;
      for (int num : pos) {
        dist += fuelUse(Math.abs(num - i));
      }
      System.out.println("Trying " + i + " with dist " + dist);

      if (dist < smallestDist) {
        smallestDist = dist;
        smallestNum = i;
      }
    }


    System.out.println(smallestNum + " with cost " + smallestDist);

  }

  public static long fuelUse(final long dist) {
    return dist*(dist+1)/2;
  }
}
