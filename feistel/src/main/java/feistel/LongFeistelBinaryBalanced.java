package feistel;

final class LongFeistelBinaryBalanced extends LongFeistelBinaryBase {

    LongFeistelBinaryBalanced(
            int rounds,
            int totalBits,
            boolean reversed,
            LongRoundFunction f
    ) {
        super(rounds, totalBits, reversed, f);
        if (totalBits % 2 != 0) {
            throw new IllegalArgumentException(
                    "totalBits must be even: " + totalBits);
        }
    }

    @Override
    long doApplyAsLong(long totalMask, long input) {
        int half = totalBits / 2;
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
    public LongFeistelBinaryBalanced reversed() {
        return new LongFeistelBinaryBalanced(rounds, totalBits, !reversed, f);
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
