package feistel;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.LongUnaryOperator;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class Feistel64NumericTest {

    private static Stream<Feistel64NumericBase> feistel() {
        RoundFunction64 f = (round, value) -> (value * 31) << round;
        return Stream.of(
                new Params(0, 0, 0, f),
                new Params(0, 0, 0, f),
                new Params(0, 0, 1, f),
                new Params(0, 0, 1, f),
                new Params(0, 1, 1, f),
                new Params(0, 1, 1, f),
                new Params(1, 0, 0, f),
                new Params(1, 0, 0, f),
                new Params(1, 0, 1, f),
                new Params(1, 0, 1, f),
                new Params(1, 1, 1, f),
                new Params(1, 1, 1, f),
                new Params(2, 64435, 3, f),
                new Params(11, 1, 1, f),
                new Params(11, 1, 1, f),
                new Params(7, 0, 200, f),
                new Params(7, 0, 200, f),
                new Params(7, 320, 0, f),
                new Params(7, 320, 0, f),
                new Params(4, 321, 123, f),
                new Params(7, 401, 2, f),
                new Params(7, 32, 75, f),
                new Params(11, 10, 100, f),
                new Params(11, 320, 200, f),
                new Params(12, 99, 199, f),
                new Params(12, 99, 19, f)
        ).flatMap(p -> Stream.of(
                new Feistel64Numeric1(p.round, p.a, p.b, false, p.f),
                new Feistel64Numeric2(p.round, p.a, p.b, false, p.f)
        ));
    }

    private static final class Params {
        final int round;
        final long a;
        final long b;
        final RoundFunction64 f;

        Params(int round, long a, long b, RoundFunction64 f) {
            this.round = round;
            this.a = a;
            this.b = b;
            this.f = f;
        }
    }

    @ParameterizedTest
    @MethodSource("feistel")
    void isPermutation(Feistel64NumericBase feistel) {
        long count = feistel.max + 1;
        assertEquals(count, LongStream
                .range(0, count)
                .map(feistel)
                .distinct()
                .peek(i -> {
                    assertTrue(i >= 0, () -> Long.toString(i));
                    assertTrue(i < count, () -> Long.toString(i));
                })
                .count());
    }

    @ParameterizedTest
    @MethodSource("feistel")
    void isReversible(Feistel64NumericBase feistel) {
        LongUnaryOperator id = feistel.reversed().compose(feistel);
        for (long i = 0; i <= feistel.max; i++) {
            assertEquals(i, id.applyAsLong(i));
        }
    }
}
