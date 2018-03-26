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

    private static Stream<LongFeistel> feistel64() {
        return params().flatMap(Params::toFeistel64);
    }

    private static Stream<Feistel<BigInteger>> feistelBigInteger() {
        return params().flatMap(Params::toFeistelBigInteger);
    }

    private static Stream<Params> params() {
        LongRoundFunction longF = (round, value) -> (value * 31) << round;
        RoundFunction<BigInteger> bigF = (round, value) ->
                value.multiply(BigInteger.valueOf(31)).shiftLeft(round);

        return Stream.of(
                new Params(0, 0, 0, longF, bigF),
                new Params(0, 0, 0, longF, bigF),
                new Params(0, 0, 1, longF, bigF),
                new Params(0, 0, 1, longF, bigF),
                new Params(0, 1, 1, longF, bigF),
                new Params(0, 1, 1, longF, bigF),
                new Params(1, 0, 0, longF, bigF),
                new Params(1, 0, 0, longF, bigF),
                new Params(1, 0, 1, longF, bigF),
                new Params(1, 0, 1, longF, bigF),
                new Params(1, 1, 1, longF, bigF),
                new Params(1, 1, 1, longF, bigF),
                new Params(2, 64435, 3, longF, bigF),
                new Params(11, 1, 1, longF, bigF),
                new Params(11, 1, 1, longF, bigF),
                new Params(7, 0, 200, longF, bigF),
                new Params(7, 0, 200, longF, bigF),
                new Params(7, 320, 0, longF, bigF),
                new Params(7, 320, 0, longF, bigF),
                new Params(4, 321, 123, longF, bigF),
                new Params(7, 401, 2, longF, bigF),
                new Params(7, 32, 75, longF, bigF),
                new Params(11, 10, 100, longF, bigF),
                new Params(11, 320, 200, longF, bigF),
                new Params(12, 99, 199, longF, bigF),
                new Params(12, 99, 19, longF, bigF)
        );
    }

    private static final class Params {
        final int rounds;
        final long a;
        final long b;
        final LongRoundFunction longF;
        final RoundFunction<BigInteger> bigF;

        Params(
                int rounds,
                long a,
                long b,
                LongRoundFunction longF,
                RoundFunction<BigInteger> bigF
        ) {
            this.rounds = rounds;
            this.a = a;
            this.b = b;
            this.longF = longF;
            this.bigF = bigF;
        }

        Stream<LongFeistel> toFeistel64() {
            return Stream.of(
                    new LongFeistelNumeric1(rounds, a, b, false, longF),
                    new LongFeistelNumeric2(rounds, a, b, false, longF)
            );
        }

        Stream<Feistel<BigInteger>> toFeistelBigInteger() {
            BigInteger a = BigInteger.valueOf(this.a);
            BigInteger b = BigInteger.valueOf(this.b);
            return Stream.of(
                    new BigIntegerFeistelNumeric1(rounds, a, b, false, bigF),
                    new BigIntegerFeistelNumeric2(rounds, a, b, false, bigF)
            );
        }
    }

    @ParameterizedTest
    @MethodSource("feistel64")
    void isPermutation64(LongFeistelNumericBase feistel) {
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
    void isReversible64(LongFeistelNumericBase feistel) {
        LongUnaryOperator id = feistel.reversed().compose(feistel);
        for (long i = 0; i <= feistel.max; i++) {
            assertEquals(i, id.applyAsLong(i));
        }
    }


    @ParameterizedTest
    @MethodSource("feistelBigInteger")
    void isPermutationBigInteger(BigIntegerFeistelNumericBase feistel) {
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
    void isReversibleBigInteger(BigIntegerFeistelNumericBase feistel) {
        Function<BigInteger, BigInteger> id = feistel.andThen(feistel.reversed());
        for (BigInteger i = ZERO; i.compareTo(feistel.max) <= 0; i = i.add(ONE)) {
            assertEquals(i, id.apply(i));
        }
    }
}
