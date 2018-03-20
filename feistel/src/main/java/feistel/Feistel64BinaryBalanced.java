package feistel;

import static java.lang.Long.toHexString;

final class Feistel64BinaryBalanced extends Feistel64BinaryBase {

    Feistel64BinaryBalanced(
            int rounds,
            int totalBits,
            boolean reversed,
            RoundFunction64 f
    ) {
        super(rounds, totalBits, reversed, f);
        if (totalBits % 2 != 0) {
            throw new IllegalArgumentException(
                    "totalBits must be even: " + totalBits);
        }
    }

    @Override
    public long applyAsLong(long input) {

        long totalMask = 0xffff_ffff_ffff_ffffL >>> (Long.SIZE - totalBits);
        if ((input & ~totalMask) != 0) {
            throw new IllegalArgumentException("input " + input +
                    " (" + toHexString(input) + ") is" +
                    " outside of mask range " + toHexString(totalMask));
        }

        long half = totalBits / 2;
        long halfMask = totalMask >>> half;
        long b = input >>> half;
        long a = input & halfMask;
        for (int i = 0; i < rounds; i++) {
            int round = reversed ? rounds - i - 1 : i;
            long F = f.applyAsLong(round, b) & halfMask;
            long a_ = a;
            a = b;
            b = a_ ^ F;
        }
        return (a << half) | b;
    }

    @Override
    public Feistel64BinaryBalanced reversed() {
        return new Feistel64BinaryBalanced(rounds, totalBits, !reversed, f);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "{rounds=" + rounds
                + ", totalBits=" + totalBits
                + ", reversed=" + reversed
                + ", f=" + f
                + "}";
    }
}
