package feistel;

import static java.lang.Math.floorMod;

final class Feistel64Numeric2 extends Feistel64NumericBase {

    Feistel64Numeric2(
            int rounds,
            long a,
            long b,
            boolean reversed,
            RoundFunction64 f
    ) {
        super(rounds, a, b, reversed, f);
    }

    @Override
    long applyForward(long x) {
        long l = x / b;
        long r = floorMod(x, b);
        long s = 1;
        for (int i = 0; i < rounds; i++) {
            s = i % 2 == 0 ? a : b;
            long l_ = l;
            l = r;
            r = floorMod(l_ + f.applyAsLong(i, r), s);
        }
        return s * l + r;
    }

    @Override
    long applyBackward(long y) {
        long s = rounds % 2 != 0 ? a : b;
        long r = floorMod(y, s);
        long l = y / s;
        for (int i = rounds - 1; i >= 0; i--) {
            s = i % 2 == 0 ? a : b;
            long r_ = r;
            r = l;
            l = floorMod(r_ - f.applyAsLong(i, l), s);
        }
        return b * l + r;
    }

    @Override
    public Feistel64Numeric2 reversed() {
        return new Feistel64Numeric2(rounds, a, b, !reversed, f);
    }
}
