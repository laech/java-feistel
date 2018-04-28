package feistel;

@FunctionalInterface
public interface RoundFunction<T> {

    T apply(int round, T input);

    @FunctionalInterface
    interface OfInt {

        int applyAsInt(int round, int input);

    }

    @FunctionalInterface
    interface OfLong {

        long applyAsLong(int round, long input);

    }
}
