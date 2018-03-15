package feistel;

import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;

final class FeistelImpl<T> implements Feistel<T> {

    private final UnaryOperator<T> forward;
    private final UnaryOperator<T> backward;

    FeistelImpl(UnaryOperator<T> forward, UnaryOperator<T> backward) {
        this.forward = requireNonNull(forward);
        this.backward = requireNonNull(backward);
    }

    @Override
    public T apply(T input) {
        return forward.apply(input);
    }

    @Override
    public Feistel<T> reversed() {
        return new FeistelImpl<>(backward, forward);
    }
}
