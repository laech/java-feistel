package feistel;

import static java.util.Objects.requireNonNull;

final class FeistelOfLongBalanced implements Feistel.OfLong {

    private final int rounds;
    private final boolean reversed;
    private final RoundFunction.OfLong f;

    FeistelOfLongBalanced(int rounds, boolean reversed, RoundFunction.OfLong f) {
        this.rounds = rounds;
        this.reversed = reversed;
        this.f = requireNonNull(f);
    }

    @Override
    public long applyAsLong(long input) {
        long b = input >>> 32;
        long a = input & 0xffff_ffffL;
        for (int i = 0; i < rounds; i++) {
            int round = reversed ? rounds - i - 1 : i;
            long F = f.applyAsLong(round, b) & 0xff_ff_ff_ffL;
            long a_ = a;
            a = b;
            b = a_ ^ F;
        }
        return (a << 32) | b;
    }

    @Override
    public FeistelOfLongBalanced reversed() {
        return new FeistelOfLongBalanced(rounds, !reversed, f);
    }
}
