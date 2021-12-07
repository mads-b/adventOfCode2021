package yah;

import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Luke5 {

  public static void main(String[] args) throws Exception {
    final List<Line> lines = Resources.readLines(Resources.getResource("luke5.txt"), StandardCharsets.UTF_8)
        .stream()
        .map(str -> str.split(" -> "))
        .map(arr -> new Coord[]{new Coord(arr[0]), new Coord(arr[1])})
        .map(arr -> new Line(arr))
        .collect(Collectors.toList());

    final Mapp mapp = new Mapp();
    for (final Line line : lines) {
      for (final Coord item : line.getCoords()) {
        mapp.put(item);
      }
    }

    System.out.println(mapp);
  }

  private static final class Mapp {

    private final Map<Integer, Map<Integer, Integer>> sparseArray = new HashMap<>();

    public void put(final Coord coord) {
      if (!sparseArray.containsKey(coord.x)) {
        sparseArray.put(coord.x, new HashMap<>());
      }

      sparseArray.get(coord.x).compute(coord.y, (k, v) -> v == null ? 1 : v+1);
    }

    public String toString() {
      int bixX = 0;
      int bixY = 0;
      for (Map.Entry<Integer, Map<Integer, Integer>> ys : sparseArray.entrySet()) {
        bixX = Math.max(bixX, ys.getKey());
        for (Map.Entry<Integer, Integer> yys : ys.getValue().entrySet())
          bixY = Math.max(bixY, yys.getKey());
      }
      int atLeastTwoOverlap = 0;
      final StringBuilder sb = new StringBuilder();

      for (int y = 0; y <= bixY; y++) {
        for (int x = 0; x <= bixX; x++) {
          if (sparseArray.containsKey(x) && sparseArray.get(x).containsKey(y)) {
            sb.append(sparseArray.get(x).get(y));
            if (sparseArray.get(x).get(y) > 1) {
              atLeastTwoOverlap++;
            }
          } else {
            sb.append(".");
          }
        }
        sb.append('\n');
      }
      return sb.append("\n Overlaps: " + atLeastTwoOverlap).toString();
    }
  }

  private static final class Line {
    private Coord[] pair;

    public Line(Coord[] pair) {
      this.pair = pair;
    }

    public List<Coord> getCoords() {
      final List<Coord> coords = new ArrayList<>();
      final int distX = Math.abs(pair[0].x - pair[1].x);
      final int distY = Math.abs(pair[0].y - pair[1].y);

      if (distX == 0) { // Vertical
        final int big = Math.max(pair[0].y, pair[1].y);
        final int small = Math.min(pair[0].y, pair[1].y);
        for (int i = small; i<= big; i++) {
          coords.add(new Coord(pair[0].x, i));
        }
      } else if (distY == 0) { // Horizontal
        final int big = Math.max(pair[0].x, pair[1].x);
        final int small = Math.min(pair[0].x, pair[1].x);
        for (int i = small; i<= big; i++) {
          coords.add(new Coord(i, pair[0].y));
        }
      } else if (distX == distY) { // Diagonal
        final int smallY = Math.min(pair[0].y, pair[1].y);
        final int bigY = Math.max(pair[0].y, pair[1].y);

        final int xFrom = pair[0].y == smallY ? pair[0].x : pair[1].x;
        final int xTo = pair[0].y == bigY ? pair[0].x : pair[1].x;
        final int mult = xTo < xFrom ? -1 : 1;

        for (int i = 0; i <= distX; i++) {
          coords.add(new Coord( xFrom + mult*i, smallY + i));
        }
      }


      return coords;
    }
  }

  private static final class Coord {

    private int x;
    private int y;

    public Coord(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public Coord(final String coord) {
      final String[] xy = coord.split(",");
      if (xy.length != 2) {
        throw new IllegalArgumentException("Not a coord " + coord);
      }
      x = Integer.parseInt(xy[0]);
      y = Integer.parseInt(xy[1]);

    }

    public String toString() {
      return "(" + x + "," + y + ")";
    }
  }
}
