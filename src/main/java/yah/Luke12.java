package yah;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;

public class Luke12 {

  public static void main(String[] args) throws Exception {
    final Graph graph = new Graph();
    Resources.readLines(Resources.getResource("luke12.txt"), StandardCharsets.UTF_8)
        .stream()
        .map(str -> str.split("-"))
        .forEach(arr -> graph.addEdgeAndNodes(arr[0], arr[1]));
    System.out.println("Full graph: " + graph.nodes.values().stream()
        .map(n -> n.name + ": [" + n.neighbors + "]")
        .collect(Collectors.joining(", ")));

    // Traverse with backtracking to find all possible routes from start to end. Set the initial boolean to true to solve part 1.
    final Node start = graph.findOrCreate("start");
    final int endCount = dfs(start, List.of(), false);

    System.out.println("Number of paths from start to end: " + endCount);
  }

  private static int dfs(final Node cur, final List<Node> prev, final boolean hasVisitedSmallCaveTwice) {
    if (cur.name.equals("end")) {
      System.out.println("Path to end: " + prev + " , " + cur);
      return 1;
    }
    final Set<Node> inaccessible = prev.stream().filter(n -> !n.isRevisitable).collect(Collectors.toSet());
    final Set<Node> unexplored = hasVisitedSmallCaveTwice ? Sets.difference(cur.neighbors, inaccessible) : cur.neighbors;
    if (unexplored.isEmpty()) {
      return 0;
    }
    int num = 0;
    final List<Node> newPrev = ImmutableList.<Node>builderWithExpectedSize(prev.size()+1)
        .addAll(prev)
        .add(cur)
        .build();
    for (final Node n : unexplored) {
      if (n.name.equals("start")) {
        continue;
      }
      boolean visitingTwice = hasVisitedSmallCaveTwice || !n.isRevisitable && prev.contains(n);
      num += dfs(n, newPrev, visitingTwice);
    }
    return num;
  }

  private static class Graph {
    private final Map<String, Node> nodes = new HashMap<>();

    public void addEdgeAndNodes(final String node1Name, final String node2Name) {
      final Node node1 = findOrCreate(node1Name);
      final Node node2 = findOrCreate(node2Name);
      node1.addNeighbor(node2);
      node2.addNeighbor(node1);
    }

    public Node findOrCreate(final String name) {
      return nodes.computeIfAbsent(name, (v) -> new Node(name));
    }
  }

  private static class Node {

    private String name;
    private boolean isRevisitable;
    private Set<Node> neighbors = new HashSet<>();

    public Node(final String name) {
      this.name = name;
      this.isRevisitable = name.toUpperCase(Locale.ROOT).equals(name);
    }

    public void addNeighbor(final Node node) {
      this.neighbors.add(node);
    }

    public int hashCode() {
      return name.hashCode();
    }

    public boolean equals(final Object o) {
      if (!(o instanceof Node)) {
        return false;
      }
      return name.equals(((Node) o).name);
    }

    public String toString() {
      return name;
    }
  }
}
