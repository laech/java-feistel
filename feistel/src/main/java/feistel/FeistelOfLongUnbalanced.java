package feistel;

import static java.util.Objects.requireNonNull;

final class FeistelOfLongUnbalanced implements Feistel.OfLong {

    private final int rounds;
    private final int totalBits;
    private final int sourceBits;
    private final int targetBits;
    private final boolean reverse;
    private final RoundFunction.OfLong f;

    FeistelOfLongUnbalanced(
            int rounds,
            int totalBits,
            int sourceBits,
            int targetBits,
            boolean reverse,
            RoundFunction.OfLong f
    ) {
        this.rounds = rounds;
        this.totalBits = totalBits;
        this.sourceBits = sourceBits;
        this.targetBits = targetBits;
        this.reverse = reverse;
        this.f = requireNonNull(f);
    }

    @Override
    public long applyAsLong(long input) {
        if (sourceBits < 0
                || targetBits < 0
                || targetBits + sourceBits > Long.SIZE) {
            throw new IllegalArgumentException();
        }
        long totalMask = 0xffff_ffff_ffff_ffffL >>> (Long.SIZE - totalBits);
        int nullBits = totalBits - sourceBits - targetBits;
        long nullMask = totalMask >>> sourceBits >>> targetBits;
        long sourceMask = totalMask >>> nullBits >>> targetBits;
        long targetMask = totalMask >>> nullBits >>> sourceBits;

        if ((input & ~totalMask) != 0) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < rounds; i++) {
            long a = input >>> targetBits >>> nullBits;
            long n = input >>> targetBits & nullMask;
            long b = input & targetMask;

            if (reverse) {
                input = ((b ^ f.applyAsLong(rounds - i - 1, a) & targetMask) << nullBits << sourceBits
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
    public FeistelOfLongUnbalanced reversed() {
        return new FeistelOfLongUnbalanced(
                rounds,
                totalBits,
                targetBits,
                sourceBits,
                !reverse,
                f
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (null == o || getClass() != o.getClass()) return false;
        FeistelOfLongUnbalanced that = (FeistelOfLongUnbalanced) o;
        return rounds == that.rounds &&
                totalBits == that.totalBits &&
                sourceBits == that.sourceBits &&
                targetBits == that.targetBits &&
                reverse == that.reverse &&
                f.equals(that.f);
    }

    @Override
    public int hashCode() {
        int result = rounds;
        result = 31 * result + totalBits;
        result = 31 * result + sourceBits;
        result = 31 * result + targetBits;
        result = 31 * result + (reverse ? 1 : 0);
        result = 31 * result + f.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "{rounds=" + rounds +
                ", totalBits=" + totalBits +
                ", sourceBits=" + sourceBits +
                ", targetBits=" + targetBits +
                ", reverse=" + reverse +
                ", function=" + f +
                '}';
    }
}
