package yah;

import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;

public class Luke11 {
  
  public static void main(String[] args) throws Exception {
    final int[][] board = Resources.readLines(Resources.getResource("luke11.txt"), StandardCharsets.UTF_8)
        .stream()
        .map(s -> s.chars().map(i -> Character.digit(i, 10)).toArray())
        .toArray(int[][]::new);
    long flashes = 0;
    for (int simSteps = 1; simSteps < 1000; simSteps++) {
      for (int i = 0; i < board.length; i++) {
        for (int j = 0; j < board[i].length; j++) {
          increase(board, i, j);
        }
      }
      boolean allFlashed = true;
      for (int i = 0; i < board.length; i++) {
        for (int j = 0; j < board[i].length; j++) {
          board[i][j] = board[i][j] > 9 ? 0 : board[i][j];
          allFlashed &= board[i][j] == 0;
          if (board[i][j] == 0) {
            flashes++;
          }
        }
      }
      if (allFlashed) {
        System.out.println("All flashed @ step " + simSteps);
        return;
      }
      System.out.println("End of SimStep " + simSteps);
      System.out.println(str(board));
    }
    System.out.println("Total flashes: " + flashes);
  }

  private static void increase(final int[][] board, final int i, final int j) {
    if (board[i][j] == 10) { //  Poor jellyfish is tired
      return;
    }
    board[i][j]++;
    if (board[i][j] <= 9) {
      return;
    }
    increaseIfInBounds(board, i-1, j-1);
    increaseIfInBounds(board, i-1, j);
    increaseIfInBounds(board, i-1, j+1);
    increaseIfInBounds(board, i, j-1);
    increaseIfInBounds(board, i, j+1);
    increaseIfInBounds(board, i+1, j-1);
    increaseIfInBounds(board, i+1, j);
    increaseIfInBounds(board, i+1, j+1);
  }
  
  private static void increaseIfInBounds(final int[][] board, final int i, final int j) {
    if (i >= 0 && i < board.length && j >= 0 && j < board[i].length) {
      increase(board, i, j);
    }
  }

  private static String str(final int[][] board) {
    final StringBuilder bldr = new StringBuilder();
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        bldr.append(board[i][j] > 9 ? 0 : board[i][j]);
      }
      bldr.append('\n');
    }
    return bldr.toString();
  }
}
