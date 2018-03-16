package feistel;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FeistelBigIntegerNumericTest {

    private static Stream<FeistelBigIntegerNumericBase> smallDomain() {
        BigInteger _31 = BigInteger.valueOf(31);
        RoundFunction<BigInteger> f = (r, v) -> v.multiply(_31).shiftLeft(r);
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
                new Params(7, 320, 200, f),
                new Params(7, 320, 200, f),
                new Params(11, 320, 200, f),
                new Params(11, 320, 200, f),
                new Params(12, 99, 99, f),
                new Params(12, 99, 99, f)
        ).flatMap(p -> {
            BigInteger a = BigInteger.valueOf(p.a);
            BigInteger b = BigInteger.valueOf(p.b);
            return Stream.of(
                    new FeistelBigIntegerNumeric1(p.round, a, b, false, p.f),
                    new FeistelBigIntegerNumeric2(p.round, a, b, false, p.f)
            );
        });
    }

    private static final class Params {
        final int round;
        final long a;
        final long b;
        final RoundFunction<BigInteger> f;

        Params(int round, long a, long b, RoundFunction<BigInteger> f) {
            this.round = round;
            this.a = a;
            this.b = b;
            this.f = f;
        }
    }

    @ParameterizedTest
    @MethodSource("smallDomain")
    void isPermutation(FeistelBigIntegerNumericBase feistel) {
        BigInteger count = feistel.max.add(ONE);
        assertEquals(count.longValue(), LongStream
                .range(0, count.longValue())
                .mapToObj(BigInteger::valueOf)
                .map(feistel)
                .distinct()
                .peek(i -> {
                    assertTrue(i.compareTo(ZERO) >= 0, () -> String.valueOf(i));
                    assertTrue(i.compareTo(count) < 0, () -> String.valueOf(i));
                })
                .count());
    }

    @ParameterizedTest
    @MethodSource("smallDomain")
    void isReversible(FeistelBigIntegerNumericBase feistel) {
        Feistel<BigInteger> reversed = feistel.reversed();
        for (BigInteger i = ZERO; i.compareTo(feistel.max) <= 0; i = i.add(ONE)) {
            BigInteger j = feistel.apply(i);
            assertTrue(j.compareTo(ZERO) >= 0, () -> String.valueOf(j));
            assertTrue(j.compareTo(feistel.max) <= 0, () -> String.valueOf(j));
            assertEquals(i, reversed.apply(j));
        }
    }
}
