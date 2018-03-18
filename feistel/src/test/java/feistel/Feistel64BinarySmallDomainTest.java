package feistel;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.LongUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class Feistel64BinarySmallDomainTest {

    private static Stream<Feistel64> smallDomain() {
        RoundFunction64 f = (round, value) -> (value * 71) + round;
        return IntStream.range(0, 20).boxed().flatMap(i -> Stream.of(
                new Feistel64Balanced(i, 16, false, f),
                new Feistel64Unbalanced(i, 16, 8, 8, false, f)
        ));
    }

    @ParameterizedTest
    @MethodSource("smallDomain")
    void isPermutation(Feistel64 feistel) {
        int count = 1 << 16;
        assertEquals(count, LongStream
                .range(0, count)
                .map(feistel)
                .distinct()
                .peek(i -> {
                    assertTrue(i >= 0, () -> String.valueOf(i));
                    assertTrue(i < count, () -> String.valueOf(i));
                })
                .count());
    }

    @ParameterizedTest
    @MethodSource("smallDomain")
    void isReversible(Feistel64 feistel) {
        LongUnaryOperator id = feistel.reversed().compose(feistel);
        for (int i = 0, count = 1 << 16; i < count; i++) {
            assertEquals(i, id.applyAsLong(i));
        }
    }
}
