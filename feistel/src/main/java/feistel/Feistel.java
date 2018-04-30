package feistel;

import java.util.function.IntUnaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.function.UnaryOperator;

import static java.lang.Integer.toUnsignedLong;
import static java.lang.Math.multiplyExact;
import static java.lang.Math.toIntExact;
import static java.util.Objects.requireNonNull;

/**
 * A <a href="https://en.wikipedia.org/wiki/Feistel_cipher">Feistel</a>
 * function is an isomorphism - a function paired with an {@link #inverse() inverse}.
 * <p>
 * Given a function {@code f} and its inverse {@code g},
 * the following holds:
 * <pre>
 * g(f(x)) == x == f(g(x))
 * </pre>
 */
public interface Feistel<T> extends UnaryOperator<T> {

    /**
     * Applies this function to the given argument.
     *
     * @throws IllegalArgumentException if the value is considered to be
     *                                  invalid by this function, for example,
     *                                  you have created a 24-bit function but
     *                                  passed in a value that is greater than
     *                                  2<sup>24</sup> - 1.
     */
    @Override
    T apply(T value);

    /**
     * Returns a function that is the inverse of this function.
     * <p>
     * Applying the inverse function on the output of this function
     * will give back the original input, equivalent to applying the
     * {@link #identity() identity function} to the original input.
     * <pre>
     * Feistel&lt;Integer&gt; f = ...
     * Feistel&lt;Integer&gt; g = f.inverse();
     * g.apply(f.apply(x)) == x == f.apply(g.apply(x));
     * </pre>
     */
    Feistel<T> inverse();

    /**
     * Returns a composed function that first applies the {@code before}
     * function to its input, and then applies this function to the result.
     *
     * @throws NullPointerException if {@code before} is null
     */
    default Feistel<T> compose(Feistel<T> before) {
        requireNonNull(before, "before cannot be null");
        return of(
                x -> apply(before.apply(x)),
                y -> before.apply(apply(y))
        );
    }

    /**
     * Returns a composed function that first applies this function to
     * its input, and then applies the {@code after} function to the result.
     *
     * @throws NullPointerException if {@code after} is null
     */
    default Feistel<T> andThen(Feistel<T> after) {
        requireNonNull(after, "after cannot be null");
        return of(
                x -> after.apply(apply(x)),
                y -> apply(after.apply(y))
        );
    }

    /**
     * Returns a function that always returns its input argument.
     */
    static <T> Feistel<T> identity() {
        return of(
                UnaryOperator.identity(),
                UnaryOperator.identity()
        );
    }

    /**
     * Creates a Feistel function from a pair of inverse functions,
     * where {@code f} is the {@link Feistel#apply(Object) apply} function,
     * and {@code g} is the {@link Feistel#inverse() inverse} function.
     * <p>
     * {@code f} and {@code g} are inverse of each other meaning:
     * <pre>
     * g(f(x)) == x == f(g(x))
     * </pre>
     *
     * @throws NullPointerException if {@code f} or {@code g} is null
     */
    static <T> Feistel<T> of(UnaryOperator<T> f, UnaryOperator<T> g) {
        requireNonNull(f, "f cannot be null");
        requireNonNull(g, "g cannot be null");
        return new Feistel<T>() {

            @Override
            public T apply(T value) {
                return f.apply(value);
            }

            @Override
            public Feistel<T> inverse() {
                return of(g, f);
            }
        };
    }

    /**
     * Creates a Feistel function from a pair functions,
     * where {@code f} is the {@link Feistel#apply(Object) apply} function,
     * and {@code g} is the {@link Feistel#inverse() inverse} function.
     * <p>
     * {@code f} and {@code g} are inverse of each other meaning:
     * <pre>
     * g(f(x)) == x == f(g(x))
     * </pre>
     *
     * @throws NullPointerException if {@code f} or {@code g} is null
     */
    static Feistel.OfLong ofLong(LongUnaryOperator f, LongUnaryOperator g) {
        requireNonNull(f, "f cannot be null");
        requireNonNull(g, "g cannot be null");
        return new Feistel.OfLong() {

            @Override
            public long applyAsLong(long value) {
                return f.applyAsLong(value);
            }

            @Override
            public Feistel.OfLong inverse() {
                return ofLong(g, f);
            }
        };
    }

    /**
     * Creates a Feistel function from a pair functions,
     * where {@code f} is the {@link Feistel#apply(Object) apply} function,
     * and {@code g} is the {@link Feistel#inverse() inverse} function.
     * <p>
     * {@code f} and {@code g} are inverse of each other meaning:
     * <pre>
     * g(f(x)) == x == f(g(x))
     * </pre>
     *
     * @throws NullPointerException if {@code f} or {@code g} is null
     */
    static Feistel.OfInt ofInt(IntUnaryOperator f, IntUnaryOperator g) {
        requireNonNull(f, "f cannot be null");
        requireNonNull(g, "g cannot be null");
        return new Feistel.OfInt() {

            @Override
            public int applyAsInt(int value) {
                return f.applyAsInt(value);
            }

            @Override
            public Feistel.OfInt inverse() {
                return ofInt(g, f);
            }
        };
    }

