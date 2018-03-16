package feistel;

@FunctionalInterface
public interface RoundFunction<T> {

    T apply(int round, T input);

}
