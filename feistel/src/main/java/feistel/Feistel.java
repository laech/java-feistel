package feistel;

import java.util.function.IntUnaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.function.UnaryOperator;

import static java.lang.Integer.toUnsignedLong;
import static java.lang.Math.multiplyExact;
import static java.lang.Math.toIntExact;

public interface Feistel<T> extends UnaryOperator<T> {

    @Override
    T apply(T input);

    Feistel<T> reversed();

    interface OfInt extends IntUnaryOperator {

        OfInt reversed();

        static OfInt binary(int rounds, RoundFunction.OfInt f) {
            return new IntFeistelImpl(new LongFeistelBinaryUnbalanced(
                    rounds, 32, 16, 16, false, toRoundFunction64(f)));
        }

        static OfInt binary(int rounds, int sourceBits, int targetBits, RoundFunction.OfInt f) {
            return new IntFeistelImpl(new LongFeistelBinaryUnbalanced(
                    rounds, 32, sourceBits, targetBits, false, toRoundFunction64(f)));
        }

        static OfInt numeric1(int rounds, int a, int b, RoundFunction.OfInt f) {
            checkNumeric(a, b);
            return new IntFeistelImpl(Feistel.OfLong.numeric1(
                    rounds, a, b, toRoundFunction64(f)));
        }

        static OfInt numeric2(int rounds, int a, int b, RoundFunction.OfInt f) {
            checkNumeric(a, b);
            return new IntFeistelImpl(Feistel.OfLong.numeric2(
                    rounds, a, b, toRoundFunction64(f)));
        }

        static void checkNumeric(int a, int b) {
            if (a < 0) {
                throw new IllegalArgumentException(
                        "a cannot be negative: " + a);
            }
            if (b < 0) {
                throw new IllegalArgumentException(
                        "b cannot be negative: " + b);
            }
            multiplyExact(a, b);
        }

        static RoundFunction.OfLong toRoundFunction64(RoundFunction.OfInt f) {
            return (round, input) -> toUnsignedLong(
                    f.applyAsInt(round, toIntExact(input)));
        }
    }

    interface OfLong extends LongUnaryOperator {

        OfLong reversed();

        static OfLong binary(int rounds, RoundFunction.OfLong f) {
            return new LongFeistelBinaryUnbalanced(rounds, 64, 32, 32, false, f);
        }

        static OfLong binary(int rounds, int sourceBits, int targetBits, RoundFunction.OfLong f) {
            return new LongFeistelBinaryUnbalanced(rounds, 64, sourceBits, targetBits, false, f);
        }

        static OfLong numeric1(int rounds, long a, long b, RoundFunction.OfLong f) {
            return new LongFeistelNumeric1(rounds, a, b, false, f);
        }

        static OfLong numeric2(int rounds, long a, long b, RoundFunction.OfLong f) {
            return new LongFeistelNumeric2(rounds, a, b, false, f);
        }
    }
}
