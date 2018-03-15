package feistel;

import feistel.Feistel.RoundFunction;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class NumericFeistelSmallDomainTest {

    private static Stream<Arguments> data() {
        RoundFunction<BigInteger> f = (round, value) ->
                value.multiply(BigInteger.valueOf(31)).shiftLeft(round);
        return Stream.of(
                Arguments.of(0, 0, 0, f),
                Arguments.of(0, 0, 1, f),
                Arguments.of(0, 1, 1, f),
                Arguments.of(1, 0, 0, f),
                Arguments.of(1, 0, 1, f),
                Arguments.of(1, 1, 1, f),
                Arguments.of(11, 1, 1, f),
                Arguments.of(7, 0, 200, f),
                Arguments.of(7, 320, 0, f),
                Arguments.of(7, 320, 200, f),
                Arguments.of(11, 320, 200, f),
                Arguments.of(12, 99, 99, f)
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    void isPermutation(int rounds, int a, int b, RoundFunction<BigInteger> f) {
        BigInteger m = BigInteger.valueOf(a);
        BigInteger n = BigInteger.valueOf(b);
        Feistel<BigInteger> feistel = Feistel.numeric(rounds, m, n, f);
        BigInteger count = m.multiply(n);
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
    @MethodSource("data")
    void canBeReversed(int rounds, int a, int b, RoundFunction<BigInteger> f) {
        BigInteger m = BigInteger.valueOf(a);
        BigInteger n = BigInteger.valueOf(b);
        Feistel<BigInteger> feistel = Feistel.numeric(rounds, m, n, f);
        Feistel<BigInteger> reversed = feistel.reversed();
        for (BigInteger i = ZERO, end = m.multiply(n); i.compareTo(end) < 0; i = i.add(ONE)) {
            BigInteger j = feistel.apply(i);
            assertTrue(j.compareTo(ZERO) >= 0, () -> String.valueOf(j));
            assertTrue(j.compareTo(end) < 0, () -> String.valueOf(j));
            assertEquals(i, reversed.apply(j));
        }
    }

}
