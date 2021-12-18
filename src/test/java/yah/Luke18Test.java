package yah;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import yah.Luke18.Pair;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Luke18Test {

  @Test
  public void firstAddition() {
    assertEquals("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]", add("[[[[4,3],4],4],[7,[[8,4],9]]]", "[1,1]"));
  }

  @Test
  public void sumList() {
    assertEquals("[[[[5,0],[7,4]],[5,5]],[6,6]]", add("[1,1]", "[2,2]", "[3,3]", "[4,4]", "[5,5]", "[6,6]"));
  }

  @Test
  public void slightlyLarger1() {
    assertEquals("[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]", add(
        "[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]",
        "[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]"));
  }

  @Test
  public void slightlyLarger2() {
    assertEquals("[[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]", add(
        "[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]",
        "[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]"));
  }

  @Test
  public void slightlyLarger3() {
    assertEquals("[[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]", add(
        "[[[[6,7],[6,7]],[[7,7],[0,7]]],[[[8,7],[7,7]],[[8,8],[8,0]]]]",
        "[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]"));
  }

  @Test
  public void slightlyLarger4() {
    assertEquals("[[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]", add(
        "[[[[7,0],[7,7]],[[7,7],[7,8]]],[[[7,7],[8,8]],[[7,7],[8,7]]]]",
        "[7,[5,[[3,8],[1,4]]]]"));
  }

  @Test
  public void slightlyLarger5() {
    assertEquals("[[[[6,6],[6,6]],[[6,0],[6,7]]],[[[7,7],[8,9]],[8,[8,1]]]]", add(
        "[[[[7,7],[7,8]],[[9,5],[8,7]]],[[[6,8],[0,8]],[[9,9],[9,0]]]]",
        "[[2,[2,2]],[8,[8,1]]]"));
  }

  public static String add(final String ... p1) {
    return Arrays.stream(p1).map(Pair::parse).reduce(Pair::add).orElseThrow().toString();
  }
}
