package feistel;

import static java.lang.Long.toHexString;

final class Feistel64Unbalanced extends Feistel64BinaryBase {

    private final int sourceBits;
    private final int targetBits;

    Feistel64Unbalanced(
            int rounds,
            int totalBits,
            int sourceBits,
            int targetBits,
            boolean reversed,
            RoundFunction64 f
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
        if (targetBits + sourceBits > Long.SIZE) {
            throw new IllegalArgumentException("" +
                    "sourceBits (" + sourceBits + ") + " +
                    "targetBits (" + targetBits + ") " +
                    "cannot be greater than Long.SIZE ");
        }

        this.sourceBits = sourceBits;
        this.targetBits = targetBits;
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
    public Feistel64Unbalanced reversed() {
        return new Feistel64Unbalanced(
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
