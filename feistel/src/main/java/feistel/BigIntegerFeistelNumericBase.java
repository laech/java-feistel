package feistel;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static java.util.Objects.requireNonNull;

abstract class BigIntegerFeistelNumericBase implements Feistel<BigInteger> {

    final int rounds;
    final BigInteger a;
    final BigInteger b;
    final BigInteger max;
    final boolean reversed;
    final RoundFunction<BigInteger> f;

    BigIntegerFeistelNumericBase(
            int rounds,
            BigInteger a,
            BigInteger b,
            boolean reversed,
            RoundFunction<BigInteger> f
    ) {
        requireNonNull(a, "a cannot be null");
        requireNonNull(b, "b cannot be null");
        requireNonNull(f, "f cannot be null");

        if (rounds < 0) {
            throw new IllegalArgumentException(
                    "rounds cannot be negative: " + rounds);
        }
        if (a.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException(
                    "a cannot be negative: " + a);
        }
        if (b.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException(
                    "b cannot be negative: " + b);
        }

        this.reversed = reversed;
        this.rounds = rounds;
        this.max = a.multiply(b).subtract(ONE);
        this.a = a;
        this.b = b;
        this.f = f;
    }

    @Override
    public final BigInteger apply(BigInteger input) {
        if (input.compareTo(ZERO) < 0 || input.compareTo(max) > 0) {
            throw new IllegalArgumentException(
                    "input out of range (min=0, max=" + max + "): " + input);
        }
        return reversed
                ? applyBackward(input)
                : applyForward(input);
    }

    abstract BigInteger applyForward(BigInteger x);

    abstract BigInteger applyBackward(BigInteger y);

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
