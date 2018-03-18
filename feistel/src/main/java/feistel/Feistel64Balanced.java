package feistel;

import static java.lang.Long.toHexString;
import static java.util.Objects.requireNonNull;

final class Feistel64Balanced implements Feistel64 {

    private final int rounds;
    private final int totalBits;
    private final boolean reversed;
    private final RoundFunction64 f;

    Feistel64Balanced(
            int rounds,
            int totalBits,
            boolean reversed,
            RoundFunction64 f
    ) {
        if (rounds < 0) {
            throw new IllegalArgumentException(
                    "rounds cannot be negative: " + rounds);
        }
        if (totalBits < 0 || totalBits > 64 || totalBits % 2 != 0) {
            throw new IllegalArgumentException(
                    "totalBits must be even and from 0 to 64: " + totalBits);
        }
        this.rounds = rounds;
        this.totalBits = totalBits;
        this.reversed = reversed;
        this.f = requireNonNull(f);
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
    public Feistel64Balanced reversed() {
        return new Feistel64Balanced(rounds, totalBits, !reversed, f);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "{rounds=" + rounds
                + ", reversed=" + reversed
                + ", function=" + f
                + "}";
    }
}
