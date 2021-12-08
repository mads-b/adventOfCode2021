package yah;

import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Luke8 {

  public static void main(String[] args) throws Exception {
    final List<SSDSolver> ssds = Resources.readLines(Resources.getResource("luke8.txt"), StandardCharsets.UTF_8)
        .stream()
        .map(s -> s.split("\\|"))
        .map(SSDSolver::new)
        .toList();
    long count = 0;
    long sum = 0;
    for (SSDSolver solver : ssds) {
      sum += Integer.parseInt(solver.problem.stream()
          .map(solver::getNumber)
          .map(String::valueOf)
          .collect(Collectors.joining()));

      count += problemCount(solver, 1);
      count += problemCount(solver, 4);
      count += problemCount(solver, 7);
      count += problemCount(solver, 8);
    }

    System.out.println("Count of 1, 4, 7, and 8s is " + count + " ah! ah! ah!");
    System.out.println("Sum of parsed numbers is " + sum);
  }

  private static long problemCount(final SSDSolver solver, final int num) {
    final Digit search = solver.getDigit(num);
    return solver.problem.stream().filter(search::equals).count();
  }

  private static class SSDSolver {
    private final Set<Digit> digits;
    private final List<Digit> problem;

    private final Digit[] solved = new Digit[10];

    public SSDSolver(final String[] line) {
      digits = Arrays.stream(line[0].split(" "))
          .map(String::trim)
          .map(Digit::new)
          .collect(Collectors.toSet());
      problem =  Arrays.stream(line[1].trim().split(" "))
          .map(String::trim)
          .map(Digit::new)
          .collect(Collectors.toList());

      solved[1] = extractUniqueNumOfSegments(2);
      solved[4] = extractUniqueNumOfSegments(4);
      solved[7] = extractUniqueNumOfSegments(3);
      solved[8] = extractUniqueNumOfSegments(7);

      final Digit inverseOne = solved[8].minus(solved[1]);
      solved[6] = digits.stream().filter(d -> d.containsAll(inverseOne)).findAny().orElseThrow();
      digits.remove(solved[6]);

      final Digit topRightSegment = solved[8].minus(solved[6]); // Top right segment. All remaining nums minus 5 has it!
      solved[5] = digits.stream().filter(d -> !d.containsAll(topRightSegment)).findAny().orElseThrow();
      digits.remove(solved[5]);

      final Digit bottomLeftSegment = solved[8].minus(solved[5]).minus(solved[1]);

      solved[9] = solved[8].minus(bottomLeftSegment);
      digits.remove(solved[9]);

      solved[0] = extractUniqueNumOfSegments(6);
      // 2 and 3 left.. 2 is the only one left with the bottom left segment
      solved[2] = digits.stream().filter(d -> d.containsAll(bottomLeftSegment)).findAny().orElseThrow();
      digits.remove(solved[2]);

      solved[3] = digits.stream().findAny().orElseThrow();
    }

    private Digit extractUniqueNumOfSegments(final int num) {
      final Digit d = digits.stream().filter(di -> di.chars.size() == num).findAny().orElseThrow();
      digits.remove(d);
      return d;
    }

    public int getNumber(final Digit d) {
      for (int i = 0; i < 10; i++) {
        if (d.equals(solved[i])) {
          return i;
        }
      }
      throw new IllegalArgumentException("Unknown digit: " + d);
    }

    public Digit getDigit(final int digit) {
      return solved[digit];
    }
  }

  private static class Digit {

    private final Set<Character> chars;

    public Digit(final String input) {
      chars = input.chars().mapToObj(i -> (char)i).collect(Collectors.toSet());
    }

    private Digit(final Set<Character> chars) {
      this.chars = chars;
    }

    public Digit minus(final Digit d) {
      final Set<Character> newSet = new HashSet<>(chars);
      newSet.removeAll(d.chars);
      return new Digit(newSet);
    }

    public boolean containsAll(final Digit check) {
      return chars.containsAll(check.chars);
    }

    public int hashCode() {
      return Objects.hashCode(chars);
    }

    public boolean equals(final Object o) {
      if (!(o instanceof Digit)) {
        return false;
      }
      return Objects.deepEquals(((Digit) o).chars, chars);
    }

    public String toString() {
      return chars.stream().map(String::valueOf).collect(Collectors.joining());
    }
  }
}
