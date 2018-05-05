package feistel;

import java.util.function.Function;

/**
 * A function paired with an {@link #inverse() inverse}.
 * <pre>
 * Isomorphism&lt;Integer, String&gt; f = ...
 * Isomorphism&lt;String, Integer&gt; g = f.inverse();
 * g.apply(f.apply(x)) == x == f.apply(g.apply(x));
 * </pre>
 */
public interface Isomorphism<T, R> extends Function<T, R> {

    // TODO where to put this?

    /**
     * Returns the inverse function.
     */
    Isomorphism<R, T> inverse();

    /**
     * Creates an isomorphism from a pair of functions,
     * where {@code f} is the {@link #apply(Object) apply} function,
     * and {@code g} is the {@link #inverse() inverse} function.
     *
     * @throws NullPointerException if {@code f} or {@code g} is null
     */
    static <T, R> Isomorphism<T, R> of(Function<T, R> f, Function<R, T> g) {
        return new Isomorphism<T, R>() {

            @Override
            public R apply(T t) {
                return f.apply(t);
            }

            @Override
            public Isomorphism<R, T> inverse() {
                return of(g, f);
            }
        };
    }

    /**
     * Returns a composed function that first applies the {@code before}
     * function to its input, and then applies this function to the result.
     *
     * @throws NullPointerException if {@code before} is null
     */
    default <V> Isomorphism<V, R> compose(Isomorphism<V, T> before) {
        return of(
                compose((Function<V, T>) before),
                before.inverse().compose((Function<R, T>) inverse())
        );
    }

    /**
     * Returns a composed function that first applies this function to
     * its input, and then applies the {@code after} function to the result.
     *
     * @throws NullPointerException if {@code after} is null
     */
    default <V> Isomorphism<T, V> andThen(Isomorphism<R, V> after) {
        return of(
                andThen((Function<R, V>) after),
                after.inverse().andThen((Function<R, T>) inverse())
        );
    }

    /**
     * Returns a function that always returns its input argument.
     */
    static <T> Isomorphism<T, T> identity() {
        return of(Function.identity(), Function.identity());
    }
}
