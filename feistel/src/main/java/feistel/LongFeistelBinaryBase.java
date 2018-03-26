package feistel;

import static java.lang.Long.toHexString;
import static java.util.Objects.requireNonNull;

abstract class LongFeistelBinaryBase implements LongFeistel {

    final int totalBits;
    final int rounds;
    final boolean reversed;
    final LongRoundFunction f;

    LongFeistelBinaryBase(
            int rounds,
            int totalBits,
            boolean reversed,
            LongRoundFunction f
    ) {
        if (rounds < 0) {
            throw new IllegalArgumentException(
                    "rounds cannot be negative: " + rounds);
        }
        if (totalBits < 0 || totalBits > Long.SIZE) {
            throw new IllegalArgumentException(
                    "totalBits must be from 0 to Long.SIZE: " + totalBits);
        }
        this.rounds = rounds;
        this.totalBits = totalBits;
        this.reversed = reversed;
        this.f = requireNonNull(f);
    }

    @Override
    public final long applyAsLong(long input) {

        long totalMask = 0xffff_ffff_ffff_ffffL >>> (Long.SIZE - totalBits);
        if ((input & ~totalMask) != 0) {
            throw new IllegalArgumentException("input " + input +
                    " (" + toHexString(input) + ") is" +
                    " outside of mask range " + toHexString(totalMask));
        }

        return doApplyAsLong(totalMask, input);
    }

    abstract long doApplyAsLong(long totalMask, long input);
}
