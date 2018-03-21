package feistel;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static java.util.Objects.requireNonNull;

abstract class FeistelBigIntegerBinaryBase implements Feistel<BigInteger> {

    final int totalBits;
    final int rounds;
    final boolean reversed;
    final RoundFunction<BigInteger> f;
    final BigInteger end;

    FeistelBigIntegerBinaryBase(
            int rounds,
            int totalBits,
            boolean reversed,
            RoundFunction<BigInteger> f
    ) {
        if (rounds < 0) {
            throw new IllegalArgumentException(
                    "rounds cannot be negative: " + rounds);
        }
        if (totalBits < 0) {
            throw new IllegalArgumentException(
                    "totalBits must be from 0 to Long.SIZE: " + totalBits);
        }
        this.rounds = rounds;
        this.totalBits = totalBits;
        this.reversed = reversed;
        this.f = requireNonNull(f);
        this.end = ONE.shiftLeft(totalBits);
    }
}
