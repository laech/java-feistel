package feistel;

import java.util.function.IntUnaryOperator;

import static java.lang.Integer.toUnsignedLong;
import static java.lang.Math.toIntExact;

public interface IntFeistel extends IntUnaryOperator {

    IntFeistel reversed();

    static IntFeistel binary(int rounds, IntRoundFunction f) {
        return new IntFeistelImpl(new LongFeistelBinaryUnbalanced(
                rounds, 32, 16, 16, false, toRoundFunction64(f)));
    }

    static IntFeistel binary(int rounds, int sourceBits, int targetBits, IntRoundFunction f) {
        return new IntFeistelImpl(new LongFeistelBinaryUnbalanced(
                rounds, 32, sourceBits, targetBits, false, toRoundFunction64(f)));
    }

    static IntFeistel numeric1(int rounds, int a, int b, IntRoundFunction f) {
        if (a < 0) {
            throw new IllegalArgumentException("a cannot be negative: " + a);
        }
        if (b < 0) {
            throw new IllegalArgumentException("b cannot be negative: " + b);
        }
        return new IntFeistelImpl(LongFeistel.numeric1(
                rounds, a, b, toRoundFunction64(f)));
    }

    static IntFeistel numeric2(int rounds, int a, int b, IntRoundFunction f) {
        if (a < 0) {
            throw new IllegalArgumentException("a cannot be negative: " + a);
        }
        if (b < 0) {
            throw new IllegalArgumentException("b cannot be negative: " + b);
        }
        return new IntFeistelImpl(LongFeistel.numeric2(
                rounds, a, b, toRoundFunction64(f)));
    }

    static LongRoundFunction toRoundFunction64(IntRoundFunction f) {
        return (round, input) -> toUnsignedLong(
                f.applyAsInt(round, toIntExact(input)));
    }
}
