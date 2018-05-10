package feistel;

import isomorphic.Isomorphism;

/**
 * Function to be called for each round of a Feistel function.
 * <p>
 * For a Feistel function to be {@link Isomorphism#inverse() invertible},
 * the round function must be a pure function - without side effects,
 * given the same input must always produce the same output.
 */
@FunctionalInterface
public interface RoundFunction<A> {

    /**
     * Applies this round function on the current round value.
     *
     * @param round the current round, for a Feistel with n rounds,
     *              the values will be {0,1,...,n-1} in that order,
     *              the order will be reversed for the
     *              {@link Isomorphism#inverse() inverse} Feistel.
     * @param value the current round value
     * @return the round result
     */
    A apply(int round, A value);

    /**
     * A round function specialized for {@code int} values.
     */
    @FunctionalInterface
    interface OfInt extends RoundFunction<Integer> {

        /**
         * Applies this round function on the current round value.
         *
         * @param round the current round, for a Feistel with n rounds,
         *              the values will be {0,1,...,n-1} in that order,
         *              the order will be reversed for the
         *              {@link Isomorphism#inverse() inverse} Feistel.
         * @param value the current round value
         * @return the round result
         */
        int applyAsInt(int round, int value);

        @Override
        default Integer apply(int round, Integer value) {
            return applyAsInt(round, value);
        }
    }

    /**
     * A round function specialized for {@code long} values.
     */
    @FunctionalInterface
    interface OfLong extends RoundFunction<Long> {

        /**
         * Applies this round function on the current round value.
         *
         * @param round the current round, for a Feistel with n rounds,
         *              the values will be {0,1,...,n-1} in that order,
         *              the order will be reversed for the
         *              {@link Isomorphism#inverse() inverse} Feistel.
         * @param value the current round value
         * @return the round result
         */
        long applyAsLong(int round, long value);

        @Override
        default Long apply(int round, Long value) {
            return applyAsLong(round, value);
        }
    }
}
