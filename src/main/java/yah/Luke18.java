package yah;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import com.google.common.io.Resources;
import org.json.JSONArray;
import org.json.JSONException;

public class Luke18 {

  public static void main(String[] args) throws Exception {
    final List<String> lines = Resources
        .readLines(Resources.getResource("luke18.txt"), StandardCharsets.UTF_8);
    final Supplier<List<Pair>> pairs = () -> lines.stream().map(Pair::parse).collect(Collectors.toList());
    final List<Pair> input = pairs.get();
    final Pair summedPair = input.stream().reduce(Pair::add).orElseThrow();
    System.out.println("Summed pair magnitude: " + summedPair.magnitude());

    long largestMagnitude = 0;
    for (String p1 : lines) {
      for (String p2 : lines) {
        if (p1.equals(p2)) {
          continue;
        }
        long newMag = Pair.parse(p1).add(Pair.parse(p2)).magnitude();
        largestMagnitude = Math.max(largestMagnitude, newMag);
      }
    }
  }

  public static class Pair {
    private Object left;
    private Object right;
    private Pair parent;

    public Pair(final Object left, final Object right, final Pair parent) {
      this.left = left;
      this.right = right;
      this.parent = parent;
    }

    private Pair(final Pair p) {
      this.parent = p;
    }

    public Object getLeft() {
      return left;
    }

    public Object getRight() {
      return right;
    }

    public long magnitude() {
      final long leftMag = (left instanceof Integer
          ? (Integer)left : ((Pair)left).magnitude()) * 3;

      final long rightMag = (right instanceof Integer
          ? (Integer)right : ((Pair)right).magnitude()) * 2;
      return leftMag + rightMag;
    }

    public Pair add(final Pair p) {
      final Pair parent = new Pair(null);
      parent.left = this;
      parent.right = p;
      this.parent = parent;
      p.parent = parent;
      parent.fullReduce();
      return parent;
    }

    public void fullReduce() {
      boolean unreduced = true;
      while (unreduced) {
        while (reduceExplode(0));
        unreduced = reduceSplit();
      }
    }

    public boolean reduceSplit() {
      if (this.left instanceof Pair) {
        if (((Pair) this.left).reduceSplit()) {
          return true;
        }
      } else {
        final Object maybeSplit = split((int)this.left, this);
        this.left = maybeSplit != null ? maybeSplit : this.left;
        if (maybeSplit != null) {
          return true;
        }
      }
      if (this.right instanceof Pair) {
        if (((Pair) this.right).reduceSplit()) {
          return true;
        }
      } else {
        final Object maybeSplit = split((int)this.right, this);
        this.right = maybeSplit != null ? maybeSplit : this.right;
        if (maybeSplit != null) {
          return true;
        }
      }
      return false;
    }

    private static Pair split(final int val, final Pair parent) {
      if (val <= 9) {
        return null;
      }
      final int div = val/2;
      final int remainder = val % 2;
      return new Pair(div, div + remainder, parent);
    }

    public boolean reduceExplode(final int depth) {
      if (depth >= 4 && this.left instanceof Integer && this.right instanceof Integer) {
        this.addToLeftOf((int) this.left);
        this.addToRightOf((int) this.right);
        // Got rid of the numbers. Time to collapse.
        if (this.parent.left == this) {
          this.parent.left = 0;
        } else {
          this.parent.right = 0;
        }
        return true;
      }
      if (this.left instanceof Pair) {
        if (((Pair)this.left).reduceExplode(depth+1)) {
          return true;
        }
      }
      if (this.right instanceof Pair) {
        if (((Pair)this.right).reduceExplode(depth+1)) {
          return true;
        }
      }
      return false;
    }

    private void addToLeftOf(final int val) {
      if (parent == null) {
      } else if (parent.left == this) {
        parent.addToLeftOf(val);
      } else if (parent.left instanceof Integer) {
        parent.left = (int)parent.left + val;
      } else if (parent.left instanceof Pair) {
        ((Pair)parent.left).addToRightOfDownTree(val);
      } else {
        parent.addToLeftOf(val);
      }
    }

    private void addToRightOf(final int val) {
      if (parent == null) {
      } else if (parent.right == this) {
        parent.addToRightOf(val);
      } else if (parent.right instanceof Integer) {
        parent.right = (int)parent.right + val;
      } else if (parent.right instanceof Pair) {
        ((Pair)parent.right).addToLeftOfDownTree(val);
      } else {
        parent.addToRightOf(val);
      }
    }

    private void addToLeftOfDownTree(final int val) {
      if (this.left instanceof Pair) {
        ((Pair) this.left).addToLeftOfDownTree(val);
        return;
      }
      this.left = (int) this.left + val;
    }

    private void addToRightOfDownTree(final int val) {
      if (this.right instanceof Pair) {
        ((Pair) this.right).addToRightOfDownTree(val);
        return;
      }
      this.right = (int) this.right + val;
    }

    @Override
    public String toString() {
      return "[" + getLeft() + "," + getRight() + "]";
    }

    public static Pair parse(final String str) {
      try {
        final JSONArray arr = new JSONArray(
            str); // Lucky for us this datastructure is JSON-compatible
        return parse(null, arr);
      } catch (final JSONException e) {
        throw new IllegalArgumentException("Parse failed on pair: " + str, e);
      }
    }

    private static Pair parse(final Pair parent, final JSONArray arr) {
      final Object left = arr.get(0);
      final Object right = arr.get(1);
      final Pair p = new Pair(parent);
      final Object leftParsed = left instanceof JSONArray
          ? parse(p, (JSONArray) left) : (Integer) left;
      final Object rightParsed = right instanceof JSONArray
          ? parse(p, (JSONArray) right) : (Integer) right;
      p.left = leftParsed;
      p.right = rightParsed;
      return p;
    }
  }
}
