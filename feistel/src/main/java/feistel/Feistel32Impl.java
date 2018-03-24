package feistel;

import static java.lang.Integer.toUnsignedLong;
import static java.lang.Math.toIntExact;
import static java.util.Objects.requireNonNull;

final class Feistel32Impl implements Feistel32 {

    private final Feistel64 delegate;

    Feistel32Impl(Feistel64 delegate) {
        this.delegate = requireNonNull(delegate);
    }

    @Override
    public int applyAsInt(int input) {
        return toIntExact(delegate.applyAsLong(toUnsignedLong(input)));
    }

    @Override
    public Feistel32 reversed() {
        return new Feistel32Impl(delegate.reversed());
    }
}
