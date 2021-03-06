package feistel;

import isomorphic.Isomorphism;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.function.Function;
import java.util.function.LongUnaryOperator;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.lang.Math.multiplyExact;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FeistelNumericTest extends BaseTest {

    private static Stream<Arguments> feistel64() {
        return params().flatMap(p ->
                p.toFeistelOfLong().map(f -> Arguments.of(p, f)));
    }

    private static Stream<Arguments> feistelBigInteger() {
        return params().flatMap(p ->
                p.toFeistelOfBigInteger().map(f -> Arguments.of(p, f)));
    }

    private static Stream<Params> params() {
        RoundFunction.OfLong longF = (round, value) -> (value * 31) << round;
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
        final RoundFunction.OfLong longF;
        final RoundFunction<BigInteger> bigF;

        Params(
                int rounds,
                long a,
                long b,
                RoundFunction.OfLong longF,
                RoundFunction<BigInteger> bigF
        ) {
            this.rounds = rounds;
            this.a = a;
            this.b = b;
            this.longF = longF;
            this.bigF = bigF;
        }

        Stream<Isomorphism.OfLong> toFeistelOfLong() {
            return Stream.of(
                    FeistelOfLongNumeric.fe1(rounds, a, b, longF),
                    FeistelOfLongNumeric.fe2(rounds, a, b, longF)
            );
        }

        Stream<Isomorphism<BigInteger, BigInteger>> toFeistelOfBigInteger() {
            BigInteger a = BigInteger.valueOf(this.a);
            BigInteger b = BigInteger.valueOf(this.b);
            return Stream.of(
                    FeistelOfBigIntegerNumeric.fe1(rounds, a, b, bigF),
                    FeistelOfBigIntegerNumeric.fe2(rounds, a, b, bigF)
            );
        }

        BigInteger countBigInteger() {
            BigInteger a = BigInteger.valueOf(this.a);
            BigInteger b = BigInteger.valueOf(this.b);
            return a.multiply(b);
        }

        long countLong() {
            return multiplyExact(a, b);
        }

        @Override
        public String toString() {
            return "Params{" +
                    "rounds=" + rounds +
                    ", a=" + a +
                    ", b=" + b +
                    '}';
        }
    }

    @ParameterizedTest
    @MethodSource("feistel64")
    void isPermutation64(Params params, Isomorphism.OfLong feistel) {
        long count = params.countLong();
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
    void isInverse64(Params params, Isomorphism.OfLong feistel) {
        LongUnaryOperator id = feistel.inverse().compose(feistel);
        long count = params.countLong();
        for (long i = 0; i < count; i++) {
            assertEquals(i, id.applyAsLong(i));
        }
    }


    @ParameterizedTest
    @MethodSource("feistelBigInteger")
    void isPermutationBigInteger(Params params, Isomorphism<BigInteger, BigInteger> feistel) {
        BigInteger count = params.countBigInteger();
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
    void isInverseBigInteger(Params params, Isomorphism<BigInteger, BigInteger> feistel) {
        Function<BigInteger, BigInteger> id = feistel.andThen(feistel.inverse());
        BigInteger count = params.countBigInteger();
        for (BigInteger i = ZERO; i.compareTo(count) < 0; i = i.add(ONE)) {
            assertEquals(i, id.apply(i));
        }
    }
}
