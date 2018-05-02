package feistel;

import static feistel.Constraints.requireNonNegative;
import static java.lang.Long.toHexString;

final class FeistelOfLongBinary {
    private FeistelOfLongBinary() {
    }

    @FunctionalInterface
    private interface BalancedImpl {
        long applyAsLong(long value, boolean inverse);
    }

    /**
     * Adapted from the traditional balanced Feistel.
     */
    static Feistel.OfLong balanced(
            int rounds,
            int totalBits,
            RoundFunction.OfLong rf
    ) {
        requireNonNegative(rounds, "rounds");
        requireNonNegative(totalBits, Long.SIZE);
        if (totalBits % 2 != 0) {
            throw new IllegalArgumentException(
                    "totalBits must be even: " + totalBits);
        }

        int halfBits = totalBits / 2;
        long totalMask = getTotalMask(totalBits);
        long halfMask = totalMask >>> halfBits;

        BalancedImpl impl = (value, inverse) -> {
            long b = value >>> halfBits;
            long a = value & halfMask;
            for (int i = 0; i < rounds; i++) {
                int round = inverse ? rounds - i - 1 : i;
                long F = rf.applyAsLong(round, b) & halfMask;
                long a_ = a;
                a = b;
                b = a_ ^ F;
            }
            return (a << halfBits) | b;
        };

        return Feistel.ofLong(x -> {
            checkMask(x, totalMask);
            return impl.applyAsLong(x, false);
        }, y -> {
            checkMask(y, totalMask);
            return impl.applyAsLong(y, true);
        });
    }

    /**
     * Adapted from Unbalanced Feistel Networks and Block-Cipher Design
     * by Bruce Schneier and John Kelsey.
     */
    static Feistel.OfLong unbalanced(
            int rounds,
            int totalBits,
            int sourceBits,
            int targetBits,
            RoundFunction.OfLong rf
    ) {
        requireNonNegative(rounds, "rounds");
        requireNonNegative(totalBits, Long.SIZE);
        requireNonNegative(sourceBits, "sourceBits");
        requireNonNegative(targetBits, "targetBits");
        if (targetBits + sourceBits > totalBits) {
            throw new IllegalArgumentException("" +
                    "sourceBits (" + sourceBits + ") + " +
                    "targetBits (" + targetBits + ") " +
                    "cannot be greater than " +
                    "totalBits (" + totalBits + ")");
        }

        long totalMask = getTotalMask(totalBits);
        int nullBits = totalBits - sourceBits - targetBits;
        long nullMask = totalMask >>> sourceBits >>> targetBits;
        long sourceMask = totalMask >>> nullBits >>> targetBits;
        long targetMask = totalMask >>> nullBits >>> sourceBits;

        return Feistel.ofLong(x -> {

            checkMask(x, totalMask);
            for (int i = 0; i < rounds; i++) {
                long a = x >>> targetBits >>> nullBits;
                long n = x >>> targetBits & nullMask;
                long b = x & targetMask;
                x = ((b << nullBits << sourceBits)
                        | n << sourceBits
                        | a ^ rf.applyAsLong(i, b) & sourceMask);
            }
            return x;

        }, y -> {

            checkMask(y, totalMask);
            for (int i = rounds - 1; i >= 0; i--) {
                long a = y >>> sourceBits >>> nullBits;
                long n = y >>> sourceBits & nullMask;
                long b = y & sourceMask;
                long F = rf.applyAsLong(i, a) & sourceMask;
                y = ((b ^ F) << nullBits << targetBits
                        | n << targetBits
                        | a);
            }
            return y;
        });
    }

    private static long getTotalMask(int totalBits) {
        return 0xffff_ffff_ffff_ffffL >>> (Long.SIZE - totalBits);
    }

    private static void checkMask(long input, long totalMask) {
        if ((input & ~totalMask) != 0) {
            throw new IllegalArgumentException("input " + input +
                    " (" + toHexString(input) + ") is" +
                    " outside of mask range " + toHexString(totalMask));
        }
    }

}
