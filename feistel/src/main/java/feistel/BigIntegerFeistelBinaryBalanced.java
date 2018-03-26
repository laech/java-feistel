package feistel;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

final class BigIntegerFeistelBinaryBalanced extends BigIntegerFeistelBinaryBase {

    private final BigInteger halfMask;
    private final int halfBits;

    BigIntegerFeistelBinaryBalanced(
            int rounds,
            int totalBits,
            boolean reversed,
            RoundFunction<BigInteger> f
    ) {
        super(rounds, totalBits, reversed, f);
        if (totalBits % 2 != 0) {
            throw new IllegalArgumentException(
                    "totalBits must be even: " + totalBits);
        }

        halfBits = totalBits / 2;
        halfMask = end.subtract(ONE).shiftRight(halfBits);
    }

    @Override
    public BigInteger apply(BigInteger input) {

        if (input.compareTo(ZERO) < 0 || input.compareTo(end) >= 0) {
            // TODO check bit count instead?
            throw new IllegalArgumentException();
        }

        BigInteger b = input.shiftRight(halfBits);
        BigInteger a = input.and(halfMask);
        for (int i = 0; i < rounds; i++) {
            int round = reversed ? rounds - i - 1 : i;
            BigInteger F = f.apply(round, b).and(halfMask);
            BigInteger a_ = a;
            a = b;
            b = a_.xor(F);
        }
        return a.shiftLeft(halfBits).or(b);
    }

    @Override
    public BigIntegerFeistelBinaryBalanced reversed() {
        return new BigIntegerFeistelBinaryBalanced(
                rounds, totalBits, !reversed, f);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "{rounds=" + rounds
                + ", totalBits=" + totalBits
                + ", reversed=" + reversed
                + ", f=" + f
                + "}";
    }
}
