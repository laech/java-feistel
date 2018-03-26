package feistel;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static java.util.Objects.requireNonNull;

abstract class BigIntegerFeistelBinaryBase implements Feistel<BigInteger> {

    final int totalBits;
    final int rounds;
    final boolean reversed;
    final RoundFunction<BigInteger> f;
    final BigInteger max;

    BigIntegerFeistelBinaryBase(
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
        this.max = ONE.shiftLeft(totalBits).subtract(ONE);
    }

    @Override
    public final BigInteger apply(BigInteger input) {

        if (input.compareTo(ZERO) < 0 || input.compareTo(max) > 0) {
            throw new IllegalArgumentException(
                    "input out of range (min=0, max=" + max + "): " + input);
        }

        return doApply(input);
    }

    abstract BigInteger doApply(BigInteger input);
}
