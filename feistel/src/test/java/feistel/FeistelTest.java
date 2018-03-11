package feistel;

import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.LongUnaryOperator;
import java.util.stream.LongStream;

import static feistel.Feistel.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class FeistelTest {

    @Test
    public void isPermutation16() {
        int count = 1 << 16;
        assertEquals(count, LongStream
                .range(0, count)
                .map(i -> doUnbalanced(i, 3, 16, 8, 8, false, (value) -> value * 11))
                .distinct()
                .peek(i -> assertTrue(String.valueOf(i), i >= 0 && i < count))
                .count());
    }

    @Test
    public void isPermutation() {
        LongUnaryOperator feistel = compute(3, (value) -> value * 11);
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
    public void isPermutationUnbalanced() {
        int step = 3231;
        long count = (1L << 32) / step;
        assertEquals(count, LongStream
                .iterate(Integer.MIN_VALUE, i -> i + step)
                .limit(count)
                .map(i -> unbalanced(i, 4, 15, 17, value -> value * 11))
                .distinct()
                .count());
    }

    @Test
    public void canBeReversed16() {
        for (int i = 0; i < 1 << 16; i++) {
            long j = doUnbalanced(i, 4, 16, 8, 8, false, (value) -> value * 31);
            long k = doUnbalanced(j, 4, 16, 8, 8, true, (value) -> value * 31);
            assertEquals(i, k);
        }
    }

    @Test
    public void canBeReversedBalanced() {
        LongUnaryOperator feistel = compute(4, (value) -> value * 31);
        LongUnaryOperator inverse = feistel.compose(feistel);
        for (long i = Integer.MIN_VALUE; i <= Integer.MAX_VALUE; i += 321) {
            assertEquals(i, inverse.applyAsLong((int) i));
        }
    }

    @Test
    public void canBeReversedBalancedl() {
        for (long i = 0; i <= 10_000_000; i++) {
            long in = ThreadLocalRandom.current().nextLong();
            long out = in;
            out = balanced(out, 5, value -> value * 31);
            out = balanced(out, 5, value -> value * 31);
            assertEquals(in, out);
        }
    }

    @Test
    public void canBeReversedUnbalanced() {
        int rounds = 11;
        int sourceBits = 3;
        int targetBits = 17;
        LongUnaryOperator roundFunction = (value) -> value * 31;
        for (long i = Integer.MIN_VALUE; i <= Integer.MAX_VALUE; i += 321) {
            long output = doUnbalanced(i, rounds, 64, sourceBits, targetBits, false, roundFunction);
            long inverse = doUnbalanced(output, rounds, 64, targetBits, sourceBits, true, roundFunction);
            assertEquals(i, inverse);
        }
    }

}
