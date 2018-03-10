package feistel;

import java.util.function.IntUnaryOperator;
import java.util.function.LongUnaryOperator;

public final class Feistel {

    private Feistel() {
    }

    public static int unbalanced(
            int input,
            int rounds,
            int sourceBits,
            int targetBits,
            IntUnaryOperator roundFunction
    ) {
        if (sourceBits < 0
                || targetBits < 0
                || targetBits + sourceBits > Integer.SIZE) {
            throw new IllegalArgumentException();
        }
        return sourceBits == targetBits && sourceBits == 16
                ? balanced(input, rounds, roundFunction)
                : doUnbalanced(input, rounds, sourceBits, targetBits, roundFunction);
    }

    private static int doUnbalanced(
            int input,
            int rounds,
            int sourceBits,
            int targetBits,
            IntUnaryOperator roundFunction
    ) {
        int sourceMask = ~(0xffffffff << sourceBits);
        int targetMask = ~(0xffffffff << targetBits);
        int nullMask = (0xffffffff >>> sourceBits) & targetMask;
        int nullBits = Integer.SIZE - sourceBits - targetBits;
        for (int i = 0; i < rounds; i++) {
            int a = input >>> nullBits >>> targetBits;
            int b = input & targetMask;
            int n = (input & nullMask) >>> targetBits;
            int f = roundFunction.applyAsInt(b) & sourceMask;
            int a_ = a;
            a = b;
            b = a_ ^ f;
            input = (a << nullBits << sourceBits | n << sourceBits | b);
        }
        int a = input >>> nullBits >>> targetBits;
        int b = input & targetMask;
        int n = (input & nullMask) >>> targetBits;
        return (b << nullBits << sourceBits | n << sourceBits | a);
    }

    public static int compute(int input, int rounds, IntUnaryOperator roundFunction) {
        return balanced(input, rounds, roundFunction);
    }

    public static int balanced(int input, int rounds, IntUnaryOperator roundFunction) {
        int a = input >>> 16;
        int b = input & 0xffff;
        for (int i = 0; i < rounds; i++) {
            int F = roundFunction.applyAsInt(b) & 0xffff;
            int a_ = a;
            a = b;
            b = a_ ^ F;
        }
        return (b << 16) | a;
    }

    public static long balanced(long input, int rounds, LongUnaryOperator roundFunction) {
        long a = input >>> 32;
        long b = input & 0xff_ff_ff_ffL;
        for (int i = 0; i < rounds; i++) {
            long F = roundFunction.applyAsLong(b) & 0xff_ff_ff_ffL;
            long a_ = a;
            a = b;
            b = a_ ^ F;
        }
        return (b << 32) | a;
    }

    public static IntUnaryOperator compute(int rounds, IntUnaryOperator roundFunction) {
        return input -> compute(input, rounds, roundFunction);
    }

    public static final class OfInt implements IntUnaryOperator {

        private final int rounds;
        private final int sourceBits;
        private final int targetBits;
        private final IntUnaryOperator roundFunction;

        public OfInt(int rounds, int sourceBits, int targetBits, IntUnaryOperator roundFunction) {
            this.rounds = rounds;
            this.sourceBits = sourceBits;
            this.targetBits = targetBits;
            this.roundFunction = roundFunction;
        }

        @Override
        public int applyAsInt(int input) {
            return unbalanced(input, rounds, sourceBits, targetBits, roundFunction);
        }

        public OfInt reversed() {
            if (sourceBits == targetBits && sourceBits + targetBits == Integer.SIZE) {
                return this;
            }
            return new OfInt(rounds, targetBits, sourceBits, roundFunction);
        }
    }
}
