package feistel;

@FunctionalInterface
public interface RoundFunction<T> {

    T apply(int round, T value);

    @FunctionalInterface
    interface OfInt extends RoundFunction<Integer> {

        int applyAsInt(int round, int value);

        @Override
        default Integer apply(int round, Integer value) {
            return applyAsInt(round, value);
        }
    }

    @FunctionalInterface
    interface OfLong extends RoundFunction<Long> {

        long applyAsLong(int round, long value);

        @Override
        default Long apply(int round, Long value) {
            return applyAsLong(round, value);
        }
    }
}
