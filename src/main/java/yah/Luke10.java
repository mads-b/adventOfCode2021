package yah;

import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Luke10 {

  private static final Map<Character, Character> BRACKETS = Map.of(
      '(', ')',
      '[', ']',
      '{', '}',
      '<', '>');
  private static final Map<Character, Integer> POINTS = Map.of(
      ')', 3,
      ']', 57,
      '}', 1197,
      '>', 25137);
  private static final Map<Character, Integer> POINTS2 = Map.of(
      ')', 1,
      ']', 2,
      '}', 3,
      '>', 4);

  public static void main(String[] args) throws Exception  {
    final List<char[]> strings = Resources.readLines(Resources.getResource("luke10.txt"), StandardCharsets.UTF_8)
        .stream()
        .map(String::toCharArray)
        .toList();

    int sum = 0;
    final List<Long> finishingScores = new ArrayList<>();
    for (final char[] arr : strings) {
      final Map.Entry<Long, Long> scores = scoreString(arr);
      sum += scores.getKey();
      finishingScores.add(scores.getValue());
    }
    final List<Long> sorted = finishingScores.stream().sorted().filter(i -> i != 0).toList();
    finishingScores.sort(Comparator.comparingLong(a-> a));
    System.out.println("Total points: " + sum + " : Finishing scores: " + finishingScores);
    System.out.println("Median: " + sorted.get(sorted.size()/2));
  }

  private static Map.Entry<Long, Long> scoreString(final char[] str) {
    long part1Sum = 0;
    final Stack<Character> stack = new Stack<>();
    for (final char cur : str) {
      if (BRACKETS.containsKey(cur)) {
        stack.push(cur);
      } else {
        final char popped = BRACKETS.get(stack.pop());
        if (popped != cur) {
          part1Sum += POINTS.get(cur);
          return new AbstractMap.SimpleEntry<>(part1Sum, 0L);
        }
      }
    }
    long finishingScore = 0;
    while(!stack.isEmpty()) {// Time for part two: Finishing the string..
      final char autocomplete = BRACKETS.get(stack.pop());
      finishingScore = finishingScore * 5 + POINTS2.get(autocomplete);
    }
    return new AbstractMap.SimpleEntry<>(part1Sum, finishingScore);
  }
}
