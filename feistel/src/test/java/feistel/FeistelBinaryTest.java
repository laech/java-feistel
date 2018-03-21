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

final class FeistelBinaryTest {

    private static Stream<Feistel64> feistel64() {
        return Stream.concat(
                balancedParams().map(BalancedParams::toFeistel64),
                unbalancedParams().map(UnbalancedParams::toFeistel64)
        );
    }

    private static Stream<Feistel<BigInteger>> feistelBigInteger() {
        return Stream.concat(
                balancedParams().map(BalancedParams::toFeistelBigInteger),
                unbalancedParams().map(UnbalancedParams::toFeistelBigInteger)
        );
    }

    private static Stream<UnbalancedParams> unbalancedParams() {
        RoundFunction64 f64 = (round, value) -> (value * 71) + round;
        RoundFunction<BigInteger> f = (round, value) ->
                value.multiply(BigInteger.valueOf(71))
                        .add(BigInteger.valueOf(round));

        return IntStream.range(0, 7).boxed().flatMap(rounds -> Stream.of(
                new UnbalancedParams(rounds, 16, 8, 8, f64, f),
                new UnbalancedParams(rounds, 16, 1, 7, f64, f),
                new UnbalancedParams(rounds, 16, 2, 3, f64, f),
                new UnbalancedParams(rounds, 16, 13, 3, f64, f),
                new UnbalancedParams(rounds, 16, 11, 2, f64, f),
                new UnbalancedParams(rounds, 16, 0, 16, f64, f),
                new UnbalancedParams(rounds, 16, 16, 0, f64, f),
                new UnbalancedParams(rounds, 16, 1, 0, f64, f),
                new UnbalancedParams(rounds, 16, 0, 1, f64, f),

                new UnbalancedParams(rounds, 64, 32, 32, f64, f),
                new UnbalancedParams(rounds, 64, 0, 32, f64, f),
                new UnbalancedParams(rounds, 64, 32, 0, f64, f),
                new UnbalancedParams(rounds, 64, 30, 2, f64, f),
                new UnbalancedParams(rounds, 64, 7, 45, f64, f),

                new UnbalancedParams(rounds, 32, 16, 16, f64, f),
                new UnbalancedParams(rounds, 32, 1, 16, f64, f),
                new UnbalancedParams(rounds, 32, 16, 3, f64, f),
                new UnbalancedParams(rounds, 32, 30, 2, f64, f),
                new UnbalancedParams(rounds, 32, 7, 25, f64, f),

                new UnbalancedParams(rounds, 1, 0, 1, f64, f),
                new UnbalancedParams(rounds, 1, 1, 0, f64, f),
                new UnbalancedParams(rounds, 3, 1, 2, f64, f),
                new UnbalancedParams(rounds, 17, 2, 8, f64, f),
                new UnbalancedParams(rounds, 33, 30, 2, f64, f),
                new UnbalancedParams(rounds, 47, 7, 7, f64, f),
                new UnbalancedParams(rounds, 63, 30, 30, f64, f)
        ));
    }

    private static Stream<BalancedParams> balancedParams() {
        RoundFunction64 f64 = (round, value) -> (value * 17) + round;
        RoundFunction<BigInteger> f = (round, value) ->
                value.multiply(BigInteger.valueOf(17))
                        .add(BigInteger.valueOf(round));

        return IntStream.range(0, 7).boxed().flatMap(rounds ->
                IntStream.of(16, 32, 64).mapToObj(totalBits ->
                        new BalancedParams(rounds, totalBits, f64, f)));
    }

    private static final class BalancedParams {
        final int rounds;
        final int totalBits;
        final RoundFunction64 f64;
        final RoundFunction<BigInteger> f;

        BalancedParams(
                int rounds,
                int totalBits,
                RoundFunction64 f64,
                RoundFunction<BigInteger> f
        ) {
            this.rounds = rounds;
            this.totalBits = totalBits;
            this.f64 = f64;
            this.f = f;
        }

        Feistel64 toFeistel64() {
            return new Feistel64BinaryBalanced(rounds, totalBits, false, f64);
        }

        Feistel<BigInteger> toFeistelBigInteger() {
            return new FeistelBigIntegerBinaryBalanced(
                    rounds, totalBits, false, f);
        }
    }

