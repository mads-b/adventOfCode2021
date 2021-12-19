package yah;

import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import com.google.common.io.Resources;

public class Luke19 {

  public static final int OVERLAP_TO_MERGE = 12;

  public static void main(String[] args) throws Exception {
    final String input = Resources
        .toString(Resources.getResource("luke19.txt"), StandardCharsets.UTF_8);
    List<Scanner> scanners = Arrays.stream(input.split("\n\n"))
        .map(Scanner::new)
        .collect(Collectors.toList());
    System.out.println("Number of scanners: " + scanners.size());
    // Leonhard Euler to the rescue! Let's see if we can't find pairs of beacons with unique
    // distances between them..
    while(scanners.size() > 1) {
      System.out.println("Scanspace " + scanners.size());
      final List<Scanner> aggregatedScanners = new ArrayList<>();
      final Set<Scanner> merged = new HashSet<>();
      for (int i = 0; i < scanners.size(); i++) {
        for (int j = i + 1; j < scanners.size(); j++) {
          final Scanner s = scanners.get(i);
          final Scanner s2 = scanners.get(j);
          if (merged.contains(s) || merged.contains(s2)) {
            continue;
          }
          // Let's try to find scanner pairs that share coords that have the same distance from each other
          Set<Long> sharedDists = Sets.intersection(s.distsAsSet(), s2.distsAsSet());
          final List<RotResult> rotationMatches = new ArrayList<>();
          for (final long dist : sharedDists) {
            rotationMatches.addAll(tryToAlignUsing(s, s2, dist));
          }
          final List<RotResult> rotationPriority = sortByFrequency(rotationMatches);
          for (final RotResult result : rotationPriority) {
            final Map<Coord, Coord> normalizedCoords = rotAndMatch(s, s2, result);
            if (!normalizedCoords.isEmpty()) {
              final List<Coord> bacons = new ArrayList<>();
                  bacons.addAll(s.beaconsWithDistancesToNeighbors.keySet());
                  bacons.addAll(normalizedCoords.values());
              aggregatedScanners.add(
                  new Scanner(bacons, "Aggregate " + Objects.hash(s.name, s2.name)));
              merged.add(s);
              merged.add(s2);
            }
          }
        }
      }
      scanners.stream().filter(s -> !merged.contains(s)).forEach(aggregatedScanners::add);
      scanners = aggregatedScanners;
    }
    final List<Coord> scannerCoords = scanners.stream()
        .flatMap(s -> s.beaconsWithDistancesToNeighbors.keySet().stream())
        .filter(c -> c.isScanner)
        .collect(Collectors.toList());
    System.out.println("Done! Space completely normalized: " + (scanners.get(0).beaconsWithDistancesToNeighbors.keySet().size() - scannerCoords.size()));
    System.out.println("Scanner positions: " + scannerCoords);
    long largestManhattanDist = 0;
    for (final Coord c1 : scannerCoords) {
      for (final Coord c2 : scannerCoords) {
        largestManhattanDist = Math.max(largestManhattanDist, Math.abs(c1.x - c2.x) + Math.abs(c1.y - c2.y) + Math.abs(c1.z - c2.z));
      }
    }
    System.out.println("Largest manhattan dist: " + largestManhattanDist);
  }

  // Using a pair of suspected shared beacons, try to figure out the translation and rotation of sc2 relative to sc1.
  public static List<RotResult> tryToAlignUsing(final Scanner sc1, final Scanner sc2, final long dist) {
    final List<Coord> pairFrom1 = sc1.coordsForDist(dist);
    final List<Coord> pairFrom2 = sc2.coordsForDist(dist);
    if (pairFrom1.size() > 2 || pairFrom2.size() > 2) {
      // Meh. Don't want to bother with this case
      return List.of();
    }
    final List<RotResult> rotationIndex = new ArrayList<>();

    final List<Coord> rotSecondOne = pairFrom2.get(0).enumerateRotations();
    final List<Coord> rotSecondTwo = pairFrom2.get(1).enumerateRotations();

    for (int r = 0; r < 24; r++) {
      final Coord offset = pairFrom1.get(0).dist(rotSecondOne.get(r));
      final Coord offset2 = pairFrom1.get(1).dist(rotSecondTwo.get(r));
      if (offset.equals(offset2)) {
        // Found a rotation of second scanner that made the two coordinated overlap!
        rotationIndex.add(new RotResult(r, offset));
      }
      final Coord offset3 = pairFrom1.get(1).dist(rotSecondOne.get(r));
      final Coord offset4 = pairFrom1.get(0).dist(rotSecondTwo.get(r));
      if (offset3.equals(offset4)) {
        // Found a rotation of second scanner that made the two coordinated overlap!
        rotationIndex.add(new RotResult(r, offset3));
      }
    }
    return rotationIndex;
  }

