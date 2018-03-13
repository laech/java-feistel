package feistel;

import java.math.BigInteger;
import java.util.function.LongUnaryOperator;
import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;

public interface Feistel<T> extends UnaryOperator<T> {

    @Override
    T apply(T input);

    Feistel<T> reversed();

    interface OfLong extends Feistel<Long>, LongUnaryOperator {

        @Override
        OfLong reversed();

        @Override
        long applyAsLong(long input);

        @Override
        default Long apply(Long input) {
            return applyAsLong(input);
        }
    }

    @FunctionalInterface
    interface RoundFunction<T> {

        T apply(int round, T input);

        static <T> RoundFunction<T> identity() {
            return (i, t) -> t;
        }

        @FunctionalInterface
        interface OfLong extends RoundFunction<Long> {

            long applyAsLong(int round, long input);

            @Override
            default Long apply(int round, Long input) {
                return applyAsLong(round, input);
            }

            static OfLong identity() {
                return (i, t) -> t;
            }
        }
    }

    static Feistel<BigInteger> numeric(int rounds, BigInteger m, BigInteger n, RoundFunction<BigInteger> f) {

        if (rounds < 0) {
            throw new IllegalArgumentException("rounds=" + rounds);
        }
        requireNonNull(m);
        requireNonNull(n);
        requireNonNull(f);

        UnaryOperator<BigInteger> forward = input -> {
            if (input.compareTo(BigInteger.ZERO) < 0) {
                throw new IllegalArgumentException(input.toString());
            }
            for (int i = 0; i < rounds; i++) {
                BigInteger a = input.divide(n);
                BigInteger b = input.remainder(n);
                BigInteger w = a.add(f.apply(i, b)).remainder(m);
                input = m.multiply(b).add(w);
            }
            return input;
        };

        UnaryOperator<BigInteger> backward = input -> {
            if (input.compareTo(BigInteger.ZERO) < 0) {
                throw new IllegalArgumentException(input.toString());
            }
            for (int i = rounds - 1; i >= 0; i--) {
                BigInteger w = input.remainder(m);
                BigInteger b = input.divide(m);
                BigInteger a = w.subtract(f.apply(i, b)).remainder(m);
                input = n.multiply(a).add(b);
            }
            return input;
        };

        return new FeistelImpl<>(forward, backward);
    }

    static BigInteger numeric(BigInteger input, int rounds, BigInteger m, BigInteger n, UnaryOperator<BigInteger> roundFunction) {
        for (int i = 0; i < rounds; i++) {
            BigInteger a = input.divide(n);
            BigInteger b = input.remainder(n);
            input = m.multiply(b).add(a.add(roundFunction.apply(b)).remainder(m));
        }
        return input;
    }

    static long numeric(long input, int rounds, long m, long n, LongUnaryOperator roundFunction) {
        for (int i = 0; i < rounds; i++) {
            long a = input / n;
            long b = input % n;
            input = m * b + (a + roundFunction.applyAsLong(b)) % m;
        }
        return input;
    }

    static long numeric2(long input, int rounds, long a, long b, LongUnaryOperator roundFunction) {
        long l = input / b;
        long r = input % b;
        long s = 1;
        for (int i = 1; i <= rounds; i++) {
            s = i % 2 != 0 ? a : b;
            long l_ = l;
            l = r;
            r = (l_ + roundFunction.applyAsLong(r)) % s;
        }
        return s * l + r;
    }

    static long unbalanced(
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
                : doUnbalanced(input, rounds, 64, sourceBits, targetBits, false, roundFunction);
    }

    static long doUnbalanced(
            long input,
            int rounds,
            int totalBits,
            int sourceBits,
            int targetBits,
            boolean reverse,
            LongUnaryOperator roundFunction
    ) {
        long totalMask = 0xffff_ffff_ffff_ffffL >>> (Long.SIZE - totalBits);
        int nullBits = totalBits - sourceBits - targetBits;
        long nullMask = totalMask >>> sourceBits >>> targetBits;
        long sourceMask = totalMask >>> nullBits >>> targetBits;
        long targetMask = totalMask >>> nullBits >>> sourceBits;

        if ((input & ~totalMask) != 0) {
            throw new IllegalArgumentException();
        }

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

    static long balanced(long input, int rounds, LongUnaryOperator roundFunction) {
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

    static LongUnaryOperator compute(int rounds, LongUnaryOperator roundFunction) {
        return input -> balanced(input, rounds, roundFunction);
    }
}

final class FeistelImpl<T> implements Feistel<T> {

    private final UnaryOperator<T> forward;
    private final UnaryOperator<T> backward;

    FeistelImpl(UnaryOperator<T> forward, UnaryOperator<T> backward) {
        this.forward = requireNonNull(forward);
        this.backward = requireNonNull(backward);
    }

    @Override
    public T apply(T input) {
        return forward.apply(input);
    }

    @Override
    public Feistel<T> reversed() {
        return new FeistelImpl<>(backward, forward);
    }
}
