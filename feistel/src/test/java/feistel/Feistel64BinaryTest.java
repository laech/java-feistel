package feistel;

import com.carrotsearch.hppc.LongHashSet;
import com.carrotsearch.hppc.LongSet;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.LongUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class Feistel64BinaryTest {

    private static Stream<Feistel64> feistel16() {
        RoundFunction64 f = (round, value) -> (value * 71) + round;
        return IntStream.range(0, 20).boxed().flatMap(i -> Stream.of(
                new Feistel64Balanced(i, 16, false, f),
                new Feistel64Unbalanced(i, 16, 8, 8, false, f)
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
                new Feistel64Unbalanced(i, 64, 32, 32, false, f)
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
}
