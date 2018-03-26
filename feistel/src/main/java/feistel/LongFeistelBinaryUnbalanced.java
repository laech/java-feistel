package feistel;

final class LongFeistelBinaryUnbalanced extends LongFeistelBinaryBase {

    private final int sourceBits;
    private final int targetBits;

    LongFeistelBinaryUnbalanced(
            int rounds,
            int totalBits,
            int sourceBits,
            int targetBits,
            boolean reversed,
            LongRoundFunction f
    ) {
        super(rounds, totalBits, reversed, f);

        if (sourceBits < 0) {
            throw new IllegalArgumentException(
                    "sourceBits cannot be negative: " + sourceBits);
        }
        if (targetBits < 0) {
            throw new IllegalArgumentException(
                    "targetBits cannot be negative: " + targetBits);
        }
        if (targetBits + sourceBits > totalBits) {
            throw new IllegalArgumentException("" +
                    "sourceBits (" + sourceBits + ") + " +
                    "targetBits (" + targetBits + ") " +
                    "cannot be greater than " +
                    "totalBits (" + totalBits + ")");
        }

        this.sourceBits = sourceBits;
        this.targetBits = targetBits;
    }

    @Override
    long doApplyAsLong(long totalMask, long input) {

        int nullBits = totalBits - sourceBits - targetBits;
        long nullMask = totalMask >>> sourceBits >>> targetBits;
        long sourceMask = totalMask >>> nullBits >>> targetBits;
        long targetMask = totalMask >>> nullBits >>> sourceBits;

        for (int i = 0; i < rounds; i++) {
            long a = input >>> targetBits >>> nullBits;
            long n = input >>> targetBits & nullMask;
            long b = input & targetMask;

            if (reversed) {
                long F = f.applyAsLong(rounds - i - 1, a) & targetMask;
                input = ((b ^ F) << nullBits << sourceBits
                        | n << sourceBits
                        | a);
            } else {
                input = ((b << nullBits << sourceBits)
                        | n << sourceBits
                        | a ^ f.applyAsLong(i, b) & sourceMask);
            }
        }
        return input;
    }

    @Override
    public LongFeistelBinaryUnbalanced reversed() {
        return new LongFeistelBinaryUnbalanced(
                rounds,
                totalBits,
                targetBits,
                sourceBits,
                !reversed,
                f
        );
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "{rounds=" + rounds +
                ", totalBits=" + totalBits +
                ", sourceBits=" + sourceBits +
                ", targetBits=" + targetBits +
                ", reversed=" + reversed +
                ", f=" + f +
                '}';
    }
}
