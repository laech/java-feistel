package feistel;

import java.util.function.IntUnaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.function.UnaryOperator;

import static java.lang.Integer.toUnsignedLong;
import static java.lang.Math.multiplyExact;
import static java.lang.Math.toIntExact;
import static java.util.Objects.requireNonNull;

public interface Feistel<T> extends UnaryOperator<T> {

    Feistel<T> reversed();

    /**
     * Returns a composed function that first applies the {@code before}
     * function to its input, and then applies this function to the result.
     *
     * @throws NullPointerException if before is null
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
     * @throws NullPointerException if after is null
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

    static <T> Feistel<T> of(UnaryOperator<T> f, UnaryOperator<T> g) {
        return new Feistel<T>() {

            @Override
            public T apply(T value) {
                return f.apply(value);
            }

            @Override
            public Feistel<T> reversed() {
                return of(g, f);
            }
        };
    }

    static Feistel.OfLong ofLong(LongUnaryOperator f, LongUnaryOperator g) {
        return new Feistel.OfLong() {

            @Override
            public long applyAsLong(long value) {
                return f.applyAsLong(value);
            }

            @Override
            public Feistel.OfLong reversed() {
                return ofLong(g, f);
            }
        };
    }

    static Feistel.OfInt ofInt(IntUnaryOperator f, IntUnaryOperator g) {
        return new Feistel.OfInt() {

            @Override
            public int applyAsInt(int value) {
                return f.applyAsInt(value);
            }

            @Override
            public Feistel.OfInt reversed() {
                return ofInt(g, f);
            }
        };
    }

    interface OfInt extends Feistel<Integer>, IntUnaryOperator {

        @Override
        OfInt reversed();

        @Override
        default Integer apply(Integer value) {
            return applyAsInt(value);
        }

        /**
         * Returns a composed function that first applies the {@code before}
         * function to its input, and then applies this function to the result.
         *
         * @throws NullPointerException if before is null
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
         * @throws NullPointerException if after is null
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

    interface OfLong extends Feistel<Long>, LongUnaryOperator {

        @Override
        OfLong reversed();

        @Override
        default Long apply(Long value) {
            return applyAsLong(value);
        }

        /**
         * Returns a composed function that first applies the {@code before}
         * function to its input, and then applies this function to the result.
         *
         * @throws NullPointerException if before is null
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
         * @throws NullPointerException if after is null
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
