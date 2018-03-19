package feistel;

import static java.lang.Long.toHexString;
import static java.util.Objects.requireNonNull;

final class Feistel64Unbalanced implements Feistel64 {

    final int totalBits;
    private final int rounds;
    private final int sourceBits;
    private final int targetBits;
    private final boolean reverse;
    private final RoundFunction64 f;

    Feistel64Unbalanced(
            int rounds,
            int totalBits,
            int sourceBits,
            int targetBits,
            boolean reverse,
            RoundFunction64 f
    ) {
        if (rounds < 0) {
            throw new IllegalArgumentException(
                    "rounds cannot be negative: " + rounds);
        }
        if (sourceBits < 0) {
            throw new IllegalArgumentException(
                    "sourceBits cannot be negative: " + sourceBits);
        }
        if (targetBits < 0) {
            throw new IllegalArgumentException(
                    "targetBits cannot be negative: " + targetBits);
        }
        if (targetBits + sourceBits > Long.SIZE) {
            throw new IllegalArgumentException("" +
                    "sourceBits (" + sourceBits + ") + " +
                    "targetBits (" + targetBits + ") " +
                    "cannot be greater than Long.SIZE ");
        }
        if (totalBits > Long.SIZE) {
            throw new IllegalArgumentException(
                    "totalBits cannot be greater than Long.SIZE: " +
                            totalBits);
        }

        this.rounds = rounds;
        this.totalBits = totalBits;
        this.sourceBits = sourceBits;
        this.targetBits = targetBits;
        this.reverse = reverse;
        this.f = requireNonNull(f);
    }

    @Override
    public long applyAsLong(long input) {
        long totalMask = 0xffff_ffff_ffff_ffffL >>> (Long.SIZE - totalBits);
        int nullBits = totalBits - sourceBits - targetBits;
        long nullMask = totalMask >>> sourceBits >>> targetBits;
        long sourceMask = totalMask >>> nullBits >>> targetBits;
        long targetMask = totalMask >>> nullBits >>> sourceBits;

        if ((input & ~totalMask) != 0) {
            throw new IllegalArgumentException("input " + input +
                    " (" + toHexString(input) + ") is" +
                    " outside of mask range " + toHexString(totalMask));
        }

        for (int i = 0; i < rounds; i++) {
            long a = input >>> targetBits >>> nullBits;
            long n = input >>> targetBits & nullMask;
            long b = input & targetMask;

            if (reverse) {
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
    public Feistel64Unbalanced reversed() {
        return new Feistel64Unbalanced(
                rounds,
                totalBits,
                targetBits,
                sourceBits,
                !reverse,
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
                ", reverse=" + reverse +
                ", f=" + f +
                '}';
    }
}
