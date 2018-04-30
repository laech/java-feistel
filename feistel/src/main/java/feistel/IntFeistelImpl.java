package feistel;

import static java.lang.Integer.toUnsignedLong;
import static java.lang.Math.toIntExact;
import static java.util.Objects.requireNonNull;

final class IntFeistelImpl implements Feistel.OfInt {

    private final Feistel.OfLong delegate;

    IntFeistelImpl(Feistel.OfLong delegate) {
        this.delegate = requireNonNull(delegate);
    }

    @Override
    public int applyAsInt(int input) {
        return toIntExact(delegate.applyAsLong(toUnsignedLong(input)));
    }

    @Override
    public Feistel.OfInt inverse() {
        return new IntFeistelImpl(delegate.inverse());
    }
}