    /**
     * A Feistel specialised for {@code int} values.
     */
    interface OfInt extends Feistel<Integer>, IntUnaryOperator {

        /**
         * Applies this function to the given argument.
         *
         * @throws IllegalArgumentException if the value is considered to be
         *                                  invalid by this function, for example,
         *                                  you have created a 24-bit function but
         *                                  passed in a value that is greater than
         *                                  2<sup>24</sup> - 1.
         */
        @Override
        int applyAsInt(int value);

        @Override
        OfInt inverse();

        @Override
        default Integer apply(Integer value) {
            return applyAsInt(value);
        }

        /**
         * Returns a composed function that first applies the {@code before}
         * function to its input, and then applies this function to the result.
         *
         * @throws NullPointerException if {@code before} is null
         */
        default OfInt compose(OfInt before) {
            requireNonNull(before, "before cannot be null");
            return ofInt(
                    x -> applyAsInt(before.applyAsInt(x)),
                    y -> before.applyAsInt(applyAsInt(y))
            );
        }

        /**
         * Returns a composed function that first applies this function to
         * its input, and then applies the {@code after} function to the result.
         *
         * @throws NullPointerException if {@code after} is null
         */
        default OfInt andThen(OfInt after) {
            requireNonNull(after, "after cannot be null");
            return ofInt(
                    x -> after.applyAsInt(applyAsInt(x)),
                    y -> applyAsInt(after.applyAsInt(y))
            );
        }

        /**
         * Returns a function that always returns its input argument.
         */
        static OfInt identity() {
            return ofInt(
                    IntUnaryOperator.identity(),
                    IntUnaryOperator.identity()
            );
        }

        static OfInt binary(int rounds, RoundFunction.OfInt f) {
            return new IntFeistelImpl(FeistelOfLongBinary.unbalanced(
                    rounds, 32, 16, 16, toRoundFunction64(f)));
        }

        static OfInt binary(int rounds, int sourceBits, int targetBits, RoundFunction.OfInt f) {
            return new IntFeistelImpl(FeistelOfLongBinary.unbalanced(
                    rounds, 32, sourceBits, targetBits, toRoundFunction64(f)));
        }

        static OfInt numeric1(int rounds, int a, int b, RoundFunction.OfInt f) {
            checkNumeric(a, b);
            return new IntFeistelImpl(Feistel.OfLong.numeric1(
                    rounds, a, b, toRoundFunction64(f)));
        }

        static OfInt numeric2(int rounds, int a, int b, RoundFunction.OfInt f) {
            checkNumeric(a, b);
            return new IntFeistelImpl(Feistel.OfLong.numeric2(
                    rounds, a, b, toRoundFunction64(f)));
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
    }

    /**
     * A Feistel specialised for {@code long} values.
     */
    interface OfLong extends Feistel<Long>, LongUnaryOperator {

        /**
         * Applies this function to the given argument.
         *
         * @throws IllegalArgumentException if the value is considered to be
         *                                  invalid by this function, for example,
         *                                  you have created a 24-bit function but
         *                                  passed in a value that is greater than
         *                                  2<sup>24</sup> - 1.
         */
        @Override
        long applyAsLong(long value);

        @Override
        OfLong inverse();

        @Override
        default Long apply(Long value) {
            return applyAsLong(value);
        }

        /**
         * Returns a composed function that first applies the {@code before}
         * function to its input, and then applies this function to the result.
         *
         * @throws NullPointerException if {@code before} is null
         */
        default OfLong compose(OfLong before) {
            requireNonNull(before, "before cannot be null");
            return ofLong(
                    x -> applyAsLong(before.applyAsLong(x)),
                    y -> before.applyAsLong(applyAsLong(y))
            );
        }

        /**
         * Returns a composed function that first applies this function to
         * its input, and then applies the {@code after} function to the result.
         *
         * @throws NullPointerException if {@code after} is null
         */
        default OfLong andThen(OfLong after) {
            requireNonNull(after, "after cannot be null");
            return ofLong(
                    x -> after.applyAsLong(applyAsLong(x)),
                    y -> applyAsLong(after.applyAsLong(y))
            );
        }

        /**
         * Returns a function that always returns its input argument.
         */
        static OfLong identity() {
            return ofLong(
                    LongUnaryOperator.identity(),
                    LongUnaryOperator.identity()
            );
        }

        static OfLong binary(int rounds, RoundFunction.OfLong f) {
            return FeistelOfLongBinary.unbalanced(rounds, 64, 32, 32, f);
        }

        static OfLong binary(int rounds, int sourceBits, int targetBits, RoundFunction.OfLong f) {
            return FeistelOfLongBinary.unbalanced(rounds, 64, sourceBits, targetBits, f);
        }

        static OfLong numeric1(int rounds, long a, long b, RoundFunction.OfLong f) {
            return FeistelOfLongNumeric.numeric1(rounds, a, b, f);
        }

        static OfLong numeric2(int rounds, long a, long b, RoundFunction.OfLong f) {
            return FeistelOfLongNumeric.numeric2(rounds, a, b, f);
        }
    }
}
