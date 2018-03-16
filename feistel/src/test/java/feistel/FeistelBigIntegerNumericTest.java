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
                new FeistelBigIntegerNumeric1(0, big(0), big(0), false, f),
                new FeistelBigIntegerNumeric2(0, big(0), big(0), false, f),
                new FeistelBigIntegerNumeric1(0, big(0), big(1), false, f),
                new FeistelBigIntegerNumeric2(0, big(0), big(1), false, f),
                new FeistelBigIntegerNumeric1(0, big(1), big(1), false, f),
                new FeistelBigIntegerNumeric2(0, big(1), big(1), false, f),
                new FeistelBigIntegerNumeric1(1, big(0), big(0), false, f),
                new FeistelBigIntegerNumeric2(1, big(0), big(0), false, f),
                new FeistelBigIntegerNumeric1(1, big(0), big(1), false, f),
                new FeistelBigIntegerNumeric2(1, big(0), big(1), false, f),
                new FeistelBigIntegerNumeric1(1, big(1), big(1), false, f),
                new FeistelBigIntegerNumeric2(1, big(1), big(1), false, f),
                new FeistelBigIntegerNumeric1(11, big(1), big(1), false, f),
                new FeistelBigIntegerNumeric2(11, big(1), big(1), false, f),
                new FeistelBigIntegerNumeric1(7, big(0), big(200), false, f),
                new FeistelBigIntegerNumeric2(7, big(0), big(200), false, f),
                new FeistelBigIntegerNumeric1(7, big(320), big(0), false, f),
                new FeistelBigIntegerNumeric2(7, big(320), big(0), false, f),
                new FeistelBigIntegerNumeric1(7, big(320), big(200), false, f),
                new FeistelBigIntegerNumeric2(7, big(320), big(200), false, f),
                new FeistelBigIntegerNumeric1(11, big(320), big(200), false, f),
                new FeistelBigIntegerNumeric2(11, big(320), big(200), false, f),
                new FeistelBigIntegerNumeric1(12, big(99), big(99), false, f),
                new FeistelBigIntegerNumeric2(12, big(99), big(99), false, f)
        );
    }

    private static BigInteger big(long i) {
        return BigInteger.valueOf(i);
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
