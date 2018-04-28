package feistel;

import com.carrotsearch.hppc.LongHashSet;
import com.carrotsearch.hppc.LongSet;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.function.Function;
import java.util.function.LongUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FeistelBinaryTest extends BaseTest {

    private static Stream<Params> params() {
        return Stream.concat(balancedParams(), unbalancedParams());
    }

    private static Stream<UnbalancedParams> unbalancedParams() {
        RoundFunction.OfLong longF = (round, value) -> (value * 71) + round;
        RoundFunction<BigInteger> bigF = (round, value) ->
                value.multiply(BigInteger.valueOf(71))
                        .add(BigInteger.valueOf(round));

        return IntStream.range(0, 7).boxed().flatMap(rounds -> Stream.of(
                new UnbalancedParams(rounds, 16, 8, 8, longF, bigF),
                new UnbalancedParams(rounds, 16, 1, 7, longF, bigF),
                new UnbalancedParams(rounds, 16, 2, 3, longF, bigF),
                new UnbalancedParams(rounds, 16, 13, 3, longF, bigF),
                new UnbalancedParams(rounds, 16, 11, 2, longF, bigF),
                new UnbalancedParams(rounds, 16, 0, 16, longF, bigF),
                new UnbalancedParams(rounds, 16, 16, 0, longF, bigF),
                new UnbalancedParams(rounds, 16, 1, 0, longF, bigF),
                new UnbalancedParams(rounds, 16, 0, 1, longF, bigF),

                new UnbalancedParams(rounds, 64, 32, 32, longF, bigF),
                new UnbalancedParams(rounds, 64, 0, 32, longF, bigF),
                new UnbalancedParams(rounds, 64, 32, 0, longF, bigF),
                new UnbalancedParams(rounds, 64, 30, 2, longF, bigF),
                new UnbalancedParams(rounds, 64, 7, 45, longF, bigF),

                new UnbalancedParams(rounds, 32, 16, 16, longF, bigF),
                new UnbalancedParams(rounds, 32, 1, 16, longF, bigF),
                new UnbalancedParams(rounds, 32, 16, 3, longF, bigF),
                new UnbalancedParams(rounds, 32, 30, 2, longF, bigF),
                new UnbalancedParams(rounds, 32, 7, 25, longF, bigF),

                new UnbalancedParams(rounds, 1, 0, 1, longF, bigF),
                new UnbalancedParams(rounds, 1, 1, 0, longF, bigF),
                new UnbalancedParams(rounds, 3, 1, 2, longF, bigF),
                new UnbalancedParams(rounds, 17, 2, 8, longF, bigF),
                new UnbalancedParams(rounds, 33, 30, 2, longF, bigF),
                new UnbalancedParams(rounds, 47, 7, 7, longF, bigF),
                new UnbalancedParams(rounds, 63, 30, 30, longF, bigF)
        ));
    }

    private static Stream<BalancedParams> balancedParams() {
        RoundFunction.OfLong longF = (round, value) -> (value * 17) + round;
        RoundFunction<BigInteger> bigF = (round, value) ->
                value.multiply(BigInteger.valueOf(17))
                        .add(BigInteger.valueOf(round));

        return IntStream.range(0, 7).boxed().flatMap(rounds ->
                IntStream.of(16, 32, 64).mapToObj(totalBits ->
                        new BalancedParams(rounds, totalBits, longF, bigF)));
    }

    private static abstract class Params {
        final int totalBits;

        Params(int totalBits) {
            this.totalBits = totalBits;
        }

        abstract Feistel.OfLong toFeistelOfLong();

        abstract Feistel<BigInteger> toFeistelBigInteger();

        abstract BigInteger maxBigInteger();
    }

    private static final class BalancedParams extends Params {
        final int rounds;
        final RoundFunction.OfLong longF;
        final RoundFunction<BigInteger> bigF;

        BalancedParams(
                int rounds,
                int totalBits,
                RoundFunction.OfLong longF,
                RoundFunction<BigInteger> bigF
        ) {
            super(totalBits);
            this.rounds = rounds;
            this.longF = longF;
            this.bigF = bigF;
        }

        @Override
        Feistel.OfLong toFeistelOfLong() {
            return new LongFeistelBinaryBalanced(rounds, totalBits, false, longF);
        }

        @Override
        Feistel<BigInteger> toFeistelBigInteger() {
            return new BigIntegerFeistelBinaryBalanced(
                    rounds, totalBits, false, bigF);
        }

        @Override
        BigInteger maxBigInteger() {
            return BigInteger.valueOf(~(0xFFFF_FFFF_FFFF_FFFFL << totalBits));
        }

        @Override
        public String toString() {
            return "BalancedParams{" +
                    "totalBits=" + totalBits +
                    ", rounds=" + rounds +
                    '}';
        }
    }

    private static final class UnbalancedParams extends Params {
        final int rounds;
        final int sourceBits;
        final int targetBits;
        final RoundFunction.OfLong longF;
        final RoundFunction<BigInteger> f;

        UnbalancedParams(
                int rounds,
                int totalBits,
                int sourceBits,
                int targetBits,
                RoundFunction.OfLong longF,
                RoundFunction<BigInteger> f
        ) {
            super(totalBits);
            this.rounds = rounds;
            this.sourceBits = sourceBits;
            this.targetBits = targetBits;
            this.longF = longF;
            this.f = f;
        }

        @Override
        Feistel.OfLong toFeistelOfLong() {
            return new LongFeistelBinaryUnbalanced(
                    rounds, totalBits, sourceBits, targetBits, false, longF);
        }

        @Override
        Feistel<BigInteger> toFeistelBigInteger() {
            return new BigIntegerFeistelBinaryUnbalanced(
                    rounds, totalBits, sourceBits, targetBits, false, f);
        }

        @Override
        BigInteger maxBigInteger() {
            return ONE.shiftLeft(totalBits).subtract(ONE);
        }

        @Override
        public String toString() {
            return "UnbalancedParams{" +
                    "totalBits=" + totalBits +
                    ", rounds=" + rounds +
                    ", sourceBits=" + sourceBits +
                    ", targetBits=" + targetBits +
                    '}';
        }
    }

    @ParameterizedTest
    @MethodSource("params")
    void isPermutation64(Params params) {
        Feistel.OfLong feistel = params.toFeistelOfLong();
        int count = testCountOfLong(params);
        long increment = testIncrementOfLong(params, count);
        long max = 1L << params.totalBits;
        LongSet longs = new LongHashSet(count);

        for (long i = 0; i < count; i++) {
            long output = feistel.applyAsLong(increment * i);
            longs.add(output);

            if (params.totalBits < 64) {
                assertTrue(output >= 0, () -> output + " >= " + 0);
                assertTrue(Long.compareUnsigned(output, max) < 0,
                        () -> output + " <= " + Long.toUnsignedString(max));
            }
        }
        assertEquals(count, longs.size());
    }

    @ParameterizedTest
    @MethodSource("params")
    void isReversible64(Params params) {
        Feistel.OfLong feistel = params.toFeistelOfLong();
        int count = testCountOfLong(params);
        long increment = testIncrementOfLong(params, count);
        LongUnaryOperator id = feistel.reversed().compose(feistel);
        for (int i = 0; i < count; i++) {
            long input = increment * i;
            assertEquals(input, id.applyAsLong(input));
        }
    }

    private int testCountOfLong(Params params) {
        return ONE
                .shiftLeft(params.totalBits)
                .min(BigInteger.valueOf(1_000_000))
                .intValue();
    }

    private long testIncrementOfLong(Params params, int count) {
        return ONE
                .shiftLeft(params.totalBits)
                .divide(BigInteger.valueOf(count))
                .max(ONE)
                .longValue();
    }

    @ParameterizedTest
    @MethodSource("params")
    void isPermutationBigInteger(Params params) {
        Feistel<BigInteger> feistel = params.toFeistelBigInteger();
        int count = testCountOfBigInteger(params);
        BigInteger increment = testIncrementOfBigInteger(params, count);
        BigInteger max = params.maxBigInteger();

        assertEquals(count, Stream
                .iterate(ZERO, i -> i.add(increment))
                .limit(count)
                .parallel()
                .map(feistel)
                .distinct()
                .peek(i -> {
                    assertTrue(i.compareTo(ZERO) >= 0, () -> i + " >= " + 0);
                    assertTrue(i.compareTo(max) <= 0, () -> i + " <= " + max);
                })
                .count());
    }

    @ParameterizedTest
    @MethodSource("params")
    void isReversibleBigInteger(Params params) {
        int count = testCountOfBigInteger(params);
        Feistel<BigInteger> feistel = params.toFeistelBigInteger();
        BigInteger increment = testIncrementOfBigInteger(params, count);
        Function<BigInteger, BigInteger> id = feistel.reversed().compose(feistel);
        Stream.iterate(ZERO, i -> i.add(increment))
                .parallel()
                .limit(count)
                .forEach(i -> assertEquals(i, id.apply(i)));
    }

    private int testCountOfBigInteger(Params params) {
        return ONE
                .shiftLeft(params.totalBits)
                .min(BigInteger.valueOf(100_000))
                .intValue();
    }

    private BigInteger testIncrementOfBigInteger(Params params, int count) {
        return ONE
                .shiftLeft(params.totalBits)
                .divide(BigInteger.valueOf(count))
                .max(ONE);
    }

}