    private static final class UnbalancedParams {
        final int rounds;
        final int totalBits;
        final int sourceBits;
        final int targetBits;
        final RoundFunction64 f64;
        final RoundFunction<BigInteger> f;

        UnbalancedParams(
                int rounds,
                int totalBits,
                int sourceBits,
                int targetBits,
                RoundFunction64 f64,
                RoundFunction<BigInteger> f
        ) {
            this.rounds = rounds;
            this.totalBits = totalBits;
            this.sourceBits = sourceBits;
            this.targetBits = targetBits;
            this.f64 = f64;
            this.f = f;
        }

        Feistel64 toFeistel64() {
            return new Feistel64BinaryUnbalanced(
                    rounds, totalBits, sourceBits, targetBits, false, f64);
        }

        Feistel<BigInteger> toFeistelBigInteger() {
            return new FeistelBigIntegerBinaryUnbalanced(
                    rounds, totalBits, sourceBits, targetBits, false, f);
        }
    }

    @ParameterizedTest
    @MethodSource("feistel64")
    void isPermutation64(Feistel64BinaryBase feistel) {
        int count = testCount(feistel);
        long increment = testIncrement(feistel, count);
        long max = 1L << feistel.totalBits;
        LongSet longs = new LongHashSet(count);

        for (long i = 0; i < count; i++) {
            long output = feistel.applyAsLong(increment * i);
            longs.add(output);

            if (feistel.totalBits < 64) {
                assertTrue(output >= 0, () -> output + " >= " + 0);
                assertTrue(Long.compareUnsigned(output, max) < 0,
                        () -> output + " <= " + Long.toUnsignedString(max));
            }
        }
        assertEquals(count, longs.size());
    }

    @ParameterizedTest
    @MethodSource("feistel64")
    void isReversible64(Feistel64BinaryBase feistel) {
        int count = testCount(feistel);
        long increment = testIncrement(feistel, count);
        LongUnaryOperator id = feistel.reversed().compose(feistel);
        for (int i = 0; i < count; i++) {
            long input = increment * i;
            assertEquals(input, id.applyAsLong(input));
        }
    }

    private int testCount(Feistel64BinaryBase feistel) {
        return ONE
                .shiftLeft(feistel.totalBits)
                .min(BigInteger.valueOf(1_000_000))
                .intValue();
    }

    private long testIncrement(Feistel64BinaryBase feistel, int count) {
        return ONE
                .shiftLeft(feistel.totalBits)
                .divide(BigInteger.valueOf(count))
                .max(ONE)
                .longValue();
    }

    @ParameterizedTest
    @MethodSource("feistelBigInteger")
    void isPermutationBigInteger(FeistelBigIntegerBinaryBase feistel) {
        int count = testCount(feistel);
        BigInteger increment = testIncrement(feistel, count);

        assertEquals(count, Stream
                .iterate(ZERO, i -> i.add(increment))
                .parallel()
                .limit(count)
                .distinct()
                .peek(i -> {
                    assertTrue(i.compareTo(ZERO) >= 0,
                            () -> i + " >= " + 0);
                    assertTrue(i.compareTo(feistel.end) < 0,
                            () -> i + " < " + feistel.end);
                })
                .count());
    }

    @ParameterizedTest
    @MethodSource("feistelBigInteger")
    void isReversibleBigInteger(FeistelBigIntegerBinaryBase feistel) {
        int count = testCount(feistel);
        BigInteger increment = testIncrement(feistel, count);
        Function<BigInteger, BigInteger> id = feistel.reversed().compose(feistel);
        Stream.iterate(ZERO, i -> i.add(increment))
                .parallel()
                .limit(count)
                .forEach(i -> assertEquals(i, id.apply(i)));
    }

    private int testCount(FeistelBigIntegerBinaryBase feistel) {
        return ONE
                .shiftLeft(feistel.totalBits)
                .min(BigInteger.valueOf(100_000))
                .intValue();
    }

    private BigInteger testIncrement(FeistelBigIntegerBinaryBase feistel, int count) {
        return ONE
                .shiftLeft(feistel.totalBits)
                .divide(BigInteger.valueOf(count))
                .max(ONE);
    }

}
