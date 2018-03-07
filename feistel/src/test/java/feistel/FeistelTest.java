package feistel;

import org.junit.Test;

import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

import static feistel.Feistel.compute;
import static feistel.Feistel.unbalanced;
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
    public void canBeReversedBalanced() {
        IntUnaryOperator feistel = compute(4, (value) -> value * 31);
        IntUnaryOperator inverse = feistel.compose(feistel);
        for (long i = Integer.MIN_VALUE; i <= Integer.MAX_VALUE; i += 321) {
            assertEquals(i, inverse.applyAsInt((int) i));
        }
    }

    @Test
    public void canBeReversedUnbalanced() {
        int rounds = 4; // 0;
        int sourceBits = 15;
        int targetBits = 16;
        IntUnaryOperator roundFunction = (value) -> value * 31;
        for (long i = Integer.MIN_VALUE; i <= Integer.MAX_VALUE; i += 321) {
            int output = unbalanced((int) i, rounds, sourceBits, targetBits, roundFunction);
            int inverse = unbalanced(output, rounds, targetBits, sourceBits, roundFunction);
            assertEquals(i, inverse);
        }
    }

}
