package feistel;

import static java.util.Objects.requireNonNull;

final class Feistel64Balanced implements Feistel64 {

    private final int rounds;
    private final boolean reversed;
    private final RoundFunction64 f;

    Feistel64Balanced(int rounds, boolean reversed, RoundFunction64 f) {
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
    public Feistel64Balanced reversed() {
        return new Feistel64Balanced(rounds, !reversed, f);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (null == o || getClass() != o.getClass()) return false;
        Feistel64Balanced that = (Feistel64Balanced) o;
        return rounds == that.rounds &&
                reversed == that.reversed &&
                f.equals(that.f);
    }

    @Override
    public int hashCode() {
        int result = rounds;
        result = 31 * result + (reversed ? 1 : 0);
        result = 31 * result + f.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "{rounds=" + rounds
                + ", reversed=" + reversed
                + ", function=" + f
                + "}";
    }
}
