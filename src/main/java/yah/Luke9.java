package yah;

import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Luke9 {

  public static void main(String[] args) throws Exception {
    final int[][] ssds = Resources.readLines(Resources.getResource("luke9.txt"), StandardCharsets.UTF_8)
        .stream()
        .map(s -> s.chars().map(i -> Character.digit(i, 10)).toArray())
        .toArray(int[][]::new);

    final List<Point> minimas = new ArrayList<>();

    for (int i = 0; i < ssds.length; i++) {
      for (int j = 0; j < ssds[i].length; j++) {
        int cur = ssds[i][j];
        boolean largerTop = j == 0 || ssds[i][j-1] > cur;
        boolean largerBottom = j == ssds[i].length - 1 || ssds[i][j+1] > cur;
        boolean largerLeft = i == 0 || ssds[i-1][j] > cur;
        boolean largerRight = i == ssds.length - 1 || ssds[i+1][j] > cur;

        if (largerTop && largerBottom && largerLeft && largerRight) {
          minimas.add(new Point(i, j, cur));
        }
      }
    }
    System.out.println("RiskSum is " + minimas.stream().map(p -> p.value+1).mapToInt(p -> p).sum());

    final boolean[][] traversalMonitor = new boolean[ssds.length][ssds[0].length];
    final List<Integer> basins = new ArrayList<>();
    for (final Point minima : minimas) {
      final int basinSize = countNonNines(ssds, traversalMonitor, minima.i, minima.j);
      basins.add(basinSize);
    }
    basins.sort(Comparator.comparingInt(a -> -a));

    final Iterator<Integer> basinIt = basins.iterator();
    System.out.println("Multiplied number: " + (basinIt.next()*basinIt.next()*basinIt.next()));
  }

  //
  private static int countNonNines(final int[][] arr, final boolean[][] traversalMonitor, final int i, final int j) {
    // Bounds check and checking if already traversed or check if nine
    if (i < 0 || i > arr.length-1 || j < 0 || j > arr[i].length - 1 || arr[i][j] == 9 || traversalMonitor[i][j]) {
      return 0;
    }
    traversalMonitor[i][j] = true;

    return 1
        + countNonNines(arr, traversalMonitor, i+1, j)
        + countNonNines(arr, traversalMonitor, i-1, j)
        + countNonNines(arr, traversalMonitor, i, j+1)
        + countNonNines(arr, traversalMonitor, i, j-1);
  }

  private static class Point {
    final int i;
    final int j;
    final int value;

    public Point(final int i, final int j, final int value) {
      this.i = i;
      this.j = j;
      this.value = value;
    }
  }
}
