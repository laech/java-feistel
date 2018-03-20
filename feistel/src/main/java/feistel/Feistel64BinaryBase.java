package feistel;

import static java.util.Objects.requireNonNull;

abstract class Feistel64BinaryBase implements Feistel64 {

    final int totalBits;
    final int rounds;
    final boolean reversed;
    final RoundFunction64 f;

    Feistel64BinaryBase(
            int rounds,
            int totalBits,
            boolean reversed,
            RoundFunction64 f
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