  public static Map<Coord, Coord> rotAndMatch(final Scanner sc1, final Scanner sc2, final RotResult rotResult) {
    final Set<Coord> sc1Coords = sc1.beaconsWithDistancesToNeighbors.keySet();
    final Map<Coord, Coord> sc2ToSc1Normalized = sc2.beaconsWithDistancesToNeighbors.keySet().stream()
        .map(c -> new SimpleEntry<>(c, Coord.ROTS.get(rotResult.rotindex).apply(c)))
        .map(e -> new SimpleEntry<>(
            e.getKey(),
            new Coord(
                e.getValue().x + rotResult.translation.x,
                e.getValue().y + rotResult.translation.y,
                e.getValue().z + rotResult.translation.z,
                e.getValue().isScanner)))
        .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));

    int count = 0;
    final List<Coord> matchsc1 = new ArrayList<>();
    final List<Coord> matchsc2 = new ArrayList<>();
    for (final Map.Entry<Coord, Coord> sc2Norm : sc2ToSc1Normalized.entrySet()) {
      if (sc1Coords.contains(sc2Norm.getValue())) {
        matchsc1.add(sc2Norm.getValue());
        matchsc2.add(sc2Norm.getKey());
        count++;
      }
    }

    if (count >= OVERLAP_TO_MERGE) {
      return sc2ToSc1Normalized;
    }
    return Map.of();
  }

  private static class RotResult {
    final int rotindex;
    final Coord translation;

    public RotResult(int rotindex, Coord translation) {
      this.rotindex = rotindex;
      this.translation = translation;
    }

    public int hashCode() {
      return Objects.hash(rotindex, translation);
    }

    public boolean equals(final Object o) {
      if (!(o instanceof RotResult)) {
        return false;
      }
      return rotindex == ((RotResult) o).rotindex
          && translation.equals(((RotResult) o).translation);
    }
  }


  private static class Scanner {

    private final Map<Coord, Map<Coord, Long>> beaconsWithDistancesToNeighbors = new HashMap<>();
    private final Map<Long, List<Coord>> distancesWithBeacons = new HashMap<>();
    private String name;

    public Scanner(final String str) {
      this(Streams.concat(Stream.of(new Coord(0,0,0, true)), Arrays.stream(str.split("\n"))
          .filter(s -> !s.startsWith("---"))
          .map(Coord::new))
          .collect(Collectors.toList()),
          str.substring(4, str.indexOf(" ---\n")));
    }

    public Scanner(final List<Coord> beacons, final String name) {
      for (int i = 0; i < beacons.size(); i++) {
        final Map<Coord, Long> dists = new HashMap<>();
        final Coord b = beacons.get(i);
        for (int j = i + 1; j < beacons.size(); j++) {
          final Coord b2 = beacons.get(j);
          final long dist = b.distSquared(b2);
          dists.put(b2, dist);
          final List<Coord> l = distancesWithBeacons.computeIfAbsent(dist, (k) -> new ArrayList<>());
          l.add(b);
          l.add(b2);
        }
        beaconsWithDistancesToNeighbors.put(b, dists);
      }
      this.name = name;
    }

    public Set<Long> distsAsSet() {
      return beaconsWithDistancesToNeighbors.values().stream()
          .flatMap(m -> m.values().stream())
          .collect(Collectors.toSet());
    }

    // Returns a list with even length of all the coord-pairs that are the parameter away from each other.
    public List<Coord> coordsForDist(final long dist) {
      return distancesWithBeacons.getOrDefault(dist, List.of());
    }

    public int hashCode() {
      return Objects.hash(name);
    }

    @Override
    public boolean equals(final Object o) {
      if (!(o instanceof Scanner)) {
        return false;
      }
      return this.name.equals(((Scanner) o).name);
    }
  }

  private static class Coord  {
    private final int x;
    private final int y;
    private final int z;
    private final boolean isScanner;

    public Coord(String str) {
      final Integer[] split = Arrays.stream(str.split(",")).map(Integer::parseInt).toArray(Integer[]::new);
      this.x = split[0];
      this.y = split[1];
      this.z = split[2];
      this.isScanner = false;
    }

    public Coord(int x, int y, int z, final boolean isScanner) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.isScanner = isScanner;
    }

    public long distSquared(final Coord c) {
      final long dx = x - c.x;
      final long dy = y - c.y;
      final long dz = z - c.z;
      return dx*dx + dy*dy + dz*dz;
    }

    public static final List<Function<Coord, Coord>> ROTS = List.of( // Descs are facing towards origin from the Z-axis
        c -> new Coord(c.x, c.y, c.z, c.isScanner), // -- Original
        c -> new Coord(-c.z, c.y, c.x, c.isScanner),// CC around Y
        c -> new Coord(-c.x, c.y, -c.z, c.isScanner),// CC around Y
        c -> new Coord(c.z, c.y, -c.x, c.isScanner),// CC around Y
        c -> new Coord(-c.y, c.x, c.z, c.isScanner), // -- Cc around Z
        c -> new Coord(-c.y, -c.z, c.x, c.isScanner), // CC around X
        c -> new Coord(-c.y, -c.x, -c.z, c.isScanner), // CC around X
        c -> new Coord(-c.y, c.z, -c.x, c.isScanner), // CC around X
        c -> new Coord(-c.x, -c.y, c.z, c.isScanner), // -- Cc around Z
        c -> new Coord(-c.z, -c.y, -c.x, c.isScanner), // -- Cc around Y
        c -> new Coord(c.x, -c.y, -c.z, c.isScanner), // -- Cc around Y
        c -> new Coord(c.z, -c.y, c.x, c.isScanner), // -- Cc around Y
        c -> new Coord(c.y, -c.x, c.z, c.isScanner), // -- Cc around Z
        c -> new Coord(c.y, -c.z, -c.x, c.isScanner), // -- Cc around X
        c -> new Coord(c.y, c.x, -c.z, c.isScanner), // -- Cc around X
        c -> new Coord(c.y, c.z, c.x, c.isScanner), // -- Cc around X
        // end of all rots where the scanner is not "facing" z-axis
        c -> new Coord(c.x, -c.z, c.y, c.isScanner), // Facing positive Z
        c -> new Coord(c.z, c.x, c.y, c.isScanner), // CC around Z
        c -> new Coord(-c.x, c.z, c.y, c.isScanner), // CC around Z
        c -> new Coord(-c.z, -c.x, c.y, c.isScanner), // CC around Z
        c -> new Coord(c.x, c.z, -c.y, c.isScanner), // Facing negative Z
        c -> new Coord(-c.z, c.x, -c.y, c.isScanner), // CC around Z
        c -> new Coord(-c.x, -c.z, -c.y, c.isScanner), // CC around Z
        c -> new Coord(c.z, -c.x, -c.y, c.isScanner)); // CC around Z


    public List<Coord> enumerateRotations() {
      return ROTS.stream().map(f -> f.apply(this)).collect(Collectors.toList());
    }

    public Coord dist(final Coord other) {
      return new Coord(x - other.x, y - other.y, z - other.z, false);
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y, z);
    }

    @Override
    public boolean equals(final Object o) {
      if (!(o instanceof Coord)) {
        return false;
      }
      final Coord other = (Coord) o;
      return x == other.x
          && y == other.y
          && z == other.z;
    }

    public String toString() {
      return "[" + x + "," + y+ "," + z + "]";
    }
  }

  private static <T> List<T> sortByFrequency(final List<T> items) {
    final Map<T, Integer> matches = new HashMap<>();
    for (final T it : items) {
        matches.compute(it, (k, v) -> v == null ? 1 : v+1);
    }
    return matches.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .map(Entry::getKey)
        .toList();
  }
}
