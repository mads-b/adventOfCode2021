package yah;

import com.google.common.io.Resources;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Luke15 {

  public static void main(String[] args) throws IOException {
    final int[][] intBoard = Resources.readLines(Resources.getResource("luke15.txt"), StandardCharsets.UTF_8)
        .stream()
        .map(s -> s.chars().map(i -> Character.digit(i, 10)).toArray())
        .toArray(int[][]::new);
    final int height = intBoard.length;
    final int width = intBoard[0].length;
    final int[][] boostedIntBoard = new int[height*5][width*5];
    for (int boostI = 0; boostI < 5; boostI++) {
      for (int boostJ = 0; boostJ < 5 ; boostJ++){
        for (int i = 0; i < height; i++) {
          for (int j = 0; j < width; j++) {
            int val = (intBoard[i][j] + boostI + boostJ);
            boostedIntBoard[i + height * boostI][j + width * boostJ] = val > 9 ? val-9 : val;
          }
        }
      }
    }
    System.out.println("Play size: " + boostedIntBoard.length + "X" + boostedIntBoard[0].length);

    final Pos[][] board = new Pos[height*5][width*5];

    for (int i = 0; i < height*5; i++) {
      for (int j = 0; j < width*5; j++) {
        board[i][j] = new Pos(i, j, boostedIntBoard[i][j]);
      }
    }
    for (int i = 0; i < height*5; i++) {
      for (int j = 0; j < width*5; j++) {
        board[i][j].initNeighbors(board);
      }
    }

    fuckYeahDjikstra(board);
    final Pos end = board[height*5-1][width*5-1];
    System.out.println("Dist: " + end.distance + " with path to goal : " + end.shortestPath);
  }

  public static void fuckYeahDjikstra(final Pos[][] board) {
    final Pos origin = board[0][0];
    origin.distance = 0;
    origin.shortestPath = new LinkedList<>();

    Set<Pos> visited = new HashSet<>();
    Set<Pos> unvisited = new HashSet<>();
    unvisited.add(origin);

    while (unvisited.size() != 0) {
      Pos current = null;
      long minDist = Long.MAX_VALUE;
      for (Pos p : unvisited) {
        if (p.distance < minDist) {
          minDist = p.distance;
          current = p;
        }
      }
      unvisited.remove(current);
      for (Pos neighbor : current.neighbors) {
        if (!visited.contains(neighbor)) {
          calculateMinimumDistance(neighbor, current);
          unvisited.add(neighbor);
        }
      }
      visited.add(current);
    }
  }

  private static void calculateMinimumDistance(Pos evaluationNode, Pos sourceNode) {
    long sourceDistance = sourceNode.distance;
    if (sourceDistance + evaluationNode.cost < evaluationNode.distance) {
      evaluationNode.distance = (sourceDistance + evaluationNode.cost);
      LinkedList<Pos> shortestPath = new LinkedList<>(sourceNode.shortestPath);
      shortestPath.add(sourceNode);
      evaluationNode.shortestPath = shortestPath;
    }
  }

  public static class Pos {
    private int i;
    private int j;
    private int cost;
    private List<Pos> shortestPath;
    private long distance = Long.MAX_VALUE;
    private Set<Pos> neighbors;

    public Pos(final int i, final int j, final int cost) {
      this.i = i;
      this.j = j;
      this.cost = cost;
    }

    private void initNeighbors(Pos[][] board) {
      final Set<Pos> neighbors = new HashSet<>();
      addPointIfInBounds(board, neighbors, this.i-1, this.j);
      addPointIfInBounds(board, neighbors, this.i+1, this.j);
      addPointIfInBounds(board, neighbors, this.i, this.j-1);
      addPointIfInBounds(board, neighbors, this.i, this.j+1);
      this.neighbors = neighbors;
    }

    private void addPointIfInBounds(final Pos[][] board, Set<Pos> set, final int i, final int j) {
      if (i >= 0 && i < board.length && j >= 0 && j < board[i].length) {
        set.add(board[i][j]);
      }
    }

    public int hashCode() {
      return Objects.hash(i, j);
    }

    public boolean equals(final Object o) {
      if (!(o instanceof Pos)) {
        return false;
      }
      return ((Pos)o).i == i && ((Pos)o).j == j;
    }

    public String toString() {
      return "[" + j + ", " + i + " (" + cost + ", " + distance + ")]";
    }
  }
}
