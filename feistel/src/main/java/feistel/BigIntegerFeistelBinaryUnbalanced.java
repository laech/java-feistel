package feistel;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

final class BigIntegerFeistelBinaryUnbalanced extends BigIntegerFeistelBinaryBase {

    private final int sourceBits;
    private final int targetBits;
    private final int nullBits;
    private final BigInteger nullMask;
    private final BigInteger sourceMask;
    private final BigInteger targetMask;

    BigIntegerFeistelBinaryUnbalanced(
            int rounds,
            int totalBits,
            int sourceBits,
            int targetBits,
            boolean reversed,
            RoundFunction<BigInteger> f
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
        if (targetBits + sourceBits > totalBits) {
            throw new IllegalArgumentException("" +
                    "sourceBits (" + sourceBits + ") + " +
                    "targetBits (" + targetBits + ") " +
                    "cannot be greater than " +
                    "totalBits (" + totalBits + ")");
        }

        this.sourceBits = sourceBits;
        this.targetBits = targetBits;

        BigInteger totalMask = end.subtract(ONE);
        nullBits = totalBits - sourceBits - targetBits;
        nullMask = totalMask.shiftRight(sourceBits + targetBits);
        sourceMask = totalMask.shiftRight(nullBits + targetBits);
        targetMask = totalMask.shiftRight(nullBits + sourceBits);
    }

    @Override
    public BigInteger apply(BigInteger input) {

        if (input.compareTo(ZERO) < 0 || input.compareTo(end) > 0) {
            // TODO check bit count instead?
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < rounds; i++) {
            BigInteger a = input.shiftRight(targetBits + nullBits);
            BigInteger n = input.shiftRight(targetBits).and(nullMask);
            BigInteger b = input.and(targetMask);

            if (reversed) {
                BigInteger F = f.apply(rounds - i - 1, a).and(targetMask);
                input = b.xor(F).shiftLeft(nullBits + sourceBits)
                        .or(n.shiftLeft(sourceBits))
                        .or(a);
            } else {
                input = b.shiftLeft(nullBits + sourceBits)
                        .or(n.shiftLeft(sourceBits))
                        .or(a.xor(f.apply(i, b)).and(sourceMask));
            }
        }
        return input;
    }

    @Override
    public BigIntegerFeistelBinaryUnbalanced reversed() {
        return new BigIntegerFeistelBinaryUnbalanced(
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
