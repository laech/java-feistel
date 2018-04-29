package feistel;

import java.math.BigInteger;
import java.util.function.UnaryOperator;

import static feistel.Constraints.calculateMax;
import static feistel.Constraints.requireNonNegative;
import static java.util.Objects.requireNonNull;

final class FeistelOfBigIntegerBinary {
    private FeistelOfBigIntegerBinary() {
    }

    private static Feistel<BigInteger> create(
            BigInteger max,
            UnaryOperator<BigInteger> f,
            UnaryOperator<BigInteger> g
    ) {
        requireNonNull(max, "max cannot be null");
        requireNonNull(f, "f cannot be null");
        requireNonNull(g, "g cannot be null");

        return Feistel.of(
                x -> {
                    requireNonNegative(x, max);
                    return f.apply(x);
                },
                y -> {
                    requireNonNegative(y, max);
                    return g.apply(y);
                }
        );
    }

    @FunctionalInterface
    private interface BalancedImpl {
        BigInteger apply(BigInteger value, boolean reversed);
    }

    static Feistel<BigInteger> balanced(
            int rounds,
            int totalBits,
            RoundFunction<BigInteger> rf
    ) {
        requireNonNegative(rounds, "rounds");
        requireNonNegative(totalBits, "totalBits");
        requireNonNull(rf, "rf cannot be null");
        if (totalBits % 2 != 0) {
            throw new IllegalArgumentException(
                    "totalBits must be even: " + totalBits);
        }

        int halfBits = totalBits / 2;
        BigInteger max = calculateMax(totalBits);
        BigInteger halfMask = max.shiftRight(halfBits);

        BalancedImpl impl = (value, reversed) -> {
            BigInteger b = value.shiftRight(halfBits);
            BigInteger a = value.and(halfMask);
            for (int i = 0; i < rounds; i++) {
                int round = reversed ? rounds - i - 1 : i;
                BigInteger F = rf.apply(round, b).and(halfMask);
                BigInteger a_ = a;
                a = b;
                b = a_.xor(F);
            }
            return a.shiftLeft(halfBits).or(b);
        };
        return create(
                max,
                x -> impl.apply(x, false),
                y -> impl.apply(y, true)
        );
    }

    static Feistel<BigInteger> unbalanced(
            int rounds,
            int totalBits,
            int sourceBits,
            int targetBits,
            RoundFunction<BigInteger> rf
    ) {
        requireNonNull(rf);
        requireNonNegative(rounds, "rounds");
        requireNonNegative(totalBits, "totalBits");
        requireNonNegative(sourceBits, "sourceBits");
        requireNonNegative(targetBits, "targetBits");

        if (targetBits + sourceBits > totalBits) {
            throw new IllegalArgumentException("" +
                    "sourceBits (" + sourceBits + ") + " +
                    "targetBits (" + targetBits + ") " +
                    "cannot be greater than " +
                    "totalBits (" + totalBits + ")");
        }

        int nullBits = totalBits - sourceBits - targetBits;
        BigInteger max = calculateMax(totalBits);
        BigInteger nullMask = max.shiftRight(sourceBits + targetBits);
        BigInteger sourceMask = max.shiftRight(nullBits + targetBits);
        BigInteger targetMask = max.shiftRight(nullBits + sourceBits);

        return create(max, x -> {

            for (int i = 0; i < rounds; i++) {
                BigInteger a = x.shiftRight(targetBits + nullBits);
                BigInteger n = x.shiftRight(targetBits).and(nullMask);
                BigInteger b = x.and(targetMask);
                x = b.shiftLeft(nullBits + sourceBits)
                        .or(n.shiftLeft(sourceBits))
                        .or(a.xor(rf.apply(i, b)).and(sourceMask));
            }
            return x;

        }, y -> {

            for (int i = rounds - 1; i >= 0; i--) {
                BigInteger a = y.shiftRight(sourceBits + nullBits);
                BigInteger n = y.shiftRight(sourceBits).and(nullMask);
                BigInteger b = y.and(sourceMask);
                y = b.xor(rf.apply(i, a).and(sourceMask))
                        .shiftLeft(nullBits + targetBits)
                        .or(n.shiftLeft(targetBits))
                        .or(a);

            }
            return y;
        });
    }

}
