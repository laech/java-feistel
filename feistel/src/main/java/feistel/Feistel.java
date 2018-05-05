package feistel;

import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;

/**
 * A generalized <a href="https://en.wikipedia.org/wiki/Feistel_cipher">Feistel</a>
 * function.
 */
public interface Feistel<T> extends UnaryOperator<T>, Isomorphism<T, T> {

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

    @Override
    Feistel<T> inverse();

    /**
     * Returns a composed function that first applies the {@code before}
     * function to its input, and then applies this function to the result.
     *
     * @throws NullPointerException if {@code before} is null
     */
    default Feistel<T> compose(Feistel<T> before) {
        return of(compose((Isomorphism<T, T>) before));
    }

    /**
     * Returns a composed function that first applies this function to
     * its input, and then applies the {@code after} function to the result.
     *
     * @throws NullPointerException if {@code after} is null
     */
    default Feistel<T> andThen(Feistel<T> after) {
        return of(andThen((Isomorphism<T, T>) after));
    }

    /**
     * Returns a function that always returns its input argument.
     */
    static <T> Feistel<T> identity() {
        return of(Isomorphism.identity());
    }

    static <T> Feistel<T> of(Isomorphism<T, T> f) {
        if (f instanceof Feistel<?>) {
            return (Feistel<T>) f;
        } else {
            return of(f, f.inverse());
        }
    }

    /**
     * Creates a Feistel function from a pair of functions,
     * where {@code f} is the {@link #apply(Object) apply} function,
     * and {@code g} is the {@link #inverse() inverse} function.
     *
     * @throws NullPointerException if {@code f} or {@code g} is null
     */
    static <T> Feistel<T> of(Function<T, T> f, Function<T, T> g) {
        return of(Isomorphism.of(f, g));
    }

    /**
     * Creates a Feistel function from a pair functions,
     * where {@code f} is the {@link OfLong#apply(Object) apply} function,
     * and {@code g} is the {@link OfLong#inverse() inverse} function.
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

    static OfLong ofLongBinary(int rounds, RoundFunction.OfLong f) {
        return FeistelOfLongBinary.unbalanced(rounds, 64, 32, 32, f);
    }

    static OfLong ofLongBinary(int rounds, int sourceBits, int targetBits, RoundFunction.OfLong f) {
        return FeistelOfLongBinary.unbalanced(rounds, 64, sourceBits, targetBits, f);
    }

    static OfLong ofLongNumeric1(int rounds, long a, long b, RoundFunction.OfLong f) {
        return FeistelOfLongNumeric.fe1(rounds, a, b, f);
    }

    static OfLong ofLongNumeric2(int rounds, long a, long b, RoundFunction.OfLong f) {
        return FeistelOfLongNumeric.fe2(rounds, a, b, f);
    }

    /**
     * Creates a Feistel function from a pair functions,
     * where {@code f} is the {@link OfInt#applyAsInt(int) apply} function,
     * and {@code g} is the {@link OfInt#inverse() inverse} function.
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

    static OfInt ofIntBinary(int rounds, RoundFunction.OfInt f) {
        return new IntFeistelImpl(FeistelOfLongBinary.unbalanced(
                rounds, 32, 16, 16, IntFeistelImpl.toRoundFunction64(f)));
    }

    static OfInt ofIntBinary(int rounds, int sourceBits, int targetBits, RoundFunction.OfInt f) {
        return new IntFeistelImpl(FeistelOfLongBinary.unbalanced(
                rounds, 32, sourceBits, targetBits, IntFeistelImpl.toRoundFunction64(f)));
    }

    static OfInt ofIntNumeric1(int rounds, int a, int b, RoundFunction.OfInt f) {
        IntFeistelImpl.checkNumeric(a, b);
        return new IntFeistelImpl(ofLongNumeric1(
                rounds, a, b, IntFeistelImpl.toRoundFunction64(f)));
    }

    static OfInt ofIntNumeric2(int rounds, int a, int b, RoundFunction.OfInt f) {
        IntFeistelImpl.checkNumeric(a, b);
        return new IntFeistelImpl(ofLongNumeric2(
                rounds, a, b, IntFeistelImpl.toRoundFunction64(f)));
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

    }
}
