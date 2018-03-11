package feistel;

import java.util.function.LongUnaryOperator;

public final class Feistel {

    private Feistel() {
    }

    public static long unbalanced(
            long input,
            int rounds,
            int sourceBits,
            int targetBits,
            LongUnaryOperator roundFunction
    ) {
        if (sourceBits < 0
                || targetBits < 0
                || targetBits + sourceBits > Long.SIZE) {
            throw new IllegalArgumentException();
        }
        return sourceBits == targetBits && sourceBits == 32
                ? balanced(input, rounds, roundFunction)
                : doUnbalanced(input, rounds, sourceBits, targetBits, false, roundFunction);
    }

    static long doUnbalanced(
            long input,
            int rounds,
            int sourceBits,
            int targetBits,
            boolean reverse,
            LongUnaryOperator roundFunction
    ) {
        int nullBits = Long.SIZE - sourceBits - targetBits;
        long nullMask = 0xffff_ffff_ffff_ffffL >>> sourceBits >>> targetBits;
        long sourceMask = 0xffff_ffff_ffff_ffffL >>> nullBits >>> targetBits;
        long targetMask = 0xffff_ffff_ffff_ffffL >>> nullBits >>> sourceBits;

        for (int i = 0; i < rounds; i++) {
            long a = input >>> targetBits >>> nullBits;
            long n = input >>> targetBits & nullMask;
            long b = input & targetMask;

            if (reverse) {
                input = ((b ^ roundFunction.applyAsLong(a) & targetMask) << nullBits << sourceBits
                        | n << sourceBits
                        | a);
            } else {
                input = ((b << nullBits << sourceBits)
                        | n << sourceBits
                        | a ^ roundFunction.applyAsLong(b) & sourceMask);
            }
        }
        return input;
    }

    public static long compute(long input, int rounds, LongUnaryOperator roundFunction) {
        return balanced(input, rounds, roundFunction);
    }

    public static long balanced(long input, int rounds, LongUnaryOperator roundFunction) {
        long a = input >>> 32;
        long b = input & 0xffff_ffffL;
        for (int i = 0; i < rounds; i++) {
            long F = roundFunction.applyAsLong(b) & 0xff_ff_ff_ffL;
            long a_ = a;
            a = b;
            b = a_ ^ F;
        }
        return (b << 32) | a;
    }

    public static LongUnaryOperator compute(int rounds, LongUnaryOperator roundFunction) {
        return input -> compute(input, rounds, roundFunction);
    }

    public static final class OfLong implements LongUnaryOperator {

        private final int rounds;
        private final int sourceBits;
        private final int targetBits;
        private final LongUnaryOperator roundFunction;

        public OfLong(int rounds, int sourceBits, int targetBits, LongUnaryOperator roundFunction) {
            this.rounds = rounds;
            this.sourceBits = sourceBits;
            this.targetBits = targetBits;
            this.roundFunction = roundFunction;
        }

        @Override
        public long applyAsLong(long input) {
            return unbalanced(input, rounds, sourceBits, targetBits, roundFunction);
        }

        public OfLong reversed() {
            if (sourceBits == targetBits && sourceBits + targetBits == Integer.SIZE) {
                return this;
            }
            return new OfLong(rounds, targetBits, sourceBits, roundFunction);
        }
    }
}
