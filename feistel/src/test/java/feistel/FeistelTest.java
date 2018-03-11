package feistel;

import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static feistel.Feistel.*;
import static org.junit.Assert.assertEquals;

public final class FeistelTest {

    @Test
    public void isPermutation() {
        IntUnaryOperator feistel = compute(3, (value) -> value * 11);
        int step = 7231;
        long count = (1L << 32) / step;
        assertEquals(count, IntStream
                .iterate(Integer.MIN_VALUE, i -> i + step)
                .limit(count)
                .map(feistel)
                .distinct()
                .count());
    }

    @Test
    public void isPermutationl() {
        long count = 1_000_000;
        assertEquals(count, LongStream
                .generate(ThreadLocalRandom.current()::nextLong)
                .limit(count)
                .map(l -> balanced(l, 4, value -> value * 11))
                .distinct()
                .count());
    }

    @Test
    public void isPermutationUnbalanced() {
        int step = 3231;
        long count = (1L << 32) / step;
        assertEquals(count, IntStream
                .iterate(Integer.MIN_VALUE, i -> i + step)
                .limit(count)
                .map(i -> unbalanced(i, 4, 15, 17, value -> value * 11))
                .distinct()
                .count());
    }

    @Test
    public void canBeReversedBalanced() {
        IntUnaryOperator feistel = compute(4, (value) -> value * 31);
        IntUnaryOperator inverse = feistel.compose(feistel);
        for (long i = Integer.MIN_VALUE; i <= Integer.MAX_VALUE; i += 321) {
            assertEquals(i, inverse.applyAsInt((int) i));
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
        IntUnaryOperator roundFunction = (value) -> value * 31;
        for (long i = Integer.MIN_VALUE; i <= Integer.MAX_VALUE; i += 321) {
            int output = doUnbalanced((int) i, rounds, sourceBits, targetBits, false, roundFunction);
            int inverse = doUnbalanced(output, rounds, targetBits, sourceBits, true, roundFunction);
            assertEquals(i, inverse);
        }
    }

}
