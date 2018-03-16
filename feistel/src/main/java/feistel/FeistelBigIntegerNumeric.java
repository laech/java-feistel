package feistel;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static java.util.Objects.requireNonNull;

final class FeistelBigIntegerNumeric implements Feistel<BigInteger> {

    private final int rounds;
    private final BigInteger a;
    private final BigInteger b;
    private final BigInteger max;
    private final boolean reversed;
    private final RoundFunction<BigInteger> f;

    FeistelBigIntegerNumeric(
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
    public BigInteger apply(BigInteger input) {
        if (input.compareTo(ZERO) < 0 || input.compareTo(max) > 0) {
            throw new IllegalArgumentException(
                    "input out of range (min=0, max=" + max + "): " + input);
        }
        return reversed
                ? applyBackward(input)
                : applyForward(input);
    }

    private BigInteger applyForward(BigInteger x) {
        for (int i = 0; i < rounds; i++) {
            BigInteger l = x.divide(b);
            BigInteger r = x.mod(b);
            BigInteger w = l.add(f.apply(i, r)).mod(a);
            x = a.multiply(r).add(w);
        }
        return x;
    }

    private BigInteger applyBackward(BigInteger y) {
        for (int i = rounds - 1; i >= 0; i--) {
            BigInteger w = y.mod(a);
            BigInteger r = y.divide(a);
            BigInteger l = w.subtract(f.apply(i, r)).mod(a);
            y = b.multiply(l).add(r);
        }
        return y;
    }

    @Override
    public FeistelBigIntegerNumeric reversed() {
        return new FeistelBigIntegerNumeric(
                rounds, a, b, !reversed, f);
    }
}
