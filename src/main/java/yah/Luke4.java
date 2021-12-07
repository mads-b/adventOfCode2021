package yah;

import com.google.common.io.CharStreams;
import com.google.common.io.Resources;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Luke4 {

  public static void main(String[] args) throws Exception {
    final List<String> lines = Resources.readLines(Resources.getResource("luke4.txt"), StandardCharsets.UTF_8);

    final List<Integer> nums = Arrays.stream(lines.get(0).split(",")).map(Integer::parseInt).collect(Collectors.toList());

    final List<Board> boards = new ArrayList<>();
    List<String> rawBoard = new ArrayList<>();
    for (int i = 2; i < lines.size(); i++) {
      if (lines.get(i).isBlank()) {
        continue;
      }
      rawBoard.add(lines.get(i));

      if (rawBoard.size() == Board.BOARD_SIZE) {
        boards.add(new Board(rawBoard));
        rawBoard = new ArrayList<>();
      }
    }

    List<Board> remaining = boards;
    List<Board> pruned = new ArrayList<>();
    Board last = null;
    for (int num : nums) {

      for (final Board b : remaining) {
        b.mark(num);
        if (last != null && last.isBingo()) {
          System.out.println("Num " + num + " called on last and it was bingo " + b.score(num));
        }
        if (!b.isBingo()) {
          pruned.add(b);
        }
      }
      remaining = pruned;
      pruned = new ArrayList<>();

      if (remaining.size() == 1) {
        last = remaining.get(0);
      }
    }
  }

  private static class Board {
    private static final int BOARD_SIZE = 5;

    private final int[][] boardState = new int[BOARD_SIZE][BOARD_SIZE];
    private final boolean[][] marks = new boolean[BOARD_SIZE][BOARD_SIZE];

    public Board(List<String> lines) {
      if (lines.size() != BOARD_SIZE) {
        throw new IllegalArgumentException("Too many lines");
      }
      for (int i = 0; i < BOARD_SIZE; i++) {
        final String line[] = lines.get(i).trim().split("\\s+");
        if (line.length != BOARD_SIZE) {
          throw new IllegalArgumentException("Too many numbers in line " + lines.get(i));
        }
        for (int j = 0; j < BOARD_SIZE; j++) {
          final int num = Integer.parseInt(line[j]);
          boardState[i][j] = num;
        }
      }
    }


    public void mark(int num) {
      for (int i = 0; i < BOARD_SIZE; i++) {
        for (int j = 0; j < BOARD_SIZE; j++) {
          if (boardState[i][j] == num) {
            marks[i][j] = true;
          }
        }
      }
    }

    public boolean isBingo() {
      for (int i = 0; i < BOARD_SIZE; i++) {
        boolean bingo = true;
        for (int j = 0; j < BOARD_SIZE; j++) {
          bingo = bingo && marks[i][j];

          if ((bingo && j == BOARD_SIZE - 1)) {
            return true;
          }
        }
      }
      for (int i = 0; i < BOARD_SIZE; i++) {
        boolean bingo = true;
        for (int j = 0; j < BOARD_SIZE; j++) {
          bingo = bingo && marks[j][i];

          if ((bingo && j == BOARD_SIZE - 1)) {
            return true;
          }
        }
      }
      return false;
    }

    public int score(final int calledNum) {
      int unmarkedSum = 0;
      for (int i = 0; i < BOARD_SIZE; i++) {
        for (int j = 0; j < BOARD_SIZE; j++) {
          if (!marks[i][j]) {
            unmarkedSum += boardState[i][j];
          }
        }
      }
      return unmarkedSum * calledNum;
    }

    public String toString() {
      String out = "";
      for (int i = 0; i < BOARD_SIZE; i++) {
        for (int j = 0; j < BOARD_SIZE; j++) {
          if (marks[i][j]) {
            out += "." + boardState[i][j] + " ";
          } else {
            out += boardState[i][j] + " ";

          }
        }
        out += "\n";
      }
      return out;
    }
  }
}
