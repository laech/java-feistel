package feistel;

import static java.lang.Math.multiplyExact;
import static java.util.Objects.requireNonNull;

abstract class LongFeistelNumericBase implements LongFeistel {

    final int rounds;
    final long a;
    final long b;
    final long max;
    final boolean reversed;
    final LongRoundFunction f;

    LongFeistelNumericBase(
            int rounds,
            long a,
            long b,
            boolean reversed,
            LongRoundFunction f
    ) {
        requireNonNull(f, "f cannot be null");

        if (rounds < 0) {
            throw new IllegalArgumentException(
                    "rounds cannot be negative: " + rounds);
        }
        if (a < 0) {
            throw new IllegalArgumentException(
                    "a cannot be negative: " + a);
        }
        if (b < 0) {
            throw new IllegalArgumentException(
                    "b cannot be negative: " + b);
        }

        this.max = multiplyExact(a, b) - 1;
        this.reversed = reversed;
        this.rounds = rounds;
        this.a = a;
        this.b = b;
        this.f = f;
    }

    @Override
    public final long applyAsLong(long input) {
        if (input < 0 || input > max) {
            throw new IllegalArgumentException(
                    "input out of range (min=0, max=" + max + "): " + input);
        }
        return reversed
                ? applyBackward(input)
                : applyForward(input);
    }

    abstract long applyForward(long x);

    abstract long applyBackward(long y);

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "{rounds=" + rounds +
                ", a=" + a +
                ", b=" + b +
                ", max=" + max +
                ", reversed=" + reversed +
                ", f=" + f +
                '}';
    }
}
