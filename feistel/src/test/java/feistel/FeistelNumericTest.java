package feistel;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.function.Function;
import java.util.function.LongUnaryOperator;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FeistelNumericTest {

    private static Stream<Feistel64> feistel64() {
        return params().flatMap(Params::toFeistel64);
    }

    private static Stream<Feistel<BigInteger>> feistelBigInteger() {
        return params().flatMap(Params::toFeistelBigInteger);
    }

    private static Stream<Params> params() {
        RoundFunction64 f64 = (round, value) -> (value * 31) << round;
        RoundFunction<BigInteger> f = (round, value) ->
                value.multiply(BigInteger.valueOf(31)).shiftLeft(round);

        return Stream.of(
                new Params(0, 0, 0, f64, f),
                new Params(0, 0, 0, f64, f),
                new Params(0, 0, 1, f64, f),
                new Params(0, 0, 1, f64, f),
                new Params(0, 1, 1, f64, f),
                new Params(0, 1, 1, f64, f),
                new Params(1, 0, 0, f64, f),
                new Params(1, 0, 0, f64, f),
                new Params(1, 0, 1, f64, f),
                new Params(1, 0, 1, f64, f),
                new Params(1, 1, 1, f64, f),
                new Params(1, 1, 1, f64, f),
                new Params(2, 64435, 3, f64, f),
                new Params(11, 1, 1, f64, f),
                new Params(11, 1, 1, f64, f),
                new Params(7, 0, 200, f64, f),
                new Params(7, 0, 200, f64, f),
                new Params(7, 320, 0, f64, f),
                new Params(7, 320, 0, f64, f),
                new Params(4, 321, 123, f64, f),
                new Params(7, 401, 2, f64, f),
                new Params(7, 32, 75, f64, f),
                new Params(11, 10, 100, f64, f),
                new Params(11, 320, 200, f64, f),
                new Params(12, 99, 199, f64, f),
                new Params(12, 99, 19, f64, f)
        );
    }

    private static final class Params {
        final int rounds;
        final long a;
        final long b;
        final RoundFunction64 f64;
        final RoundFunction<BigInteger> f;

        Params(
                int rounds,
                long a,
                long b,
                RoundFunction64 f64,
                RoundFunction<BigInteger> f
        ) {
            this.rounds = rounds;
            this.a = a;
            this.b = b;
            this.f64 = f64;
            this.f = f;
        }

        Stream<Feistel64> toFeistel64() {
            return Stream.of(
                    new Feistel64Numeric1(rounds, a, b, false, f64),
                    new Feistel64Numeric2(rounds, a, b, false, f64)
            );
        }

        Stream<Feistel<BigInteger>> toFeistelBigInteger() {
            BigInteger a = BigInteger.valueOf(this.a);
            BigInteger b = BigInteger.valueOf(this.b);
            return Stream.of(
                    new FeistelBigIntegerNumeric1(rounds, a, b, false, f),
                    new FeistelBigIntegerNumeric2(rounds, a, b, false, f)
            );
        }
    }

    @ParameterizedTest
    @MethodSource("feistel64")
    void isPermutation64(Feistel64NumericBase feistel) {
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
    @MethodSource("feistel64")
    void isReversible64(Feistel64NumericBase feistel) {
        LongUnaryOperator id = feistel.reversed().compose(feistel);
        for (long i = 0; i <= feistel.max; i++) {
            assertEquals(i, id.applyAsLong(i));
        }
    }


    @ParameterizedTest
    @MethodSource("feistelBigInteger")
    void isPermutationBigInteger(FeistelBigIntegerNumericBase feistel) {
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
    @MethodSource("feistelBigInteger")
    void isReversibleBigInteger(FeistelBigIntegerNumericBase feistel) {
        Function<BigInteger, BigInteger> id = feistel.andThen(feistel.reversed());
        for (BigInteger i = ZERO; i.compareTo(feistel.max) <= 0; i = i.add(ONE)) {
            assertEquals(i, id.apply(i));
        }
    }
}
