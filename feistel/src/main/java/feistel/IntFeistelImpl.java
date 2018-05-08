package feistel;

import isomorphic.Isomorphism;

import static java.lang.Integer.toUnsignedLong;
import static java.lang.Math.multiplyExact;
import static java.lang.Math.toIntExact;
import static java.util.Objects.requireNonNull;

final class IntFeistelImpl implements Isomorphism.OfInt {

    private final Isomorphism.OfLong delegate;

    IntFeistelImpl(Isomorphism.OfLong delegate) {
        this.delegate = requireNonNull(delegate);
    }

    static void checkNumeric(int a, int b) {
        if (a < 0) {
            throw new IllegalArgumentException(
                    "a cannot be negative: " + a);
        }
        if (b < 0) {
            throw new IllegalArgumentException(
                    "b cannot be negative: " + b);
        }
        multiplyExact(a, b);
    }

    static RoundFunction.OfLong toRoundFunction64(RoundFunction.OfInt f) {
        return (round, input) -> toUnsignedLong(
                f.applyAsInt(round, toIntExact(input)));
    }

    @Override
    public int applyAsInt(int input) {
        return toIntExact(delegate.applyAsLong(toUnsignedLong(input)));
    }

    @Override
    public Isomorphism.OfInt inverse() {
        return new IntFeistelImpl(delegate.inverse());
    }
}
