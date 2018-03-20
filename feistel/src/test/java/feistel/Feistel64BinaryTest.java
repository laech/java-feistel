package feistel;

import com.carrotsearch.hppc.LongHashSet;
import com.carrotsearch.hppc.LongSet;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.function.LongUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.BigInteger.ONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class Feistel64BinaryTest {

    private static Stream<Feistel64> feistel() {
        RoundFunction64 f = (round, value) -> (value * 71) + round;
        return IntStream.range(0, 11).boxed().flatMap(i -> Stream.of(
                new Feistel64Balanced(i, 16, false, f),
                new Feistel64Unbalanced(i, 16, 8, 8, false, f),
                new Feistel64Unbalanced(i, 16, 1, 7, false, f),
                new Feistel64Unbalanced(i, 16, 2, 3, false, f),
                new Feistel64Unbalanced(i, 16, 13, 3, false, f),
                new Feistel64Unbalanced(i, 16, 11, 2, false, f),
                new Feistel64Unbalanced(i, 16, 0, 16, false, f),
                new Feistel64Unbalanced(i, 16, 16, 0, false, f),
                new Feistel64Unbalanced(i, 16, 1, 0, false, f),
                new Feistel64Unbalanced(i, 16, 0, 1, false, f),

                new Feistel64Balanced(i, 64, false, f),
                new Feistel64Unbalanced(i, 64, 32, 32, false, f),
                new Feistel64Unbalanced(i, 64, 0, 32, false, f),
                new Feistel64Unbalanced(i, 64, 32, 0, false, f),
                new Feistel64Unbalanced(i, 64, 30, 2, false, f),
                new Feistel64Unbalanced(i, 64, 7, 45, false, f),

                new Feistel64Balanced(i, 32, false, f),
                new Feistel64Unbalanced(i, 32, 16, 16, false, f),
                new Feistel64Unbalanced(i, 32, 1, 16, false, f),
                new Feistel64Unbalanced(i, 32, 16, 3, false, f),
                new Feistel64Unbalanced(i, 32, 30, 2, false, f),
                new Feistel64Unbalanced(i, 32, 7, 25, false, f),

                new Feistel64Unbalanced(i, 1, 0, 1, false, f),
                new Feistel64Unbalanced(i, 1, 1, 0, false, f),
                new Feistel64Unbalanced(i, 3, 1, 2, false, f),
                new Feistel64Unbalanced(i, 17, 2, 8, false, f),
                new Feistel64Unbalanced(i, 33, 30, 2, false, f),
                new Feistel64Unbalanced(i, 47, 7, 7, false, f),
                new Feistel64Unbalanced(i, 63, 30, 30, false, f)
        ));
    }

    @ParameterizedTest
    @MethodSource("feistel")
    void isPermutation(Feistel64BinaryBase feistel) {
        int count = testCount(feistel);
        long increment = testIncrement(feistel, count);
        long max = 1L << feistel.totalBits;
        LongSet longs = new LongHashSet(count);

        for (long i = 0; i < count; i++) {
            long output = feistel.applyAsLong(increment * i);
            longs.add(output);

            if (feistel.totalBits < 64) {
                assertTrue(output >= 0, () -> output + " >= " + 0);
                assertTrue(Long.compareUnsigned(output, max) < 0,
                        () -> output + " <= " + Long.toUnsignedString(max));
            }
        }
        assertEquals(count, longs.size());
    }

    @ParameterizedTest
    @MethodSource("feistel")
    void isReversible(Feistel64BinaryBase feistel) {
        int count = testCount(feistel);
        long increment = testIncrement(feistel, count);
        LongUnaryOperator id = feistel.reversed().compose(feistel);
        for (int i = 0; i < count; i++) {
            long input = increment * i;
            assertEquals(input, id.applyAsLong(input));
        }
    }

    private int testCount(Feistel64BinaryBase feistel) {
        return ONE
                .shiftLeft(feistel.totalBits)
                .min(BigInteger.valueOf(1_000_000))
                .intValue();
    }

    private long testIncrement(Feistel64BinaryBase feistel, int count) {
        return ONE
                .shiftLeft(feistel.totalBits)
                .divide(BigInteger.valueOf(count))
                .max(ONE)
                .longValue();
    }

}
