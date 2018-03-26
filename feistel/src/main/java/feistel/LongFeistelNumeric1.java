package feistel;

import static java.lang.Math.floorMod;

final class LongFeistelNumeric1 extends LongFeistelNumericBase {

    LongFeistelNumeric1(
            int rounds,
            long a,
            long b,
            boolean reversed,
            LongRoundFunction f
    ) {
        super(rounds, a, b, reversed, f);
    }

    @Override
    long applyForward(long x) {
        for (int i = 0; i < rounds; i++) {
            long l = x / b;
            long r = floorMod(x, b);
            long w = floorMod(l + f.applyAsLong(i, r), a);
            x = a * r + w;
        }
        return x;
    }

    @Override
    long applyBackward(long y) {
        for (int i = rounds - 1; i >= 0; i--) {
            long w = floorMod(y, a);
            long r = y / a;
            long l = floorMod(w - f.applyAsLong(i, r), a);
            y = b * l + r;
        }
        return y;
    }

    @Override
    public LongFeistelNumeric1 reversed() {
        return new LongFeistelNumeric1(rounds, a, b, !reversed, f);
    }
}