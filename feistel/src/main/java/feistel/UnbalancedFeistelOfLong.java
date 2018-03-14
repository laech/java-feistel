package feistel;

import static java.util.Objects.requireNonNull;

final class UnbalancedFeistelOfLong implements Feistel.OfLong {

    private final int rounds;
    private final int totalBits;
    private final int sourceBits;
    private final int targetBits;
    private final boolean reverse;
    private final RoundFunction.OfLong f;

    UnbalancedFeistelOfLong(
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
    public UnbalancedFeistelOfLong reversed() {
        return new UnbalancedFeistelOfLong(
                rounds,
                totalBits,
                targetBits,
                sourceBits,
                !reverse,
                f
        );
    }
}
