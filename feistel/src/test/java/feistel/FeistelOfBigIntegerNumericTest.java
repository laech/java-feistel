package feistel;

import feistel.Feistel.RoundFunction;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FeistelOfBigIntegerNumericTest {

    private static Stream<Params> smallDomain() {
        RoundFunction<BigInteger> f = (round, value) ->
                value.multiply(BigInteger.valueOf(31)).shiftLeft(round);
        return Stream.of(
                new Params(0, 0, 0, f),
                new Params(0, 0, 1, f),
                new Params(0, 1, 1, f),
                new Params(1, 0, 0, f),
                new Params(1, 0, 1, f),
                new Params(1, 1, 1, f),
                new Params(11, 1, 1, f),
                new Params(7, 0, 200, f),
                new Params(7, 320, 0, f),
                new Params(7, 320, 200, f),
                new Params(11, 320, 200, f),
                new Params(12, 99, 99, f)
        );
    }

    @ParameterizedTest
    @MethodSource("smallDomain")
    void isPermutation(Params params) {
        BigInteger count = params.count;
        assertEquals(count.longValue(), LongStream
                .range(0, count.longValue())
                .mapToObj(BigInteger::valueOf)
                .map(params.feistel)
                .distinct()
                .peek(i -> {
                    assertTrue(i.compareTo(ZERO) >= 0, () -> String.valueOf(i));
                    assertTrue(i.compareTo(count) < 0, () -> String.valueOf(i));
                })
                .count());
    }

    @ParameterizedTest
    @MethodSource("smallDomain")
    void isReversible(Params params) {
        Feistel<BigInteger> reversed = params.feistel.reversed();
        BigInteger max = params.count;
        for (BigInteger i = ZERO; i.compareTo(max) < 0; i = i.add(ONE)) {
            BigInteger j = params.feistel.apply(i);
            assertTrue(j.compareTo(ZERO) >= 0, () -> String.valueOf(j));
            assertTrue(j.compareTo(max) < 0, () -> String.valueOf(j));
            assertEquals(i, reversed.apply(j));
        }
    }

    private static final class Params {
        final int rounds;
        final BigInteger a;
        final BigInteger b;
        final BigInteger count;
        final RoundFunction<BigInteger> f;
        final Feistel<BigInteger> feistel;

        Params(int rounds, long a, long b, RoundFunction<BigInteger> f) {
            this.rounds = rounds;
            this.a = BigInteger.valueOf(a);
            this.b = BigInteger.valueOf(b);
            this.count = this.a.multiply(this.b);
            this.f = f;
            this.feistel = new FeistelOfBigIntegerNumeric(
                    rounds, this.a, this.b, false, f);
        }
    }
}
