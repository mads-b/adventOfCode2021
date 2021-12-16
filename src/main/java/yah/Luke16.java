package yah;

import com.google.common.io.Resources;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Luke16 {

  public static void main(String[] args) throws IOException {
    final String messageHex = Resources.toString(Resources.getResource("luke16.txt"), StandardCharsets.UTF_8);

    // Part 1
    System.out.println(new Packet("D2FE28")); // Data packet
    System.out.println(new Packet("38006F45291200")); // Operator packet with ltv 0
    System.out.println(new Packet("EE00D40C823060")); // Operator packet with ltv 1
    System.out.println(new Packet("8A004A801A8002F478")); // Additional samples
    System.out.println(new Packet("620080001611562C8802118E34"));
    System.out.println(new Packet("C0015000016115A2E0802F182340"));
    System.out.println(new Packet("A0016C880162017C3686B18A3D4780"));

    // Part 2 examples
    System.out.println(new Packet("C200B40A82"));
    System.out.println(new Packet("04005AC33890"));
    System.out.println(new Packet("880086C3E88112"));
    System.out.println(new Packet("CE00C43D881120"));
    System.out.println(new Packet("D8005AC2A8F0"));
    System.out.println(new Packet("F600BC2D8F"));
    System.out.println(new Packet("9C005AC2F8F0"));
    System.out.println(new Packet("9C0141080250320F1802104A08"));

    System.out.println(new Packet(messageHex)); // The data
  }

  // Addressing Java byte alignment issues by having this cursor class that allows us to retrieve arbitrary numbers of bits.
  public static class Cursor {
    private int curBit;
    private final byte[] bytes;

    public Cursor(final byte[] arr) {
      this.bytes = arr;
    }

    private int byteNum(int idx) {
      return idx / 8;
    }

    private int bitIndex(int idx) {
      return idx % 8;
    }

    public boolean bit() {
      boolean out = ((bytes[byteNum(curBit)] >> 7 - bitIndex(curBit)) & 0b1) == 1;
      curBit++;
      return out;
    }

    // Retrieves up to 24 bits as an integer
    public int bits(final int length) {
      final int lastBit = curBit + length -1; // inclusive
      final int curBitIdx = bitIndex(curBit);
      final int curByteNum = byteNum(curBit);

      int out = 0;
      for (int i = byteNum(lastBit); i >= curByteNum; i--) {
        out = out | ((bytes[i] & 0xFF) << (24-(i-curByteNum)*8));
      }
      out = (out << curBitIdx) >>> 32-length; // now the bits we want are at the far right.
      curBit = lastBit+1;
      return out;
    }

    public BigInteger literal() {
      boolean lastNibble;
      final byte[] nibbles = new byte[16]; // We only get a nibble per chunk, meaning 4 bits
      int i = 0;
      do {
        lastNibble = !bit();
        nibbles[i] = (byte) bits(4);
        i++;
      } while(!lastNibble);

      long out = 0;
      for (int j = nibbles.length-1; j >= 0; j--) {
        out = out | (((long) nibbles[j] << (60-j*4)));
      }
      return BigInteger.valueOf(out >>> 64-i*4);
    }
  }

  public static class Packet {
    private int version;
    private Operation typeId;
    private boolean lengthTypeId;
    private BigInteger literalValue = BigInteger.valueOf(-1L);
    private List<Packet> containingPackets = new ArrayList<>();

    public Packet(final String hex) {
      this(new Cursor(HexFormat.of().parseHex(hex)));
    }

    public Packet(final Cursor cur) {
      version = cur.bits(3);
      typeId = Operation.forInt(cur.bits(3));

      if (typeId == Operation.VALUE) { // Literal!
        this.literalValue = cur.literal();
        return;
      }
      lengthTypeId = cur.bit();
      if (lengthTypeId) {
        final int numberOfSubPackets = cur.bits(11);
        for (int i = 0; i < numberOfSubPackets; i++) {
          containingPackets.add(new Packet(cur));
        }
      } else {
        final int numberOfRemainingBits = cur.bits(15);
        final int curPos = cur.curBit;
        // Ugh, hate this format. The other one puts me far more at ease.
        while (cur.curBit < curPos + numberOfRemainingBits) {
          containingPackets.add(new Packet(cur));
        }
      }
      this.literalValue = value();
    }

    public int versionSum() {
      return version + containingPackets.stream().map(Packet::versionSum).mapToInt(i -> i).sum();
    }

    public BigInteger value() {
      return typeId.function.apply(this);
    }

    public String toString() {
      return "Packet with version " + version + " and type " + typeId + " has versionSum " + versionSum() + " and value " + literalValue;
    }
  }

  public enum Operation {
    SUM(0, p -> reduce(p, BigInteger::add)),
    PRODUCT(1, p -> reduce(p, BigInteger::multiply)),
    MINIMUM(2, p -> reduce(p, BigInteger::min)),
    MAXIMUM(3, p -> reduce(p, BigInteger::max)),
    VALUE(4, p -> p.literalValue),
    GREATER_THAN(5, p -> p.containingPackets.get(0).value()
        .compareTo(p.containingPackets.get(1).value()) > 0 ? BigInteger.ONE : BigInteger.ZERO),
    LESS_THAN(6, p -> p.containingPackets.get(1).value()
        .compareTo(p.containingPackets.get(0).value()) > 0 ? BigInteger.ONE : BigInteger.ZERO),
    EQUAL(7, p -> reduce(p, (l, r) -> l.equals(r) ? BigInteger.ONE : BigInteger.ZERO));

    private final int val;
    private final Function<Packet, BigInteger> function;

    Operation(final int val, final Function<Packet, BigInteger> function) {
      this.val = val;
      this.function = function;
    }

    public static Operation forInt(final int val) {
      return Arrays.stream(values()).filter(v -> v.val == val).findFirst().orElseThrow();
    }

    private static BigInteger reduce(final Packet p, BinaryOperator<BigInteger> op) {
      return p.containingPackets.stream().map(Packet::value).reduce(op).orElseThrow();
    }
  }
}
