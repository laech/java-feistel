package feistel;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;

final class FeistelBigIntegerNumeric2 extends FeistelBigIntegerNumericBase {

    FeistelBigIntegerNumeric2(
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
        BigInteger l = x.divide(b);
        BigInteger r = x.mod(b);
        BigInteger s = ONE;
        for (int i = 0; i < rounds; i++) {
            s = i % 2 == 0 ? a : b;
            BigInteger l_ = l;
            l = r;
            r = l_.add(f.apply(i, r)).mod(s);
        }
        return s.multiply(l).add(r);
    }

    @Override
    BigInteger applyBackward(BigInteger y) {
        BigInteger s = rounds % 2 != 0 ? a : b;
        BigInteger r = y.mod(s);
        BigInteger l = y.divide(s);
        for (int i = rounds - 1; i >= 0; i--) {
            s = i % 2 == 0 ? a : b;
            BigInteger r_ = r;
            r = l;
            l = r_.subtract(f.apply(i, l)).mod(s);
        }
        return b.multiply(l).add(r);
    }

    @Override
    public FeistelBigIntegerNumeric2 reversed() {
        return new FeistelBigIntegerNumeric2(rounds, a, b, !reversed, f);
    }
}
