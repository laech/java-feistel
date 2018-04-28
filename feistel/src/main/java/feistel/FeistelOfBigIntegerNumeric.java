package feistel;

import java.math.BigInteger;
import java.util.function.UnaryOperator;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static java.util.Objects.requireNonNull;

final class FeistelOfBigIntegerNumeric {
    private FeistelOfBigIntegerNumeric() {
    }

    private static Feistel<BigInteger> create(
            int rounds,
            BigInteger a,
            BigInteger b,
            UnaryOperator<BigInteger> f,
            UnaryOperator<BigInteger> g
    ) {
        requireNonNull(a, "a cannot be null");
        requireNonNull(b, "b cannot be null");
        requireNonNull(f, "f cannot be null");
        requireNonNull(g, "g cannot be null");
        requireNonNegative(rounds, "rounds");
        requireNonNegative(a, "a");
        requireNonNegative(b, "b");

        BigInteger max = a.multiply(b).subtract(ONE);
        return Feistel.of(
                x -> {
                    checkRange(x, max);
                    return f.apply(x);
                },
                y -> {
                    checkRange(y, max);
                    return g.apply(y);
                }
        );
    }

    static Feistel<BigInteger> numeric1(
            int rounds,
            BigInteger a,
            BigInteger b,
            RoundFunction<BigInteger> rf
    ) {
        requireNonNull(rf, "rf cannot be null");
        return create(rounds, a, b, x -> {

            for (int i = 0; i < rounds; i++) {
                BigInteger l = x.divide(b);
                BigInteger r = x.mod(b);
                BigInteger w = l.add(rf.apply(i, r)).mod(a);
                x = a.multiply(r).add(w);
            }
            return x;

        }, y -> {

            for (int i = rounds - 1; i >= 0; i--) {
                BigInteger w = y.mod(a);
                BigInteger r = y.divide(a);
                BigInteger l = w.subtract(rf.apply(i, r)).mod(a);
                y = b.multiply(l).add(r);
            }
            return y;
        });
    }

    static Feistel<BigInteger> numeric2(
            int rounds,
            BigInteger a,
            BigInteger b,
            RoundFunction<BigInteger> rf
    ) {
        requireNonNull(rf, "rf cannot be null");
        return create(rounds, a, b, x -> {

            BigInteger l = x.divide(b);
            BigInteger r = x.mod(b);
            BigInteger s = ONE;
            for (int i = 0; i < rounds; i++) {
                s = i % 2 == 0 ? a : b;
                BigInteger l_ = l;
                l = r;
                r = l_.add(rf.apply(i, r)).mod(s);
            }
            return s.multiply(l).add(r);

        }, y -> {

            BigInteger s = rounds % 2 != 0 ? a : b;
            BigInteger r = y.mod(s);
            BigInteger l = y.divide(s);
            for (int i = rounds - 1; i >= 0; i--) {
                s = i % 2 == 0 ? a : b;
                BigInteger r_ = r;
                r = l;
                l = r_.subtract(rf.apply(i, l)).mod(s);
            }
            return b.multiply(l).add(r);
        });
    }

    private static void requireNonNegative(BigInteger value, String name) {
        if (value.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException(
                    name + " cannot be negative: " + value);
        }
    }

    private static void requireNonNegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(
                    name + " cannot be negative: " + value);
        }
    }

    private static void checkRange(BigInteger value, BigInteger max) {
        if (value.compareTo(ZERO) < 0 || value.compareTo(max) > 0) {
            throw new IllegalArgumentException(
                    "value out of range (min=0, max=" + max + "): " + value);
        }
    }

}
