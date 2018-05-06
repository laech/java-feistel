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
public interface Isomorphism<A, B> extends Function<A, B> {

    // TODO where to put this?

    /**
     * Returns the inverse function.
     */
    Isomorphism<B, A> inverse();

    /**
     * Creates an isomorphism from a pair of functions,
     * where {@code f} is the {@link #apply(Object) apply} function,
     * and {@code g} is the {@link #inverse() inverse} function.
     *
     * @throws NullPointerException if {@code f} or {@code g} is null
     */
    static <A, B> Isomorphism<A, B> of(Function<A, B> f, Function<B, A> g) {
        return new Isomorphism<A, B>() {

            @Override
            public B apply(A a) {
                return f.apply(a);
            }

            @Override
            public Isomorphism<B, A> inverse() {
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
    default <A0> Isomorphism<A0, B> compose(Isomorphism<A0, A> before) {
        return of(
                compose((Function<A0, A>) before),
                before.inverse().compose((Function<B, A>) inverse())
        );
    }

    /**
     * Returns a composed function that first applies this function to
     * its input, and then applies the {@code after} function to the result.
     *
     * @throws NullPointerException if {@code after} is null
     */
    default <C> Isomorphism<A, C> andThen(Isomorphism<B, C> after) {
        return of(
                andThen((Function<B, C>) after),
                after.inverse().andThen((Function<B, A>) inverse())
        );
    }

    /**
     * Returns a function that always returns its input argument.
     */
    static <A> Isomorphism<A, A> identity() {
        return of(Function.identity(), Function.identity());
    }
}
