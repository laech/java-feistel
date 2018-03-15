package feistel;

import java.math.BigInteger;
import java.util.function.LongUnaryOperator;
import java.util.function.UnaryOperator;

public interface Feistel<T> extends UnaryOperator<T> {

    @Override
    T apply(T input);

    Feistel<T> reversed();

    interface OfLong extends Feistel<Long>, LongUnaryOperator {

        @Override
        OfLong reversed();

        @Override
        long applyAsLong(long input);

        @Override
        default Long apply(Long input) {
            return applyAsLong(input);
        }

        // TODO conflict Function.compose, LongUnaryOperator.compose etc
    }

    @FunctionalInterface
    interface RoundFunction<T> {

        T apply(int round, T input);

        static <T> RoundFunction<T> identity() {
            return (i, t) -> t;
        }

        @FunctionalInterface
        interface OfLong extends RoundFunction<Long> {

            long applyAsLong(int round, long input);

            @Override
            default Long apply(int round, Long input) {
                return applyAsLong(round, input);
            }

            static OfLong identity() {
                return (i, t) -> t;
            }
        }
    }

    static Feistel<BigInteger> numeric(int rounds, BigInteger a, BigInteger b, RoundFunction<BigInteger> f) {
        return new FeistelOfBigIntegerNumeric(rounds, a, b, false, f);
    }

    static BigInteger numeric(BigInteger input, int rounds, BigInteger m, BigInteger n, UnaryOperator<BigInteger> roundFunction) {
        for (int i = 0; i < rounds; i++) {
            BigInteger a = input.divide(n);
            BigInteger b = input.remainder(n);
            input = m.multiply(b).add(a.add(roundFunction.apply(b)).remainder(m));
        }
        return input;
    }

    static long numeric(long input, int rounds, long m, long n, LongUnaryOperator roundFunction) {
        for (int i = 0; i < rounds; i++) {
            // TODO use unsigned ops
            long a = input / n;
            long b = input % n; // TODO use non negative mod
            input = m * b + (a + roundFunction.applyAsLong(b)) % m;
        }
        return input;
    }

    static long numeric2(long input, int rounds, long a, long b, LongUnaryOperator roundFunction) {
        long l = input / b;
        long r = input % b;
        long s = 1;
        for (int i = 1; i <= rounds; i++) {
            s = i % 2 != 0 ? a : b;
            long l_ = l;
            l = r;
            r = (l_ + roundFunction.applyAsLong(r)) % s;
        }
        return s * l + r;
    }

    static OfLong binary(
            int rounds,
            int totalBits,
            int sourceBits,
            int targetBits,
            RoundFunction.OfLong f
    ) {
        return new FeistelOfLongUnbalanced(
                rounds,
                totalBits,
                sourceBits,
                targetBits,
                false,
                f
        );
    }
}
