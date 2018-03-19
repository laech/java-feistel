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

    private static Stream<Feistel64> feistel16() {
        RoundFunction64 f = (round, value) -> (value * 71) + round;
        return IntStream.range(0, 20).boxed().flatMap(i -> Stream.of(
                new Feistel64Balanced(i, 16, false, f),
                new Feistel64Unbalanced(i, 16, 8, 8, false, f),
                new Feistel64Unbalanced(i, 16, 1, 7, false, f),
                new Feistel64Unbalanced(i, 16, 2, 3, false, f),
                new Feistel64Unbalanced(i, 16, 13, 3, false, f),
                new Feistel64Unbalanced(i, 16, 11, 2, false, f),
                new Feistel64Unbalanced(i, 16, 0, 16, false, f),
                new Feistel64Unbalanced(i, 16, 16, 0, false, f),
                new Feistel64Unbalanced(i, 16, 1, 0, false, f),
                new Feistel64Unbalanced(i, 16, 0, 1, false, f)
        ));
    }

    @ParameterizedTest
    @MethodSource("feistel16")
    void isPermutation16Bit(Feistel64 feistel) {
        int count = 1 << 16;
        LongSet longs = new LongHashSet(count);
        for (int i = 0; i < count; i++) {
            long j = feistel.applyAsLong(i);
            assertTrue(j >= 0, () -> String.valueOf(j));
            assertTrue(j < count, () -> String.valueOf(j));
            longs.add(j);
        }
        assertEquals(count, longs.size());
    }

    @ParameterizedTest
    @MethodSource("feistel16")
    void isReversible16Bit(Feistel64 feistel) {
        LongUnaryOperator id = feistel.reversed().compose(feistel);
        for (int i = 0, count = 1 << 16; i < count; i++) {
            assertEquals(i, id.applyAsLong(i));
        }
    }

    private static Stream<Feistel64> feistel64() {
        RoundFunction64 f = (round, value) -> (value * 11) * round;
        return IntStream.range(0, 11).boxed().flatMap(i -> Stream.of(
                new Feistel64Balanced(i, 64, false, f),
                new Feistel64Unbalanced(i, 64, 32, 32, false, f),
                new Feistel64Unbalanced(i, 64, 0, 32, false, f),
                new Feistel64Unbalanced(i, 64, 32, 0, false, f),
                new Feistel64Unbalanced(i, 64, 30, 2, false, f),
                new Feistel64Unbalanced(i, 64, 7, 45, false, f)
        ));
    }

    @ParameterizedTest
    @MethodSource("feistel64")
    void isPermutation64Bit(Feistel64 feistel) {
        int count = 1_000_000;
        LongSet longs = new LongHashSet(count);
        for (int i = 1; i <= count; i++) {
            longs.add(feistel.applyAsLong(Long.MIN_VALUE + 987654321L * i));
        }
        assertEquals(count, longs.size());
    }

    @ParameterizedTest
    @MethodSource("feistel64")
    void isReversible64Bit(Feistel64 feistel) {
        LongUnaryOperator id = feistel.reversed().compose(feistel);
        for (long i = Long.MIN_VALUE, count = 0;
             count < 1_000_000;
             count++, i += 987654321) {
            assertEquals(i, id.applyAsLong(i));
        }
    }

    private static Stream<Feistel64> feistel32() {
        RoundFunction64 f = (round, value) -> (value * 11) * round;
        return IntStream.range(0, 11).boxed().flatMap(i -> Stream.of(
                new Feistel64Balanced(i, 32, false, f),
                new Feistel64Unbalanced(i, 32, 16, 16, false, f),
                new Feistel64Unbalanced(i, 32, 1, 16, false, f),
                new Feistel64Unbalanced(i, 32, 16, 3, false, f),
                new Feistel64Unbalanced(i, 32, 30, 2, false, f),
                new Feistel64Unbalanced(i, 32, 7, 25, false, f)
        ));
    }

    @ParameterizedTest
    @MethodSource("feistel32")
    void isPermutation32Bit(Feistel64 feistel) {
        int count = 1_000_000;
        LongSet longs = new LongHashSet(count);
        for (int i = 0; i < count; i++) {
            long input = Integer.toUnsignedLong(Integer.MIN_VALUE + 4321 * i);
            long output = feistel.applyAsLong(input);
            assertTrue(output >= 0, () -> String.valueOf(output));
            assertTrue(output < (1L << 32), () -> String.valueOf(output));
            longs.add(output);
        }
        assertEquals(count, longs.size());
    }

    @ParameterizedTest
    @MethodSource("feistel32")
    void isReversible32Bit(Feistel64 feistel) {
        LongUnaryOperator id = feistel.reversed().compose(feistel);
        for (int i = Integer.MIN_VALUE, count = 0;
             count < 1_000_000;
             count++, i += 4321) {
            long input = Integer.toUnsignedLong(i);
            assertEquals(input, id.applyAsLong(input));
        }
    }

    private static Stream<Feistel64> feistelOddBits() {
        RoundFunction64 f = (round, value) -> (value * 11) * round;
        return IntStream.range(0, 11).boxed().flatMap(i -> Stream.of(
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
    @MethodSource("feistelOddBits")
    void isPermutationOddBits(Feistel64Unbalanced feistel) {
        int count = feistelOddBitsTestLimit(feistel);
        LongSet longs = new LongHashSet(count);
        for (int i = 0; i < count; i++) {
            longs.add(feistel.applyAsLong(i));
        }
        assertEquals(count, longs.size());
    }

    @ParameterizedTest
    @MethodSource("feistelOddBits")
    void isReversibleOddBits(Feistel64Unbalanced feistel) {
        int count = feistelOddBitsTestLimit(feistel);
        LongUnaryOperator id = feistel.reversed().compose(feistel);
        for (int i = 0; i < count; i++) {
            assertEquals(i, id.applyAsLong(i));
        }
    }

    private int feistelOddBitsTestLimit(Feistel64Unbalanced feistel) {
        return ONE
                .shiftLeft(feistel.totalBits)
                .min(BigInteger.valueOf(1_000_000))
                .intValue();
    }

}
