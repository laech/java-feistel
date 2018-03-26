package feistel;

import static java.lang.Integer.toUnsignedLong;
import static java.lang.Math.toIntExact;
import static java.util.Objects.requireNonNull;

final class IntFeistelImpl implements IntFeistel {

    private final LongFeistel delegate;

    IntFeistelImpl(LongFeistel delegate) {
        this.delegate = requireNonNull(delegate);
    }

    @Override
    public int applyAsInt(int input) {
        return toIntExact(delegate.applyAsLong(toUnsignedLong(input)));
    }

    @Override
    public IntFeistel reversed() {
        return new IntFeistelImpl(delegate.reversed());
    }
}
