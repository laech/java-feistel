package feistel;

import java.util.function.UnaryOperator;

public interface Feistel<T> extends UnaryOperator<T> {

    @Override
    T apply(T input);

    Feistel<T> reversed();

}
