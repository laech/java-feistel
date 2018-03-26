package feistel;

import java.util.function.LongUnaryOperator;

public interface LongFeistel extends LongUnaryOperator {

    LongFeistel reversed();

    static LongFeistel binary(int rounds, LongRoundFunction f) {
        return new LongFeistelBinaryUnbalanced(rounds, 64, 32, 32, false, f);
    }

    static LongFeistel binary(int rounds, int sourceBits, int targetBits, LongRoundFunction f) {
        return new LongFeistelBinaryUnbalanced(rounds, 64, sourceBits, targetBits, false, f);
    }

    static LongFeistel numeric1(int rounds, long a, long b, LongRoundFunction f) {
        return new LongFeistelNumeric1(rounds, a, b, false, f);
    }

    static LongFeistel numeric2(int rounds, long a, long b, LongRoundFunction f) {
        return new LongFeistelNumeric2(rounds, a, b, false, f);
    }
}
