package feistel;

import isomorphic.Isomorphism;

import java.util.function.LongUnaryOperator;

import static feistel.Constraints.requireNonNegative;
import static java.lang.Math.floorMod;
import static java.lang.Math.multiplyExact;
import static java.util.Objects.requireNonNull;

final class FeistelOfLongNumeric {
    private FeistelOfLongNumeric() {
    }

    private static Isomorphism.OfLong create(
            int rounds,
            long a,
            long b,
            LongUnaryOperator f,
            LongUnaryOperator g
    ) {
        requireNonNull(f, "f cannot be null");
        requireNonNegative(rounds, "rounds");
        requireNonNegative(a, "a");
        requireNonNegative(b, "b");

        long max = calculateMax(a, b);
        return Isomorphism.OfLong.of(
                x -> {
                    requireNonNegative(x, max);
                    return f.applyAsLong(x);
                },
                y -> {
                    requireNonNegative(y, max);
                    return g.applyAsLong(y);
                }
        );
    }

    /**
     * Algorithm FE1 from Format-Preserving Encryption by
     * Mihir Bellare, Thomas Ristenpart, Phillip Rogaway, and Till Stegers
     */
    static Isomorphism.OfLong fe1(
            int rounds,
            long a,
            long b,
            RoundFunction.OfLong rf
    ) {
        requireNonNull(rf, "rf cannot be null");
        return create(rounds, a, b, x -> {

            for (int i = 0; i < rounds; i++) {
                long l = x / b;
                long r = floorMod(x, b);
                long w = floorMod(l + rf.applyAsLong(i, r), a);
                x = a * r + w;
            }
            return x;

        }, y -> {

            for (int i = rounds - 1; i >= 0; i--) {
                long w = floorMod(y, a);
                long r = y / a;
                long l = floorMod(w - rf.applyAsLong(i, r), a);
                y = b * l + r;
            }
            return y;
        });
    }

    /**
     * Algorithm FE2 from Format-Preserving Encryption by
     * Mihir Bellare, Thomas Ristenpart, Phillip Rogaway, and Till Stegers
     */
    static Isomorphism.OfLong fe2(
            int rounds,
            long a,
            long b,
            RoundFunction.OfLong rf
    ) {
        requireNonNull(rf, "rf cannot be null");
        return create(rounds, a, b, x -> {

            long l = x / b;
            long r = floorMod(x, b);
            long s = 1;
            for (int i = 0; i < rounds; i++) {
                s = i % 2 == 0 ? a : b;
                long l_ = l;
                l = r;
                r = floorMod(l_ + rf.applyAsLong(i, r), s);
            }
            return s * l + r;

        }, y -> {

            long s = rounds % 2 != 0 ? a : b;
            long r = floorMod(y, s);
            long l = y / s;
            for (int i = rounds - 1; i >= 0; i--) {
                s = i % 2 == 0 ? a : b;
                long r_ = r;
                r = l;
                l = floorMod(r_ - rf.applyAsLong(i, l), s);
            }
            return b * l + r;
        });
    }

    private static long calculateMax(long a, long b) {
        try {
            return multiplyExact(a, b) - 1;
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException(
                    a + "x" + b + " overflows", e);
        }
    }
}
