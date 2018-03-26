package feistel;

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
}
