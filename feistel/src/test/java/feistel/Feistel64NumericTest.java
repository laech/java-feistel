package feistel;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class Feistel64NumericTest {

    private static Stream<Feistel64NumericBase> smallDomain() {
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
                new Params(11, 1, 1, f),
                new Params(11, 1, 1, f),
                new Params(7, 0, 200, f),
                new Params(7, 0, 200, f),
                new Params(7, 320, 0, f),
                new Params(7, 320, 0, f),
                new Params(4, 320, 200, f),
                new Params(7, 320, 200, f),
                new Params(7, 320, 200, f),
                new Params(11, 320, 200, f),
                new Params(11, 320, 200, f),
                new Params(12, 99, 99, f),
                new Params(12, 99, 99, f)
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
    @MethodSource("smallDomain")
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
    @MethodSource("smallDomain")
    void isReversible(Feistel64NumericBase feistel) {
        Feistel64 reversed = feistel.reversed();
        for (long i = 0; i <= feistel.max; i++) {
            long j = feistel.applyAsLong(i);
            assertTrue(j >= 0, () -> Long.toString(j));
            assertTrue(j <= feistel.max, () -> Long.toString(j));
            assertEquals(i, reversed.applyAsLong(j));
        }
    }
}
