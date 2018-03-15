package feistel;


import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.LongUnaryOperator;
import java.util.stream.LongStream;

import static feistel.Feistel.*;
import static java.math.BigInteger.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FeistelTest {

    @Test
    void isPermutation16() {
        int count = 1 << 16;
        assertEquals(count, LongStream
                .range(0, count)
                .map(Feistel.binary(3, 16, 8, 8, (round, value) -> value * 11))
                .distinct()
                .peek(i -> assertTrue(i >= 0 && i < count, () -> String.valueOf(i)))
                .count());
    }

    @Test
    void isPermutationNumericBigInteger() {
        BigInteger m = BigInteger.valueOf(320);
        BigInteger n = BigInteger.valueOf(200);
        BigInteger count = m.multiply(n);
        assertEquals(count.longValue(), LongStream
                .range(0, count.longValue())
                .mapToObj(BigInteger::valueOf)
                .map(i -> numeric(i, 7, m, n, (value) -> value.multiply(BigInteger.valueOf(11))))
                .distinct()
                .peek(i -> {
                    assertTrue(i.compareTo(ZERO) >= 0, () -> String.valueOf(i));
                    assertTrue(i.compareTo(count) < 0, () -> String.valueOf(i));
                })
                .count());
    }

    @Test
    void isPermutationNumeric() {
        int m = 320;
        int n = 200;
        int count = m * n;
        assertEquals(count, LongStream
                .range(0, count)
                .map(i -> numeric(i, 7, m, n, (value) -> value * 11))
                .distinct()
                .peek(i -> assertTrue(i >= 0 && i < count, () -> String.valueOf(i)))
                .count());
    }

    @Test
    void isPermutationNumeric2() {
        int a = 320;
        int b = 200;
        int count = a * b;
        assertEquals(count, LongStream
                .range(0, count)
                .map(i -> numeric2(i, 7, a, b, (value) -> value * 11))
                .distinct()
                .peek(i -> assertTrue(i >= 0 && i < count, () -> String.valueOf(i)))
                .count());
    }

    @Test
    void isPermutation() {
        LongUnaryOperator feistel = new FeistelOfLongBalanced(3, false, (round, value) -> value * 11);
        int step = 7231;
        long count = (1L << 32) / step;
        assertEquals(count, LongStream
                .iterate(Integer.MIN_VALUE, i -> i + step)
                .limit(count)
                .map(feistel)
                .distinct()
                .count());
    }

    @Test
    void isPermutationUnbalanced() {
        int step = 3231;
        long count = (1L << 32) / step;
        assertEquals(count, LongStream
                .iterate(Integer.MIN_VALUE, i -> i + step)
                .limit(count)
                .map(Feistel.binary(4, 64, 15, 17, (round, value) -> value * 11))
                .distinct()
                .count());
    }

    @Test
    void canBeReversed16() {
        Feistel.OfLong forward = Feistel.binary(4, 16, 8, 8, (round, value) -> value * 31);
        Feistel.OfLong backward = forward.reversed();
        for (int i = 0; i < 1 << 16; i++) {
            assertEquals(i, backward.applyAsLong(forward.applyAsLong(i)));
        }
    }

    @Test
    void canBeReversedBalanced() {
        Feistel.OfLong feistel = new FeistelOfLongBalanced(4, false, (round, value) -> value * 31);
        Feistel.OfLong inverse = feistel.reversed();
        for (long i = Integer.MIN_VALUE; i <= Integer.MAX_VALUE; i += 321) {
            assertEquals(i, inverse.applyAsLong(feistel.applyAsLong(i)));
        }
    }

    @Test
    void canBeReversedBalancedl() {
        Feistel.OfLong forward = new FeistelOfLongBalanced(5, false, (round, value) -> value * 31);
        Feistel.OfLong backward = forward.reversed();
        for (long i = 0; i <= 10_000_000; i++) {
            long in = ThreadLocalRandom.current().nextLong();
            long out = in;
            out = forward.apply(out);
            out = backward.apply(out);
            assertEquals(in, out);
        }
    }

    @Test
    void canBeReversedUnbalanced() {
        RoundFunction.OfLong f = (round, value) -> value * 31;
        Feistel.OfLong forward = Feistel.binary(11, 64, 3, 17, f);
        Feistel.OfLong backward = forward.reversed();
        for (long i = Integer.MIN_VALUE; i <= Integer.MAX_VALUE; i += 321) {
            assertEquals(i, backward.applyAsLong(forward.applyAsLong(i)));
        }
    }

}
