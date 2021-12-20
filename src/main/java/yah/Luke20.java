package yah;

import java.nio.charset.StandardCharsets;
import com.google.common.io.Resources;

public class Luke20 {

  public static void main(String[] args) throws Exception {
    final String input = Resources
        .toString(Resources.getResource("luke20.txt"), StandardCharsets.UTF_8);
    final String[] split = input.split("\n\n");
    Board board = new Board(split[1], split[0]);
    System.out.println(board);
    final long time = System.currentTimeMillis();
    for (int i = 0; i < 50; i++) {
      board = board.filter();
    }
    System.out.println("Ran in " + (System.currentTimeMillis() - time));
    System.out.println("pixels lit: " + board.litPixels());
  }

  public static class Board {
    private boolean[][] board;
    private boolean[] lookup = new boolean[512];
    private boolean isOddIteration = false;

    public Board(final String input, final String lookupTable) {
      for (int i = 0; i < 512; i++) {
        lookup[i] = lookupTable.charAt(i) == '#';
      }

      final String[] boardLines = input.split("\n");
      this.board = new boolean[boardLines.length][boardLines[0].length()];
      for (int i = 0; i < board.length; i++) {
        for (int j = 0; j < board[0].length; j++) {
          board[i][j] = boardLines[i].charAt(j) == '#';
        }
      }
    }

    private Board(final boolean[][] board, final boolean[] lookup, final boolean isOddIteration) {
      this.board = board;
      this.lookup = lookup;
      this.isOddIteration = isOddIteration;
    }

    private int pixelAt(final int i, final int j) {
      if (i < 0 || i >= board.length || j < 0 || j >= board[0].length) {
        return isOddIteration ? (lookup[0] ? 1 : 0) : 0;
      }
      return board[i][j] ? 1 : 0;
    }

    public Board filter() {
      final boolean[][] newBoard = new boolean[board.length+2][board[0].length+2];
      for (int i = 0; i < newBoard.length; i++) {
        for (int j = 0; j < newBoard[i].length; j++) {
          int n;
          n = pixelAt(i-2, j-2);
          n = (n << 1) + pixelAt(i-2, j-1);
          n = (n << 1) + pixelAt(i-2, j);
          n = (n << 1) + pixelAt(i-1, j-2);
          n = (n << 1) + pixelAt(i-1, j-1);
          n = (n << 1) + pixelAt(i-1, j);
          n = (n << 1) + pixelAt(i, j-2);
          n = (n << 1) + pixelAt(i, j-1);
          n = (n << 1) + pixelAt(i, j);
          newBoard[i][j] = lookup[n];
        }
      }
      return new Board(newBoard, lookup, !isOddIteration);
    }

    public long litPixels() {
      long litPixels = 0;
      for (int i = 0; i < board.length; i++) {
        for (int j = 0; j < board[i].length; j++) {
          litPixels += board[i][j] ? 1 : 0;
        }
      }
      return litPixels;
    }

    public String toString() {
      final StringBuilder bldr = new StringBuilder();
      for (int i = 0; i < board.length; i++) {
        for (int j = 0; j < board[i].length; j++) {
          bldr.append(board[i][j] ? '#' : '.');
        }
        bldr.append('\n');
      }
      return bldr.toString();
    }
  }
}
