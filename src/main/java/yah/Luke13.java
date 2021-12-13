package yah;

import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.google.common.io.Resources;

public class Luke13 {

  public static void main(String[] args) throws Exception {
    final List<String> list = Resources.readLines(Resources.getResource("luke13.txt"), StandardCharsets.UTF_8);
    int bigX = 0;
    int bigY = 0;
    final List<Map.Entry<Integer, Integer>> coords = list.stream().filter(s -> s.contains(","))
        .map(s -> s.split(","))
        .map(a -> new SimpleEntry<>(Integer.parseInt(a[0]), Integer.parseInt(a[1])))
        .collect(Collectors.toList());
    for (Map.Entry<Integer, Integer> coord : coords) {
      bigX = Math.max(coord.getKey(), bigX);
      bigY = Math.max(coord.getValue(), bigY);
    }
    System.out.println("Start");

    final boolean[][] arr = new boolean[bigX+1][bigY+1];

    for (Map.Entry<Integer, Integer> coord : coords) {
      arr[coord.getKey()][coord.getValue()] = true;
    }

    for (final String str : list) {
      if (str.startsWith("fold along x=")) {
        foldHorizontally(arr, Integer.parseInt(str.substring(13)));
      }
      if (str.startsWith("fold along y=")) {
        foldVertically(arr, Integer.parseInt(str.substring(13)));
      }
    }
    System.out.println(toString(arr));
  }

  private static String toString(final boolean[][] arr) {
    int dots = 0;
    final StringBuilder b = new StringBuilder();
    for (int y = 0; y < arr[0].length; y++) {
      boolean hasTrue = false;
      // Output was too large to print so we just omit empty rows.
      for (int x = 0; x< arr.length; x++) {
        hasTrue |= arr[x][y];
      }
      if (!hasTrue) {
        continue;
      }
      for (int x = 0; x < arr.length; x++) {
        dots += arr[x][y] ? 1 : 0;
        b.append(arr[x][y] ? '#' : '.');
      }
      b.append('\n');
    }
    b.append("this is " + dots + " dots.");
    return b.toString();
  }

  private static void foldHorizontally(boolean[][] arr, int foldLine) {
    for (int y = 0; y < arr[0].length; y++) {
      for (int x = foldLine; x < arr.length; x++) {
        if (arr[x][y]) {
          final int newX = 2*foldLine - x;
          arr[newX][y] = true;
          arr[x][y] = false;
        }
      }
    }
  }

  private static void foldVertically(final boolean[][] arr, final int foldLine) {
    for (int y = foldLine; y < arr[0].length; y++) {
      for (int x = 0; x < arr.length; x++) {
        if (arr[x][y]) {
          final int newY = 2*foldLine - y;
          arr[x][newY] = true;
          arr[x][y] = false;
        }
      }
    }
  }
}
