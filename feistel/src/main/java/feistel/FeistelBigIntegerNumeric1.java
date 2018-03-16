package feistel;

import java.math.BigInteger;

final class FeistelBigIntegerNumeric1 extends FeistelBigIntegerNumericBase {

    FeistelBigIntegerNumeric1(
            int rounds,
            BigInteger a,
            BigInteger b,
            boolean reversed,
            RoundFunction<BigInteger> f
    ) {
        super(rounds, a, b, reversed, f);
    }

    @Override
    BigInteger applyForward(BigInteger x) {
        for (int i = 0; i < rounds; i++) {
            BigInteger l = x.divide(b);
            BigInteger r = x.mod(b);
            BigInteger w = l.add(f.apply(i, r)).mod(a);
            x = a.multiply(r).add(w);
        }
        return x;
    }

    @Override
    BigInteger applyBackward(BigInteger y) {
        for (int i = rounds - 1; i >= 0; i--) {
            BigInteger w = y.mod(a);
            BigInteger r = y.divide(a);
            BigInteger l = w.subtract(f.apply(i, r)).mod(a);
            y = b.multiply(l).add(r);
        }
        return y;
    }

    @Override
    public FeistelBigIntegerNumeric1 reversed() {
        return new FeistelBigIntegerNumeric1(rounds, a, b, !reversed, f);
    }
}
