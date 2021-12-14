package yah;

import com.google.common.base.MoreObjects;
import com.google.common.collect.*;
import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Luke14 {
  private static final String ARROW = " -> ";
  private static Table<Character, Character, Character> POLYMERIZATION;

  public static void main(String[] args) throws Exception {
    final List<String> list = Resources.readLines(Resources.getResource("luke14.txt"), StandardCharsets.UTF_8);
    String polymer = list.get(0);
    final ImmutableTable.Builder<Character, Character, Character> pBldr = ImmutableTable.builder();
    list.stream()
        .filter(s -> s.contains(ARROW))
        .map(s -> s.split(ARROW))
        .forEach(arr -> pBldr.put(arr[0].charAt(0), arr[0].charAt(1), arr[1].charAt(0)));
    POLYMERIZATION = pBldr.build();

    Table<Character, Character, Long> polymerByPairs = tableize(polymer);
    System.out.println("Start!" + polymerByPairs);

    final int steps = 40;
    for (int i = 0; i < steps; i++) {
      polymerByPairs = polymerize(polymerByPairs);
      System.out.println(polymerByPairs);
    }

    Map<Character, Long> counts = new HashMap<>();
    for (Table.Cell<Character, Character, Long> cell : polymerByPairs.cellSet()) {
      counts.compute(cell.getRowKey(), (k, v) -> cell.getValue() + (v == null ? 0 : v));
      counts.compute(cell.getColumnKey(), (k, v) -> cell.getValue() + (v == null ? 0 : v));
    }

    counts.computeIfPresent(polymer.charAt(0), (k, v) -> v+1);
    counts.computeIfPresent(polymer.charAt(polymer.length()-1), (k, v) -> v+1);

    long bigCount = 0;
    long smallCount = Long.MAX_VALUE;

    for (Map.Entry<Character, Long> count : counts.entrySet()) {
      if (count.getValue() > bigCount) {
        bigCount = count.getValue();
      }
      if (count.getValue() < smallCount) {
        smallCount = count.getValue();
      }
    }

    for (final Map.Entry<Character, Long> count : counts.entrySet()) {
      System.out.println((count.getKey()) + " happened " + count.getValue()/2 + " times.");
    }
    System.out.println("Big minus small: " + (bigCount/2 - smallCount/2));

  }

  public static Table<Character, Character, Long> polymerize(final Table<Character, Character, Long> input) {
    Table<Character, Character, Long> newPolymer = HashBasedTable.create();
    for (Table.Cell<Character, Character, Long> cell : input.cellSet()) {
      final char middleChar = POLYMERIZATION.get(cell.getRowKey(), cell.getColumnKey());
      increment(newPolymer, cell.getRowKey(), middleChar, cell.getValue());
      increment(newPolymer, middleChar, cell.getColumnKey(), cell.getValue());
    }
    return newPolymer;
  }

  private static void increment(final Table<Character, Character, Long> table, final Character c1, final Character c2, final long num) {
    Long nowCount = table.get(c1, c2);
    long newNum = MoreObjects.firstNonNull(nowCount, 0L) + num;
    table.put(c1, c2, newNum);
  }

  private static Table<Character, Character, Long> tableize(final String polymer) {
    Table<Character, Character, Long> polymerByPairs = HashBasedTable.create();
    for (int i = 0; i < polymer.length()-1; i++) {
      increment(polymerByPairs, polymer.charAt(i), polymer.charAt(i+1), 1L);
    }
    return polymerByPairs;
  }
}
