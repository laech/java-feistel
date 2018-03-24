package feistel;

import java.util.function.LongUnaryOperator;

public interface Feistel64 extends LongUnaryOperator {

    Feistel64 reversed();

    static Feistel64 binary(int rounds, RoundFunction64 f) {
        return new Feistel64BinaryUnbalanced(rounds, 64, 32, 32, false, f);
    }

    static Feistel64 binary(int rounds, int sourceBits, int targetBits, RoundFunction64 f) {
        return new Feistel64BinaryUnbalanced(rounds, 64, sourceBits, targetBits, false, f);
    }

    static Feistel64 numeric1(int rounds, long a, long b, RoundFunction64 f) {
        return new Feistel64Numeric1(rounds, a, b, false, f);
    }

    static Feistel64 numeric2(int rounds, long a, long b, RoundFunction64 f) {
        return new Feistel64Numeric2(rounds, a, b, false, f);
    }
}
