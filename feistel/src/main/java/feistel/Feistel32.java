package feistel;

import java.util.function.IntUnaryOperator;

import static java.lang.Integer.toUnsignedLong;
import static java.lang.Math.toIntExact;

public interface Feistel32 extends IntUnaryOperator {

    Feistel32 reversed();

    static Feistel32 binary(int rounds, RoundFunction32 f) {
        return new Feistel32Impl(new Feistel64BinaryUnbalanced(
                rounds, 32, 16, 16, false, toRoundFunction64(f)));
    }

    static Feistel32 binary(int rounds, int sourceBits, int targetBits, RoundFunction32 f) {
        return new Feistel32Impl(new Feistel64BinaryUnbalanced(
                rounds, 32, sourceBits, targetBits, false, toRoundFunction64(f)));
    }

    static Feistel32 numeric1(int rounds, int a, int b, RoundFunction32 f) {
        if (a < 0) {
            throw new IllegalArgumentException("a cannot be negative: " + a);
        }
        if (b < 0) {
            throw new IllegalArgumentException("b cannot be negative: " + b);
        }
        return new Feistel32Impl(Feistel64.numeric1(
                rounds, a, b, toRoundFunction64(f)));
    }

    static Feistel32 numeric2(int rounds, int a, int b, RoundFunction32 f) {
        if (a < 0) {
            throw new IllegalArgumentException("a cannot be negative: " + a);
        }
        if (b < 0) {
            throw new IllegalArgumentException("b cannot be negative: " + b);
        }
        return new Feistel32Impl(Feistel64.numeric2(
                rounds, a, b, toRoundFunction64(f)));
    }

    static RoundFunction64 toRoundFunction64(RoundFunction32 f) {
        return (round, input) -> toUnsignedLong(
                f.applyAsInt(round, toIntExact(input)));
    }
}
